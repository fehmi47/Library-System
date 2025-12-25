package com.example.demo.controller;

import com.example.demo.dto.GorevliKayitRequest;
import com.example.demo.entity.Gorevli;
import com.example.demo.service.GorevliService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth") // Güvenlik ayarlarında bu yolu public yapmıştık.
public class AuthController {

    private final GorevliService gorevliService;

    public AuthController(GorevliService gorevliService) {
        this.gorevliService = gorevliService;
    }

    @PostMapping("/register/gorevli")
    public ResponseEntity<Gorevli> registerGorevli(@RequestBody GorevliKayitRequest request) {
        try {
            Gorevli yeniGorevli = gorevliService.kayitGorevli(request);
            return new ResponseEntity<>(yeniGorevli, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
