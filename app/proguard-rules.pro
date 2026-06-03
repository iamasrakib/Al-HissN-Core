# Keep TensorFlow Lite rules
-keep class org.tensorflow.lite.** { *; }
-keep class org.tensorflow.lite.support.** { *; }
-keep class com.alhissn.ml.TFLiteClassifier { *; }
-keep class com.alhissn.ml.ClassificationResult { *; }

# Keep Room database models and DAOs
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>(...);
}
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Dao class * { *; }
-keep class * implements androidx.room.RoomDatabase { *; }
-keep class com.alhissn.data.ThreatDao { *; }
-keep class com.alhissn.data.ThreatEntity { *; }

# Keep system services (VpnService, AccessibilityService, IME)
-keep class * extends android.net.VpnService { *; }
-keep class * extends android.accessibilityservice.AccessibilityService { *; }
-keep class * extends android.inputmethodservice.InputMethodService { *; }
-keep class com.alhissn.network.AlHissnVpnService { *; }
-keep class com.alhissn.keyboard.AlHissnKeyboardService { *; }
-keep class com.alhissn.accessibility.AlHissnScreenScanner { *; }

# Keep Biometric classes
-keep class androidx.biometric.** { *; }

# Keep Jetpack Compose runtime
-keep class androidx.compose.runtime.** { *; }
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep DataStore & Coroutines
-keep class androidx.datastore.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Keep OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Strip Logs
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Optimization
-optimizationpasses 5
-allowaccessmodification

# Keep serializable custom classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}