package com.example.demo.controller;

import com.example.demo.entity.Yazar;
import com.example.demo.service.YazarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/yazar")
public class YazarController {
    private final YazarService yazarService;

    public YazarController(YazarService yazarService) {
        this.yazarService = yazarService;
    }

    @GetMapping("/liste")
    public List<Yazar> listele(){
        return yazarService.tumYazarlariGoster();
    }

    @PostMapping("/ekle")
    public Yazar ekle(@RequestBody Yazar yazar){
        return yazarService.yazarKaydet(yazar);
    }

    @PutMapping("/guncelle/{id}")
    public ResponseEntity<Yazar> guncelle(@PathVariable Integer id, @RequestBody Yazar yazar){
        Yazar guncelleneYazar = yazarService.yazarGuncelle(id,yazar);
        return ResponseEntity.ok(guncelleneYazar);
    }

    @DeleteMapping("/sil/{id}")
    public ResponseEntity<String> sil(@PathVariable Integer id){
        yazarService.yazarSil(id);
        return ResponseEntity.ok("Kategori başarıyla silindi. ID: " + id);
    }
}
