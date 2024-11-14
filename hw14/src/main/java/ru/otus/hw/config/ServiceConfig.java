package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw.service.TransformService;
import ru.otus.hw.service.TransformServiceImpl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ServiceConfig {

    private final ResourcePropertyProvider resourcePropertyProvider;

    @Bean
    public TransformService lifeCycleTransformService() throws IOException, URISyntaxException {
        List<String> butterflyForms = getButterflyForms();
        List<String> femaleNames = getNames(resourcePropertyProvider.getFemaleNames());
        List<String> maleNames = getNames(resourcePropertyProvider.getMaleNames());
        return new TransformServiceImpl(butterflyForms, femaleNames, maleNames);
    }

    private List<String> getButterflyForms() throws IOException {
        List<String> butterflyForms = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        URL butterFlyFormsUrl = classLoader.getResource(resourcePropertyProvider.getButterFlyForms());
        assert butterFlyFormsUrl != null;
        File dir = new File(butterFlyFormsUrl.getFile());
        File[] files = dir.listFiles();
        assert files != null;
        for (File file : files) {
            butterflyForms.add(Files.readString(file.toPath()));
        }
        return butterflyForms;
    }

    private List<String> getNames(String fileName) throws URISyntaxException, IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL  nemesURL = classLoader.getResource(fileName);
        assert nemesURL != null;
        String allNames =  Files.readString(Path.of(nemesURL.toURI()));
        return List.of(allNames.split("\n"));
    }

}
