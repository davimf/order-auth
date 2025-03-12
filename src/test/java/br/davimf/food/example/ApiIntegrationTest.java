package br.davimf.food.example;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

import br.davimf.food.example.controller.model.AuthenticationRequest;
import br.davimf.food.example.controller.model.OrderRequest;
import br.davimf.food.example.entity.Order;
import br.davimf.food.example.entity.OrderStatus;
import br.davimf.food.example.repository.OrderRepository;
import br.davimf.food.example.repository.TokenRepository;
import br.davimf.food.example.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestContainersConfiguration.class, TestClockConfig.class})
public class ApiIntegrationTest {
  private static final String USER_USERNAME = "user@food-auth.com";
  private static final String USER_PASSWORD = "u$erPass2025";
  private static final String USER2_USERNAME = "user2@food-auth.com";
  private static final String USER2_PASSWORD = "u$er2Pass2025";
  private static final String DRIVER_USERNAME = "driver@food-auth.com";
  private static final String DRIVER_PASSWORD = "driverPa$$2025";

  @Autowired UserRepository userRepository;
  @Autowired OrderRepository orderRepository;
  @Autowired TokenRepository tokenRepository;
  @Autowired TestClockConfig testClockConfig;

  @LocalServerPort private Integer port;

  private static String getAccessToken(String userName, String password) {
    return given()
        .contentType(ContentType.JSON)
        .body(new AuthenticationRequest(userName, password))
        .when()
        .post("/v1/auth/authenticate")
        .then()
        .statusCode(HttpStatus.OK.value())
        .extract()
        .jsonPath()
        .getString("accessToken");
  }

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = "http://localhost:" + port + "/api";
    orderRepository.deleteAll();
    tokenRepository.deleteAll();
    SecurityContextHolder.clearContext();
    testClockConfig.resetTime();
  }

  @Test
  void shouldGetDeliverableOrder() {
    var readyOriginAddress = "Rua Z, 22";
    var readyDeliveryAddress = "Rua B, 44";
    var readyPackageAmount = 3;
    var readyStatus = OrderStatus.READY;
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var driverApp = userRepository.findFirstByUserName(DRIVER_USERNAME).orElseThrow();
    List<Order> orders =
        List.of(
            new Order(userApp, OrderStatus.ORDERED, "Rua A, 35", "Rua T, 44", 2),
            new Order(
                userApp,
                readyStatus,
                readyOriginAddress,
                readyDeliveryAddress,
                readyPackageAmount));
    orderRepository.saveAll(orders);
    var driverAccessToken = getAccessToken(driverApp.getUserName(), DRIVER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + driverAccessToken)
        .when()
        .get("/v1/orders/next-ready")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("status", equalTo(readyStatus.toString()))
        .body("userId", equalTo(userApp.getId().toString()))
        .body("originAddress", equalTo(readyOriginAddress))
        .body("deliveryAddress", equalTo(readyDeliveryAddress))
        .body("packageAmount", equalTo(readyPackageAmount));
  }

  @Test
  void shouldGenerateDifferentTokensOnReAuthentication() {
    var driverUserName =
        userRepository.findFirstByUserName(DRIVER_USERNAME).orElseThrow().getUserName();
    var token = getAccessToken(driverUserName, DRIVER_PASSWORD);
    testClockConfig.advanceTime(Duration.ofSeconds(1));
    var newToken = getAccessToken(driverUserName, DRIVER_PASSWORD);
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + token)
        .when()
        .get("/v1/orders/next-ready")
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value());
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + newToken)
        .when()
        .get("/v1/orders/next-ready")
        .then()
        .statusCode(HttpStatus.NO_CONTENT.value());
    assertThat(token).isNotNull();
    assertThat(newToken).isNotNull();
    assertThat(token).isNotEqualTo(newToken);
  }

  @Test
  void shouldReturnDeliverableOrderEmpty() {
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var driverApp = userRepository.findFirstByUserName(DRIVER_USERNAME).orElseThrow();
    List<Order> orders =
        List.of(
            new Order(userApp, OrderStatus.ORDERED, "Rua A, 35", "Rua T, 44", 2),
            new Order(userApp, OrderStatus.ORDERED, "Rua B, 35", "Rua T, 44", 3));
    orderRepository.saveAll(orders);
    var driverAccessToken = getAccessToken(driverApp.getUserName(), DRIVER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + driverAccessToken)
        .when()
        .get("/v1/orders/next-ready")
        .then()
        .statusCode(HttpStatus.NO_CONTENT.value());
  }

  @Test
  void shouldNotGetDeliverableOrderDueToUnknownUser() {
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    orderRepository.save(new Order(userApp, OrderStatus.ORDERED, "Rua A, 35", "Rua T, 44", 2));

    given()
        .contentType(ContentType.JSON)
        .when()
        .get("/v1/orders/next-ready")
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value());
  }

  @Test
  void shouldNotGetDeliverableOrderDueToUserType() {
    var readyOriginAddress = "Rua Z, 22";
    var readyDeliveryAddress = "Rua B, 44";
    var readyPackageAmount = 3;
    var readyStatus = OrderStatus.READY;
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    List<Order> orders =
        List.of(
            new Order(userApp, OrderStatus.ORDERED, "Rua A, 35", "Rua T, 44", 2),
            new Order(
                userApp,
                readyStatus,
                readyOriginAddress,
                readyDeliveryAddress,
                readyPackageAmount));
    orderRepository.saveAll(orders);
    var userAccessToken = getAccessToken(userApp.getUserName(), USER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + userAccessToken)
        .when()
        .get("/v1/orders/next-ready")
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value());
  }

  @Test
  void shouldGetOrderStatusWhenUser() {
    var orderStatus = OrderStatus.ORDERED;
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var orderId =
        orderRepository.save(new Order(userApp, orderStatus, "Rua A, 35", "Rua T, 44", 2)).getId();
    var userAccessToken = getAccessToken(userApp.getUserName(), USER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + userAccessToken)
        .when()
        .get("/v1/orders/" + orderId + "/status")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("status", equalTo(orderStatus.toString()));
  }

  @Test
  void shouldGetOrderStatusWhenDriver() {
    var orderStatus = OrderStatus.ORDERED;
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var driverApp = userRepository.findFirstByUserName(DRIVER_USERNAME).orElseThrow();
    var orderId =
        orderRepository.save(new Order(userApp, orderStatus, "Rua A, 35", "Rua T, 44", 2)).getId();
    var driverAccessToken = getAccessToken(driverApp.getUserName(), DRIVER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + driverAccessToken)
        .when()
        .get("/v1/orders/" + orderId + "/status")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("status", equalTo(orderStatus.toString()));
  }

  @Test
  void shouldUpdateOrderStatus() {
    var newStatus = OrderStatus.ON_DELIVERY;
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var orderId =
        orderRepository
            .save(new Order(userApp, OrderStatus.READY, "Rua A, 35", "Rua T, 44", 2))
            .getId();
    var driverAppUserName =
        userRepository.findFirstByUserName(DRIVER_USERNAME).orElseThrow().getUserName();
    var driverAccessToken = getAccessToken(driverAppUserName, DRIVER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + driverAccessToken)
        .when()
        .put("/v1/orders/" + orderId + "/status/" + newStatus)
        .then()
        .statusCode(HttpStatus.OK.value());
  }

  @Test
  void shouldNotUpdateOrderStatusDueToStatus() {
    var newStatus = OrderStatus.ON_DELIVERY;
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var orderId =
        orderRepository
            .save(new Order(userApp, OrderStatus.DELIVERED, "Rua A, 35", "Rua T, 44", 2))
            .getId();
    var driverAppUserName =
        userRepository.findFirstByUserName(DRIVER_USERNAME).orElseThrow().getUserName();
    var driverAccessToken = getAccessToken(driverAppUserName, DRIVER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + driverAccessToken)
        .when()
        .put("/v1/orders/" + orderId + "/status/" + newStatus)
        .then()
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
  }

  @Test
  void shouldGetOrderById() {
    var orderStatus = OrderStatus.ORDERED;
    var originAddress = "Rua Z, 22";
    var deliveryAddress = "Rua B, 44";
    var packageAmount = 3;
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var orderId =
        orderRepository
            .save(new Order(userApp, orderStatus, originAddress, deliveryAddress, packageAmount))
            .getId();
    var userAccessToken = getAccessToken(userApp.getUserName(), USER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + userAccessToken)
        .when()
        .get("/v1/orders/" + orderId)
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("status", equalTo(orderStatus.toString()))
        .body("userId", equalTo(userApp.getId().toString()))
        .body("originAddress", equalTo(originAddress))
        .body("deliveryAddress", equalTo(deliveryAddress))
        .body("packageAmount", equalTo(packageAmount));
  }

  @Test
  void shouldNotGetOrderByIdDueToUnknownOrderId() {
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var orders =
        List.of(
            new Order(userApp, OrderStatus.ORDERED, "Rua A, 35", "Rua T, 44", 2),
            new Order(userApp, OrderStatus.ORDERED, "Rua A, 88", "Rua T, 44", 2));
    orderRepository.saveAll(orders);
    var userAccessToken = getAccessToken(userApp.getUserName(), USER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + userAccessToken)
        .when()
        .get("/v1/orders/" + UUID.randomUUID())
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void shouldSaveNewOrder() {
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    OrderRequest orderRequest = new OrderRequest(OrderStatus.ORDERED, "Rua A, 35", "Rua T, 44", 2);
    var userAccessToken = getAccessToken(userApp.getUserName(), USER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + userAccessToken)
        .body(orderRequest)
        .when()
        .post("/v1/orders")
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .header(
            "Location",
            matchesPattern(
                ".*/v1/orders/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) // Corrected Pattern
        .body(blankOrNullString())
        .extract()
        .header("Location");
  }

  @Test
  void shouldNotSaveNewOrderDueToUserType() {
    var driverApp = userRepository.findFirstByUserName(DRIVER_USERNAME).orElseThrow();
    OrderRequest orderRequest = new OrderRequest(OrderStatus.ORDERED, "Rua A, 35", "Rua T, 44", 2);
    var driverAccessToken = getAccessToken(driverApp.getUserName(), DRIVER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + driverAccessToken)
        .body(orderRequest)
        .when()
        .post("/v1/orders")
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value());
  }

  @Test
  void shouldRemoveOrder() {
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var orderId =
        orderRepository
            .save(new Order(userApp, OrderStatus.ORDERED, "Rua A, 35", "Rua T, 44", 2))
            .getId();
    var userAccessToken = getAccessToken(userApp.getUserName(), USER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + userAccessToken)
        .when()
        .delete("/v1/orders/" + orderId)
        .then()
        .statusCode(HttpStatus.OK.value());
    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + userAccessToken)
        .when()
        .get("/v1/orders/" + orderId)
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value());
  }

  @Test
  void shouldNotRemoveOrderDueToStatus() {
    var userApp = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var orderId =
        orderRepository
            .save(new Order(userApp, OrderStatus.READY, "Rua A, 35", "Rua T, 44", 2))
            .getId();
    var userAccessToken = getAccessToken(userApp.getUserName(), USER_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + userAccessToken)
        .when()
        .delete("/v1/orders/" + orderId)
        .then()
        .statusCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
  }

  @Test
  void shouldNotRemoveOrderDueToOtherUser() {
    var userOwner = userRepository.findFirstByUserName(USER_USERNAME).orElseThrow();
    var userApp = userRepository.findFirstByUserName(USER2_USERNAME).orElseThrow();
    var orderId =
        orderRepository
            .save(new Order(userOwner, OrderStatus.ORDERED, "Rua A, 35", "Rua T, 44", 2))
            .getId();
    var userAccessToken = getAccessToken(userApp.getUserName(), USER2_PASSWORD);

    given()
        .contentType(ContentType.JSON)
        .header("Authorization", "Bearer " + userAccessToken)
        .when()
        .delete("/v1/orders/" + orderId)
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value());
  }
}
