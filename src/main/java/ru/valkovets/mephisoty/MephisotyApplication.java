package ru.valkovets.mephisoty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class MephisotyApplication {

public static void main(final String[] args) {
    SpringApplication.run(MephisotyApplication.class, args);
}
// todo Создать схему для таблиц-аудиторов
}
