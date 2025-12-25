package com.example.demo.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne
    @JoinColumn(name = "uyeID", nullable = false)
    private Uye uye;

    @ManyToOne
    @JoinColumn(name = "gorevliID", nullable = false)
    private Gorevli gorevli;

    @ManyToOne
    @JoinColumn(name = "kitapID", nullable = false)
    private Kitap kitap;

    @Column(name = "emanetTarihi", nullable = false)
    private LocalDate emanetTarihi;

    @Column(name = "beklenenTeslimTarihi", nullable = false)
    private LocalDate beklenenTeslimTarihi;

    @Column(name = "gercekTeslimTarihi")
    private LocalDate gercekTeslimTarihi;

    @OneToOne(mappedBy = "emanet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private Ceza ceza;

    @PrePersist
    public void prePersist() {
        // Eğer service katmanında tarih setlenmemişse (null ise) bugün yap
        if (this.emanetTarihi == null) {
            this.emanetTarihi = LocalDate.now();
        }
        // Beklenen tarih null ise 15 gün ekle
        if (this.beklenenTeslimTarihi == null){
            this.beklenenTeslimTarihi = LocalDate.now().plusDays(15);
        }
    }

    public Uye getUye() {
        return uye;
    }

    public void setUye(Uye uye) {
        this.uye = uye;
    }

    public Gorevli getGorevli() {
        return gorevli;
    }

    public void setGorevli(Gorevli gorevli) {
        this.gorevli = gorevli;
    }

    public Kitap getKitap() {
        return kitap;
    }

    public void setKitap(Kitap kitap) {
        this.kitap = kitap;
    }

    public LocalDate getEmanetTarihi() {
        return emanetTarihi;
    }

    public void setEmanetTarihi(LocalDate emanetTarihi) {
        this.emanetTarihi = emanetTarihi;
    }

    public LocalDate getBeklenenTeslimTarihi() {
        return beklenenTeslimTarihi;
    }

    public void setBeklenenTeslimTarihi(LocalDate beklenenTeslimTarihi) {
        this.beklenenTeslimTarihi = beklenenTeslimTarihi;
    }

    public LocalDate getGercekTeslimTarihi() {
        return gercekTeslimTarihi;
    }

    public void setGercekTeslimTarihi(LocalDate gercekTeslimTarihi) {
        this.gercekTeslimTarihi = gercekTeslimTarihi;
    }

    public Ceza getCeza() {
        return ceza;
    }

    public void setCeza(Ceza ceza) {
        this.ceza = ceza;
    }
}
