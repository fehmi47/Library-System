package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
// Bu sınıf altındaki tüm metotlar LIBRARIAN rolü gerektirecek (SecurityConfig'den geliyor)
public class AdminTestController {

    @GetMapping("/hello")
    public String helloAdmin() {
        return "Merhaba Görevli! Yalnızca LIBRARIAN erişebilir.";
    }
}