# CS 520: Project Document Template Spring 2026
**Instructor:** Heather Conboy  
**University of Massachusetts Amherst**  

**Team Members:**  
- Ava Fu / pzyltn  
- Rihui Lu / DiuqsM  
- Srijan Varma Vegeshni / svegeshni  
- Samuel Woldegiorgies / Samuel-T-W  

**Team Name:** HappnIn  

**Google Drive Project Document Link:** [Click Here](https://docs.google.com/document/d/1p354fhSKHzoXj135QVr-_xmQSH65ur6HnvE-zd7q-0M/edit?tab=t.0)  
**GitHub Repo Link:** [Click Here](https://github.com/DiuqsM/Team28-520-Project)  

---

## 1. Requirements

### 1.1 Overview
Our app makes it easier for Five Colleges students to find and post events in the area open to the public and connect with others attending the events. Currently, each college has its own events pages, so unless students are regularly monitoring other colleges’ offerings, it can be easy to overlook exciting events. Our app will scrape public event information (such as Campus Pulse) and allow users to post events they’re hosting or that the scraper might have missed. Users will be able to create accounts, register for events, add each other as friends, view others attending events, and message each other about events. These features will make it easier for students to engage with other people and organizations within the Five Colleges community.

### 1.2 Features
- User authentication and role management  
- Messaging between app users  
- Event Feed and Search  
- Event Registration  
- Event Posting  

### 1.3 Functional Requirements (Use Cases)
**Create Account:** Guest user taps sign up and enters credentials. The system validates the credentials and creates a new user account. Failure case if email/number is already in use or the user provided a weak password.  

**Log In:** Existing users use the log in button and enter their credentials. The system authenticates the credentials and creates a user session. Failure case if credentials are wrong or do not exist.  

**Browsing Events:** User opens the Explore tab and the system loads the events within their radius and sorts them by event date and popularity. Failure case if there are no events or the system cannot load events.  

**Scrape Public Event Sources:** When a scheduled background process is triggered, the system automatically accesses publicly available college event pages, extracts relevant event information such as title, time, location, and description, validates the data, removes duplicates, and stores new entries in the database. If the source website structure changes and parsing fails, the system logs an error for administrative review; if a network issue occurs, the system retries after a delay; and if duplicate events are detected, they are skipped.  

**Register for an Event:** When a registered user clicks the “Register” button on an event page, the system verifies that the event is open and not at capacity, adds the user to the attendee list, and sends a confirmation notification; the user can then view their registration status in their account dashboard. If the event is full, the system displays an “Event Full” message; if the user is already registered, the system prevents duplicate registration; and if the event has been canceled, registration is disabled.  

**Add Friend:** When a registered user selects “Add Friend” on another user’s profile, the system sends a friend request notification to the recipient, and if the recipient accepts, both users are added to each other’s friend lists and can view each other’s public event participation. If the users are already friends, the system prevents duplicate requests; if the request is declined, the requester is notified; and if either user has blocked the other, the request cannot be sent.  

**Messaging:** After registering for an event, the user clicks on the “Chat” button on the event page, the system verifies that the event is open, and displays the chat. The user is able to communicate with others who are also attending the event. Failure case if the event is already done or the system is unable to retrieve messages.  

**Create Event:** User with admin permission can create an event by using the Create Event function. The host enters event details such as time, place, age limit, and price. The system validates and checks if the required fields are acceptable and adds it to the Explore page with its unique event ID. Failure case if missing fields or admin rejects the event with a valid reason.  

### 1.4 Non-Functional Requirements
- Availability over consistency for event query  
- Scale to support up to 100 concurrent requests on all user-facing APIs  
- Durability by adding an extra replica for our database  

### 1.5 Challenges & Risks
- If we are web scraping websites, future updates to the websites may require rewriting the scraping logic.  
- User-hosted events may not always be verified as student-safe.  
- Some sites prevent web scraping or do not provide a public API.  

---

## 2. Design

### 2.1 Architecture Diagram
Illustrate the high-level structure of the project using a diagram, for example:  
- A flowchart showing data flow between frontend, backend, and database components.  
- An MVC-based design showing component interactions.  

**Description of Major Classes and Responsibilities:** Explain how the system demonstrates object-oriented design (class structure, separation of concerns, encapsulation, modularity) and how OO principles are applied.  

### 2.2 UI Design
Provide screenshots, wireframes, or sketches of main pages, e.g.:  
- A user profile page displaying personal details and activity  
- A dashboard view showing key metrics  

### 2.3 Data Model
Illustrate how data is structured in the system, e.g., a class diagram or ER diagram showing relationships between core entities like Users, Events, Registrations, Messages.  

### 2.4 Tech Stack With Justification
- **Backend:** Express.js and Node.js – Provides scalable server-side logic and active community support. Can use Jest for testing.  
- **Database:** PostgreSQL – Efficient relational data access, supports large datasets, and has extensive documentation.  

### 2.5 Challenges & Risks
Refer to section 1.5.  

### 2.6 Work Plan
- Task distribution among team members, showing equitable contribution  
- Timeline for implementation  

---

## 3. Implementation

### 3.1 Project Organization
Provide screenshots of your repository structure.  

### 3.2 UI Screenshots
Provide screenshots of key pages.  

### 3.3 Data Model Screenshots
Provide class diagrams or ER diagrams.  

### 3.4 Challenges & Risks
Refer to section 1.5.  

### 3.5 Version Control & Team Contributions
- Branching strategy used  
- Pull request workflow  
- Code review process  
- Primary responsibilities of each team member  
- Screenshots or commit summaries demonstrating contributions  

---

## 4. Evaluation

### 4.1 Evaluation of Functional Requirements
Detail testing of each functional requirement, including unit, integration, and system tests. Example: Unit tests were written for each module using Jest, achieving 85% code coverage.  

### 4.2 Evaluation of Non-Functional Requirements
Describe evaluation of performance, usability, and scalability. Example: Load testing was conducted to simulate up to 1,000 concurrent users. A usability study with 10 participants provided feedback on navigation and clarity.  

### 4.3 Test Coverage Summary
Report overall test coverage percentage and the testing framework/tool used.  

---

## 5. Discussion (Lessons Learned, Limitations & Future Plans)

### 5.1 Lessons Learned
Discuss team organization, communication style, and new SE tools or techniques applied.  

### 5.2 Limitations
Critically assess shortcomings, e.g., unmet requirements, performance bottlenecks, or user experience issues. Example: “One limitation is the current response time under peak load, which we plan to address by optimizing database queries.”  

### 5.3 Future Development Plans
Outline potential enhancements, additional features, or integration with new technologies. Example: “Future enhancements include adding advanced analytics, improving mobile support, and integrating machine learning models.”  

### 5.4 Development Process Reflection
Explain sprint planning, changes between midpoint and final, and risk mitigation strategies.  
