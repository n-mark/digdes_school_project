package ru.digdes.school;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@EnableRabbit
public class Launch {
    public static void main(String[] args) {
        SpringApplication.run(Launch.class, args);
    }
}
