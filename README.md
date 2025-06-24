# ClockIn - ALP Final Project - Kelompok 2 (Backend)

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0+-green?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1?style=flat&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![XAMPP](https://img.shields.io/badge/XAMPP-recommended-FB7A24?style=flat&logo=xampp&logoColor=white)](https://www.apachefriends.org/download.html)

This repository contains the backend part of the Challenge Based Learning (CBL) project which aims to create an interactive platform to support users in solving time management challenges. The system is built with a full-stack development approach, using the latest technologies and implementing professional software development practices on the server side and its API.

> [!NOTE]
> This project is still under active development. Database settings and deployment strategies will likely be adjusted when it enters a production environment.

## 🔗 Visit Frontend Repository 
You can find the frontend repository for this project here: 
[**Frontend-ALP-Kelompok2**](https://github.com/1nnocentia/timeproductivityweb)

## 👥 Team Members
- **Innocentia Handani**
- **Arsya Aulia Amira**
- **Patrick Shiawase Aruji**
- **Rasya Febrian Dema**
- **Abel El Zachary**


> [!IMPORTANT]
> As part of an academic project, the development of approaches in this repository may vary as we learn and adapt. While we strive to implement industry-standard practices, we recognize that consistency will continue to evolve as the team gains insight and experience.

## 📌 Purpose

This repository serves as the backend for the ClockIn project — a time management support system developed as part of the Challenge Based Learning (CBL) academic initiative. It provides a structured API and data processing logic that powers features such as:

- 🧠 **Streak tracking** to motivate user consistency  
- 📅 **Personal scheduling** and task prioritization  
- 🚦 **Progress categorization** based on urgency and importance  
- 🔔 **Notifications and reminders** to reduce missed activities  
- 📊 **Leaderboard system** to encourage healthy competition  

The backend is designed with scalability, modularity, and maintainability in mind using Java Spring Boot, and connects to a MySQL database managed locally via XAMPP.

## 📦 Project Structure

The backend repository contains the following key components:

```
Backend Structure
├── src
│ ├── main
│ │ ├── java/com/clockin/clockin
│ │ │ ├── config # Security and application configuration classes
│ │ │ ├── controller # REST API controllers
│ │ │ ├── dto # Data Transfer Objects
│ │ │ ├── filter # Custom authentication filters
│ │ │ ├── model # Entity classes (mapped to database)
│ │ │ ├── repository # Spring Data JPA repositories
│ │ │ ├── service # Interfaces for service layer
│ │ │ └── service/impl # Service implementations (business logic)
│ │ └── resources
│ │ ├── application.properties # App config (DB, port, etc.)
├── pom.xml # Maven configuration file
```

This structure follows a layered architecture pattern, separating concerns between configuration, authentication, controller, service, and persistence layers.

> This backend works together with the ClockIn frontend to deliver a complete productivity web application.

### Setup
```bash
# Clone repository
git clone https://github.com/1nnocentia/timemanagement-clockin-backend.git

# Configure database
# Start XAMPP MySQL service
# Database 'clockin' will be auto-created

# Run application
mvn spring-boot:run
```

**API Base URL:** `http://localhost:8080/api`

## 🔧 Troubleshooting

> [!WARNING]
> Make sure to back up important data first before making changes to the database. Deleting a database will permanently delete all existing data.

### Common Issues

**XAMPP Not Running**
- **Problem:** Application fails to connect to database
- **Solution:** Ensure XAMPP is running and MySQL service is active
- **Check:** Verify MySQL is accessible at `localhost:3306`

**Java Version Mismatch**
- **Problem:** Build fails or application won't start
- **Solution:** Ensure your Java version matches the one specified in `pom.xml`
- **Check:** Verify you're using Java 17+ as specified in the project configuration

> [!TIP]
> If you experience recurring issues, try clearing the Maven cache by running `mvn clean install`, then restarting your IDE.
