package com.api.hitshot.presentation.view;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String redirectMainPage() {

        return "redirect:/home";
    }

    @GetMapping("/home")
    public String showMainPage() {

        return "home";
    }

}
