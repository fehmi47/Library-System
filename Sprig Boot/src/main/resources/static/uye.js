/**
 * Kütüphane Yönetim Sistemi - Üye Paneli (uye.js)
 * Tüm fonksiyonlar backend ile uyumlu hale getirilmiştir.
 */

const API_BASE = "/api";
const getAuth = () => sessionStorage.getItem("auth");

// --- SAYFA YÜKLENDİĞİNDE ---
document.addEventListener("DOMContentLoaded", () => {
    // 1. Giriş kontrolü
    if (!getAuth()) {
        window.location.href = "index.html";
        return;
    }

    // 2. Özet verileri yükle
    loadOzet();

    // 3. Varsayılan olarak özet panelini aç
    showPanel('panel-ozet');
});

// --- PANEL GEÇİŞ YÖNETİMİ ---
window.showPanel = function(panelId) {
    // Tüm panelleri gizle
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.menu li').forEach(li => li.classList.remove('active'));

    // İstenen paneli aç
    const targetPanel = document.getElementById(panelId);
    if(targetPanel) targetPanel.classList.add('active');

    // Menüdeki butonu aktif yap
    const btnId = panelId.replace('panel-', 'btn-');
    const targetBtn = document.getElementById(btnId);
    if(targetBtn) targetBtn.classList.add('active');

    // Panelle ilgili verileri yükle
    if (panelId === 'panel-kitaplar') loadKitaplar();
    if (panelId === 'panel-emanetler') loadEmanetler();
    if (panelId === 'panel-cezalar') loadCezalar();
    if (panelId === 'panel-ozet') loadOzet();
}

// --- ÇIKIŞ YAP ---
window.logout = function() {
    if(confirm("Çıkış yapmak istediğinize emin misiniz?")) {
        sessionStorage.clear();
        window.location.href = "index.html";
    }
}

// ============================================================
// 1. GENEL BAKIŞ (ÖZET) İŞLEMLERİ
// ============================================================
async function loadOzet() {
    console.log("Özet veriler yükleniyor...");

    // A) KİTAP SAYISI
    try {
        const res = await fetch(`${API_BASE}/kitap/liste`, { headers: { 'Authorization': getAuth() } });
        if (res.ok) {
            const data = await res.json();
            if (document.getElementById("ozet-kitap"))
                document.getElementById("ozet-kitap").innerText = data.length;
        }
    } catch (e) { console.error("Kitap sayısı hatası:", e); }

    // B) AKTİF EMANET SAYISI
    try {
        const res = await fetch(`${API_BASE}/emanet/benim-emanetlerim`, { headers: { 'Authorization': getAuth() } });
        if (res.ok) {
            const data = await res.json();
            const aktifSayi = data.filter(e => e.gercekTeslimTarihi === null).length;
            if (document.getElementById("ozet-emanet"))
                document.getElementById("ozet-emanet").innerText = aktifSayi;
        }
    } catch (e) { console.error("Emanet sayısı hatası:", e); }

    // C) CEZA BORCU
    try {
        const res = await fetch(`${API_BASE}/ceza/benim-cezalar`, { headers: { 'Authorization': getAuth() } });
        if (res.ok) {
            const data = await res.json();
            let toplamBorc = 0;
            data.forEach(c => {
                const durum = c.durum ? c.durum.toString().toUpperCase() : "";
                // Eğer durum ODENDI değilse borcu topla
                if (durum !== 'ODENDI' && c.odendiMi !== true) {
                    toplamBorc += (c.tutar || c.cezaMiktari || 0);
                }
            });
            if (document.getElementById("ozet-ceza"))
                document.getElementById("ozet-ceza").innerText = toplamBorc + " TL";
        }
    } catch (e) { console.error("Ceza bilgisi hatası:", e); }
}

