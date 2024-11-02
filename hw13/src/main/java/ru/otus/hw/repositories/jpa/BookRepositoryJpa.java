package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.hw.models.jpa.BookJpa;

import java.util.List;

public interface BookRepositoryJpa extends JpaRepository<BookJpa, Long> {

}
