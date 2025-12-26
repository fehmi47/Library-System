/**
 * Kütüphane Yönetim Sistemi - Merkezi JavaScript (app.js)
 */

const getAuth = () => sessionStorage.getItem("auth");

async function login() {
    const eposta = document.getElementById("username").value;
    const sifre = document.getElementById("password").value;

    if (!eposta || !sifre) {
        alert("Lütfen tüm alanları doldurun!");
        return;
    }

    // --- DÜZELTME BURADA YAPILDI ---
    // Türkçe karakterleri (UTF-8) önce encodeURIComponent ile kodluyoruz,
    // sonra unescape ile binary string'e çevirip btoa ile Base64 yapıyoruz.
    // Bu sayede "şifre" gibi kelimeler hata vermez.
    const authHeader = 'Basic ' + btoa(unescape(encodeURIComponent(eposta + ":" + sifre)));

    try {
        // 1. ADIM: Genel Giriş Kontrolü
        const response = await fetch('/api/kitap/liste', {
            method: 'GET',
            headers: { 'Authorization': authHeader }
        });

        if (response.status === 401) {
            alert("❌ Hatalı e-posta veya şifre!");
            return;
        }

        if (response.ok) {
            sessionStorage.setItem("auth", authHeader);

            // 2. ADIM: Yetki Kontrolü
            const adminCheck = await fetch('/api/admin/check', {
                headers: { 'Authorization': authHeader }
            });

            if (adminCheck.status === 200) {
                window.location.replace("admin.html");
            }
            else if (adminCheck.status === 403 || adminCheck.status === 200) {
                window.location.replace("uye.html");
            }
            else {
                // Her ihtimale karşı üyeye yönlendir
                window.location.replace("uye.html");
            }
        } else {
            alert("Giriş yapılamadı. Sunucu hatası.");
        }
    } catch (error) {
        console.error("Bağlantı Hatası:", error);
        alert("Sunucuya bağlanılamadı.");
    }
}

// Dinamik Form ID Takibi (Hangi öğeyi güncellediğimizi bilmek için)
let editKitapId = null;
let editKatId = null;
let editYazId = null;
let editKullaniciId = null;
let editKullaniciTipi = null;

// --- 1. KİTAP YÖNETİMİ ---

// Düzenle butonuna basınca verileri form kutularına doldurur
function kDuzenleModu(id, ad, sayfa, adet, katId, yazId, tarih) {
    editKitapId = id;
    document.getElementById("kAd").value = ad;
    document.getElementById("kSayfa").value = sayfa;
    document.getElementById("kAdet").value = adet;
    document.getElementById("kKategoriId").value = katId;
    document.getElementById("kYazarId").value = yazId;
    document.getElementById("kTarih").value = tarih;

    document.getElementById("kBaslik").innerText = "📝 Kitap Düzenle (ID: " + id + ")";
    document.getElementById("kBtn").innerText = "🔄 Güncelle";
    document.getElementById("kBtn").style.background = "#f39c12";
    document.getElementById("kIptal").style.display = "block";
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function kFormSifirla() {
    editKitapId = null;
    document.getElementById("kAd").value = "";
    document.getElementById("kSayfa").value = "";
    document.getElementById("kAdet").value = "";
    document.getElementById("kKategoriId").value = "";
    document.getElementById("kYazarId").value = "";
    document.getElementById("kTarih").value = "";

    document.getElementById("kBaslik").innerText = "📚 Kitap Yönetimi (Ekleme Modu)";
    document.getElementById("kBtn").innerText = "💾 Kaydet";
    document.getElementById("kBtn").style.background = "#1abc9c";
    document.getElementById("kIptal").style.display = "none";
}

async function adminKitapKaydet() {
    const ad = document.getElementById("kAd").value;
    if (isInvalid(ad)) { alert("Kitap adı boş olamaz!"); return; }

    const data = {
        ad: ad,
        sayfaSayisi: parseInt(document.getElementById("kSayfa").value),
        adet: parseInt(document.getElementById("kAdet").value),
        yayinTarihi: document.getElementById("kTarih").value,
        kategori: { id: parseInt(document.getElementById("kKategoriId").value) },
        yazar: { id: parseInt(document.getElementById("kYazarId").value) }
    };

    const method = editKitapId ? 'PUT' : 'POST';
    const url = editKitapId ? `/api/kitap/guncelle/${editKitapId}` : '/api/kitap/ekle';

    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Authorization': getAuth(), 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });
        if (res.ok) {
            alert(editKitapId ? "Kitap güncellendi!" : "Kitap eklendi!");
            kFormSifirla();
            tumKitaplariGetir();
        }
    } catch (e) { alert("Bağlantı hatası!"); }
}

