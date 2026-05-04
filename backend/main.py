import os
from fastapi import FastAPI, HTTPException
from supabase import create_client, Client
from dotenv import load_dotenv

load_dotenv()

app = FastAPI()

url: str = os.environ.get("SUPABASE_URL")
key: str = os.environ.get("SUPABASE_KEY")

if not url or not key:
    raise ValueError("Supabase credentials not found. Check your .env file.")

supabase: Client = create_client(url, key)


@app.get("/")
def read_root():
    return {"message": "Welcome to the HappnIn API!"}

@app.get("/events")
def get_events():
    try:
        response = supabase.table('events').select("*").execute()

        return {"data": response.data}
    
    except Exception as e:
        # Good practice: catch errors so your server doesn't crash if the DB fails
        raise HTTPException(status_code=500, detail=str(e))