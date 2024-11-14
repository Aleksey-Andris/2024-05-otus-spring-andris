package ru.otus.hw.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Butterfly {

    private String name;

    private Sex sex;

    private String form;

    @Override
    public String toString() {
        return "Hi, I'm %s \n %s".formatted(name, form);
    }
}
