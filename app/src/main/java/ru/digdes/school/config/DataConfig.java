package ru.digdes.school.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"ru.digdes.school.dao"})
@EntityScan(basePackages = {"ru.digdes.school.model"})
@ComponentScan(basePackages = {"ru.digdes"})
public class DataConfig {
}
