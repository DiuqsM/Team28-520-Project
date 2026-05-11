import pytest
from fastapi import FastAPI, Depends, HTTPException
from fastapi.testclient import TestClient
from dependencies import get_current_user
from unittest.mock import patch, MagicMock

app = FastAPI()

@app.get("/dummy-secure-route")
def dummy_route(user = Depends(get_current_user)):
    return {"message": "You made it past security!", "user_id": user.id}

client = TestClient(app)

def test_missing_authorization_header():
    response = client.get("/dummy-secure-route")

    assert response.status_code == 401
    assert response.json()["detail"] == "Not authenticated"

def test_malformed_authorization_header():
    headers = {"Authorization": "BearerThisIsMissingASpace"}
    response = client.get("/dummy-secure-route", headers=headers)

    assert response.status_code in [401, 403]

def test_invalid_or_expired_token():
    headers = {"Authorization": "Bearer completely.fake.jwt.token"}
    response = client.get("/dummy-secure-route", headers=headers)

    assert response.status_code in [401, 403]

def test_valid_token_success():
    """Test that a perfectly valid token successfully returns the user object."""
    headers = {"Authorization": "Bearer a.perfectly.valid.jwt.token"}

    # We patch the exact function inside dependencies.py that talks to Supabase
    with patch("dependencies.supabase.auth.get_user") as mock_get_user:

        # 1. Create a fake "User" object that looks exactly like what Supabase returns
        mock_user = MagicMock()
        mock_user.user.id = "mocked-valid-user-id"

        # 2. Tell our interceptor to hand back this fake user
        mock_get_user.return_value = mock_user

        # 3. Hit the dummy route
        response = client.get("/dummy-secure-route", headers=headers)

        # 4. Prove that we bypassed the 401s and successfully hit the route!
        assert response.status_code == 200
        assert response.json()["user_id"] == "mocked-valid-user-id"
        assert response.json()["message"] == "You made it past security!"

def test_supabase_auth_crash():
    """Test the fallback error handler when Supabase Auth completely crashes."""
    headers = {"Authorization": "Bearer some.token.here"}

    with patch("dependencies.supabase.auth.get_user") as mock_get_user:

        # Force the Supabase client to violently crash
        mock_get_user.side_effect = Exception("Supabase Auth API is down!")

        response = client.get("/dummy-secure-route", headers=headers)

        # Depending on how your except block is written, it might return 401, 403, or 500.
        # But it definitely shouldn't be 200!
        assert response.status_code in [401, 403, 500]

