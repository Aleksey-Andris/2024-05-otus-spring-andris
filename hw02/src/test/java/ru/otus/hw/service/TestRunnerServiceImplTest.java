package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    private TestServiceImpl testService;

    @Test
    public void executeTest() {
        var firstName = "someFirstName";
        var lastName = "someLastName";
        var student = new Student(firstName, lastName);
        var answers1 = List.of(
                new Answer("Some answer_1.1", true),
                new Answer("Some answer_1.2", false));
        var answers2 = List.of(
                new Answer("Some answer_2.1", false),
                new Answer("Some answer_2.2", true));
        var question1 = new Question("Some question_1", answers1);
        var question2 = new Question("Some question_2", answers2);
        var questions = List.of(question1, question2);

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readIntForRangeWithPrompt(eq(1), eq(2), anyString(), anyString())).thenReturn(1);

        var testResult = testService.executeTestFor(student);

        verify(questionDao, times(1)).findAll();
        verify(ioService, times(2)).readIntForRangeWithPrompt(eq(1), eq(2), anyString(), anyString());
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(1), eq(questions.get(0).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(2), eq(questions.get(1).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(1), eq(answers1.get(0).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(2), eq(answers1.get(1).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(1), eq(answers2.get(0).text()));
        verify(ioService, times(1)).printFormattedLine(anyString(), eq(2), eq(answers2.get(1).text()));

        assertThat(testResult)
                .isNotNull()
                .extracting(TestResult::getStudent)
                .isNotNull()
                .isEqualTo(student);

        assertThat(testResult.getRightAnswersCount())
                .isEqualTo(1);

        assertThat(testResult.getAnsweredQuestions())
                .isNotNull()
                .isNotEmpty()
                .contains(question1, question2);
    }

}