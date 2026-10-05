# Smart ATS Resume Analyser

An AI-powered full-stack web application built with **Java Spring Boot**
and **Spring Security**. The application analyzes uploaded resumes and
provides ATS-focused feedback, including technical skills,
communication, experience, resume quality, and improvement suggestions.

The project uses **LLaMA 3 locally through Ollama** for AI-powered
suggestions, so the application does not depend on a paid cloud AI API
for its resume analysis.

## Core Features

-   **User Registration & Login**
    -   User registration and login using Spring Security.
    -   Secure password handling with BCrypt.
    -   User and resume analysis data are stored in the database.
-   **Resume Upload**
    -   Supports **PDF and DOCX** resume files.
    -   Resume text is extracted automatically for analysis.
-   **ATS Resume Analysis**
    -   Analyzes resume content for ATS-related information.
    -   Evaluates areas such as:
        -   Technical Skills
        -   Communication
        -   Experience
        -   Resume quality
        -   Improvement areas
-   **AI-Powered Suggestions**
    -   Uses **LLaMA 3 with Ollama** for local AI-based resume
        suggestions.
    -   Provides practical feedback based on the extracted resume
        content.
-   **Resume Analysis History**
    -   Logged-in users can view their previous analysis reports.
    -   Analysis information is stored in the database.
-   **Modern Web Interface**
    -   Built using Thymeleaf.
    -   Responsive pages for registration, login, resume upload,
        analysis, and history.
    -   Includes a clean and simple user interface.
-   **File Content Extraction**
    -   Apache Tika is used for extracting text from PDF and DOCX files.
    -   Apache POI is used where DOCX document structure needs to be
        inspected.

## Technology Stack

  -----------------------------------------------------------------------
Category                Technology              Purpose
  ----------------------- ----------------------- -----------------------
**Backend**             Java 21                 Core programming
language

                          Spring Boot 3.5.7       Main application
                                                  framework

                          Spring Security 6       Authentication and
                                                  authorization

                          Spring Data JPA         Database interaction

**Frontend**            Thymeleaf               Server-side HTML
template engine

                          HTML5 / CSS3            User interface

                          JavaScript              Client-side
                                                  functionality

**Database**            MySQL / XAMPP           Stores users and
analysis reports

**AI**                  LLaMA 3                 Local AI model for
resume suggestions

                          Ollama                  Runs LLaMA 3 locally

**File Parsing**        Apache Tika             Extracts text from PDF
and DOCX files

                          Apache POI              Processes DOCX document
                                                  structure

**Build Tool**          Apache Maven            Dependency management
and project build
  -----------------------------------------------------------------------

## AI Model

This project uses **LLaMA 3 through Ollama** for AI-powered resume
suggestions.

### Why Ollama?

-   Runs the AI model locally.
-   No paid Gemini API is required.
-   Resume analysis can be processed through the locally running model.
-   Provides a practical way to integrate an AI model into a Java Spring
    Boot application.

Before using the AI analysis functionality, install Ollama and make sure
the required LLaMA 3 model is available locally.

## Getting Started

Follow these steps to run the project on your local machine.

### Prerequisites

Install the following:

-   **JDK 21**
-   **Apache Maven** (optional because the project includes Maven
    Wrapper)
-   **MySQL / XAMPP**
-   **Ollama**
-   **LLaMA 3 model**
-   IntelliJ IDEA or another Java IDE

### 1. Clone the Repository

``` bash
git clone https://github.com/Samadhanchavan73/ATS-Resume-Analyzer.git
cd ATS-Resume-Analyzer
```

Replace `YOUR-USERNAME` with your GitHub username.

### 2. Set Up the Database

Start MySQL through XAMPP or another MySQL server.

Create the database:

``` sql
CREATE DATABASE ats_resume;
```

The Spring Boot application uses JPA/Hibernate to create or update the
required database tables when configured with:

``` properties
spring.jpa.hibernate.ddl-auto=update
```

### 3. Configure `application.properties`

Open:

``` text
src/main/resources/application.properties
```

Configure the database connection according to your local MySQL setup.

Example:

