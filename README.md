# Student Management System (SMS) 🎓

An enterprise-grade, production-ready **Student Management System (SMS)** suitable for modern colleges and universities. This portfolio-worthy system features a multi-tiered role-based permission system, AI-powered academic insights, QR code attendance verification, mock credit card payments, and comprehensive admin dashboard statistics.

Built using **Java 21, Spring Boot 3, and React 19**.

---

## 🚀 Tech Stack

### Backend
- **Java 21** & **Spring Boot 3** (Spring Web MVC, Security, Data JPA)
- **Spring Security** with stateless **JWT Authentication** (access/refresh tokens)
- **MySQL 8.0** for relational storage
- **Hibernate (JPA)** connection pooling (HikariCP)
- **Maven** for dependency management
- **ZXing** for secure QR code generation
- **OpenRouter WebClient** integration for generative AI features

### Frontend
- **React 19** & **Vite**
- **React Router v6**
- **Axios** with global interceptors
- **Bootstrap 5** (Premium custom Glassmorphism theme)
- **React Hook Form** for form validation
- **Recharts** for metric visualization

### DevOps & Deployment
- **Docker** & **Docker Compose**
- **H2 Database** for integrated unit/slice testing (JUnit 5 & Mockito)

---

## 🎨 Key Features

1. **Role-Based Security**: Defined routes & APIs restricted to `ROLE_ADMIN`, `ROLE_TEACHER`, and `ROLE_STUDENT`.
2. **Student & Faculty Management**: Full CRUD interface including department categorization and profile picture uploads.
3. **QR Attendance Check-In**: Faculty can generate a time-restricted QR code from the portal, and students scan to mark their attendance.
4. **Billing & Tuition Checkout**: Integrated billing list with tuition, hostel, and library fee invoicing. Includes a mock secure card checkout portal.
5. **AI Academic Advisor**: Integrates OpenRouter API (Mistral/Llama) to provide study schedules, analyze grades, predict attendance warning risk, and answer student questions.
6. **Statistial Dashboards**: Live data caching for stats aggregation (Total count of students, teachers, courses, and financial collection charts).

---

## ⚙️ Running Locally

### Prerequisites
- **Java 21 or higher**
- **Node.js (v18+) & NPM**
- **Docker & Docker Compose** (Optional, for database/containerization)

### 1. Database Setup
To start the database container using Docker, run:
```bash
docker-compose up -d mysql-db
```
The database will start on port `3306` with credentials configured in `docker-compose.yml`.

### 2. Run Backend
Create/modify the configuration in `src/main/resources/application.properties` with your database credentials and OpenRouter key:
```properties
app.jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
app.ai.openrouter.api-key=your-openrouter-key-here
```
Compile and run the Spring Boot application:
```bash
mvn spring-boot:run
```
*The database is automatically seeded on startup with default roles and an admin user:*
- **Username**: `admin@sms.edu`
- **Password**: `Admin@1234`

### 3. Run Frontend
Navigate to the frontend folder, install dependencies, and start the development server:
```bash
cd frontend
npm install
npm run dev
```
Open `http://localhost:5173` in your browser.

---

## 🔬 Running Tests
Run the JUnit 5 and Mockito test suite:
```bash
mvn test
```
The test suite utilizes an in-memory **H2 database** with profile `test` configured in `src/test/resources/application-test.properties`.

---

## 🐳 Docker Deployment
To launch the entire platform (MySQL DB + Spring Boot API) in containers:
```bash
docker-compose up --build
```
The backend API will serve requests at `http://localhost:8080` and connect seamlessly with the database.
