package com.virtualbooks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Ruta principal (página de inicio)
    @GetMapping("/")
    public String index() {
        return "index"; // templates/index.html
    }

    // Ruta "about" (página de información)
    @GetMapping("/about")
    public String about() {
        return "about"; // templates/about.html
    }
}
