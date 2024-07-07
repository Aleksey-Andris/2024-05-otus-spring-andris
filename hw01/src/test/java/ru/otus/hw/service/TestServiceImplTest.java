package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TestServiceImplTest {
    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        ioService = Mockito.mock(IOService.class);
        questionDao = Mockito.mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    public void executeTest() {
        List<Answer> answers1 = List.of(
                new Answer("Any answer_1.1", false),
                new Answer("Any answer_1.2", true));
        List<Answer> answers2 = List.of(
                new Answer("Any answer_2.1", false),
                new Answer("Any answer_2.2", true));
        List<Question> questions = List.of(
                new Question("Any question_1", answers1),
                new Question("Any question_2", answers2)
        );
        when(questionDao.findAll()).thenReturn(questions);

        testService.executeTest();

        verify(ioService, times(1)).printFormattedLine(anyString(), eq(1), eq(questions.get(0).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(2), eq(questions.get(1).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(1), eq(answers1.get(0).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(2), eq(answers1.get(1).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(1), eq(answers2.get(0).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(2), eq(answers2.get(1).text()));
    }
}