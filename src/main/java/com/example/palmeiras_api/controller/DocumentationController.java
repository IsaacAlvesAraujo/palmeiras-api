package com.example.palmeiras_api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DocumentationController {

    @GetMapping({"/docs", "/documentation"})
    public RedirectView documentation() {
        return new RedirectView("/documentation.html");
    }

    @GetMapping("/swagger")
    public RedirectView swagger() {
        return new RedirectView("/swagger-ui.html");
    }
}
