package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import ru.otus.hw.domain.Butterfly;
import ru.otus.hw.domain.Caterpillar;
import ru.otus.hw.domain.Cocoon;
import ru.otus.hw.domain.Larva;
import ru.otus.hw.domain.Sex;

import java.util.List;

@Slf4j
public class TransformServiceImpl implements TransformService {

    private final List<String> butterflyForms;

    private final List<String> femaleNames;

    private final List<String> maleNames;

    public TransformServiceImpl(List<String> butterflyForms,
                                List<String> femaleNames,
                                List<String> maleNames) {
        this.butterflyForms = butterflyForms;
        this.femaleNames = femaleNames;
        this.maleNames = maleNames;
    }

    @Override
    public Caterpillar transformLarvae(Larva larva) {
        delay();
        return new Caterpillar(generateName(larva.getSex()), larva.getSex());
    }


    @Override
    public Cocoon transformCaterpillar(Caterpillar caterpillar) {
        delay();
        return new Cocoon(caterpillar);
    }

    @Override
    public Butterfly transformCocoon(Cocoon cocoon) {
        delay();
        Butterfly butterfly = new Butterfly(
                cocoon.getCaterpillar().getName(),
                cocoon.getCaterpillar().getSex(),
                generateForm());
        log.info("The butterfly was born:\n {}", butterfly);
        return butterfly;
    }

    private String generateName(Sex sex) {
        return Sex.MALE.equals(sex) ? maleNames.get(RandomUtils.nextInt(0, maleNames.size()))
                : femaleNames.get(RandomUtils.nextInt(0, femaleNames.size()));
    }

    private String generateForm() {
        return butterflyForms.get(RandomUtils.nextInt(0, butterflyForms.size()));
    }

    private void delay() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
