package com.example.demo.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EMANET")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Emanet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    // --------- ÜYE ---------
    @ManyToOne
    @JoinColumn(name = "uyeID", nullable = false)
    private Uye uye;

    // --------- GÖREVLİ ---------
    @ManyToOne
    @JoinColumn(name = "gorevliID", nullable = false)
    private Gorevli gorevli;

    // --------- KİTAP ---------
    @ManyToOne
    @JoinColumn(name = "kitapID", nullable = false)
    private Kitap kitap;

    // --------- TARİHLER ---------
    @Column(name = "emanetTarihi", nullable = false)
    private LocalDate emanetTarihi;

    @Column(name = "beklenenTeslimTarihi", nullable = false)
    private LocalDate beklenenTeslimTarihi;

    @Column(name = "gercekTeslimTarihi")
    private LocalDate gercekTeslimTarihi;

    // --------- CEZALAR ---------
    @OneToOne(mappedBy = "emanet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Ceza ceza;

    // --------- DEFAULT EMANET TARİHİ ---------
    @PrePersist
    public void prePersist() {
        if (emanetTarihi == null) {
            emanetTarihi = LocalDate.now();
        }
    }
}