async function tumKitaplariGetir() {
    try {
        const res = await fetch('/api/kitap/liste', { headers: { 'Authorization': getAuth() } });
        const data = await res.json();
        const body = document.getElementById("kitapTableBody");
        body.innerHTML = data.map(k => `
            <tr>
                <td>${k.id}</td>
                <td>${k.ad}</td>
                <td>${k.yazar ? k.yazar.ad + ' ' + k.yazar.soyad : '-'}</td>
                <td>${k.kategori ? k.kategori.ad : '-'}</td>
                <td>${k.adet}</td>
                <td>
                    <button onclick="kDuzenleModu(${k.id}, '${k.ad}', ${k.sayfaSayisi}, ${k.adet}, ${k.kategori?.id}, ${k.yazar?.id}, '${k.yayinTarihi}')" class="btn-sm" style="background:#f39c12; color:white;">Düzenle</button>
                    <button onclick="kSil(${k.id})" class="btn-sm" style="background:#e74c3c; color:white;">Sil</button>
                </td>
            </tr>`).join('');
    } catch (e) { console.error("Kitaplar yüklenemedi"); }
}

async function kSil(id) {
    if (confirm("Bu kitabı silmek istediğinize emin misiniz?")) {
        await fetch(`/api/kitap/sil/${id}`, { method: 'DELETE', headers: { 'Authorization': getAuth() } });
        tumKitaplariGetir();
    }
}

// --- 2. KATEGORİ YÖNETİMİ ---

function katDuzenleModu(id, ad) {
    editKatId = id;
    document.getElementById("katAd").value = ad;
    document.getElementById("katBaslik").innerText = "📝 Kategori Düzenle (ID: " + id + ")";
    document.getElementById("katBtn").innerText = "🔄 Güncelle";
    document.getElementById("katBtn").style.background = "#f39c12";
    document.getElementById("katIptal").style.display = "block";
}

function katFormSifirla() {
    editKatId = null;
    document.getElementById("katAd").value = "";
    document.getElementById("katBaslik").innerText = "📁 Kategori Yönetimi (Ekleme Modu)";
    document.getElementById("katBtn").innerText = "💾 Kaydet";
    document.getElementById("katBtn").style.background = "#1abc9c";
    document.getElementById("katIptal").style.display = "none";
}

async function adminKategoriKaydet() {
    const ad = document.getElementById("katAd").value;
    if (isInvalid(ad)) { alert("Kategori adı boş olamaz!"); return; }

    const method = editKatId ? 'PUT' : 'POST';
    const url = editKatId ? `/api/kategori/guncelle/${editKatId}` : '/api/kategori/ekle';

    const res = await fetch(url, {
        method: method,
        headers: { 'Authorization': getAuth(), 'Content-Type': 'application/json' },
        body: JSON.stringify({ ad: ad })
    });
    if (res.ok) { katFormSifirla(); tumKategorileriGetir(); }
}

