import requests
import json

url = "https://cvmxpzpspojhqnmyxhkv.supabase.co/rest/v1/one_time_codes"
headers = {
    "apikey": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN2bXhwenBzcG9qaHFubXl4aGt2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk5ODg5MjQsImV4cCI6MjA5NTU2NDkyNH0.WRy1L5rRoUvC_5AKcXcmqM8KuKNAn5F9Q_-e4CghbtE",
    "Authorization": "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImN2bXhwenBzcG9qaHFubXl4aGt2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk5ODg5MjQsImV4cCI6MjA5NTU2NDkyNH0.WRy1L5rRoUvC_5AKcXcmqM8KuKNAn5F9Q_-e4CghbtE",
    "Content-Type": "application/json",
    "Prefer": "return=representation"
}
body = {
    "code": "TEST12",
    "is_used": False
}

try:
    response = requests.post(url, headers=headers, json=body)
    print("Status Code:", response.status_code)
    print("Response text:", response.text)
except Exception as e:
    print("Exception occurred:", str(e))
