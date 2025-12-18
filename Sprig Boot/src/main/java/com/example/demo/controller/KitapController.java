package com.example.demo.controller;

import com.example.demo.entity.Kategori;
import com.example.demo.entity.Kitap;
import com.example.demo.service.KitapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitap")
public class KitapController {
    private final KitapService kitapService;

    public KitapController(KitapService kitapService) {
        this.kitapService = kitapService;
    }

    @GetMapping("/liste")
    public List<Kitap> listele(){
        return kitapService.tumKitaplariGoster();
    }

    @PostMapping("/ekle")
    public Kitap ekle(@RequestBody Kitap kitap){
        return  kitapService.kitapKaydet(kitap);
    }

    @PutMapping("/guncelle/{id}")
    public ResponseEntity<Kitap> guncelle(@PathVariable Integer id,@RequestBody Kitap kitap){
        Kitap guncellenenKitap = kitapService.kitapGuncelle(id, kitap);
        return ResponseEntity.ok(guncellenenKitap);
    }

    @DeleteMapping("/sil/{id}")
    public ResponseEntity<String> sil(@PathVariable Integer id){
        kitapService.kitapSil(id);
        return ResponseEntity.ok("Kitap başarıyla silindi. ID: " + id);
    }


}
