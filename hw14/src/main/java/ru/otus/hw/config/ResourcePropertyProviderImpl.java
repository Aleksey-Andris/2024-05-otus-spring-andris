package ru.otus.hw.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ResourcePropertyProviderImpl implements ResourcePropertyProvider {

    @Value("${resources.butterfly-forms}")
    private String butterFlyForms;

    @Value("${resources.female-names}")
    private String femaleNames;

    @Value("${resources.male-names}")
    private String maleNames;

}
