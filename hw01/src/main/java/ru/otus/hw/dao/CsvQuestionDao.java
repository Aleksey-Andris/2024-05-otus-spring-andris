package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try (InputStream inputStream = getInputStreamFromResource()) {
            return parseFromCSV(inputStream);
        } catch (IOException e) {
            throw new QuestionReadException("Error reading CSV file", e);
        }
    }

    private InputStream getInputStreamFromResource() {
        String resourceName = fileNameProvider.getTestFileName();
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(resourceName);
        return Objects.requireNonNull(inputStream, String.format("%s not found", resourceName));
    }

    private List<Question> parseFromCSV(InputStream inputStream) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            List<QuestionDto> questionsDto = getDtoFromBufferedReader(bufferedReader);
            return dtoToDomain(questionsDto);
        } catch (IOException e) {
            throw new QuestionReadException("Error parsing CSV data");
        }
    }

    private List<QuestionDto> getDtoFromBufferedReader(BufferedReader bufferedReader) {
        CsvToBean<QuestionDto> csvToBean = buildCsvToBean(bufferedReader);
        List<QuestionDto> questionsDto = csvToBean.parse();
        if (questionsDto.isEmpty()) {
            String resourceName = fileNameProvider.getTestFileName();
            throw new QuestionReadException(String.format("No questions found in %s", resourceName));
        }
        return questionsDto;
    }

    private List<Question> dtoToDomain(List<QuestionDto> questionsDto) {
        return questionsDto.stream()
                .map(QuestionDto::toDomainObject)
                .collect(Collectors.toList());
    }

    private CsvToBean<QuestionDto> buildCsvToBean(BufferedReader reader) {
        return new CsvToBeanBuilder<QuestionDto>(reader)
                .withSkipLines(1)
                .withSeparator(';')
                .withType(QuestionDto.class)
                .build();
    }

}