async function tumKategorileriGetir() {
    const res = await fetch('/api/kategori/liste', { headers: { 'Authorization': getAuth() } });
    const data = await res.json();
    document.getElementById("kategoriTableBody").innerHTML = data.map(c => `
        <tr>
            <td>${c.id}</td><td>${c.ad}</td>
            <td>
                <button onclick="katDuzenleModu(${c.id}, '${c.ad}')" class="btn-sm" style="background:#f39c12; color:white;">Düzenle</button>
                <button onclick="katSil(${c.id})" class="btn-sm" style="background:#e74c3c; color:white;">Sil</button>
            </td>
        </tr>`).join('');
}

async function katSil(id) {
    if (confirm("Kategoriyi silmek istediğinize emin misiniz?")) {
        const res = await fetch(`/api/kategori/sil/${id}`, { method: 'DELETE', headers: { 'Authorization': getAuth() } });
        if (!res.ok) alert("Bu kategoriye bağlı kitaplar olduğu için silinemedi.");
        tumKategorileriGetir();
    }
}

// --- 3. YAZAR YÖNETİMİ ---

function yazDuzenleModu(id, ad, soyad) {
    editYazId = id;
    document.getElementById("yazAd").value = ad;
    document.getElementById("yazSoyad").value = soyad;
    document.getElementById("yazBaslik").innerText = "📝 Yazar Düzenle (ID: " + id + ")";
    document.getElementById("yazBtn").innerText = "🔄 Güncelle";
    document.getElementById("yazBtn").style.background = "#f39c12";
    document.getElementById("yazIptal").style.display = "block";
}

function yazFormSifirla() {
    editYazId = null;
    document.getElementById("yazAd").value = "";
    document.getElementById("yazSoyad").value = "";
    document.getElementById("yazBaslik").innerText = "✍️ Yazar Yönetimi (Ekleme Modu)";
    document.getElementById("yazBtn").innerText = "💾 Kaydet";
    document.getElementById("yazBtn").style.background = "#1abc9c";
    document.getElementById("yazIptal").style.display = "none";
}

async function adminYazarKaydet() {
    const ad = document.getElementById("yazAd").value;
    const soyad = document.getElementById("yazSoyad").value;
    if (isInvalid(ad) || isInvalid(soyad)) { alert("Ad ve soyad boş olamaz!"); return; }

    const method = editYazId ? 'PUT' : 'POST';
    const url = editYazId ? `/api/yazar/guncelle/${editYazId}` : '/api/yazar/ekle';

    const res = await fetch(url, {
        method: method,
        headers: { 'Authorization': getAuth(), 'Content-Type': 'application/json' },
        body: JSON.stringify({ ad: ad, soyad: soyad })
    });
    if (res.ok) { yazFormSifirla(); tumYazarlariGetir(); }
}

async function tumYazarlariGetir() {
    const res = await fetch('/api/yazar/liste', { headers: { 'Authorization': getAuth() } });
    const data = await res.json();
    document.getElementById("yazarTableBody").innerHTML = data.map(y => `
        <tr>
            <td>${y.id}</td><td>${y.ad} ${y.soyad}</td>
            <td>
                <button onclick="yazDuzenleModu(${y.id}, '${y.ad}', '${y.soyad}')" class="btn-sm" style="background:#f39c12; color:white;">Düzenle</button>
                <button onclick="yazarSil(${y.id})" class="btn-sm" style="background:#e74c3c; color:white;">Sil</button>
            </td>
        </tr>`).join('');
}

