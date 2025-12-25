package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(name = "telefonNo", length = 20)
    private String telefonNo;


    @Column(name = "rol", length = 50)
    private String rol; // Görevli unvanı

    @Column(name = "sifre", length = 255, nullable = false)
    private String sifre; // Görevli şifresi

    @Column(name = "eposta", length = 100, nullable = false, unique = true)
    private String eposta; // Görevli emaili

    @OneToMany(mappedBy = "gorevli", fetch = FetchType.LAZY)
    @JsonIgnore
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

    public String getTelefonNo() {
        return telefonNo;
    }

    public void setTelefonNo(String telefonNo) {
        this.telefonNo = telefonNo;
    }
}
