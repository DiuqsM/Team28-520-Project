from fastapi.testclient import TestClient
from main import app

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