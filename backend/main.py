import os
from typing import Optional
from datetime import date, timedelta
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
def get_events(
    keyword: Optional[str] = None,
    location: Optional[str] = None,
    max_price: Optional[float] = None,
    min_age: Optional[int] = None,
    start_date: Optional[date] = None,
    end_date: Optional[date] = None
):
    try:
        query = supabase.table('events').select("*")

        if keyword:
            # .ilike() is case-insensitive. % signs are wildcards.
            # search both title and description for keyword.
            search_term = f"%{keyword}%"
            query = query.or_(f"title.ilike.{search_term},description.ilike.{search_term}")

        if location:
            query = query.eq("location", location)

        if max_price is not None:
            # .lte() = less than equal to
            query = query.lte("price", max_price)

        # if the age limit is LESS than an age
        # e.g. events for 21+ not shown if min_age set to 18
        if min_age is not None:
            query = query.lte("age_limit", min_age)

        # event happens on or after start date
        if start_date:
            query = query.gte("starts_at", str(start_date))

        # event happens on or before end date
        if end_date:
            # cutoff is midnight, so add a day to include ON
            next_day = end_date + timedelta(days=1)
            query = query.lt("starts_at", str(next_day))

        response = query.execute()

        return {"data": response.data}
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))