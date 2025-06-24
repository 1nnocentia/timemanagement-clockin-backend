# ALP Final Project - Kelompok 2 (Backend)

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
