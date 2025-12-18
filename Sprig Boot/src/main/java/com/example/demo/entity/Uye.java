package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
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
    private String eposta;

    @Column(name = "sifre", length = 255, nullable = false)
    private String sifre;

    @Enumerated(EnumType.STRING)
    @Column(name = "cinsiyet", columnDefinition = "ENUM('E','K','B') DEFAULT 'B'")
    private Cinsiyet cinsiyet = Cinsiyet.B;

    @OneToMany(mappedBy = "uye", fetch = FetchType.LAZY)
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
}
