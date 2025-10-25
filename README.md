
# 🌍 Country Currency & Exchange Rate API

A RESTful API built with **Spring Boot** that fetches country data from external APIs, stores it in **MySQL**, and provides CRUD operations with currency exchange rate calculations.

---

## 🚀 Features

- Fetch country data from RestCountries API  
- Fetch exchange rates from Open Exchange Rates API  
- Calculate estimated GDP based on population and exchange rates  
- Store and cache data in MySQL database  
- Filter countries by region or currency  
- Sort countries by estimated GDP  
- Generate summary images with top countries  
- Complete CRUD operations  

---

## 🧰 Tech Stack

- **Java 17**  
- **Spring Boot 3.2.0**  
- **Spring Data JPA**  
- **MySQL**  
- **Lombok**  
- **Maven**

---

## 🧩 Prerequisites

- Java 17 or higher  
- Maven 3.6+  
- MySQL 8.0+

---

## ⚙️ Installation & Setup

### 1. Clone the repository
```bash
git clone <your-repo-url>
cd country-api
2. Configure MySQL Database
sql
Copy code
CREATE DATABASE country_db;
3. Configure Environment Variables
Copy the .env.example file to .env:

bash
Copy code
cp .env.example .env
Edit .env:

properties
Copy code
DATABASE_URL=jdbc:mysql://localhost:3306/country_db?createDatabaseIfNotExist=true
DB_USERNAME=your_username
DB_PASSWORD=your_password
PORT=8080
4. Build the project
bash
Copy code
mvn clean install
5. Run the application
bash
Copy code
mvn spring-boot:run
or

bash
Copy code
java -jar target/country-api-1.0.0.jar
The API starts at: http://localhost:8080

🌐 API Endpoints
1. Refresh Countries Data
http
Copy code
POST /countries/refresh
Response:

json
Copy code
{
  "message": "Countries refreshed successfully"
}
2. Get All Countries
http
Copy code
GET /countries
GET /countries?region=Africa
GET /countries?currency=NGN
GET /countries?sort=gdp_desc
Response:

json
Copy code
[
  {
    "id": 1,
    "name": "Nigeria",
    "capital": "Abuja",
    "region": "Africa",
    "population": 206139589,
    "currencyCode": "NGN",
    "exchangeRate": 1600.23,
    "estimatedGdp": 25767448125.2,
    "flagUrl": "https://flagcdn.com/ng.svg",
    "lastRefreshedAt": "2025-10-22T18:00:00"
  }
]
3. Get Country by Name
http
Copy code
GET /countries/{name}
Example:

http
Copy code
GET /countries/Nigeria
4. Delete Country
http
Copy code
DELETE /countries/{name}
Example:

http
Copy code
DELETE /countries/Nigeria
Response:

json
Copy code
{
  "message": "Country deleted successfully"
}
5. Get Status
http
Copy code
GET /status
Response:

json
Copy code
{
  "total_countries": 250,
  "last_refreshed_at": "2025-10-22T18:00:00"
}
6. Get Summary Image
http
Copy code
GET /countries/image
🔎 Query Parameters
Parameter	Description	Example
region	Filter by region	?region=Africa
currency	Filter by currency code	?currency=NGN
sort	Sort by GDP	?sort=gdp_desc or ?sort=gdp_asc

⚠️ Error Responses
400 Bad Request
{
  "error": "Validation failed",
  "details": {
    "currency_code": "is required"
  }
}

404 Not Found
{
  "error": "Country not found"
}

500 Internal Server Error
{
  "error": "Internal server error"
}

503 Service Unavailable
{

🗄️ Database Schema
CREATE TABLE countries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    capital VARCHAR(255),
    region VARCHAR(255),
    population BIGINT NOT NULL,
    currency_code VARCHAR(10),
    exchange_rate DOUBLE,
    estimated_gdp DOUBLE,
    flag_url VARCHAR(512),
    last_refreshed_at DATETIME
);

🧪 Testing
mvn test

☁️ Deployment
Railway

Create a project on Railway

Add MySQL database

Connect your GitHub repo

Set environment variables:

DATABASE_URL

DB_USERNAME

DB_PASSWORD

PORT

Deploy

Heroku
heroku create your-app-name
git push heroku main

AWS EC2

Launch EC2 instance

Install Java 17 & MySQL

Upload JAR file & run app

🔐 Environment Variables
Variable	Description	Default
DATABASE_URL	MySQL connection string	jdbc:mysql://localhost:3306/country_db
DB_USERNAME	Database username	root
DB_PASSWORD	Database password	password
PORT	Server port	8080
📦 Dependencies

Spring Boot Starter Web

Spring Boot Starter Data JPA

Spring Boot Starter Validation

MySQL Connector

Lombok

Jackson

💱 Currency Handling

Only the first currency is stored

If no currency: currency_code, exchange_rate = null, GDP = 0

If currency missing in exchange rate API: GDP = null

📈 GDP Calculation
estimated_gdp = (population × random(1000–2000)) ÷ exchange_rate


Random multiplier regenerates on each refresh.
  "error": "External data source unavailable",
  "details": "Could not fetch data from RestCountries API"
}
