package com.example.demo.controller;


import com.example.demo.entity.Gorevli;
import com.example.demo.service.GorevliService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;


@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final GorevliService gorevliService;

    public AdminController(GorevliService gorevliService) {
        this.gorevliService = gorevliService;
    }

    @GetMapping("/check")
    @PreAuthorize("hasRole('LIBRARIAN')") // Sadece ROLE_LIBRARIAN yetkisi olanlar girebilir
    public ResponseEntity<String> checkAdmin() {
        return ResponseEntity.ok("Onaylandı");
    }
    // AdminController.java veya GorevliController.java
    @GetMapping("/gorevli-liste")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<Gorevli> gorevliListele() {
        return gorevliService.tumGorevlileriGoster(); // Service içinde bu metodun olduğunu varsayıyoruz
    }
}


