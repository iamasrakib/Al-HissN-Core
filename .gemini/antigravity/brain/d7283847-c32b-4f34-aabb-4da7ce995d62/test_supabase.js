const url = "https://cvmxpzpspojhqnmyxhkv.supabase.co/rest/v1/one_time_codes";
const headers = {
    "apikey": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN2bXhwenBzcG9qaHFubXl4aGt2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk5ODg5MjQsImV4cCI6MjA5NTU2NDkyNH0.WRy1L5rRoUvC_5AKcXcmqM8KuKNAn5F9Q_-e4CghbtE",
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN2bXhwenBzcG9qaHFubXl4aGt2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk5ODg5MjQsImV4cCI6MjA5NTU2NDkyNH0.WRy1L5rRoUvC_5AKcXcmqM8KuKNAn5F9Q_-e4CghbtE",
    "Content-Type": "application/json",
    "Prefer": "return=representation"
};
const body = JSON.stringify({
    code: "TEST12",
    is_used: false
});

fetch(url, {
    method: "POST",
    headers: headers,
    body: body
})
.then(async res => {
    console.log("Status Code:", res.status);
    console.log("Response text:", await res.text());
})
.catch(err => {
    console.error("Fetch Error:", err);
});
