package ru.otus.hw.repositories;

import lombok.Getter;

@Getter
public enum Graphs {

    FETCH_GRAPH("jakarta.persistence.fetchgraph"),

    LOAD_GRAPH("jakarta.persistence.loadgraph");

    private final String val;

    Graphs(String val) {
        this.val = val;
    }
}
