workspace "food-auth" "example" {
  model {
    customer = person "Customer" "A user who orders food." "User"
    restaurant = person "Restaurant" "A restaurant that provides food." "Business"
    deliveryDriver = person "Driver" "A driver who delivers food." "Driver"

    foodDeliveryAPI = softwaresystem "Food Delivery API" "Handles food orders, user management, and delivery." {
      webAPI = container "Web API" "Provides RESTful API endpoints for customers, restaurants, and drivers." "Spring Boot (Java)"
      orderProcessor = container "Order Processor" "Processes orders asynchronously using a message queue." "Spring Boot (Java)"
      database = container "Database" "Stores user, restaurant, order, and delivery information." "PostgreSQL"
      cache = container "Cache" "Stores frequently accessed data for performance improvement." "Redis"
    }

    authAPI = softwaresystem "Auth Delivery API" "Handles authentication." {
      authWebAPI = container "Auth Web API" "Provides RESTful API endpoints for authentication." "Spring Boot (Java)"
      authenticationService = container "Authentication Service" "Handles user authentication and authorization." "Spring Boot (Java)"
      authRepositoryCache = container "Cache" "Stores frequently accessed data for performance improvement." "Redis"
    }

    googleOAuth = softwaresystem "Google OAuth" "Provides user authentication via Google." "External Service"

    customer -> authAPI "Authenticates using" "JWT"
    restaurant -> authAPI "Authenticates using" "JWT"
    deliveryDriver -> authAPI "Authenticates using" "JWT"

    authAPI -> googleOAuth "Verifies Google authentication"

    messageQueue = softwaresystem "Message Queue" "Handles asynchronous communication between the Web API and Order Processor." "RabbitMQ"
    customer -> foodDeliveryAPI "Places orders and manages accounts using" "HTTPS"
    restaurant -> foodDeliveryAPI "Manages menus and order statuses using" "HTTPS"
    deliveryDriver -> foodDeliveryAPI "Receives and updates delivery status using" "HTTPS"

    webAPI -> database "Stores and retrieves data from" "SQL"
    orderProcessor -> database "Stores and retrieves data from" "SQL"
    webAPI -> messageQueue "Sends messages to"
    orderProcessor -> messageQueue "Receives messages from"
    webAPI -> cache "Caches data for performance"

    authWebAPI -> authenticationService "Handles user authentication and authorization"
    authenticationService -> authRepositoryCache "Caches data for performance"
  }
  views {
    systemlandscape "SystemLandscape" {
      include *
      autoLayout
    }

    container foodDeliveryAPI "Containers" {
      include *
      autoLayout
    }

    container AuthAPI "ContainersAuthAPI" {
      include *
      autoLayout
    }

    styles {
      element "Person" {
        shape Person
        background #08427b
        color #ffffff
      }
      element "Software System" {
        background #1168bd
        color #ffffff
      }
      element "Container" {
        background #438dd5
        color #ffffff
      }
      element "Database" {
        shape Cylinder
      }
      element "Message Queue" {
        shape RoundedBox
      }
    }
  }
}
