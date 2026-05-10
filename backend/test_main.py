from fastapi.testclient import TestClient
from main import app
from dependencies import get_current_user
import uuid
from database import supabase

# create a fake user object that looks like what Supabase returns
class MockUser:
    def __init__(self):
        # Use a fake UUID for testing
        self.id = "3ff1edff-469c-4866-a53f-4020386bc3d8"

def override_get_current_user():
    return MockUser()

mock_test_user = MockUser()

client = TestClient(app)

# test API is awake
def test_read_root():
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"message": "Welcome to the HappnIn API!"}

# test fetch all events
def test_get_all_events():
    response = client.get("/events")

    assert response.status_code == 200

    json_data = response.json()
    assert "data" in json_data

    assert isinstance(json_data["data"], list)

# test keyword filter
def test_filter_events_by_keyword():
    test_keyword = "shop"

    response = client.get(f"/events?keyword={test_keyword}")

    assert response.status_code == 200
    events = response.json()["data"]

    for event in events:
        title = event.get("title")
        description = event.get("description")

        safe_title = title.lower()
        # convert None to empty string if present
        safe_description = description.lower() if description else ""

        # convert keyword into lower case just in case
        lower_keyword = test_keyword.lower()

        # only return events that contain the keyword in the title or description
        assert lower_keyword in safe_title or lower_keyword in safe_description

# test location equality filter
def test_filter_events_by_location():
    test_location = "Northampton, MA"
    response = client.get(f"/events?location={test_location}")

    assert response.status_code == 200
    events = response.json()["data"]

    # only return events that exactly match the given location
    for event in events:
        assert event["location"] == test_location

# test max price filter
def test_filter_events_by_max_price():
    test_price = 20.0
    response = client.get(f"/events?max_price={test_price}")

    assert response.status_code == 200
    events = response.json()["data"]

    # only return events that are None (free) or <= given amount
    for event in events:
        price = event["price"]
        assert price is None or price <= test_price

# test date filter
def test_filter_events_by_date_range():
    test_start = "2026-08-24"
    test_end = "2026-08-25"

    response = client.get(f"/events?start_date={test_start}&end_date={test_end}")

    assert response.status_code == 200
    events = response.json()["data"]

    for event in events:
        starts_at = event["starts_at"]

        # return only events that fall within the date range
        # +1 day for the ending cutoff bc cutoff for a given day is at midnight, i.e. cutting off at 25 would exclude events happening the day of the 25th
        assert starts_at >= test_start
        assert starts_at < "2026-08-26"

def test_filter_events_by_user_age():
    test_age = 18
    response = client.get(f"/events?user_age={test_age}")

    assert response.status_code == 200
    events = response.json()["data"]

    for event in events:
        age_limit = event["age_limit"]
        assert age_limit is None or age_limit <= test_age

def test_event_crud_lifecycle():
    app.dependency_overrides[get_current_user] = override_get_current_user

    try:
        new_event_payload = {
            "title": "Pytest Test Festival",
            "description": "An automated fake event.",
            "location": "Test City",
            "price": 25.0,
            "age_limit": 18,
            "starts_at": "2026-12-31T20:00:00",
            "ends_at": "2027-01-01T02:00:00"
        }

        create_response = client.post("/events", json=new_event_payload)
        assert create_response.status_code == 201

        created_event = create_response.json()["data"]
        event_id = created_event["id"]

        assert created_event["title"] == "Pytest Test Festival"
        assert created_event["created_by"] == mock_test_user.id

        update_payload = {
            "price": 15.0
        }

        update_response = client.patch(f"/events/{event_id}", json=update_payload)
        assert update_response.status_code == 200

        updated_event = update_response.json()["data"]

        assert updated_event["price"] == 15.0
        assert updated_event["title"] == "Pytest Test Festival"

        delete_response = client.delete(f"/events/{event_id}")
        assert delete_response.status_code == 204

        fetch_response = client.patch(f"/events/{event_id}", json={"title": "Ghost Event"})
        assert fetch_response.status_code in [403, 404, 400]

    finally:
        app.dependency_overrides.clear()

def test_update_event_unauthorized_or_not_found():
    app.dependency_overrides[get_current_user] = override_get_current_user

    try:
        random_event_id = str(uuid.uuid4())

        response = client.patch(f"/events/{random_event_id}", json={"title": "Hacked Event!"})

        assert response.status_code == 403
        assert "Not authorized" in response.json()["detail"]

    finally:
        app.dependency_overrides.clear()

def test_update_event_no_data():
    app.dependency_overrides[get_current_user] = override_get_current_user

    try:
        random_event_id = str(uuid.uuid4())

        response = client.patch(f"/events/{random_event_id}", json={})

        assert response.status_code == 400
        assert "No fields provided" in response.json()["detail"]

    finally:
        app.dependency_overrides.clear()