// ============================================================
// 2. KİTAP İŞLEMLERİ (LİSTELEME & ÖDÜNÇ ALMA)
// ============================================================
async function loadKitaplar() {
    const tbody = document.querySelector("#table-kitaplar tbody");
    tbody.innerHTML = "<tr><td colspan='5'>Yükleniyor...</td></tr>";

    try {
        const res = await fetch(`${API_BASE}/kitap/liste`, { headers: { 'Authorization': getAuth() } });
        const data = await res.json();

        if (data.length === 0) {
            tbody.innerHTML = "<tr><td colspan='5'>Kütüphanede kitap bulunmuyor.</td></tr>";
            return;
        }

        tbody.innerHTML = data.map(k => `
            <tr>
                <td><b>${k.ad}</b></td>
                <td>${k.yazar ? k.yazar.ad + ' ' + k.yazar.soyad : '-'}</td>
                <td><span class="badge bg-info">${k.kategori ? k.kategori.ad : 'Genel'}</span></td>
                <td>
                    ${k.adet > 0
                        ? `<span class="badge bg-success">${k.adet} Adet</span>`
                        : `<span class="badge bg-danger">Tükendi</span>`}
                </td>
                <td>
                    <button class="btn btn-primary" onclick="oduncAl(${k.id})" ${k.adet <= 0 ? 'disabled' : ''}>
                        Ödünç Al
                    </button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        tbody.innerHTML = "<tr><td colspan='5' style='color:red'>Kitaplar yüklenemedi.</td></tr>";
    }
}

window.oduncAl = async function(kitapId) {
    if(!confirm("Bu kitabı ödünç almak istiyor musunuz?")) return;

    try {
        // Backend'de "oduncVer" metodunda üye ID'sini token'dan buluyoruz, sadece kitapId yeterli
        const res = await fetch(`${API_BASE}/emanet/odunc-al`, {
            method: 'POST',
            headers: { 'Authorization': getAuth(), 'Content-Type': 'application/json' },
            body: JSON.stringify({ kitapId: kitapId })
        });

        if (res.ok) {
            alert("✅ Kitap başarıyla ödünç alındı!");
            loadKitaplar(); // Stok güncellensin
            loadOzet();     // Sayaç artsın
        } else {
            alert("❌ Hata: " + await res.text());
        }
    } catch (e) {
        alert("Sunucu hatası: " + e);
    }
}

// ============================================================
// 3. EMANET İŞLEMLERİ (LİSTELEME & İADE ETME)
// ============================================================
async function loadEmanetler() {
    const tbody = document.querySelector("#table-emanetler tbody");
    // HTML'de 5 sütun ayarladık, colspan 5 olmalı
    tbody.innerHTML = "<tr><td colspan='5'>Yükleniyor...</td></tr>";

    try {
        const res = await fetch(`${API_BASE}/emanet/benim-emanetlerim`, {
            headers: { 'Authorization': getAuth() }
        });

        let data = [];
        if(res.ok) data = await res.json();

        if (data.length === 0) {
            tbody.innerHTML = "<tr><td colspan='5'>Henüz ödünç aldığınız kitap yok.</td></tr>";
            return;
        }

        tbody.innerHTML = data.map(e => {
            const iadeEdilmedi = (e.gercekTeslimTarihi === null);
            return `
            <tr>
                <td>${e.kitap ? e.kitap.ad : 'Bilinmiyor'}</td>
                <td>${e.emanetTarihi || '-'}</td>
                <td>${e.beklenenTeslimTarihi || '-'}</td>
                <td>
                    ${!iadeEdilmedi
                        ? '<span class="badge bg-success">İade Edildi</span>'
                        : '<span class="badge bg-warning">Okunuyor</span>'}
                </td>
                <td>
                    ${iadeEdilmedi
                        ? `<button class="btn btn-sm" style="background:#e67e22; color:white" onclick="kitapIadeEt(${e.id})">📚 İade Et</button>`
                        : '-'}
                </td>
            </tr>
        `}).join('');

        // Özeti de güncelle
        const aktifSayi = data.filter(e => e.gercekTeslimTarihi === null).length;
        if(document.getElementById("ozet-emanet"))
            document.getElementById("ozet-emanet").innerText = aktifSayi;

    } catch (e) {
        console.error(e);
        tbody.innerHTML = "<tr><td colspan='5' style='color:red'>Veriler alınamadı.</td></tr>";
    }
}

window.kitapIadeEt = async function(emanetId) {
    if(!confirm("Kitabı iade etmek istediğinize emin misiniz?")) return;

    try {
        // Mevcut endpoint'i kullanıyoruz (Service katmanında güvenlik kontrolü eklemiştik)
        const res = await fetch(`${API_BASE}/emanet/iade-et/${emanetId}`, {
            method: 'PUT',
            headers: { 'Authorization': getAuth() }
        });

        if (res.ok) {
            const mesaj = await res.text();
            alert("✅ " + (mesaj || "İade işlemi başarılı."));
            loadEmanetler(); // Listeyi yenile
            loadOzet();      // Sayacı düşür
            // Ceza çıkmış olabilir, ceza listesini de yenilemek iyi olur
            loadCezalar();
        } else {
            alert("❌ Hata: " + await res.text());
        }
    } catch (e) {
        alert("Sunucu hatası: " + e);
    }
}

// ============================================================
// 4. CEZA İŞLEMLERİ (LİSTELEME & ÖDEME)
// ============================================================
async function loadCezalar() {
    const tbody = document.querySelector("#table-cezalar tbody");
    tbody.innerHTML = "<tr><td colspan='4'>Yükleniyor...</td></tr>";

    try {
        const res = await fetch(`${API_BASE}/ceza/benim-cezalar`, {
            headers: { 'Authorization': getAuth() }
        });

        let data = [];
        if(res.ok) data = await res.json();

        if (data.length === 0) {
            tbody.innerHTML = "<tr><td colspan='4'>Ceza kaydınız bulunmuyor. Teşekkürler! 🎉</td></tr>";
            return;
        }

        tbody.innerHTML = data.map(c => {
            const miktar = c.tutar || c.cezaMiktari || 0;
            const durumStr = c.durum ? c.durum.toString().toUpperCase() : "";
            const isPaid = (durumStr === 'ODENDI' || durumStr === 'ÖDENDİ' || c.odendiMi === true);

            const kitapAdi = (c.emanet && c.emanet.kitap) ? c.emanet.kitap.ad :
                             (c.kitap ? c.kitap.ad : '-');

            return `
            <tr>
                <td>${kitapAdi}</td>
                <td>${miktar} TL</td>
                <td>
                    ${isPaid
                        ? '<span class="badge bg-success">Ödendi</span>'
                        : '<span class="badge bg-danger">Ödenmedi</span>'}
                </td>
                <td>
                    ${!isPaid
                        ? `<button class="btn" style="background:#27ae60;" onclick="cezaOde(${c.id})">💸 Öde</button>`
                        : '<i class="fas fa-check" style="color:green"></i>'}
                </td>
            </tr>
        `}).join('');

        loadOzet(); // Toplam borcu güncelle

    } catch (e) {
        tbody.innerHTML = "<tr><td colspan='4' style='color:red'>Cezalar yüklenemedi.</td></tr>";
    }
}

window.cezaOde = async function(id) {
    if(!confirm("Bu cezayı ödemek istiyor musunuz?")) return;

    try {
        const res = await fetch(`${API_BASE}/ceza/ode/${id}`, {
            method: 'POST',
            headers: { 'Authorization': getAuth() }
        });

        if (res.ok) {
            alert("✅ Ödeme başarıyla gerçekleşti!");
            loadCezalar(); // Tabloyu yenile
            loadOzet();    // Karttaki borcu sıfırla
        } else {
            alert("❌ İşlem başarısız: " + await res.text());
        }
    } catch (e) {
        alert("Hata: " + e.message);
    }
}