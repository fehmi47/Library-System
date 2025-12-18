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

    // --------- EMANET İLİŞKİSİ ---------
    @OneToOne
    @JoinColumn(name = "emanetID",referencedColumnName = "ID", nullable = false)
    private Emanet emanet;

    // --------- CEZA MİKTARI ---------
    @Column(name = "cezaMiktari", nullable = false, precision = 10, scale = 2)
    private BigDecimal cezaMiktari;

    // --------- CEZA TARİHİ ---------
    @Column(name = "cezaTarihi", nullable = false)
    private LocalDate cezaTarihi;

    // --------- ÖDEME TARİHİ ---------
    @Column(name = "odemeTarihi")
    private LocalDate odemeTarihi;

    // --------- DURUM ---------
    @Column(name = "durum", nullable = false, length = 20)
    private String durum = "Ödenmedi";

    // --------- DEFAULT DATE ---------
    @PrePersist
    public void prePersist() {
        if (cezaTarihi == null) {
            cezaTarihi = LocalDate.now();
        }
    }

    
}

