package br.davimf.food.example;

import org.springframework.boot.SpringApplication;

public class TestOrderAuthApiApplication {

  public static void main(String[] args) {
    SpringApplication.from(OrderAuthApiApplication::main)
        .with(TestContainersConfiguration.class)
        .run(args);
  }
}
