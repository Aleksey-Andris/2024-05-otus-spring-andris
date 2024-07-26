package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CsvQuestionDaoTest {

    @Autowired
    private CsvQuestionDao testDao;

    @Test
    void findAll() {
        var answers1 = List.of(
                new Answer("Some answer_1.1", true),
                new Answer("Some answer_1.2", false),
                new Answer("Some answer_1.3", false));
        var answers2 = List.of(
                new Answer("Some answer_2.1", false),
                new Answer("Some answer_2.2", true));
        var question1 = new Question("Some question_1", answers1);
        var question2 = new Question("Some question_2", answers2);
        var expectedQuestions = List.of(question1, question2);

        var actualQuestions = testDao.findAll();

        assertThat(actualQuestions)
                .isNotNull()
                .isNotEmpty()
                .isEqualTo(expectedQuestions);
    }

}