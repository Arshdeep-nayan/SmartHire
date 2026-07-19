# 🚀 SmartHire




> [!IMPORTANT]
> ### Testing Protected APIs
>
> Most APIs in SmartHire are secured using JWT authentication.
>
> Before testing protected endpoints:
>
> 1. Open the **Swagger UI**.
> 2. Select **Auth Service** from the service dropdown.
> 3. Call **POST `/api/login`** to generate a JWT.
> 4. Copy the JWT from the response.
> 5. Click the **Authorize (🔒)** button.
> 6. Paste the JWT into the authorization field and click **Authorize**.
> 7. Switch to any service and test the protected endpoints.







> **An AI-powered Job Portal Backend built using Java, Spring Boot Microservices, Spring Cloud, Apache Kafka, Google Gemini, Docker, MySQL, and MongoDB.**

<p align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge\&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.x-6DB33F?style=for-the-badge\&logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-Microservices-success?style=for-the-badge)
![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-success?style=for-the-badge)
![Kafka](https://img.shields.io/badge/Apache-Kafka-black?style=for-the-badge\&logo=apachekafka)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge\&logo=docker)
![MySQL](https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge\&logo=mysql)
![MongoDB](https://img.shields.io/badge/MongoDB-NoSQL-47A248?style=for-the-badge\&logo=mongodb)
![Log4j2](https://img.shields.io/badge/Log4j2-Logging-blue?style=for-the-badge)

</p>

---

# 📖 Overview

SmartHire is a backend-only job portal built using a **microservices architecture** with Spring Boot and Spring Cloud. The project demonstrates secure authentication, distributed system design, AI-powered resume screening, asynchronous event-driven communication, and containerized deployment using modern Java backend technologies.

---

# ✨ Features

* 🔐 JWT Authentication & Authorization
* 👥 Role-Based Access Control (ADMIN, RECRUITER, CANDIDATE)
* 🏗️ Spring Boot Microservices Architecture
* 🌐 Spring Cloud API Gateway
* 🔍 Eureka Service Discovery
* 🔄 OpenFeign Inter-Service Communication
* ⚡ Apache Kafka Event Streaming
* 🤖 AI Resume Screening using Google Gemini
* 📄 Resume Management
* 🔔 Notification Service
* 🗄️ MySQL & MongoDB Integration
* 📊 Search, Pagination & Sorting APIs
* 📑 Swagger / OpenAPI Documentation
* 📝 Log4j2 Logging
* 🐳 Docker & Docker Compose Support

---

# 🏛️ Architecture

```text
                                 Client
                                    │
                                    ▼
                            API Gateway (8085)
                           JWT Validation & Routing
                                    │
        ┌──────────────┬────────────┼──────────────┬──────────────┐
        ▼              ▼            ▼              ▼              ▼
 Auth Service     Job Service   Candidate     AI Screening   Notification
    (8081)          (8082)      Service          (8084)        Service
                                  (8083)                          (8086)
                                       │
                                       ▼
                                Apache Kafka
                                       │
                                       ▼
                          Eureka Discovery Server
                                       │
                      MySQL                      MongoDB
```

---

# 🧩 Microservices

| Service                  | Port | Database        | Responsibility                                |
| ------------------------ | ---- | --------------- | --------------------------------------------- |
| **Auth Service**         | 8081 | MySQL           | User Registration, Login & JWT Authentication |
| **Job Service**          | 8082 | MySQL           | Job Management, Search, Pagination & Sorting  |
| **Candidate Service**    | 8083 | MySQL & MongoDB | Candidate & Resume Management                 |
| **AI Screening Service** | 8084 | MongoDB         | AI Resume Screening using Google Gemini       |
| **Notification Service** | 8086 | MongoDB         | Kafka-based Notification Management           |
| **API Gateway**          | 8085 | —               | Centralized Routing & JWT Validation          |
| **Discovery Server**     | 8761 | —               | Eureka Service Discovery                      |

---

# 🛠️ Technology Stack

| Category                  | Technologies                       |
| ------------------------- | ---------------------------------- |
| **Language**              | Java 21                            |
| **Framework**             | Spring Boot 3.5.x, Spring Cloud    |
| **Security**              | Spring Security, JWT, BCrypt       |
| **Databases**             | MySQL, MongoDB                     |
| **AI**                    | Spring AI, Google Gemini 2.5 Flash |
| **Messaging**             | Apache Kafka                       |
| **Service Communication** | OpenFeign                          |
| **Documentation**         | Swagger / OpenAPI                  |
| **Logging**               | Log4j2, SLF4J                      |
| **Build Tool**            | Maven                              |
| **Containerization**      | Docker, Docker Compose             |

---

# 📂 Project Structure

```text
SmartHire
│
├── AUTH-SERVICE
├── JOB-SERVICE
├── CANDIDATE-SERVICE
├── AI-SCREENING-SERVICE
├── NOTIFICATION-SERVICE
├── API-GATEWAY
├── DISCOVERY-SERVER
└── docker-compose.yml
```

---

# 📌 Core Workflow

```text
                Client
                   │
                   ▼
             API Gateway
                   │
      ┌────────────┼────────────┐
      ▼            ▼            ▼
 Auth Service   Job Service   Candidate Service
                                    │
                                    ▼
                          AI Screening Service
                                    │
                                    ▼
                         Notification Service

      Service Discovery → Eureka

      Async Communication → Apache Kafka

      Databases → MySQL & MongoDB
```

---
# 🔐 Authentication Flow

```text
                    Login Request
                          │
                          ▼
                 Authentication Service
                          │
               Validate Credentials
                          │
                          ▼
                  Generate JWT Token
                          │
                          ▼
                    Return JWT Token
                          │
─────────────────────────────────────────────
         Every Protected API Request
─────────────────────────────────────────────
                          │
                          ▼
                     API Gateway
                          │
                 Validate JWT Token
                          │
          Extract User ID & Role Claims
                          │
                    Authorize Request
                          │
                          ▼
              Forward Request to Service
```

---

# ⚡ Kafka Event Flow

## Candidate Application

```text
Candidate
    │
    ▼
POST /api/candidate/apply
    │
    ▼
Candidate Service
    │
    ▼
Kafka Topic : arshdeep
    │
    ▼
Notification Service
    │
    ▼
Notification Created
```

### AI Screening Completed

```text
Recruiter
    │
    ▼
POST /api/screening/screen
    │
    ▼
AI Screening Service
    │
    ▼
Kafka Topic : screening-completed
    │
    ▼
Notification Service
    │
    ▼
Notification Created
```

---

# 🌐 Live Demo

| Service              | URL                                      |
| -------------------- | ---------------------------------------- |
| **Swagger UI**       | http://13.62.16.155:8085/swagger-ui.html |

> **Note:** Protected endpoints require a valid JWT. Authenticate using `/api/login` and include the token in the request header:
>
> `Authorization: Bearer <your-jwt-token>`

---

# 🚀 Running the Project

### Clone the Repository

```bash
git clone https://github.com/Arshdeep-nayan/SmartHire.git

cd SmartHire
```

### Start the Infrastructure

```bash
docker-compose up -d
```

### Start Services

Run the services in the following order:

1. Discovery Server
2. Auth Service
3. Job Service
4. Candidate Service
5. AI Screening Service
6. Notification Service
7. API Gateway

---

# 📚 REST API Reference

<details>
<summary><b>🔐 Auth Service (8081)</b></summary>

| Method | Endpoint | Description | Access |
| ------ | -------- | ----------- | ------ |
| POST | `/api/register` | Register new user | Public |
| POST | `/api/login` | Login & Generate JWT | Public |

</details>

---

<details>
<summary><b>💼 Job Service (8082)</b></summary>

| Method | Endpoint | Description | Access |
| ------ | -------- | ----------- | ------ |
| GET | `/jobs/jobs/all` | Get all jobs | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/job/{id}` | Get job by ID | ADMIN, RECRUITER, CANDIDATE |
| POST | `/jobs/job/add` | Create job | ADMIN, RECRUITER |
| PUT | `/jobs/job/update/{id}` | Update job | ADMIN, RECRUITER |
| DELETE | `/jobs/job/delete/{id}` | Delete job | ADMIN |
| PATCH | `/jobs/job/{id}/activate` | Activate job | ADMIN, RECRUITER |
| PATCH | `/jobs/job/{id}/deactivate` | Deactivate job | ADMIN, RECRUITER |
| GET | `/jobs/search?keyword=` | Search jobs | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/location/{location}` | Jobs by location | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/company/{company}` | Jobs by company | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/salary?minSalary=&maxSalary=` | Salary range | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/type/{jobType}` | Job type | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/experience/{experience}` | Experience | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/active` | Active jobs | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/page?page=&size=` | Pagination | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/sort/salary` | Sort by salary | ADMIN, RECRUITER, CANDIDATE |
| GET | `/jobs/sort/date` | Sort by posted date | ADMIN, RECRUITER, CANDIDATE |

</details>

---

<details>
<summary><b>👤 Candidate Service (8083)</b></summary>

### Candidate APIs

| Method | Endpoint | Description | Access |
| ------ | -------- | ----------- | ------ |
| GET | `/api/candidates/all` | Get all candidates | ADMIN |
| GET | `/api/candidate/{id}` | Get candidate by ID | ADMIN, RECRUITER, CANDIDATE |
| POST | `/api/candidate/add` | Register candidate | ADMIN, CANDIDATE |
| PUT | `/api/candidate/update/{id}` | Update candidate | ADMIN, CANDIDATE |
| DELETE | `/api/candidate/delete/{id}` | Delete candidate | ADMIN |
| PATCH | `/api/candidate/{id}/activate` | Activate candidate | ADMIN |
| PATCH | `/api/candidate/{id}/deactivate` | Deactivate candidate | ADMIN |
| PATCH | `/api/candidate/{id}/hire` | Mark candidate as hired | ADMIN, RECRUITER |
| GET | `/api/candidate/search?keyword=` | Search candidates | ADMIN, RECRUITER |
| GET | `/api/candidate/skill/{skill}` | Search by skill | ADMIN, RECRUITER |
| GET | `/api/candidate/location/{location}` | Search by location | ADMIN, RECRUITER |
| GET | `/api/candidate/experience/{experience}` | Search by experience | ADMIN, RECRUITER |
| GET | `/api/candidate/status/{status}` | Search by status | ADMIN |
| GET | `/api/candidate/page?page=&size=` | Pagination | ADMIN |
| GET | `/api/candidate/sort/date` | Sort by registration date | ADMIN |
| POST | `/api/candidate/apply?candidateId=&jobId=` | Apply for job (Publishes Kafka Event) | CANDIDATE |

### Resume APIs

| Method | Endpoint | Description | Access |
| ------ | -------- | ----------- | ------ |
| POST | `/resumes` | Upload resume | ADMIN, CANDIDATE |
| GET | `/resumes/candidate/{candidateId}` | Get candidate resume | ADMIN, RECRUITER, CANDIDATE |
| PUT | `/resumes/candidate/{candidateId}` | Update candidate resume | ADMIN, CANDIDATE |
| DELETE | `/resumes/candidate/{candidateId}` | Delete candidate resume | ADMIN, CANDIDATE |

</details>

---

<details>
<summary><b>🤖 AI Screening Service (8084)</b></summary>

| Method | Endpoint | Description | Access |
| ------ | -------- | ----------- | ------ |
| POST | `/api/screening/screen?candidateId=&jobId=` | Screen candidate using Gemini AI | ADMIN, RECRUITER |
| GET | `/api/screening/all` | Get all screenings | ADMIN |
| GET | `/api/screening/{id}` | Get screening by ID | ADMIN, RECRUITER |
| GET | `/api/screening/candidate/{candidateId}` | Get screenings by candidate | ADMIN |
| GET | `/api/screening/job/{jobId}` | Get screenings by job | ADMIN, RECRUITER |
| GET | `/api/screening/candidate/{candidateId}/job/{jobId}` | Get screening by candidate & job | ADMIN, RECRUITER |
| DELETE | `/api/screening/{id}` | Delete screening | ADMIN |

</details>

---

<details>
<summary><b>🔔 Notification Service (8086)</b></summary>

| Method | Endpoint | Description | Access |
| ------ | -------- | ----------- | ------ |
| POST | `/api/notification/add` | Create notification (Internal Only) | Internal |
| GET | `/api/notification/all` | Get all notifications | ADMIN |
| GET | `/api/notification/{id}` | Get notification by ID | ADMIN |
| GET | `/api/notification/candidate/{candidateId}` | Get notifications by candidate | ADMIN, CANDIDATE |
| GET | `/api/notification/job/{jobId}` | Get notifications by job | ADMIN, RECRUITER |
| GET | `/api/notification/unread` | Get all unread notifications | ADMIN |
| GET | `/api/notification/unread/candidate/{candidateId}` | Get unread notifications by candidate | ADMIN, CANDIDATE |
| GET | `/api/notification/unread/job/{jobId}` | Get unread notifications by job | ADMIN, RECRUITER |
| PATCH | `/api/notification/{id}/read` | Mark notification as read | ADMIN, RECRUITER, CANDIDATE |
| DELETE | `/api/notification/{id}` | Delete notification | ADMIN |

</details>

---

<details>
<summary><b>🌐 API Gateway (8085)</b></summary>

### Public Endpoints

| Method | Endpoint |
| ------ | -------- |
| POST | `/api/register` |
| POST | `/api/login` |

### Protected Routes

- `/jobs/**`
- `/api/candidate/**`
- `/api/candidates/**`
- `/resumes/**`
- `/api/screening/**`
- `/api/notification/**`

**JWT authentication and Role-Based Access Control (RBAC) enforced at the API Gateway before forwarding requests to downstream microservices.**

</details>

---

# 📖 Swagger

| Service              | Endpoint                                   |
| -------------------- | ------------------------------------------ |
| Gateway UI           | `http://13.62.16.155:8085/swagger-ui.html` |
| Auth Service         | `/auth/v3/api-docs`                        |
| Job Service          | `/jobs/v3/api-docs`                        |
| Candidate Service    | `/candidate/v3/api-docs`                   |
| AI Screening Service | `/screening/v3/api-docs`                   |
| Notification Service | `/notification/v3/api-docs`                |

---
# 🗄️ Databases

| Database    | Purpose                                                 |
| ----------- | ------------------------------------------------------- |
| **MySQL**   | Stores users, jobs, and candidate information           |
| **MongoDB** | Stores resumes, AI screening results, and notifications |

---

# 🐳 Docker Services

The project is fully containerized using **Docker** and **Docker Compose**.

The following services are orchestrated using Docker Compose:

* Discovery Server
* API Gateway
* Auth Service
* Job Service
* Candidate Service
* AI Screening Service
* Notification Service
* MySQL
* MongoDB
* Apache Kafka
* Zookeeper

---

# 💻 Skills Demonstrated

* Java 21
* Spring Boot
* Spring Cloud
* Spring Security
* JWT Authentication & Authorization
* REST API Development
* Microservices Architecture
* API Gateway
* Eureka Service Discovery
* OpenFeign
* Apache Kafka
* Event-Driven Architecture
* Spring AI
* Google Gemini API
* MySQL
* MongoDB
* Docker
* Docker Compose
* Swagger / OpenAPI
* Log4j2
* Maven

---

# 📌 Project Summary

SmartHire is a backend-only job portal built using a microservices architecture with Java and Spring Boot. The project integrates secure JWT authentication, centralized routing through Spring Cloud Gateway, service discovery with Eureka, asynchronous communication using Apache Kafka, AI-powered resume screening through Google Gemini, and containerized deployment using Docker.

It demonstrates practical implementation of modern backend technologies while following a modular and scalable architecture.

---

# 👨‍💻 Author

**Arshdeep Nayan**

Java Backend Developer

Passionate about building scalable backend applications using Java, Spring Boot, Microservices, Cloud technologies, and AI integrations.

**LinkedIn:** https://www.linkedin.com/in/arshdeep-nayan-6a6033311/

---

