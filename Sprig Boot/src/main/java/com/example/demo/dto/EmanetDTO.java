package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmanetDTO {
    private Integer kitapId;
    private Integer uyeId;
    private LocalDate emanetTarihi;
    private LocalDate gerçekTeslimTarihi;

    // Lombok @Data kullanıyorsan getter/setter'lar otomatik oluşur.
    // Ama manuel eklemek istersen aşağıdakileri kullanabilirsin:

    public Integer getKitapId() {
        return kitapId;
    }

    public void setKitapId(Integer kitapId) {
        this.kitapId = kitapId;
    }

    public Integer getUyeId() {
        return uyeId;
    }

    public void setUyeId(Integer uyeId) {
        this.uyeId = uyeId;
    }

    public LocalDate getGerçekTeslimTarihi() {
        return gerçekTeslimTarihi;
    }

    public void setGerçekTeslimTarihi(LocalDate iadeTarihi) {
        this.gerçekTeslimTarihi = iadeTarihi;
    }

    public LocalDate getEmanetTarihi() {
        return emanetTarihi;
    }

    public void setEmanetTarihi(LocalDate emanetTarihi) {
        this.emanetTarihi = emanetTarihi;
    }
}