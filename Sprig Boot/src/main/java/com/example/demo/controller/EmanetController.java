package com.example.demo.controller;

import com.example.demo.dto.EmanetDTO;
import com.example.demo.entity.Emanet;
import com.example.demo.service.EmanetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emanet")
public class EmanetController {

    private final EmanetService emanetService;

    public EmanetController(EmanetService emanetService) {
        this.emanetService = emanetService;
    }

    @GetMapping("/liste")
    public List<Emanet> listele() {
        return emanetService.tumEmanetler();
    }

    @PostMapping("/odunc-al")
    public ResponseEntity<Emanet> oduncAl(@RequestBody EmanetDTO dto) {
        return ResponseEntity.ok(emanetService.oduncVer(dto));
    }

    @PutMapping("/iade-et/{id}")
    public ResponseEntity<String> iadeEt(@PathVariable Integer id) {
        return ResponseEntity.ok(emanetService.iadeAl(id));
    }

    @GetMapping("/benim-emanetlerim")
    public ResponseEntity<List<Emanet>> getBenimEmanetlerim() {
        // Tüm işi service hallediyor
        return ResponseEntity.ok(emanetService.getBenimEmanetlerim());
    }
}