package com.example.demo.service;

import com.example.demo.entity.Kategori;
import com.example.demo.repository.KategoriRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KategoriService {
    private final KategoriRepository kategoriRepository;

    public KategoriService(KategoriRepository kategoriRepository) {
        this.kategoriRepository = kategoriRepository;
    }

    public List<Kategori> tumKategorileriGoster(){
        return kategoriRepository.findAll();
    }

    public Kategori kategoriKaydet(Kategori kategori){
        return  kategoriRepository.save(kategori);
    }

    public void kategoriSil(Integer id){
        if (kategoriRepository.existsById(id)){
            kategoriRepository.deleteById(id);
        }else{
            throw new RuntimeException("Silinmek istenen kategori bulunamadı! ID: " + id);
        }
    }

    public Kategori kategoriGuncelle(Integer id,Kategori yeniKategori){
        Kategori mevcutKategori = kategoriRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Güncellenecek kategori bulunamadı! ID: " + id));

        mevcutKategori.setAd(yeniKategori.getAd());

        return kategoriRepository.save(mevcutKategori);
    }
}
