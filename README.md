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
