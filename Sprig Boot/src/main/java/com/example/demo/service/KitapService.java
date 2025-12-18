package com.example.demo.service;

import com.example.demo.entity.Kitap;
import com.example.demo.repository.KitapRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KitapService {
    private final KitapRepository kitapRepository;

    public KitapService(KitapRepository kitapRepository) {
        this.kitapRepository = kitapRepository;
    }

    public List<Kitap> tumKitaplariGoster(){
        return kitapRepository.findAll();
    }

    public Kitap kitapKaydet(Kitap kitap){
        return kitapRepository.save(kitap);
    }

    public void kitapSil(Integer id){
        if(kitapRepository.existsById(id)){
            kitapRepository.deleteById(id);
        }else{
            throw new RuntimeException("Silinmek istenen kitap bulunamadı! ID: " + id);
        }
    }

    public Kitap kitapGuncelle(Integer id, Kitap yeniKitap){
        Kitap mevcutKitap = kitapRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek kitap bulunamadı! ID: " + id));

        mevcutKitap.setAd(yeniKitap.getAd());
        mevcutKitap.setAdet(yeniKitap.getAdet());
        mevcutKitap.setKategori(yeniKitap.getKategori());
        mevcutKitap.setYazar(yeniKitap.getYazar());
        mevcutKitap.setSayfaSayisi(yeniKitap.getSayfaSayisi());
        mevcutKitap.setYayinTarihi(yeniKitap.getYayinTarihi());

        return kitapRepository.save(mevcutKitap);
    }
}
