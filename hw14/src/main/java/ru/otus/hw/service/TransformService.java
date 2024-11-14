package ru.otus.hw.service;

import ru.otus.hw.domain.Butterfly;
import ru.otus.hw.domain.Caterpillar;
import ru.otus.hw.domain.Cocoon;
import ru.otus.hw.domain.Larva;

public interface TransformService {

    Caterpillar transformLarvae(Larva larva);

    Cocoon transformCaterpillar(Caterpillar caterpillar);

    Butterfly transformCocoon(Cocoon cocoon);

}
