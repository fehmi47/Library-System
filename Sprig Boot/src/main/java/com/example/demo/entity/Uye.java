package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "UYE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Uye {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "ad", length = 100, nullable = false)
    private String ad;

    @Column(name = "soyad", length = 100, nullable = false)
    private String soyad;

    @Column(name = "telefonNo", length = 20)
    private String telefonNo;


    @Column(name = "eposta", length = 100, unique = true, nullable = false)
    @NotBlank(message = "E-posta boş olamaz") // Boş gelmesini engeller
    @Email(message = "Lütfen geçerli bir e-posta adresi giriniz")
    private String eposta;

    @Column(name = "sifre", length = 255, nullable = false)
    private String sifre;

    @Column(name = "rol", length = 50)
    private String rol;

    @Column(name = "sifirlamaKodu", length = 255)
    private String sifirlamaKodu;

    @OneToMany(mappedBy = "uye", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Emanet> emanetler = new ArrayList<>();

    public String getSifre() {
        return this.sifre;
    }

    public String getEposta() {
        return this.eposta;
    }

    public void setSifre(String hashedPassword) {
        if(hashedPassword != null && !hashedPassword.trim().isEmpty()){
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

    public String getTelefonNo() {
        return telefonNo;
    }

    public void setTelefonNo(String telefonNo) {
        this.telefonNo = telefonNo;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }


    public String getSifirlamaKodu() {
        return sifirlamaKodu;
    }

    public void setSifirlamaKodu(String sifirlamaKodu) {
        this.sifirlamaKodu = sifirlamaKodu;
    }
}
