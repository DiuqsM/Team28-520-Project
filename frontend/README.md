# Setup Instructions

## Opening the Project Correctly
**CRITICAL:** Do *not* open the root `HappnIn/` folder in Android Studio. Doing so will break the Gradle sync and cause the IDE to fail to recognize the Android framework.

1. Open Android Studio.
2. Click **Open**.
3. Navigate into the `HappnIn` repository and select **only the `frontend/` folder**.
4. Allow Gradle to finish its initial sync (wait for the Elephant icon to appear in the top right).

## Running the App
Once synced, select an Emulator (or a physical device) from the top device dropdown and click the green **Run (Play)** button. New devices may be added by selecting the **Device Manager** on the right sidebar. 

*Note: The app expects the FastAPI backend to be running locally. By default, the Android emulator routes `http://10.0.2.2:8000` to your computer's `localhost:8000`.*

## Architecture Overview

Our networking layer is built using **Retrofit**. If you need to add new API calls or database tables, you will interact with two main files:

### `Models.kt`
This file defines the shape of the data coming from our FastAPI server.
* We use `@SerializedName` to map Python's `snake_case` database columns (e.g., `first_name`) into Kotlin's standard `camelCase` variables (e.g., `firstName`).
* Nullable fields are demarcated with a ? at the end of the type. 
* Some fields have changed from the previous database schema (such as password_hash in the users table) or the project documentation (such as created_at). Please refer to `Models.kt` for the expected response shape and change it accordingly if needed. 

### `ApiClient.kt`
This file contains the Retrofit interface (`ApiService`) that builds our HTTP requests.
* Endpoints like `@GET("events")` handle our database queries.
* We use `@Query` parameters with default `= null` values to match FastAPI's optional parameters, allowing for requests directly from the Jetpack Compose UI.