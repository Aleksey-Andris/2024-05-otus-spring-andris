package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.exceptions.AnswerReadException;
import ru.otus.hw.exceptions.QuestionReadException;


@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestService testService;

    private final StudentService studentService;

    private final ResultService resultService;

    private final LocalizedIOService ioService;

    @Override
    public void run() {
        var student = studentService.determineCurrentStudent();
        TestResult testResult;
        try {
            testResult = testService.executeTestFor(student);
        } catch (QuestionReadException e) {
            ioService.printLineLocalized("TestRunnerService.exception.question.read");
            return;
        } catch (AnswerReadException e) {
            ioService.printLineLocalized("TestRunnerService.exception.answer.read");
            return;
        }
        resultService.showResult(testResult);
    }

}