async function yazarSil(id) {
    if (confirm("Yazarı silmek istediğinize emin misiniz?")) {
        await fetch(`/api/yazar/sil/${id}`, { method: 'DELETE', headers: { 'Authorization': getAuth() } });
        tumYazarlariGetir();
    }
}



 // --- KULLANICI / GÖREVLİ KAYDETME VE GÜNCELLEME ---
 async function adminKullaniciKaydet() {
     const ad = document.getElementById("uAd").value;
     const soyad = document.getElementById("uSoyad").value;
     const eposta = document.getElementById("uEposta").value;
     const telefon = document.getElementById("uTelefon").value;
     const sifre = document.getElementById("uSifre").value;
     const rol = document.getElementById("uRol").value;

     // TÜRKÇE HATA KONTROLÜ
     if (!ad || !eposta) {
         alert("Lütfen Ad ve E-posta alanlarını doldurunuz!");
         return;
     }

     const data = { ad, soyad, eposta, telefon, sifre, rol };

     let url = "";
     let method = editKullaniciId ? "PUT" : "POST";

     // Dinamik URL Belirleme
     if (editKullaniciId) {
         url = (rol === "LIBRARIAN")
             ? `/api/admin/gorevli-guncelle/${editKullaniciId}`
             : `/api/uye/guncelle/${editKullaniciId}`;
     } else {
         url = (rol === "LIBRARIAN")
             ? "/api/auth/register/gorevli"
             : "/api/uye/ekle";
     }

     try {
         const res = await fetch(url, {
             method: method,
             headers: { 'Authorization': getAuth(), 'Content-Type': 'application/json' },
             body: JSON.stringify(data)
         });

         if (res.ok) {
             alert("İşlem başarıyla tamamlandı!");
             uFormSifirla();
             tumKullanicilariGetir();
         } else {
             const err = await res.text();
             alert("İşlem başarısız oldu: " + err);
         }
     } catch (e) {
         alert("Bağlantı Hatası: Sunucuya ulaşılamıyor!");
     }
 }

 // --- DÜZENLEME MODUNU AÇMA ---
 function uDuzenleModu(id, ad, soyad, eposta, tel, tip) {
     editKullaniciId = id;
     document.getElementById("uAd").value = ad;
     document.getElementById("uSoyad").value = soyad;
     document.getElementById("uEposta").value = eposta;
     // 'null' string kontrolü
     document.getElementById("uTelefon").value = (tel === 'null' || !tel) ? '' : tel;
     document.getElementById("uRol").value = tip;

     document.getElementById("uBaslik").innerText = `📝 ${tip === 'MEMBER' ? 'Üye' : 'Görevli'} Düzenle (ID: ${id})`;
     document.getElementById("uBtn").innerText = "🔄 Güncelle";
     document.getElementById("uIptal").style.display = "block";

     // Formun olduğu yere yumuşak geçiş yap
     window.scrollTo({ top: 0, behavior: 'smooth' });
 }

 // --- FORMU SIFIRLAMA ---
 function uFormSifirla() {
     editKullaniciId = null;
     document.getElementById("uAd").value = "";
     document.getElementById("uSoyad").value = "";
     document.getElementById("uEposta").value = "";
     document.getElementById("uTelefon").value = "";
     document.getElementById("uSifre").value = "";
     document.getElementById("uIptal").style.display = "none";
     document.getElementById("uBaslik").innerText = "👥 Kullanıcı Yönetimi (Ekleme Modu)";
     document.getElementById("uBtn").innerText = "💾 Kaydet";
 }

 // ======================================================
 // --- 1. KULLANICI VE GÖREVLİ LİSTELEME (ID VE TELEFON DÜZELTİLDİ) ---
 // ======================================================
 async function tumKullanicilariGetir(filtre = 'HEPSI') {
     const body = document.getElementById("kullaniciTableBody");
     if (!body) return;

     body.innerHTML = "<tr><td colspan='6' style='text-align:center;'>Veriler yükleniyor...</td></tr>";

     try {
         const [resUye, resGor] = await Promise.all([
             fetch('/api/uye/liste', { headers: { 'Authorization': getAuth() } }),
             fetch('/api/admin/gorevli-liste', { headers: { 'Authorization': getAuth() } })
         ]);

         const uyeler = resUye.ok ? await resUye.json() : [];
         const gorevliler = resGor.ok ? await resGor.json() : [];
         body.innerHTML = "";

         // GÖREVLİLER
         if (filtre === 'HEPSI' || filtre === 'LIBRARIAN') {
             gorevliler.forEach(g => {
                 const tel = g.telefon || g.tel || g.telefonNo || '-';
                 body.innerHTML += `
                     <tr>
                         <td>${g.id}</td>
                         <td><span style="color:#e67e22; font-weight:bold;">GÖREVLİ</span></td>
                         <td>${g.ad} ${g.soyad}</td>
                         <td>${g.eposta}</td>
                         <td>${tel}</td>
                         <td>
                             <button onclick="uDuzenleModu(${g.id}, '${g.ad}', '${g.soyad}', '${g.eposta}', '${tel}', 'LIBRARIAN')" class="btn-sm" style="background:#f39c12; color:white;">Düzenle</button>
                             <button onclick="uSil(${g.id}, 'LIBRARIAN')" class="btn-sm" style="background:#e74c3c; color:white;">Sil</button>
                         </td>
                     </tr>`;
             });
         }

         // ÜYELER
         if (filtre === 'HEPSI' || filtre === 'MEMBER') {
             uyeler.forEach(u => {
                 const tel = u.telefon || u.tel || u.telefonNo || '-';
                 body.innerHTML += `
                     <tr>
                         <td>${u.id}</td>
                         <td><span style="color:#3498db; font-weight:bold;">ÜYE</span></td>
                         <td>${u.ad} ${u.soyad}</td>
                         <td>${u.eposta}</td>
                         <td>${tel}</td>
                         <td>
                             <button onclick="uDuzenleModu(${u.id}, '${u.ad}', '${u.soyad}', '${u.eposta}', '${tel}', 'MEMBER')" class="btn-sm" style="background:#f39c12; color:white;">Düzenle</button>
                             <button onclick="uSil(${u.id}, 'MEMBER')" class="btn-sm" style="background:#e74c3c; color:white;">Sil</button>
                         </td>
                     </tr>`;
             });
         }
     } catch (e) {
         body.innerHTML = "<tr><td colspan='6' style='color:red; text-align:center;'>Hata!</td></tr>";
     }
 }


 // --- KULLANICI / GÖREVLİ SİLME ---
 async function uSil(id, tip) {
     if (!confirm(`Bu ${tip === 'MEMBER' ? 'üyeyi' : 'görevliyi'} silmek istediğinize emin misiniz?`)) return;

     const url = (tip === "MEMBER")
         ? `/api/uye/sil/${id}`
         : `/api/admin/gorevli-sil/${id}`;

     try {
         const res = await fetch(url, {
             method: 'DELETE',
             headers: { 'Authorization': getAuth() }
         });

         if (res.ok) {
             tumKullanicilariGetir();
         } else {
             alert("Silme işlemi başarısız oldu!");
         }
     } catch (e) {
         alert("Bağlantı Hatası: Sunucuya ulaşılamıyor!");
     }
 }



 // ======================================================
 // --- EMANET VE CEZA İŞLEMLERİ (GÜNCEL TAM BLOK) ---
 // ======================================================

 async function adminEmanetVer() {
     const kIdInput = document.getElementById("eKitapId");
     const uIdInput = document.getElementById("eUyeId");

     // Güvenlik: Eğer kutular yoksa dur
     if (!kIdInput || !uIdInput) {
         console.error("Hata: Input alanları bulunamadı!");
         return;
     }

     const kId = kIdInput.value;
     const uId = uIdInput.value;

     if (!kId || !uId) {
         alert("Lütfen Kitap ID ve Üye ID alanlarını doldurun!");
         return;
     }

     // Backend'e sadece kimin neyi aldığını gönderiyoruz
     const dto = {
         kitapId: parseInt(kId),
         uyeId: parseInt(uId)
         // Tarih göndermiyoruz, backend tarafında LocalDate.now() ve .plusDays(15) çalışacak
     };

     try {
         const res = await fetch('/api/emanet/odunc-al', {
             method: 'POST',
             headers: {
                 'Authorization': getAuth(),
                 'Content-Type': 'application/json'
             },
             body: JSON.stringify(dto)
         });

         if (res.ok) {
             alert("Kitap başarıyla ödünç verildi!");
             kIdInput.value = "";
             uIdInput.value = "";
             emanetleriGetir(); // Listeyi tazele
         } else {
             const errorMsg = await res.text();
             alert("Hata: " + errorMsg);
         }
     } catch (e) {
         alert("Bağlantı hatası!");
     }
 }

 // 2. AKTİF EMANETLERİ LİSTELEME (İADE EDİLENLERİ FİLTRELER)
 async function emanetleriGetir() {
     const body = document.getElementById("emanetTableBody");
     if (!body) return;

     body.innerHTML = "<tr><td colspan='7'>Yükleniyor...</td></tr>";

     try {
         const res = await fetch('/api/emanet/liste', {
             headers: { 'Authorization': getAuth() }
         });
         const liste = await res.json();
         body.innerHTML = "";

         // Sadece gercekTeslimTarihi null olan (teslim edilmemiş) kitapları göster
         const aktifEmanetler = liste.filter(e => e.gercekTeslimTarihi === null);

         if (aktifEmanetler.length === 0) {
             body.innerHTML = "<tr><td colspan='7'>Aktif emanet kaydı bulunamadı.</td></tr>";
             return;
         }

         aktifEmanetler.forEach(e => {
             const uyeAd = e.uye ? `${e.uye.ad} ${e.uye.soyad}` : "Bilinmiyor";
             const kitapAd = e.kitap ? e.kitap.ad : "Bilinmiyor";
             const gorevliAd = e.gorevli ? e.gorevli.ad : "-";

             body.innerHTML += `
                 <tr>
                     <td>${e.id}</td>
                     <td>${uyeAd}</td>
                     <td>${kitapAd}</td>
                     <td>${gorevliAd}</td>
                     <td>${e.emanetTarihi}</td>
                     <td><span style="color:#e67e22; font-weight:bold;">${e.beklenenTeslimTarihi}</span></td>
                     <td>
                         <button onclick="emanetIadeEt(${e.id})" class="btn-sm" style="background:#27ae60; color:white; border:none; border-radius:4px; padding:5px 10px; cursor:pointer;">İade Et</button>
                     </td>
                 </tr>`;
         });
     } catch (e) {
         body.innerHTML = "<tr><td colspan='7' style='color:red;'>Liste yüklenemedi.</td></tr>";
     }
 }

 // 3. EMANET İADE ETME (BACKEND iadeAl METODU İLE UYUMLU)
 async function emanetIadeEt(id) {
     if (!confirm("Kitap iade ediliyor, onaylıyor musunuz?")) return;

     try {
         // Backend'deki endpoint ismin iade-et veya iade-al hangisiyse ona göre düzelt
         const res = await fetch(`/api/emanet/iade-et/${id}`, {
             method: 'PUT', // iadeAl metodun veritabanını güncellediği için PUT/POST uygundur
             headers: { 'Authorization': getAuth() }
         });

         const mesaj = await res.text(); // Backend'den dönen "Kitap başarıyla iade edildi" vb. mesajı al

         if (res.ok) {
             alert(mesaj);
             emanetleriGetir(); // Listeyi güncelle (İade edilen satır kaybolacak)
             if (typeof tumCezalariGetir === 'function') tumCezalariGetir(); // Varsa ceza listesini tazele
         } else {
             alert("Hata: " + mesaj);
         }
     } catch (e) {
         alert("İade işlemi sırasında bir hata oluştu!");
     }
 }


 // TÜM CEZALARI LİSTELEME
 async function tumCezalariGetir() {
     const body = document.getElementById("cezaTableBody");
     if (!body) return;

     try {
         const res = await fetch('/api/ceza/tum-cezalar', {
             headers: { 'Authorization': getAuth() }
         });
         const cezalar = await res.json();
         body.innerHTML = "";

         if (cezalar.length === 0) {
             body.innerHTML = "<tr><td colspan='5'>Henüz bir ceza kaydı bulunmuyor.</td></tr>";
             return;
         }

cezalar.forEach(c => {
    const miktar = c.cezaMiktari || c.tutar || "0.00";
    const uyeAd = c.uye ? `${c.uye.ad} ${c.uye.soyad}` : (c.emanet && c.emanet.uye ? `${c.emanet.uye.ad} ${c.emanet.uye.soyad}` : "Bilinmiyor");

    // DÜZELTME: Büyük/küçük harf ve Türkçe karakter karmaşasını önlemek için
    // durumu büyük harfe çevirip öyle kontrol ediyoruz.
    const durum = c.durum ? c.durum.toUpperCase() : "";
    const isPaid = (durum === "ÖDENDİ" || durum === "ODENDI" || c.odendiMi === true);

    const durumText = isPaid ?
        "<span style='color:green; font-weight:bold;'>✅ ÖDENDİ</span>" :
        "<span style='color:red; font-weight:bold;'>❌ ÖDENMEDİ</span>";

    const odemeButonu = !isPaid ?
        `<button onclick="cezaOde(${c.id})" class="btn-sm" style="background:#f39c12; color:white; border:none; border-radius:4px; padding:5px 10px; cursor:pointer;">Öde</button>` :
        "<span style='color:gray;'>-</span>";

    body.innerHTML += `
        <tr>
            <td>${c.id}</td>
            <td>${uyeAd}</td>
            <td><b>${miktar} TL</b></td>
            <td>${durumText}</td>
            <td>${odemeButonu}</td>
        </tr>`;
});
     } catch (e) {
         console.error("Ceza listesi hatası:", e);
         body.innerHTML = "<tr><td colspan='5' style='color:red;'>Cezalar yüklenemedi. Sunucu yanıtını kontrol edin.</td></tr>";
     }
 }
 // CEZA ÖDEME İŞLEMİ
 async function cezaOde(id) {
     if (!confirm("Ceza ödemesini onaylıyor musunuz?")) return;

     try {
         // Backend'deki endpointine göre adresi kontrol et (örneğin: /api/ceza/ode/{id})
         const res = await fetch(`/api/ceza/ode/${id}`, {
             method: 'POST',
             headers: { 'Authorization': getAuth() }
         });

         if (res.ok) {
             alert("Ödeme başarıyla kaydedildi!");
             tumCezalariGetir(); // Listeyi güncelle
         } else {
             const msg = await res.text();
             alert("Ödeme hatası: " + msg);
         }
     } catch (e) {
         alert("Bağlantı hatası oluştu!");
     }
 }

 // 6. PANEL GEÇİŞ TETİKLEYİCİSİ (KRİTİK!)
 function showPanel(id) {
     // 1. Tüm panelleri gizle
     document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));

     // 2. İlgili paneli göster
     const target = document.getElementById(id);
     if (target) {
         target.classList.add('active');
     }

     // 3. Panel türüne göre verileri ÇIKIŞ YAPMADAN anında çek
     if (id === 'kitapYonetim') tumKitaplariGetir();
     if (id === 'emanetYonetim') {
         emanetleriGetir();
         tumCezalariGetir();
     }
     if (id === 'kullaniciYonetim') tumKullanicilariGetir('HEPSI');
     if (id === 'yazarYonetim') tumYazarlariGetir();
     if (id === 'kategoriYonetim') tumKategorileriGetir();
 }

 document.addEventListener("DOMContentLoaded", () => {
     const token = sessionStorage.getItem("auth");

     // Eğer admin panelindeysek ve oturum varsa kitapları getir
     if (window.location.pathname.includes("admin.html") && token) {
         showPanel('kitapYonetim');
     }
 });

// Çıkış ve Genel Fonksiyonlar
function logout() { sessionStorage.clear(); window.location.href = "index.html"; }