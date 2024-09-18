package ru.otus.hw.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import ru.otus.hw.exceptions.ContentNotFoundException;
import ru.otus.hw.exceptions.EntityNotFoundException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(ContentNotFoundException.class)
    public ModelAndView handleContentNotFoundException(ContentNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ModelAndView("err404");
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ModelAndView modelAndView = new ModelAndView("err500");
        modelAndView.addObject("message", ex.getMessage());
        return modelAndView;
    }

}
