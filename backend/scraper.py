import os
import re
import time
from urllib.parse import urljoin

import requests
from bs4 import BeautifulSoup
from dateutil import parser
from dotenv import load_dotenv
from supabase import create_client


BASE_URL = "https://events.umass.edu/"
TIMEZONE_INFO = {
    "EDT": -4 * 3600,
    "EST": -5 * 3600,
}

load_dotenv()

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_KEY = os.getenv("SUPABASE_KEY")

if not SUPABASE_URL or not SUPABASE_KEY:
    raise ValueError("Missing SUPABASE_URL or SUPABASE_KEY in .env")

supabase = create_client(SUPABASE_URL, SUPABASE_KEY)


def get_soup(url):
    headers = {
        "User-Agent": "Mozilla/5.0 (compatible; student-project-event-scraper/1.0)"
    }

    response = requests.get(url, headers=headers, timeout=15)
    response.raise_for_status()

    return BeautifulSoup(response.text, "html.parser")


def clean_text(text):
    if not text:
        return None

    return re.sub(r"\s+", " ", text).strip()


def get_event_links():
    soup = get_soup(BASE_URL)

    links = []

    for a in soup.find_all("a", href=True):
        href = a["href"]

        if "/event/" in href:
            full_url = urljoin(BASE_URL, href)

            if full_url not in links:
                links.append(full_url)

    return links


def parse_event_datetime(date_text):
    date_text = clean_text(date_text)

    if not date_text:
        return None, None

    match = re.match(
        r"^([A-Za-z]+,\s+[A-Za-z]+\s+\d{1,2},\s+\d{4})(?:\s+(.+))?$",
        date_text,
    )

    if not match:
        return None, None

    date_part = match.group(1)
    time_part = match.group(2)

    if not time_part:
        start_dt = parser.parse(date_part)
        return start_dt.isoformat(), None
    time_part = time_part.replace("–", " to ").replace("-", " to ")

    if " to " in time_part:
        start_time, end_time = time_part.split(" to ", 1)
    else:
        start_time = time_part
        end_time = None
    tz_match = re.search(r"\b(EDT|EST)\b", time_part)
    timezone = tz_match.group(1) if tz_match else "EDT"

    start_string = f"{date_part} {start_time} {timezone}"
    start_dt = parser.parse(start_string, tzinfos=TIMEZONE_INFO)

    end_dt = None
    if end_time:
        end_time_clean = re.sub(r"\b(EDT|EST)\b", "", end_time).strip()
        end_string = f"{date_part} {end_time_clean} {timezone}"
        end_dt = parser.parse(end_string, tzinfos=TIMEZONE_INFO)

    return start_dt.isoformat(), end_dt.isoformat() if end_dt else None


def extract_description(soup):

    text = soup.get_text("\n", strip=True)
    lines = [clean_text(line) for line in text.split("\n") if clean_text(line)]

    if "About this Event" not in lines:
        return None

    start_index = lines.index("About this Event") + 1

    stop_words = {
        "Add to calendar",
        "Event Details",
        "Hide map Show map",
    }

    desc_lines = []

    for line in lines[start_index:]:
        if line in stop_words:
            break

        if line.startswith("Save to"):
            continue
        if line == "View map":
            continue
        if "google.com" in line:
            continue

        desc_lines.append(line)

    filtered = []
    for line in desc_lines:
        if re.search(r"\d+\s+.+,\s+Amherst,\s+Massachusetts", line):
            continue
        if line.startswith("http"):
            continue
        filtered.append(line)

    description = " ".join(filtered)

    return clean_text(description)


def extract_location(soup):
    text = soup.get_text("\n", strip=True)
    lines = [clean_text(line) for line in text.split("\n") if clean_text(line)]

    if "About this Event" in lines:
        idx = lines.index("About this Event")

        for line in lines[idx + 1: idx + 6]:
            if line not in {"View map", "Add to calendar"} and not line.startswith("Save to"):
                return line

    return "UMass Amherst"


def extract_date_line(soup):
    text = soup.get_text("\n", strip=True)
    lines = [clean_text(line) for line in text.split("\n") if clean_text(line)]

    date_pattern = re.compile(
        r"^[A-Za-z]+,\s+[A-Za-z]+\s+\d{1,2},\s+\d{4}"
    )

    for line in lines:
        if date_pattern.match(line):
            return line

    return None


def scrape_event(event_url):
    soup = get_soup(event_url)

    title_tag = soup.find("h1")
    title = clean_text(title_tag.get_text()) if title_tag else None

    date_line = extract_date_line(soup)
    starts_at, ends_at = parse_event_datetime(date_line)

    location = extract_location(soup)
    description = extract_description(soup)

    if not title or not starts_at or not location:
        print(f"Skipping incomplete event: {event_url}")
        return None

    event = {
        "created_by": None,
        "title": title,
        "description": description,
        "location": location,
        "price": None,
        "starts_at": starts_at,
        "ends_at": ends_at,
        "age_limit": None,
    }

    return event


def insert_event(event):
    result = supabase.table("events").insert(event).execute()
    return result


def main():
    event_links = get_event_links()
    print(f"Found {len(event_links)} event links")

    inserted = 0

    for url in event_links:
        try:
            event = scrape_event(url)

            if event is None:
                continue

            print(f"Inserting: {event['title']}")
            insert_event(event)
            inserted += 1

            time.sleep(1)

        except Exception as e:
            print(f"Error scraping {url}: {e}")

    print(f"Done. Inserted {inserted} events.")


if __name__ == "__main__":
    main()