package com.example.demo.service;

import com.example.demo.dto.EmanetDTO;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import org.springframework.security.core.Authentication;
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

    public List<Emanet> getBenimEmanetlerim() {
        // 1. O an giriş yapmış kullanıcının e-postasını al
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Üyeyi veritabanında bul
        Uye uye = uyeRepository.findByEposta(email)
                .orElseThrow(() -> new RuntimeException("Giriş yapan üye bulunamadı!"));

        // 3. O üyeye ait emanetleri getir
        return emanetRepository.findAllByUye(uye);
    }

    @Transactional
    public Emanet oduncVer(EmanetDTO dto) {
        Uye hedefUye;

        // 1. ÜYE BELİRLEME
        // Eğer DTO'da uyeId varsa (Admin panelinden geliyorsa)
        if (dto.getUyeId() != null) {
            hedefUye = uyeRepository.findById(dto.getUyeId())
                    .orElseThrow(() -> new RuntimeException("Üye bulunamadı!"));
        } else {
            // DTO'da yoksa (Üye kendi panelinden alıyorsa) giriş yapan kişiyi bul
            String loginOlanEposta = SecurityContextHolder.getContext().getAuthentication().getName();
            hedefUye = uyeRepository.findByEposta(loginOlanEposta)
                    .orElseThrow(() -> new RuntimeException("Giriş yapan üye bulunamadı!"));
        }

        // 2. KİTAP VE STOK KONTROLÜ
        Kitap kitap = kitapRepository.findById(dto.getKitapId())
                .orElseThrow(() -> new RuntimeException("Kitap bulunamadı!"));

        if (kitap.getAdet() <= 0) {
            throw new RuntimeException("Stok yetersiz! Kitap tükenmiş.");
        }

        // ... kitap bulundu ve stok kontrolü yapıldı ...
        if (kitap.getAdet() <= 0) {
            throw new RuntimeException("Stok yetersiz! Kitap tükenmiş.");
        }

        // --- YENİ EKLENEN KISIM: AYNI KİTAP KONTROLÜ ---
        boolean zatenVar = emanetRepository.existsByUyeAndKitapAndGercekTeslimTarihiIsNull(hedefUye, kitap);
        if (zatenVar) {
            throw new RuntimeException("Bu kitabı zaten ödünç aldınız! Önce elinizdekini iade etmelisiniz.");
        }

        // 3. EMANET NESNESİ OLUŞTURMA
        Emanet emanet = new Emanet();
        emanet.setUye(hedefUye);
        emanet.setKitap(kitap);

        // TARİHLERİ OTOMATİK AYARLA
        emanet.setEmanetTarihi(LocalDate.now());
        // Frontend'den bekleme, HER ZAMAN bugünden 15 gün sonrası
        emanet.setBeklenenTeslimTarihi(LocalDate.now().plusDays(15));

        // 4. GÖREVLİ ATAMA (Admin mi, Üye mi?)
        String aktifKullaniciEposta = SecurityContextHolder.getContext().getAuthentication().getName();
        var gorevliOptional = gorevliRepository.findByEposta(aktifKullaniciEposta);

        if (gorevliOptional.isPresent()) {
            // İşlemi yapan Admin/Görevli ise onu ata
            emanet.setGorevli(gorevliOptional.get());
        } else {
            // İşlemi yapan Üye ise, veritabanındaki varsayılan "Sistem" görevlisini (ID=1) ata.
            // DİKKAT: Veritabanında ID=1 olan bir görevli olduğundan emin ol!
            Gorevli varsayilanGorevli = gorevliRepository.findById(1)
                    .orElseThrow(() -> new RuntimeException("Sistem hatası: Varsayılan görevli (ID:1) bulunamadı."));
            emanet.setGorevli(varsayilanGorevli);
        }

        // 5. STOK DÜŞ VE KAYDET
        kitap.setAdet(kitap.getAdet() - 1);
        kitapRepository.save(kitap);

        return emanetRepository.save(emanet);
    }


    @Transactional
    public String iadeAl(Integer emanetId) {
        // 1. Emanet Kaydını Bul
        Emanet emanet = emanetRepository.findById(emanetId)
                .orElseThrow(() -> new RuntimeException("Emanet kaydı bulunamadı!"));

        // --- GÜVENLİK KONTROLÜ (YENİ EKLENEN KISIM) ---
        // Giriş yapan kişinin bilgilerini al
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String girisYapanEmail = auth.getName();

        // Bu kişi Admin/Görevli mi? (Rol kontrolü)
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LIBRARIAN") || a.getAuthority().equals("LIBRARIAN"));

        // Kural: Eğer Admin DEĞİLSE ve Emanet bu kişiye ait DEĞİLSE hata ver.
        if (!isAdmin && !emanet.getUye().getEposta().equals(girisYapanEmail)) {
            throw new RuntimeException("HATA: Yetkisiz işlem! Sadece kendi emanetlerinizi iade edebilirsiniz.");
        }
        // -----------------------------------------------

        // 2. Zaten iade edilmiş mi kontrolü
        if (emanet.getGercekTeslimTarihi() != null) {
            return "Bu kitap zaten iade edilmiş.";
        }

        // 3. İade Tarihini İşle
        emanet.setGercekTeslimTarihi(LocalDate.now());

        // 4. Stok Artır
        Kitap kitap = emanet.getKitap();
        kitap.setAdet(kitap.getAdet() + 1);
        kitapRepository.save(kitap);

        // 5. Ceza Hesaplama (Senin yazdığın orijinal mantık)
        long gecikmeGunu = ChronoUnit.DAYS.between(emanet.getBeklenenTeslimTarihi(), emanet.getGercekTeslimTarihi());
        String mesaj = "Kitap başarıyla iade edildi.";

        if (gecikmeGunu > 0) {
            BigDecimal cezaTutari = BigDecimal.valueOf(gecikmeGunu * 10.0);

            // Ceza nesnesi oluşturma
            Ceza ceza = new Ceza();
            ceza.setEmanet(emanet); // Emanet ile ilişkilendir
            ceza.setCezaMiktari(cezaTutari);
            ceza.setCezaTarihi(LocalDate.now());
            ceza.setDurum("ÖDENMEDİ"); // Durum string ise

            // Emanet üzerinden cezayı set et (Eğer ilişki varsa)
            emanet.setCeza(ceza);


            mesaj = "İade edildi. Gecikme: " + gecikmeGunu + " gün. Ceza: " + cezaTutari + " TL.";
        }

        emanetRepository.save(emanet);
        return mesaj;
    }


}