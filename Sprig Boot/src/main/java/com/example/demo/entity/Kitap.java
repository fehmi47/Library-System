package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "KITAP")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kitap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id; // DB INT ile uyumlu

    @Column(name = "ad", nullable = false, length = 255)
    private String ad;

    @Column(name = "sayfasayisi")
    private Integer sayfaSayisi;

    @Column(name = "yayinTarihi")
    private LocalDate yayinTarihi;

    @Column(name = "adet", nullable = false)
    private Integer adet = 0;

    // --------- KATEGORI ---------
    @ManyToOne
    @JoinColumn(name = "kategoriID", nullable = false)
    private Kategori kategori;

    @ManyToOne
    @JoinColumn(name  = "yazarID",nullable = false)
    private Yazar yazar;

    // --------- EMANETLER (1:N) ---------
    @OneToMany(mappedBy = "kitap", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Emanet> emanetler = new ArrayList<>();

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public Integer getSayfaSayisi() {
        return sayfaSayisi;
    }

    public void setSayfaSayisi(Integer sayfaSayisi) {
        this.sayfaSayisi = sayfaSayisi;
    }

    public LocalDate getYayinTarihi() {
        return yayinTarihi;
    }

    public void setYayinTarihi(LocalDate yayinTarihi) {
        this.yayinTarihi = yayinTarihi;
    }

    public Integer getAdet() {
        return adet;
    }

    public void setAdet(Integer adet) {
        this.adet = adet;
    }

    public Kategori getKategori() {
        return kategori;
    }

    public void setKategori(Kategori kategori) {
        this.kategori = kategori;
    }

    public Yazar getYazar() {
        return yazar;
    }

    public void setYazar(Yazar yazar) {
        this.yazar = yazar;
    }
}
