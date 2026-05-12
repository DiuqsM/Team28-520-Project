import os
from typing import Optional
from datetime import date, datetime, timedelta
from fastapi import FastAPI, Depends, HTTPException
from supabase import create_client, Client
from dotenv import load_dotenv
from database import supabase
from dependencies import get_current_user
from pydantic import BaseModel

class EventCreate(BaseModel):
    title: str
    description: Optional[str] = None
    location: str
    price: Optional[float] = None
    age_limit: Optional[int] = None
    starts_at: datetime
    ends_at: Optional[datetime]

class EventUpdate(BaseModel):
    title: Optional[str] = None
    description: Optional[str] = None
    location: Optional[str] = None
    price: Optional[float] = None
    age_limit: Optional[int] = None
    starts_at: Optional[datetime] = None
    ends_at: Optional[datetime] = None

class RegistrationCreate(BaseModel):
    event_id: str

load_dotenv()

app = FastAPI()

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

@app.post("/events", status_code=201)
def create_event(
    event: EventCreate,
    user = Depends(get_current_user)
):
    try:
        new_event_data = event.model_dump(mode="json")

        new_event_data["created_by"] = user.id

        response = supabase.table("events").insert(new_event_data).execute()

        return {"message": "Event created successfully", "data": response.data[0]}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


#update event
@app.patch("/events/{event_id}")
def update_event(
    event_id: str,
    event: EventUpdate,
    user = Depends(get_current_user)
):
    try:
        update_data = event.model_dump(exclude_unset=True, mode="json") # only update fields user actually sent

        if not update_data:
            raise HTTPException(status_code=400, detail="No fields provided to update.")

        # can only change if ur the user who created it
        response = supabase.table("events").update(update_data)\
            .eq("id", event_id)\
            .eq("created_by", user.id)\
            .execute()

        if not response.data:
            raise HTTPException(status_code=403, detail="Not authorized to edit this event, or event not found.")

        return {"message": "Event updated successfully", "data": response.data[0]}

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

#delete event
@app.delete("/events/{event_id}", status_code=204)
def delete_event(
    event_id: str,
    user = Depends(get_current_user)
):
    try:
        # can only delete if ur the user who created it
        response = supabase.table("events").delete()\
            .eq("id", event_id)\
            .eq("created_by", user.id)\
            .execute()

        if not response.data:
            raise HTTPException(status_code=403, detail="Not authorized to delete this event, or event not found.")

        # 204 No Content is success, but no data to return
        return None

    except HTTPException:
        raise
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
            query = query.or_(f"username.ilike.{search_term},first_name.ilike.{search_term},last_name.ilike.{search_term}")

        response = query.execute()
        return {"data": response.data}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/registrations")
def get_registrations(
    id: Optional[str] = None,
    user_id: Optional[str] = None,
    event_id: Optional[str] = None,
    user = Depends(get_current_user)
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

@app.post("/registrations", status_code=201)
def create_registration(
    registration: RegistrationCreate,
    user = Depends(get_current_user)
):
    try:
        new_reg_data = {
            "user_id": user.id,
            "event_id": registration.event_id
        }

        response = supabase.table("registration").insert(new_reg_data).execute()

        return {"message": "Successfully registered for event", "data": response.data[0]}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.delete("/registrations/by-event/{event_id}", status_code=204)
def delete_registration_by_event(
    event_id: str,
    user = Depends(get_current_user)
):
    try:
        response = supabase.table("registration").delete()\
            .eq("event_id", event_id)\
            .eq("user_id", user.id)\
            .execute()

        if not response.data:
            raise HTTPException(status_code=403, detail="Not authorized to cancel this registration, or it doesn't exist.")

        return None

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.delete("/registrations/{registration_id}", status_code=204)
def delete_registration(
    registration_id: str,
    user = Depends(get_current_user)
):
    try:
        response = supabase.table("registration").delete()\
            .eq("id", registration_id)\
            .eq("user_id", user.id)\
            .execute()

        if not response.data:
            raise HTTPException(status_code=403, detail="Not authorized to cancel this registration, or it doesn't exist.")

        return None

    except HTTPException:
        raise
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
