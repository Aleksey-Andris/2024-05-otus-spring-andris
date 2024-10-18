package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {

    private long id;

    private String title;

    private AuthorDTO author;

    private List<GenreDTO> genres;

}
