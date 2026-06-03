Add-Type -AssemblyName System.Drawing

$source = @'
using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.Runtime.InteropServices;
using System.Collections.Generic;

public class ImageFixer {
    public static void RemoveBackground(string inputPath, string outputPath) {
        using (Bitmap bmp = new Bitmap(inputPath)) {
            // Lock bits for fast access
            BitmapData bmpData = bmp.LockBits(new Rectangle(0, 0, bmp.Width, bmp.Height), ImageLockMode.ReadWrite, bmp.PixelFormat);
            int bytesPerPixel = Bitmap.GetPixelFormatSize(bmp.PixelFormat) / 8;
            int byteCount = Math.Abs(bmpData.Stride) * bmp.Height;
            byte[] pixels = new byte[byteCount];
            IntPtr ptrFirstPixel = bmpData.Scan0;
            Marshal.Copy(ptrFirstPixel, pixels, 0, pixels.Length);
            
            int width = bmpData.Width;
            int height = bmpData.Height;
            int stride = bmpData.Stride;
            
            bool[] visited = new bool[width * height];
            Queue<Point> q = new Queue<Point>();
            
            // Add edges to queue
            for (int x = 0; x < width; x++) {
                q.Enqueue(new Point(x, 0));
                q.Enqueue(new Point(x, height - 1));
            }
            for (int y = 0; y < height; y++) {
                q.Enqueue(new Point(0, y));
                q.Enqueue(new Point(width - 1, y));
            }
            
            while (q.Count > 0) {
                Point p = q.Dequeue();
                int px = p.X;
                int py = p.Y;
                
                if (px < 0 || px >= width || py < 0 || py >= height) continue;
                if (visited[py * width + px]) continue;
                visited[py * width + px] = true;
                
                int pIndex = py * stride + px * bytesPerPixel;
                byte b = pixels[pIndex];
                byte g = pixels[pIndex + 1];
                byte r = pixels[pIndex + 2];
                byte a = bytesPerPixel == 4 ? pixels[pIndex + 3] : (byte)255;
                
                if (a == 0) continue; // already transparent
                
                // Is this a background pixel?
                // Checkerboard colors: (30,30,30) and (43,43,43) and maybe some anti-aliasing.
                // If it's mostly gray and dark, we consider it background.
                int maxDiff = Math.Max(Math.Abs(r - g), Math.Max(Math.Abs(g - b), Math.Abs(r - b)));
                
                if (r < 70 && g < 70 && b < 70 && maxDiff < 20) {
                    // Make transparent
                    if (bytesPerPixel == 4) pixels[pIndex + 3] = 0;
                    else {
                        pixels[pIndex] = 0;
                        pixels[pIndex + 1] = 0;
                        pixels[pIndex + 2] = 0;
                    }
                    
                    // Add neighbors
                    q.Enqueue(new Point(px - 1, py));
                    q.Enqueue(new Point(px + 1, py));
                    q.Enqueue(new Point(px, py - 1));
                    q.Enqueue(new Point(px, py + 1));
                }
            }
            
            Marshal.Copy(pixels, 0, ptrFirstPixel, pixels.Length);
            bmp.UnlockBits(bmpData);
            
            bmp.Save(outputPath, ImageFormat.Png);
        }
    }
}
'@

Add-Type -TypeDefinition $source -ReferencedAssemblies System.Drawing

[ImageFixer]::RemoveBackground("c:\Users\iamas\Desktop\Code-Base\Al-HissN\app\src\main\res\drawable\splash_illustration.png", "c:\Users\iamas\Desktop\Code-Base\Al-HissN\app\src\main\res\drawable\splash_illustration_transparent.png")
Write-Host "Done!"
