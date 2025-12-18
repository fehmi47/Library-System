package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "GOREVLI")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gorevli {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ad", length = 100, nullable = false)
    private String ad; // Görevli adı

    @Column(name = "soyad", length = 100, nullable = false)
    private String soyad; // Görevli soyadı

    @Column(name = "unvan", length = 100)
    private String unvan; // Görevli unvanı

    @Column(name = "sifre", length = 255, nullable = false)
    private String sifre; // Görevli şifresi

    @Column(name = "eposta", length = 100, nullable = false, unique = true)
    private String eposta; // Görevli emaili

    @OneToMany(mappedBy = "gorevli", fetch = FetchType.LAZY)
    private List<Emanet> emanetler = new ArrayList<>();

    public String getSifre() {
        return this.sifre;
    }

    public String getEposta() {
        return this.eposta;
    }

    public void setSifre(String hashedPassword) {
        if (hashedPassword != null && !hashedPassword.trim().isEmpty()) {
            this.sifre = hashedPassword;
        } else {
            throw new IllegalArgumentException("Şifre boş olamaz");
        }
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public String getUnvan() {
        return unvan;
    }

    public void setUnvan(String unvan) {
        this.unvan = unvan;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }
}
