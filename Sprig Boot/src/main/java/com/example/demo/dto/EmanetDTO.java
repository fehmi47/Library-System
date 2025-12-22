package com.example.demo.dto;

import lombok.Data;

@Data
public class EmanetDTO {
    private Integer kitapId; // Üye sadece hangi kitabı istediğini söyler

    public Integer getKitapId() {
        return kitapId;
    }

    public void setKitapId(Integer kitapId) {
        this.kitapId = kitapId;
    }
}