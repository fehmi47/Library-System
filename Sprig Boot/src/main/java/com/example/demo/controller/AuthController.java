package com.example.demo.controller;

import com.example.demo.dto.GorevliKayitRequest;
import com.example.demo.entity.Gorevli;
import com.example.demo.entity.Uye; // Senin entity sınıfın
import com.example.demo.repository.UyeRepository; // Repository importu
import com.example.demo.service.GorevliService;
import com.example.demo.service.SmsService;
import com.example.demo.service.UyeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GorevliService gorevliService;
    private final UyeService uyeService;
    private final SmsService smsService;

    public AuthController(GorevliService gorevliService, UyeService uyeService, SmsService smsService) {
        this.gorevliService = gorevliService;
        this.uyeService = uyeService;
        this.smsService = smsService;
    }


    @PostMapping("/register/gorevli")
    public ResponseEntity<Gorevli> registerGorevli(@RequestBody GorevliKayitRequest request) {
        try {
            Gorevli yeniGorevli = gorevliService.kayitGorevli(request);
            return new ResponseEntity<>(yeniGorevli, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


// --- ÜYE KAYDI ---
    @PostMapping("/register")
    public ResponseEntity<String> registerUye(@RequestBody Uye uye) {
        try {
            // Service içindeki metodunuz şifreyi zaten hash'liyor!
            // Ayrıca email kontrolünü de orada yapıyorsanız burası tertemiz olur.
            uyeService.uyeKaydet(uye);

            return ResponseEntity.ok("Kayıt başarılı! Giriş yapabilirsiniz.");
        } catch (RuntimeException e) {
            // Service'te hata fırlatılırsa (örn: email varsa) burada yakalıyoruz
            return ResponseEntity.badRequest().body("E-posta mevcut veya hatalı girildi");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> sifremiUnuttum(@RequestParam String telefon) {
        try {
            // Service metodunu çağırıyoruz
            smsService.sifirlamaKoduGonder(telefon);

            // Hata olmazsa başarılı mesajı dön
            return ResponseEntity.ok("Doğrulama kodu gönderildi.");

        } catch (RuntimeException e) {
            // 🔥 BURASI ÖNEMLİ: Hata yakalanırsa (Kullanıcı yoksa)
            // Frontend'e temiz bir mesaj gönderiyoruz (e.getMessage() senin yazdığın mesajdır)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DTO Sınıfı
    public static class SifirlamaIstegi {
        public String telefon;
        public String kod;
        public String yeniSifre;
    }

    // 2. ADIM: ŞİFREYİ YENİLE (Düzeltilmiş Hali)
    @PostMapping("/reset-password")
    public ResponseEntity<String> sifreyiYenile(@RequestBody SifirlamaIstegi istek) {
        try {
            smsService.sifreyiSifirla(istek.telefon, istek.kod, istek.yeniSifre);
            return ResponseEntity.ok("Şifreniz başarıyla değiştirildi! Giriş yapabilirsiniz.");

        } catch (RuntimeException e) {
            // Hatalı kod girilirse veya başka bir sorunda burası çalışır
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}