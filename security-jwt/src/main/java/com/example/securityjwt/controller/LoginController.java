package com.example.securityjwt.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/formLogin")
    public String formLogin() {
        return "loginForm";
    }
}
