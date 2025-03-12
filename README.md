# Food Delivery API - Spring Boot Example

This is a Java application built with the Spring Boot framework, utilizing Gradle, Docker,
PostgreSQL, Liquibase for database migrations, JWT for authentication, and Swagger for API
documentation.

## Documentation

* [Architecture Decision Records (ADR01)](./docs/adrs/ADR01.md)
* [Project Backlog](./docs/BACKLOG.md)

## Tools

All tools listed are open source or free for non-commercial use.

### Development Environment

* [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/download/)

### Containerization

* [Docker](https://docs.docker.com/get-docker/)

### Database Management

* [DBeaver Community](https://dbeaver.io/download/)

### HTTP Request Tools

* [HTTPie](https://httpie.io/download)
* [Postman](https://www.postman.com/downloads/)

### JWT Debugging

* [JSON Web Token (JWT) Debugger](https://jwt.io/)

### AES Key Generation

* [ASecuritySite.com Encryption Tools](https://asecuritysite.com/encryption/plain)

## Docker Compose Support

This project includes a Docker Compose file (`compose.yaml`) for easy setup and development.

## Testcontainers Support

This project uses [Testcontainers](https://java.testcontainers.org/) for integration testing during
development.

## Utilities

### Swagger link

[local-environment](http://localhost:8081/swagger-ui/index.html#/)

### Docker Compose Commands

To start or restart the application:

```bash
docker-compose down
docker-compose build
docker-compose up -d
```

### Initial Test Data

The following user credentials are pre-configured for testing:

```
USER: user@food-auth.com / u$erPass2025
DRIVER: driver@food-auth.com / driverPa$$2025
USER2: user2@food-auth.com / u$er2Pass2025
```

### cURL Commands for API Testing

#### Generate Access Token (User or Driver)

```bash
curl -X POST \
http://localhost:8081/api/v1/auth/authenticate \
-H 'Content-Type: application/json' \
-d '{
"userName": "{USER_NAME}",
"password": "{PASSWORD}"
}'
```

### Create new order (User)

```bash
curl --request POST \
  --url http://localhost:8081/api/v1/orders \
  --header 'Authorization: Bearer {VALID_TOKEN}' \
  --header 'Content-Type: application/json' \
  --data '{
  "status": "ORDERED",
  "originAddress": "123 Main St",
  "deliveryAddress": "456 Oak Ave",
  "packageAmount": 3
}'
```

### Get order data (User)

```bash
curl --request GET \
--url http://localhost:8081/api/v1/orders/{ORDER_ID} \
--header 'Authorization: Bearer {VALID_TOKEN}'
```

### Get status of order (User or Driver)

```bash
curl --request GET \
--url http://localhost:8081/api/v1/orders/{ORDER_ID}/status \
--header 'Authorization: Bearer {VALID_TOKEN}'
```

### Update order status (Driver)

```bash
curl --request PUT \
--url http://localhost:8081/api/v1/orders/{ORDER_ID}/status/{STATUS} \
--header 'Authorization: Bearer {VALID_TOKEN}'
```

### Remove order (User)

```bash
curl --request DELETE \
--url http://localhost:8081/api/v1/orders/{ORDER_ID} \
--header 'Authorization: Bearer {VALID_TOKEN}'
```

### Get next order able to be delivered (Driver)

```bash
curl --request GET \
--url http://localhost:8081/api/v1/orders/next-ready \
--header 'Authorization: Bearer {VALID_TOKEN}'
```

## Reference Documentation

For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.3/gradle-plugin)
* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.3/gradle-plugin/packaging-oci-image.html)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/3.4.3/reference/testing/testcontainers.html#testing.testcontainers)
* [Docker Compose Support](https://docs.spring.io/spring-boot/3.4.3/reference/features/dev-services.html#features.dev-services.docker-compose)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.3/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/3.4.3/reference/actuator/index.html)
* [Testcontainers](https://java.testcontainers.org/)
* [Spring REST Docs](https://docs.spring.io/spring-restdocs/docs/current/reference/htmlsingle/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Markdown Document Reader](https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html#_markdown)