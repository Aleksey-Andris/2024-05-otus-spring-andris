package ru.otus.hw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.print("""
                \r
                Чтобы перейти на страницу сайта открывай:
                http://localhost:8080
                Если ты пользователь, используй user / user;
                Если админ - admin / admin
                """);
    }

}