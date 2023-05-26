package ru.digdes.school.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"ru"})
@EntityScan(basePackages = {"ru"})
@ComponentScan(basePackages = {"ru"})
public class Launch {
    public static void main(String[] args) {
        SpringApplication.run(Launch.class, args);
    }
}
