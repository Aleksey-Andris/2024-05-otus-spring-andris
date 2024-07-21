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

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
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
        ioService.printFormattedLine("Question %d: %s", questionNumber, question.text());
    }

    private void printAnswers(List<Answer> answers) {
        ioService.printLine("Answer options:");
        int answerCounter = 0;
        for (Answer answer : answers) {
            answerCounter++;
            ioService.printFormattedLine("%d) %s", answerCounter, answer.text());
        }
    }

    private int readAnswer(int answersSize) {
        String prompt = String.format("%nPlease enter the correct answer number from %s to %s", 1, answersSize);
        String errMessage = String.format("Not a valid value. The value must be a number from %s to %s!",
                1, answersSize);
        try {
            return ioService.readIntForRangeWithPrompt(1, answersSize, prompt, errMessage);
        } catch (IllegalArgumentException e) {
            throw new AnswerReadException("Wrong answer format", e);
        }
    }

    private boolean validateAnswer(int answerNumber, List<Answer> answers) {
        return answers.get(answerNumber - 1).isCorrect();
    }

}
