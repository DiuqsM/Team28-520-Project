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
    user_age: Optional[int] = None,
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
            # NULL is considered free
            query = query.or_(f"price.lte.{max_price},price.is.null")

        # if the age limit is LESS than or equal to the user age
        # e.g. events for 21+ not shown if user_age set to 18
        if user_age is not None:
            # NULL is considered no age restriction / 0+
            query = query.or_(f"age_limit.lte.{user_age},age_limit.is.null")

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

@app.get("/users")
def get_users(
    id: Optional[str] = None,
    name = None
):
    try:
        query = supabase.table('users').select("*")

        if id:
            query = query.eq("id", id)

        # search username, first name, and last name for keyword
        if name:
            search_term = f"%{name}%"
            query = query.or_(f"username.ilike.{search_term},first_namee.ilike.{search_term},last_name.ilike.{search_term}")

        response = query.execute()
        return {"data": response.data}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/registrations")
def get_registrations(
    id: Optional[str] = None,
    user_id: Optional[str] = None,
    event_id: Optional[str] = None
):
    try:
        query = supabase.table('registration').select("*")

        if id:
            query = query.eq("id", id)

        if user_id:
            query = query.eq("user_id", user_id)

        if event_id:
            query = query.eq("event_id", event_id)

        response = query.execute()
        return {"data": response.data}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# for private user info, like phone #
@app.get("/users/me")
def get_my_profile(user = Depends(get_current_user)):
    # get uuid from auth state
    user_id = user.id

    response = supabase.table('users').select("*").eq("id", user_id).execute()

    if not response.data:
        raise HTTPException(status_code=404, detail="User profile not found.")

    return {
        "message": "Welcome to your private data!",
        "user_id": user_id,
        "profile": response.data[0] # return the actual dictionary, not a list
    }