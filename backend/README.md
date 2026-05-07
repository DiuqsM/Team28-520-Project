# Setup Instructions

## Prerequisites
Ensure you have Python 3 installed on your machine.

## Set up the Virtual Environment
To set up the virtual environment, open the terminal and run: 
```bash
# Navigate to the backend folder
cd backend

# Create the virtual environment
python3 -m venv venv

# Activate the virtual environment
# On macOS/Linux:
source venv/bin/activate
# On Windows:
venv\Scripts\activate
```
A new folder, venv/, should've been created in backend/. Do not commit this folder. To deactivate the virtual environment, run: 
```bash
deactivate 
```

## Install Dependencies 
All necessary dependencies are in requirements.txt. To install, open the terminal, ensure you are inside of the virtual environment (it should say "(venv)" at the start), and run: 
```bash
pip install -r requirements.txt
```

## Environmental Variables
Create a .env file in the root backend/ directory and add the Supabase credentials. Do not ever commit the .env. It should look like:
```python
SUPABASE_URL=your_supabase_project_url_here
SUPABASE_KEY=your_supabase_anon_key_here
```

## Run the Server
To start running the server, open the terminal and run:
```bash
fastapi dev 
```
Make sure you start the server before making any API requests. 
The API is hosted at http://127.0.0.1:8000.
The API docs can be found at http://127.0.0.1:8000/docs. 

## Running Tests 
We use pytest to ensure our endpoints and filters are working correctly. To run the pytests, open the terminal and run: 
```bash
pytest 
```