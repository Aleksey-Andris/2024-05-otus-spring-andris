package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDTO;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final GenreConverter genreConverter;

    @Override
    public List<GenreDTO> findAll() {
        return genreConverter.modelsToDTO(genreRepository.findAll());
    }

}
