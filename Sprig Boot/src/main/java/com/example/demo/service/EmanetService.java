package com.example.demo.service;

import com.example.demo.dto.EmanetDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class EmanetService {

    private final EmanetRepository emanetRepository;
    private final KitapRepository kitapRepository;
    private final UyeRepository uyeRepository;
    private final GorevliRepository gorevliRepository;

    public EmanetService(EmanetRepository emanetRepository, KitapRepository kitapRepository,
                         UyeRepository uyeRepository, GorevliRepository gorevliRepository) {
        this.emanetRepository = emanetRepository;
        this.kitapRepository = kitapRepository;
        this.uyeRepository = uyeRepository;
        this.gorevliRepository = gorevliRepository;
    }

    public List<Emanet> tumEmanetler() {
        return emanetRepository.findAll();
    }

    @Transactional
    public Emanet oduncVer(EmanetDTO dto) {
        Uye hedefUye;
        // 1. Üyeyi Belirle (Admin bir üyeye mi veriyor yoksa üye kendisi mi alıyor?)
        if (dto.getUyeId() != null) {
            hedefUye = uyeRepository.findById(dto.getUyeId())
                    .orElseThrow(() -> new RuntimeException("Üye bulunamadı!"));
        } else {
            String loginOlanEposta = SecurityContextHolder.getContext().getAuthentication().getName();
            hedefUye = uyeRepository.findByEposta(loginOlanEposta)
                    .orElseThrow(() -> new RuntimeException("Giriş yapan üye bulunamadı!"));
        }

        // 2. Kitap ve Stok Kontrolü
        Kitap kitap = kitapRepository.findById(dto.getKitapId())
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı!"));
        if (kitap.getAdet() <= 0) throw new RuntimeException("Stok yetersiz!");

        // 3. Emanet Oluştur
        Emanet emanet = new Emanet();
        emanet.setUye(hedefUye);
        emanet.setKitap(kitap);

        // TARİH: Her zaman bugün (Senin istediğin gibi)
        emanet.setEmanetTarihi(LocalDate.now());

        // BEKLENEN İADE: DTO'dan gelen tarih (Gelecekteki bir gün)
        emanet.setBeklenenTeslimTarihi(dto.getGerçekTeslimTarihi());

        // GÖREVLİ: O an giriş yapan admin/görevli kimse o setlenir
        String aktifGorevliEposta = SecurityContextHolder.getContext().getAuthentication().getName();
        Gorevli gorevli = gorevliRepository.findByEposta(aktifGorevliEposta)
                .orElseThrow(() -> new RuntimeException("Görevli bulunamadı!"));
        emanet.setGorevli(gorevli);

        // 4. Stok Düş ve Kaydet
        kitap.setAdet(kitap.getAdet() - 1);
        kitapRepository.save(kitap);

        return emanetRepository.save(emanet);
    }

    @Transactional
    public String iadeAl(Integer emanetId) {
        Emanet emanet = emanetRepository.findById(emanetId)
                .orElseThrow(() -> new RuntimeException("Emanet kaydı bulunamadı!"));

        if (emanet.getGercekTeslimTarihi() != null) {
            return "Bu kitap zaten iade edilmiş.";
        }

        emanet.setGercekTeslimTarihi(LocalDate.now());
        Kitap kitap = emanet.getKitap();
        kitap.setAdet(kitap.getAdet() + 1);
        kitapRepository.save(kitap);

        long gecikmeGunu = ChronoUnit.DAYS.between(emanet.getBeklenenTeslimTarihi(), emanet.getGercekTeslimTarihi());
        String mesaj = "Kitap başarıyla iade edildi.";

        if (gecikmeGunu > 0) {
            BigDecimal cezaTutari = BigDecimal.valueOf(gecikmeGunu * 10.0);
            Ceza ceza = new Ceza();
            ceza.setEmanet(emanet);
            ceza.setCezaMiktari(cezaTutari);
            ceza.setCezaTarihi(LocalDate.now());
            ceza.setDurum("Ödenmedi");
            emanet.setCeza(ceza);
            mesaj = "İade edildi. Gecikme: " + gecikmeGunu + " gün. Ceza: " + cezaTutari + " TL.";
        }

        emanetRepository.save(emanet);
        return mesaj;
    }
}