package com.example.demo.service;

import com.example.demo.entity.Uye;
import com.example.demo.repository.UyeRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class SmsService {

    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";
    public static final String TWILIO_WHATSAPP_NUMBER = "";
    public final  UyeRepository uyeRepository;
    private final PasswordEncoder passwordEncoder;

    public SmsService(UyeRepository uyeRepository, PasswordEncoder passwordEncoder) {
        this.uyeRepository = uyeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void smsGonder(String aliciNo, String mesajIcerigi) {
        try {
            PhoneNumber to = new PhoneNumber("whatsapp:" + aliciNo);
            PhoneNumber from = new PhoneNumber("whatsapp:" + TWILIO_WHATSAPP_NUMBER);

            Message.creator(to, from, mesajIcerigi).create();

            System.out.println("✅ WhatsApp mesajı gönderildi: " + aliciNo);

        } catch (Exception e) {
            System.err.println("❌ WhatsApp Gönderilemedi: " + e.getMessage());
        }
    }

    @Transactional
    public void sifirlamaKoduGonder(String telefonNo) {

        Uye uye = uyeRepository.findByTelefonNo(telefonNo)
                .orElseThrow(() -> new RuntimeException("Bu telefon numarası ile kayıtlı üye bulunamadı!"));

        String kod = String.valueOf(new Random().nextInt(9000) + 1000);//rastgele kod üretiriz

        uye.setSifirlamaKodu(kod);
        uyeRepository.save(uye);


        String gonderilecekNo = telefonNo;
        if (!gonderilecekNo.startsWith("+90")) {
            gonderilecekNo = "+90" + gonderilecekNo.replaceAll("\\s+", "").replaceFirst("^0+(?!$)", "");
        }

        System.out.println("Giden Kod (Console): " + kod);
        smsGonder(gonderilecekNo, "Dogrulama Kodunuz: " + kod);
    }


    @Transactional
    public void sifreyiSifirla(String telefonNo, String girilenKod, String yeniSifre) {
        if(yeniSifre == null || yeniSifre.length() < 6) {
            throw new RuntimeException("Yeni şifreniz en az 6 karakter olmalıdır!");
        }

        Uye uye = uyeRepository.findByTelefonNo(telefonNo)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));


        if (uye.getSifirlamaKodu() == null || !uye.getSifirlamaKodu().equals(girilenKod)) {
            throw new RuntimeException("Girdiğiniz kod hatalı!");
        }

        uye.setSifre(passwordEncoder.encode(yeniSifre));

        uye.setSifirlamaKodu(null);

        uyeRepository.save(uye);
    }





}