``` properties
spring.datasource.url=jdbc:mysql://localhost:3306/ats_resume
spring.datasource.username=root
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

If your MySQL installation has a password, enter your local MySQL
password in the `spring.datasource.password` field.

**Security note:** Do not publish real database passwords, API keys,
tokens, or other secrets in a public GitHub repository.

### 4. Set Up Ollama and LLaMA 3

Install Ollama and make sure it is available from the command line.

Then download the LLaMA 3 model required by the application.

Example:

``` bash
ollama pull llama3
```

Start Ollama if it is not already running.

The Spring Boot application can then communicate with the local Ollama
service for AI suggestions.

### 5. Build the Application

Using Maven Wrapper:

**Windows:**

``` bash
mvnw.cmd clean install
```

**Linux / macOS:**

``` bash
./mvnw clean install
```

### 6. Run the Application

**Windows:**

``` bash
mvnw.cmd spring-boot:run
```

**Linux / macOS:**

``` bash
./mvnw spring-boot:run
```

Alternatively, run the main class from IntelliJ IDEA:

``` text
AtsResumeAnalyserApplication.java
```

### 7. Open the Application

After the application starts, open:

``` text
http://localhost:8080
```

Useful pages include:

``` text
/register
/login
/
 /history
```

## 📁 Project Structure

The project follows a standard Spring Boot MVC architecture.
The project follows a standard Spring Boot (Model-View-Controller \+ Service) architecture.

src/main/java/com/anant/ats/resumeanalyser  
¦  
+-- config/              \# @Configuration beans (SecurityConfig, MarkdownConfig)  
¦  
+-- controller/          \# @Controller classes (Handles web requests)  
¦   +-- AnalysisController.java  
¦   +-- RegistrationController.java  
¦  
+-- model/               \# @Entity classes (Database tables)  
¦   +-- AnalysisReport.java  
¦   +-- User.java  
¦  
+-- repository/          \# @Repository interfaces (Spring Data JPA)  
¦   +-- AnalysisReportRepository.java  
¦   +-- UserRepository.java  
¦  
+-- service/             \# @Service classes (Business logic)  
¦   +-- AnalysisService.java  
¦   +-- CustomUserDetailsService.java  
¦   +-- SuggestionService.java  
¦   +-- TextExtractionService.java  
¦   +-- UserService.java  
¦  
+-- AtsResumeAnalyserApplication.java  \# Main entry point


Frontend resources are located under:

``` text
src/main/resources
├── static/
│   └── images/
└── templates/
    ├── history.html
    ├── index.html
    ├── layout.html
    ├── login.html
    └── register.html
```

## Application Flow

``` text
User
  │
  ▼
Register / Login
  │
  ▼
Upload Resume (PDF / DOCX)
  │
  ▼
Text Extraction
  │
  ▼
Resume Analysis
  │
  ▼
LLaMA 3 through Ollama
  │
  ▼
AI Suggestions & Analysis Report
  │
  ▼
Save Report in Database
  │
  ▼
View Analysis / History
```

## Security

The application uses **Spring Security** for authentication and
authorization.

Important security practices:

-   Passwords are handled using BCrypt.
-   Authentication is managed by Spring Security.
-   Database credentials should be kept private.
-   API keys and tokens, if added in future, should not be committed to
    a public repository.

## Testing

The project contains Spring Boot test support under:

``` text
src/test/
```

You can run the tests with:

``` bash
mvnw.cmd test
```

on Windows, or:

``` bash
./mvnw test
```

on Linux/macOS.

## Current Limitations

-   AI suggestions depend on the locally installed Ollama model.
-   AI response quality depends on the selected LLaMA 3 model and local
    system resources.
-   The application requires MySQL and Ollama to be configured locally.
-   Resume analysis is intended as an assisting tool and should not be
    treated as a guarantee of ATS acceptance.

## Future Enhancements

Possible future improvements include:

-   Job-description matching.
-   More detailed ATS scoring.
-   Support for additional resume formats.
-   More AI model options.
-   Better visual analytics and dashboards.
-   Cloud deployment.
-   Improved validation and error handling.
-   More comprehensive automated tests.


##  Project

**Smart ATS Resume Analyser**

Built using Java, Spring Boot, MySQL, LLaMA 3, and Ollama.
