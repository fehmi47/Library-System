package com.example.demo.controller;

import com.example.demo.entity.Uye;
import com.example.demo.entity.Yazar;
import com.example.demo.service.UyeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/uye")
public class UyeController {
    private final UyeService uyeService;

    public UyeController(UyeService uyeService) {
        this.uyeService = uyeService;
    }

    @GetMapping("/liste")
    public List<Uye> listele(){
        return uyeService.tumUyeleriGoster();
    }

    @PostMapping("/ekle")
    public Uye ekle(@RequestBody Uye uye){
        return uyeService.uyeKaydet(uye);
    }

    @PutMapping("/guncelle/{id}")
    public ResponseEntity<Uye> guncelle(@PathVariable Integer id, @RequestBody Uye uye){
        Uye guncellenenUye = uyeService.uyeGuncelle(id,uye);
        return ResponseEntity.ok(guncellenenUye);
    }

    @DeleteMapping("/sil/{id}")
    public ResponseEntity<String> sil(@PathVariable Integer id){
        uyeService.uyeSil(id);
        return ResponseEntity.ok("Uye başarıyla silindi. ID: " + id);
    }
}
