package ru.otus.hw.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApplicationProperty implements SecurityPropertyProvider {

    @Value("${security.token-valid-second}")
    private int tokenValiditySeconds;

    @Value("${security.token-sign-key}")
    private String tokenSignKey;

}
