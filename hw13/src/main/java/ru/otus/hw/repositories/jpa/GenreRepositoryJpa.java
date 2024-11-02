package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.jpa.GenreJpa;

import java.util.List;
import java.util.Set;

public interface GenreRepositoryJpa extends JpaRepository<GenreJpa, Long> {

}
