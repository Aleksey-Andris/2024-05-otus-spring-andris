package ru.otus.hw.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
        var modelAndView = new ModelAndView("err404");
        modelAndView.setStatus(HttpStatus.NOT_FOUND);
        return modelAndView;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        ModelAndView modelAndView = new ModelAndView("err500");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDeniedException(AccessDeniedException ex) {
        ModelAndView modelAndView = new ModelAndView("err403");
        modelAndView.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        return modelAndView;
    }

}
