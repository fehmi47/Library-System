package com.example.demo.service;

import com.example.demo.entity.Kategori;
import com.example.demo.entity.Yazar;
import com.example.demo.repository.YazarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class YazarService {
    private final YazarRepository yazarRepository;

    public YazarService(YazarRepository yazarRepository) {
        this.yazarRepository = yazarRepository;
    }

    public List<Yazar> tumYazarlariGoster(){
        return yazarRepository.findAll();
    }

    public Yazar yazarKaydet(Yazar yazar){
        return yazarRepository.save(yazar);
    }

    public void yazarSil(Integer id){
        if(yazarRepository.existsById(id)){
            yazarRepository.deleteById(id);
        }else{
            throw new RuntimeException("Silinmek istenen kategori bulunamadı! ID: " + id);
        }
    }

    public Yazar yazarGuncelle(Integer id, Yazar yeniYazar){
        Yazar mevcutYazar = yazarRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Güncellenecek kategori bulunamadı! ID: " + id));

        mevcutYazar.setAd(yeniYazar.getAd());
        mevcutYazar.setSoyad(yeniYazar.getSoyad());

        return yazarRepository.save(mevcutYazar);
    }
}
