package ru.otus.hw.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookShortDTO {

    private long id;

    @NotBlank
    private String title;

    @Min(1)
    private long authorId;

    @NotEmpty
    @NotNull
    private Set<Long> genresIds;
}
