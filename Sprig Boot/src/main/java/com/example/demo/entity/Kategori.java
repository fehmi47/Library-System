package com.example.demo.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kitap kategorilerini temsil eden entity.
 * Veritabanındaki 'KATEGORI' tablosuna eşlenir.
 */
@Entity
@Table(name = "KATEGORI")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kategori {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id; // DB INT ile uyumlu

    @Column(name = "ad", length = 50, unique = true, nullable = false)
    @JsonProperty("ad")
    private String ad;

    @OneToMany(mappedBy = "kategori", cascade = CascadeType.ALL, fetch = FetchType.LAZY) private List<Kitap> kitaplar = new ArrayList<>();
    @JsonIgnore
    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
