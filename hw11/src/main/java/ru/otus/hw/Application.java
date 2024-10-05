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
                
                Если просто посмотреть, используй user / user;
                Если создавать и редактировать контент - moderator / moderator;
                Если все вышеперечисленное + удаление, используй  super / super.
                
                Посмотреть свои роли можно тут:
                http://localhost:8080/hello
                
                """);
    }

}