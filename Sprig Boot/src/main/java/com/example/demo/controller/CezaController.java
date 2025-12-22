package com.example.demo.controller;

import com.example.demo.entity.Ceza;
import com.example.demo.service.CezaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ceza")
public class CezaController {
    private final CezaService cezaService;

    public CezaController(CezaService cezaService) {
        this.cezaService = cezaService;
    }

    @GetMapping("/tum-cezalar")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<Ceza> listele(){
        return cezaService.tumCezalariGoster();
    }

    @GetMapping("/benim-cezalar")
    public List<Ceza> uyeListele(){
        return cezaService.benimCezalarıGoster();
    }

    @PostMapping("/ode/{id}")
    public ResponseEntity<String> cezaOde(@PathVariable Integer id){
        return ResponseEntity.ok(cezaService.cezaOde(id));
    }

}
