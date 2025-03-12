# Architecture Decision Record (ADR01)

# 1. Choosing Core Technologies for the Food Delivery API MVP

* **Status:** Proposed
* **Date:** 2025-03-10
* **Deciders:** davimf

## Context

For the initial Minimum Viable Product (MVP) of the Food Delivery API, we need to select core
technologies that provide a balance between rapid development, essential functionality, and future
scalability.

## Decision

We have decided to use the following technologies for the MVP:

* **Database:** PostgreSQL (SQL)
* **Scalability:** RPM (Requests Per Minute) based scaling
* **Security/Authorization:** JSON Web Tokens (JWT)

## Rationale

### PostgreSQL (SQL)

* **Integrity and Consistency:** SQL databases, particularly PostgreSQL, offer strong data integrity
  and consistency, which are crucial for managing orders and user data.
* **Spring Boot Integration:** Spring Boot provides excellent support for PostgreSQL, simplifying
  database interactions and development.
* **Pagination and Querying:** SQL databases excel at handling complex queries and pagination, which
  will be essential for managing large datasets as the application grows.
* **Simplicity:** For an MVP, Postgres is very simple to setup, and very well documented.

### RPM (Requests Per Minute) Based Scaling

* **MVP Focus:** For an MVP, focusing on scaling based on the number of requests ensures that the
  application can handle a reasonable load of users.
* **Simplicity:** RPM scaling is relatively straightforward to implement and monitor.
* **Cost-Effectiveness:** It allows us to optimize resource usage based on actual traffic patterns.
* **Future Scalability:** While not the most advanced scaling strategy, it provides a solid
  foundation for future scalability by allowing us to easily increase resource allocation as needed.

### JSON Web Tokens (JWT)

* **Role-Based Authorization:** JWTs enable us to implement role-based authorization, allowing us to
  differentiate between user and driver access levels.
* **Stateless Authentication:** JWTs are stateless, which simplifies server-side authentication
  logic and improves scalability.
* **Spring Security Integration:** Spring Security provides excellent support for JWTs, simplifying
  the implementation of secure authentication.
* **Standardization:** JWT is a widely adopted standard, ensuring interoperability and ease of use.

## Consequences

### PostgreSQL (SQL)

* **Relational Data:** We will need to design a relational database schema to represent our data.
* **Learning Curve:** Developers unfamiliar with SQL may require some time to learn.

### RPM (Requests Per Minute) Based Scaling

* **Limited Elasticity:** RPM scaling may not be as elastic as more advanced scaling strategies.
* **Monitoring:** We will need to implement monitoring to track request rates and adjust resource
  allocation accordingly.

### JSON Web Tokens (JWT)

* **Security Considerations:** We will need to implement proper security measures to protect JWTs
  from attacks.
* **Token Management:** We will need to implement a strategy for managing token expiration and
  revocation.

## C4 model

These diagrams represent the current system, excluding cache and OAuth2 integration, which are
planned for future development beyond the MVP.

* **Model**
  ![model.png](../c4-model/exports/ADR_01/model.png)

* **Order API**
  ![orderAPI.png](../c4-model/exports/ADR_01/orderAPI.png)

* **Auth API**
  ![authAPI.png](../c4-model/exports/ADR_01/authAPI.png)

We leveraged the official [C4 model website](https://c4model.com/diagrams/example) for reference and
the [Structurizr DSL generator](https://structurizr.com/dsl) to implement
this [C4 model dsl](../c4-model/ADR_01.dsl).

## Further Considerations

* We will evaluate more advanced scaling strategies as the application grows.
* We will continuously monitor and optimize database performance.
* We will conduct regular security audits to ensure the security of our authentication system.
