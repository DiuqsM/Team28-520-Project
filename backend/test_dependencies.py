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
    headers = {"Authorization": "Bearer a.perfectly.valid.jwt.token"}

    with patch("dependencies.supabase.auth.get_user") as mock_get_user:

        mock_user = MagicMock()
        mock_user.user.id = "mocked-valid-user-id"

        mock_get_user.return_value = mock_user

        response = client.get("/dummy-secure-route", headers=headers)

        assert response.status_code == 200
        assert response.json()["user_id"] == "mocked-valid-user-id"
        assert response.json()["message"] == "You made it past security!"

def test_supabase_auth_crash():
    headers = {"Authorization": "Bearer some.token.here"}

    with patch("dependencies.supabase.auth.get_user") as mock_get_user:

        mock_get_user.side_effect = Exception("Supabase Auth API is down!")

        response = client.get("/dummy-secure-route", headers=headers)

        assert response.status_code in [401, 403, 500]

