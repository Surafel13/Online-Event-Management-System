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
