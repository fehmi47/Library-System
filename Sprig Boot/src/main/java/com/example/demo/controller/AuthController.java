package com.example.demo.controller;

import com.example.demo.dto.GorevliKayitRequest;
import com.example.demo.entity.Gorevli;
import com.example.demo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Güvenlik ayarlarında bu yolu public yapmıştık.
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/gorevli")
    public ResponseEntity<Gorevli> registerGorevli(@RequestBody GorevliKayitRequest request) {
        try {
            Gorevli yeniGorevli = authService.kayitGorevli(request);
            return new ResponseEntity<>(yeniGorevli, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
