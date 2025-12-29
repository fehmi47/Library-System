 Kütüphane Yönetim Sistemi

Bu proje, kütüphane süreçlerini dijitalleştirmek, kitap takibini kolaylaştırmak ve üye-kütüphane etkileşimini hızlandırmak amacıyla geliştirilmiş kapsamlı bir web uygulamasıdır. **Spring Boot** altyapısı ve **Vanilla JavaScript** kullanılarak Full-Stack olarak tasarlanmıştır.

 Proje Hakkında

Kütüphane Yönetim Sistemi; yöneticilerin (kütüphanecilerin) envanter ve üye yönetimini yapabildiği, üyelerin ise kitapları inceleyip ödünç alma durumlarını ve cezalarını takip edebildiği rol bazlı bir otomasyon sistemidir. Proje, güvenli kimlik doğrulama ve **Twilio API** entegrasyonu ile SMS/WhatsApp üzerinden şifre sıfırlama gibi modern özellikler barındırır.

Temel Özellikler

Güvenlik ve Kimlik Doğrulama
Rol Bazlı Yetkilendirme (RBAC):** Yönetici (Librarian) ve Üye (Member) için ayrıştırılmış paneller.
Spring Security: Güvenli giriş (Login) ve kayıt (Register) işlemleri.
Şifre Sıfırlama: Twilio API entegrasyonu ile SMS veya WhatsApp üzerinden doğrulama kodu gönderimi.
Güvenli Depolama:BCrypt ile şifrelerin hashlenerek veritabanında saklanması.

  Kitap ve Envanter Yönetimi
 Kitap Ekleme, Silme ve Güncelleme (Sadece Yönetici).
 Kategori ve Yazar Yönetimi.
 Dinamik kitap arama ve listeleme.
 Stok takibi.

  Ödünç ve İade Sistemi
 Kitap ödünç alma ve iade etme işlemleri.
 Teslim tarihi geçen kitapların otomatik tespiti.
 Geçmiş emanet işlemlerinin listelenmesi.

 Ceza Sistemi
 Gecikmiş kitaplar için otomatik ceza hesaplama.
 Üye panelinden ceza sorgulama ve ödeme (Simülasyon).

 Kullanıcı Paneli (Frontend)
Yönetici Paneli: Tüm üyeleri, kitapları ve cezaları tek ekrandan yönetme.
Üye Paneli: Kişisel ödünç geçmişini, mevcut cezaları ve kitapları görüntüleme.
Responsive (Mobil Uyumlu) tasarım.

Kullanılan Teknolojiler

Backend: Java, Spring Boot, Spring Security, Spring Data JPA, Hibernate
Frontend HTML5, CSS3, JavaScript (Vanilla JS - Frameworksüz), Fetch API
Veritabanı: MySQL (veya PostgreSQL)
API Entegrasyonu: Twilio (SMS & WhatsApp Services)
Araçlar: Maven, Postman, IntelliJ IDEA

