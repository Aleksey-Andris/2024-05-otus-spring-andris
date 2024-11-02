package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.jpa.GenreJpa;

public interface GenreRepositoryJpa extends JpaRepository<GenreJpa, Long> {

}
