package com.example.demo.controller;

import com.example.demo.entity.Kategori;
import com.example.demo.service.KategoriService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kategori")
public class KategoriController {
    private final KategoriService kategoriService;

    public KategoriController(KategoriService kategoriService){
        this.kategoriService = kategoriService;
    }

    @GetMapping("/liste")
    public List<Kategori> listele(){
        return kategoriService.tumKategorileriGoster(); // Service'deki metot adıyla eşitledik
    }

    @PostMapping("/ekle")
    public Kategori ekle(@RequestBody Kategori kategori){
        return kategoriService.kategoriKaydet(kategori);
    }

    @PutMapping("/guncelle/{id}")
    // ID tipini Integer yaptık (Veritabanıyla uyum için)
    public ResponseEntity<Kategori> guncelle(@PathVariable Integer id, @RequestBody Kategori kategori){
        Kategori guncellenenKategori = kategoriService.kategoriGuncelle(id, kategori);
        return ResponseEntity.ok(guncellenenKategori);
    }

    @DeleteMapping("/sil/{id}")
    // ID tipini Integer yaptık
    public ResponseEntity<String> sil(@PathVariable Integer id){
        kategoriService.kategoriSil(id);
        return ResponseEntity.ok("Kategori başarıyla silindi. ID: " + id);
    }
}