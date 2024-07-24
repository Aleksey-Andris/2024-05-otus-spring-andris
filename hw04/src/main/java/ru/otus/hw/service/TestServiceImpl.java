package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.AnswerReadException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        int questionCounter = 1;
        for (var question : questions) {
            printQuestion(question, questionCounter++);
            var answers = question.answers();
            printAnswers(answers);
            int studentAnswer = readAnswer(answers.size());
            var isAnswerValid = validateAnswer(studentAnswer, answers);
            testResult.applyAnswer(question, isAnswerValid);
            ioService.printLine("");
        }
        return testResult;
    }

    private void printQuestion(Question question, int questionNumber) {
        ioService.printFormattedLineLocalized("TestService.question", questionNumber, question.text());
    }

    private void printAnswers(List<Answer> answers) {
        ioService.printLineLocalized("TestService.answer.options");
        int answerCounter = 0;
        for (Answer answer : answers) {
            answerCounter++;
            ioService.printFormattedLine("%d) %s", answerCounter, answer.text());
        }
    }

    private int readAnswer(int answersSize) {
        ioService.printLine("");
        String promptCode = "TestService.input.answer";
        String errMessageCode = "TestService.input.answer.err.message";
        try {
            return ioService.readIntForRangeWithPromptLocalized(1, answersSize, promptCode, errMessageCode);
        } catch (IllegalArgumentException e) {
            throw new AnswerReadException("Wrong answer format", e);
        }
    }

    private boolean validateAnswer(int answerNumber, List<Answer> answers) {
        return answers.get(answerNumber - 1).isCorrect();
    }

}