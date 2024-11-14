package ru.otus.hw.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Butterfly;
import ru.otus.hw.domain.Larva;
import ru.otus.hw.domain.Sex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class LifeCycleServiceImpl implements LifeCycleService {

    private static final List<Sex> SEXES = List.of(Sex.MALE, Sex.FEMALE);

    private final LifeCycleGateway lifeCycleGateway;


    @Override
    public void startLifeCycle() {
        List<Larva> larvae = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var larva = new Larva(SEXES.get(RandomUtils.nextInt(0, SEXES.size())));
            larvae.add(larva);
        }
        Collection<Butterfly> butterflies = lifeCycleGateway.process(larvae);
        log.info("{} butterfly transformed", butterflies.size());
    }

}
