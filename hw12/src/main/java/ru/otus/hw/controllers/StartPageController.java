package ru.otus.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartPageController {

    @GetMapping("/")
    public String getStartPage() {
        return "redirect:/author";
    }

}
