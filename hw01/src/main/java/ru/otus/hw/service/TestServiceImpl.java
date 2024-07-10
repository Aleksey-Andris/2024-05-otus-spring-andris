package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printFormattedLine("%nPlease answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        int questionCounter = 0;
        for (Question question : questions) {
            questionCounter++;
            ioService.printFormattedLine("Question %d: %s", questionCounter, question.text());
            printAnswer(question.answers());
            ioService.printLine("");
        }
    }

    private void printAnswer(List<Answer> answers) {
        ioService.printLine("Answer options:");
        int answerCounter = 0;
        for (Answer answer : answers) {
            answerCounter++;
            ioService.printFormattedLine("%d) %s", answerCounter, answer.text());
        }
    }

}
