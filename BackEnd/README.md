# Online Event Management System

A full-stack Event Management and Ticket Reservation System built with Java Servlets, JDBC, and a modern Vanilla JS frontend.

## Prerequisites
- **JDK 8 or higher**
- **Apache Maven 3.6+**
- **MySQL Server 8.0+**
- **Apache Tomcat 9.0+**

## How to Run the Application

### 1. Database Setup
1.  Open your MySQL terminal or a GUI like MySQL Workbench.
2.  Execute the `database.sql` script located in the `BackEnd/` directory. This creates the `eventmanagement` database and required tables.
3.  **Note**: If your MySQL `root` user has a password, update the `PASSWORD` constant in `src/main/java/com/eventmgmt/utils/DBUtils.java`.

### 2. Build the Project
1.  Open your terminal in the `BackEnd` directory.
2.  Build and package the application using Maven:
    ```bash
    mvn clean package
    ```
3.  A file named `OnlineEventManagement.war` will be generated in the `target/` directory.

### 3. Deploy to Tomcat
1.  Copy the generated `OnlineEventManagement.war` from `target/`.
2.  Paste it into the `webapps` folder of your Tomcat installation.
3.  Start your Tomcat server.

### 4. Access the App
1.  Open your browser and navigate to:
    `http://localhost:9090/OnlineEventManagement/` 
    *(Replace 9090 with your specific Tomcat port if it differs.)*

---

## Key Features
- **Seamless Auth**: Automatic login after registration for a smooth user experience.
- **Dynamic Dashboard**: Real-time event listings and capacity tracking.
- **Admin Control**: Specialized panel for organizers to create events and track all bookings.
- **Secure Architecture**: Session-based authentication and SQL injection protection via PreparedStatements.

## API Endpoints (For Testing)

### Authentication
- `POST /api/auth/register` - { name, email, password, role }
- `POST /api/auth/login` - { email, password }
- `GET /api/auth/check` - Verifies current session
- `POST /api/auth/logout` - Terminates session

### Events
- `GET /api/events` - Fetch all events
- `POST /api/events` - Create event (Admin only)
- `PUT /api/events/{id}` - Update event (Admin only)
- `DELETE /api/events/{id}` - Delete event (Admin only)

### Bookings
- `GET /api/bookings` - View tickets (User sees personal, Admin sees all)
- `POST /api/bookings` - { eventId }
