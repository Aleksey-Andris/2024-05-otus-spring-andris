package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.jpa.AuthorJpa;

public interface AuthorRepositoryJpa extends JpaRepository<AuthorJpa, Long> {

}
