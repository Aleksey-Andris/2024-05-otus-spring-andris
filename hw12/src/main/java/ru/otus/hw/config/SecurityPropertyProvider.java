package ru.otus.hw.config;

public interface SecurityPropertyProvider {

    int getTokenValiditySeconds();

    String getTokenSignKey();

}
