package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Ceza işlemlerini temsil eden entity.
 * Veritabanındaki CEZA tablosuna karşılık gelir.
 */
@Entity
@Table(name = "CEZA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ceza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "emanetID",referencedColumnName = "ID", nullable = false)
    private Emanet emanet;

    @Column(name = "cezaMiktari", nullable = false, precision = 10, scale = 2)
    private BigDecimal cezaMiktari;

    @Column(name = "cezaTarihi", nullable = false)
    private LocalDate cezaTarihi;

    @Column(name = "odemeTarihi")
    private LocalDate odemeTarihi;


    @Column(name = "durum", nullable = false, length = 20)
    private String durum = "Ödenmedi";

    @PrePersist
    public void prePersist() {
        if (cezaTarihi == null) {
            cezaTarihi = LocalDate.now();
        }
    }

    public Emanet getEmanet() {
        return emanet;
    }

    public void setEmanet(Emanet emanet) {
        this.emanet = emanet;
    }

    public BigDecimal getCezaMiktari() {
        return cezaMiktari;
    }

    public void setCezaMiktari(BigDecimal cezaMiktari) {
        this.cezaMiktari = cezaMiktari;
    }

    public LocalDate getCezaTarihi() {
        return cezaTarihi;
    }

    public void setCezaTarihi(LocalDate cezaTarihi) {
        this.cezaTarihi = cezaTarihi;
    }

    public LocalDate getOdemeTarihi() {
        return odemeTarihi;
    }

    public void setOdemeTarihi(LocalDate odemeTarihi) {
        this.odemeTarihi = odemeTarihi;
    }

    public String getDurum() {
        return durum;
    }

    public void setDurum(String durum) {
        this.durum = durum;
    }

}

