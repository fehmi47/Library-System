package com.example.demo.service;


import com.example.demo.entity.Kitap;
import com.example.demo.entity.Uye;
import com.example.demo.repository.UyeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UyeService {
    private final UyeRepository uyeRepository;
    private final PasswordEncoder passwordEncoder;

    public UyeService(UyeRepository uyeRepository, PasswordEncoder passwordEncoder) {
        this.uyeRepository = uyeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Uye> tumUyeleriGoster(){
        return uyeRepository.findAll();
    }

    public Uye uyeKaydet(Uye uye){
        uye.setSifre(passwordEncoder.encode(uye.getSifre()));
        return uyeRepository.save(uye);
    }

    public void uyeSil(Integer id){
        if(uyeRepository.existsById(id)){
            uyeRepository.deleteById(id);
        }else{
            throw new RuntimeException("Silinmek istenen uye bulunamadı! ID: " + id);
        }
    }

    public Uye uyeGuncelle(Integer id, Uye yeniUye) {
        Uye mevcutUye = uyeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Güncellenecek uye bulunamadı! ID: " + id));

        mevcutUye.setAd(yeniUye.getAd());
        mevcutUye.setSoyad(yeniUye.getSoyad());
        mevcutUye.setEposta(yeniUye.getEposta());
        mevcutUye.setRol(yeniUye.getRol());

        if (yeniUye.getSifre() != null && !yeniUye.getSifre().isEmpty()) {
            String hashliSifre = passwordEncoder.encode(yeniUye.getSifre());
            mevcutUye.setSifre(hashliSifre);
        }

        return uyeRepository.save(mevcutUye);
    }
}
