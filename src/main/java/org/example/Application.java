package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({"classpath:spring/camel-context.xml"})
public class Application {

  public static void main(String[] args) {
    System.out.println("start");
    SpringApplication.run(Application.class, args);
    System.out.println("finish");
  }
}