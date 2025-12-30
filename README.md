# Online Event Management and Ticket Generation System

A backend-only Java Servlet and JDBC based system for managing events and tickets.

## Pratt-requisites
- Apache Tomcat (v9+)
- MySQL Server (v8+)
- MySQL Connector J (jar should be placed in `src/main/webapp/WEB-INF/lib`)
- Servlet API (usually provided by Tomcat)

## Setup Instructions

1.  **Database Setup**:
    - Execute the contents of `database.sql` in your MySQL instance.
    - If your MySQL `root` password is not empty, update it in `src/main/java/com/eventmgmt/utils/DBUtils.java`.

2.  **Dependencies**:
    - Download `mysql-connector-java-*.jar` and place it in `src/main/webapp/WEB-INF/lib/`.

3.  **Deployment**:
    - Compile the Java files into classes.
    - Package the project as a `.war` file or deploy the `webapp` folder to Tomcat.
      ## API Documentation (Test using Postman)

**Note**: All POST/PUT APIs now support both `x-www-form-urlencoded` and `application/json` Content-Types.

### 1. Authentication
- **Register**: `POST /api/auth/register`
  - Body: `name`, `email`, `password`, `role` (ADMIN or USER)
- **Login**: `POST /api/auth/login`
  - Body: `email`, `password`
- **Logout**: `POST /api/auth/logout`
### 2. Event Management
- **List Events**: `GET /api/events`
- **Create Event (Admin)**: `POST /api/events`
  - Body: `title`, `date` (YYYY-MM-DD), `time` (HH:mm), `location`, `capacity`
  - *Requires ADMIN session cookie.*
- **Update Event (Admin)**: `PUT /api/events/{id}`
- **Delete Event (Admin)**: `DELETE /api/events/{id}`
### 3. Booking & Users
- **View Bookings**: `GET /api/bookings` (User see theirs, Admin see all)
- **Book Ticket**: `POST /api/bookings`
  - Body: `eventId`
- **View Users (Admin)**: `GET /api/admin/users`
  ### Testing with Postman
1.  **Login first** to get the `JSESSIONID` cookie.
2.  Postman automatically manages cookies if you use the desktop version.
3.  For Admin APIs, ensure your user role is `ADMIN` in the database.
## Key Features
- **Session-based Auth**: Uses `HttpSession` to track logged-in users.
- **Role-based Access**: Restricts Admin APIs to users with `ADMIN` role.
- **Capacity Management**: Automatically reduces event capacity upon successful booking.
- **Unique Ticket ID**: Generates a random unique ID for every ticket.
- **SQL Security**: Uses `PreparedStatement` to prevent SQL injection.
