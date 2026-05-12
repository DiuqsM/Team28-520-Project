# Build Guide

## Prerequisites

- **Android Studio** Meerkat (2024.3) or newer
- **JDK 21** (Gradle toolchain downloads it automatically via Foojay if missing)
- **Python 3.10+** (for the backend)
- A **Supabase** project with the app's schema applied

---

## Frontend (Android)

### 1. Clone and open the project

Open Android Studio and choose **File → Open**, then select the `frontend/` directory (not the repo root).

### 2. Set up local.properties

The build reads two secrets from `frontend/local.properties`. This file is git-ignored, so create it manually:

```
SUPABASE_URL=https://<your-project-ref>.supabase.co
SUPABASE_KEY=<your-anon-public-key>
```

Both values are found in your Supabase project under **Settings → API**.

### 3. Sync Gradle

Android Studio will prompt to sync after opening. Click **Sync Now**. If it doesn't, go to **File → Sync Project with Gradle Files**.

### 4. Run the app

Connect a physical device or start an emulator (API 24+), then click the green **Run** button or press `Shift+F10`.

**From the terminal:**
```bash
cd frontend
./gradlew installDebug   # build and install on connected device/emulator
```

### Build variants

| Task | Command |
|------|---------|
| Debug APK | `./gradlew assembleDebug` |
| Release APK | `./gradlew assembleRelease` |
| Unit tests | `./gradlew test` |
| Instrumented tests | `./gradlew connectedAndroidTest` |
| Lint | `./gradlew lint` |
| Single test class | `./gradlew test --tests "com.example.happnin.ExampleUnitTest"` |

---

## Backend (FastAPI + Supabase)

### 1. Set up a virtual environment

```bash
cd backend
python -m venv .venv
source .venv/bin/activate   # Windows: .venv\Scripts\activate
pip install -r requirements.txt
```

### 2. Configure environment variables

Copy the example file and fill in your Supabase credentials:

```bash
cp .env.example .env
```

Edit `.env`:

```
SUPABASE_URL=https://<your-project-ref>.supabase.co
SUPABASE_KEY=<your-service-role-or-anon-key>
```

### 3. Run the dev server

```bash
uvicorn main:app --reload
```

The API will be available at `http://127.0.0.1:8000`. Interactive docs at `http://127.0.0.1:8000/docs`.

### 4. Run tests

```bash
pytest
pytest --cov=.          # with coverage report
```

---

## Supabase setup

If you're starting from scratch with a new Supabase project:

1. Create a new project at [supabase.com](https://supabase.com).
2. Apply the schema from `backend/database/schema_design/mermaid_design.txt` using the Supabase SQL editor.
3. Enable **Google OAuth** under **Authentication → Providers** if you want sign-in to work.
4. Copy the **Project URL** and **anon public key** into both `frontend/local.properties` and `backend/.env`.

---

## Connecting the Android app to a local backend

By default the app points at the deployed Supabase project. To proxy through a local FastAPI instance, update the base URL in the Retrofit client to `http://10.0.2.2:8000` (the Android emulator's alias for `localhost`) before building.