def test_delete_event_unauthorized_or_not_found():
    app.dependency_overrides[get_current_user] = override_get_current_user

    try:
        random_event_id = str(uuid.uuid4())

        response = client.delete(f"/events/{random_event_id}")

        assert response.status_code == 403
        assert "Not authorized" in response.json()["detail"]

    finally:
        app.dependency_overrides.clear()

def test_get_users_by_name():
    unique_name = f"TestBot_{mock_test_user.id[:5]}"

    try:

        response = client.get("/users?name=Ava")

        assert response.status_code == 200
        data = response.json()["data"]

        # assert db found at least 1 person matching our search, I should be in the table
        assert len(data) >= 1

    finally:
        pass

def test_get_users_by_id():
    response = client.get(f"/users?id={mock_test_user.id}")

    assert response.status_code == 200
    data = response.json()["data"]

    assert len(data) == 1
    assert data[0]["id"] == mock_test_user.id

def test_get_registrations_by_event_id():
    app.dependency_overrides[get_current_user] = override_get_current_user

    fake_event_id = str(uuid.uuid4())
    fake_registration_id = str(uuid.uuid4())

    try:
        supabase.table("events").insert({
            "id": fake_event_id,
            "title": "Reg Test Event",
            "location": "Test City",
            "starts_at": "2026-12-31T20:00:00",
            "created_by": mock_test_user.id
        }).execute()

        supabase.table("registration").insert({
            "id": fake_registration_id,
            "user_id": mock_test_user.id,
            "event_id": fake_event_id
        }).execute()

        response = client.get(f"/registrations?event_id={fake_event_id}")

        assert response.status_code == 200
        data = response.json()["data"]

        assert len(data) == 1
        assert data[0]["user_id"] == mock_test_user.id
        assert data[0]["event_id"] == fake_event_id

    finally:
        supabase.table("registration").delete().eq("id", fake_registration_id).execute()
        supabase.table("events").delete().eq("id", fake_event_id).execute()

        app.dependency_overrides.clear()

def test_get_registrations_by_user_id():
    app.dependency_overrides[get_current_user] = override_get_current_user

    try:
        response = client.get(f"/registrations?user_id={mock_test_user.id}")

        assert response.status_code == 200
        data = response.json()["data"]

        assert isinstance(data, list)
    finally:
        app.dependency_overrides.clear()

def test_get_my_profile():
    app.dependency_overrides[get_current_user] = override_get_current_user

    try:
        response = client.get("/users/me")

        assert response.status_code == 200
        json_data = response.json()

        assert "Welcome to your private data!" in json_data["message"]
        assert json_data["user_id"] == mock_test_user.id
        assert "profile" in json_data
    finally:
        app.dependency_overrides.clear()

def test_create_registration():
    app.dependency_overrides[get_current_user] = override_get_current_user
    fake_event_id = str(uuid.uuid4())

    try:
        supabase.table("events").insert({
            "id": fake_event_id,
            "title": "Reg Create Test Event",
            "location": "Test City",
            "starts_at": "2026-12-31T20:00:00",
            "created_by": mock_test_user.id
        }).execute()

        response = client.post("/registrations", json={"event_id": fake_event_id})

        assert response.status_code == 201
        data = response.json()["data"]

        assert data["event_id"] == fake_event_id
        assert data["user_id"] == mock_test_user.id

        new_reg_id = data["id"]

    finally:
        if 'new_reg_id' in locals():
            supabase.table("registration").delete().eq("id", new_reg_id).execute()

        supabase.table("events").delete().eq("id", fake_event_id).execute()
        app.dependency_overrides.clear()


def test_delete_registration():
    app.dependency_overrides[get_current_user] = override_get_current_user
    fake_event_id = str(uuid.uuid4())
    fake_reg_id = str(uuid.uuid4())

    try:
        supabase.table("events").insert({
            "id": fake_event_id,
            "title": "Reg Delete Test Event",
            "location": "Test City",
            "starts_at": "2026-12-31T20:00:00",
            "created_by": mock_test_user.id
        }).execute()

        supabase.table("registration").insert({
            "id": fake_reg_id,
            "user_id": mock_test_user.id,
            "event_id": fake_event_id
        }).execute()

        response = client.delete(f"/registrations/{fake_reg_id}")

        assert response.status_code == 204

    finally:
        supabase.table("registration").delete().eq("id", fake_reg_id).execute()
        supabase.table("events").delete().eq("id", fake_event_id).execute()
        app.dependency_overrides.clear()


def test_delete_registration_unauthorized():
    app.dependency_overrides[get_current_user] = override_get_current_user

    try:
        fake_reg_id = str(uuid.uuid4())
        response = client.delete(f"/registrations/{fake_reg_id}")

        assert response.status_code == 403
        assert "Not authorized" in response.json()["detail"]

    finally:
        app.dependency_overrides.clear()

def test_unauthenticated_access_blocked():
    app.dependency_overrides.clear()

    response = client.get("/users/me") # request needs user to be signed in

    assert response.status_code in [401, 403]