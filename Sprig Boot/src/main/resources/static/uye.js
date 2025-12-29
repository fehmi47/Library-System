/**
 * Kütüphane Yönetim Sistemi - Üye Paneli (uye.js)
 * SADELEŞTİRİLMİŞ & GÜÇLENDİRİLMİŞ VERSİYON
 */

const API_BASE = "/api";
// Auth bilgisini al (Yoksa null döner)
const getAuth = () => sessionStorage.getItem("auth") || localStorage.getItem("auth");
const getEl = (id) => document.getElementById(id); // Kısayol

// ============================================================
// 1. MERKEZİ YARDIMCI FONKSİYONLAR
// ============================================================

// [YENİ] GENEL İSTEK YÖNETİCİSİ (Fetch Wrapper)
async function safeRequest(endpoint, method = 'GET', body = null) {
    try {
        const options = {
            method: method,
            headers: { 'Authorization': getAuth(), 'Content-Type': 'application/json' }
        };
        if (body) options.body = JSON.stringify(body);

        const res = await fetch(`${API_BASE}${endpoint}`, options);
        return res;
    } catch (e) {
        console.error(`İstek hatası (${endpoint}):`, e);
        return null;
    }
}

// [YENİ] GENEL AKSİYON YÖNETİCİSİ (Ödünç, İade, Ödeme)
async function executeAction(endpoint, method, body, confirmMsg, successMsg, refreshCallbacks = []) {
    if (!confirm(confirmMsg)) return;

    const res = await safeRequest(endpoint, method, body);

    if (res && res.ok) {
        alert(successMsg || "İşlem başarılı!");
        // İlgili panelleri yenile (Örn: Hem tabloyu hem özeti güncelle)
        refreshCallbacks.forEach(cb => { if (typeof cb === 'function') cb(); });
    } else {
        const err = res ? await res.text() : "Bağlantı hatası";
        alert("❌ İşlem başarısız: " + err);
    }
}

// --- SAYFA YÜKLENDİĞİNDE ---
document.addEventListener("DOMContentLoaded", () => {
    if (!getAuth()) {
        window.location.href = "index.html";
        return;
    }
    loadOzet();
    showPanel('panel-ozet');
});

// --- PANEL GEÇİŞ ---
window.showPanel = function(panelId) {
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.menu li').forEach(li => li.classList.remove('active'));

    const targetPanel = getEl(panelId);
    if (targetPanel) targetPanel.classList.add('active');

    const btnId = panelId.replace('panel-', 'btn-');
    const targetBtn = getEl(btnId);
    if (targetBtn) targetBtn.classList.add('active');

    // Verileri Yükle
    if (panelId === 'panel-kitaplar') loadKitaplar();
    if (panelId === 'panel-emanetler') loadEmanetler();
    if (panelId === 'panel-cezalar') loadCezalar();
    if (panelId === 'panel-ozet') loadOzet();
}

// --- ÇIKIŞ ---
window.logout = function() {
    if (confirm("Çıkış yapmak istediğinize emin misiniz?")) {
        sessionStorage.clear();
        localStorage.clear();
        window.location.href = "index.html";
    }
}

// ============================================================
// 2. ÖZET EKRANI (Promise.all ile Hızlandırıldı)
// ============================================================
async function loadOzet() {
    // Tüm istekleri paralel atıyoruz, birbirini beklemiyorlar -> Daha Hızlı
    const [resKitap, resEmanet, resCeza] = await Promise.allSettled([
        safeRequest('/kitap/liste'),
        safeRequest('/emanet/benim-emanetlerim'),
        safeRequest('/ceza/benim-cezalar')
    ]);

    // 1. Kitap Sayısı
    if (resKitap.status === 'fulfilled' && resKitap.value?.ok) {
        const data = await resKitap.value.json();
        const el = getEl("ozet-kitap");
        if (el) el.innerText = data.length;
    }

    // 2. Aktif Emanet
    if (resEmanet.status === 'fulfilled' && resEmanet.value?.ok) {
        const data = await resEmanet.value.json();
        const aktif = data.filter(e => e.gercekTeslimTarihi === null).length;
        const el = getEl("ozet-emanet");
        if (el) el.innerText = aktif;
    }

    // 3. Ceza Borcu
    if (resCeza.status === 'fulfilled' && resCeza.value?.ok) {
        const data = await resCeza.value.json();
        let toplam = 0;
        data.forEach(c => {
            const durum = c.durum ? c.durum.toString().toUpperCase() : "";
            if (durum !== 'ODENDI' && c.odendiMi !== true) {
                toplam += (c.tutar || c.cezaMiktari || 0);
            }
        });
        const el = getEl("ozet-ceza");
        if (el) el.innerText = toplam + " TL";
    }
}

// ============================================================
// 3. KİTAP İŞLEMLERİ
// ============================================================
async function loadKitaplar() {
    const tbody = document.querySelector("#table-kitaplar tbody");
    if (!tbody) return;
    tbody.innerHTML = "<tr><td colspan='5'>Yükleniyor...</td></tr>";

    const res = await safeRequest('/kitap/liste');
    if (res && res.ok) {
        const data = await res.json();
        if (!data.length) { tbody.innerHTML = "<tr><td colspan='5'>Kütüphanede kitap yok.</td></tr>"; return; }

        tbody.innerHTML = data.map(k => `
            <tr>
                <td><b>${k.ad}</b></td>
                <td>${k.yazar ? k.yazar.ad + ' ' + k.yazar.soyad : '-'}</td>
                <td><span class="badge bg-info">${k.kategori ? k.kategori.ad : 'Genel'}</span></td>
                <td>${k.adet > 0 ? `<span class="badge bg-success">${k.adet} Adet</span>` : `<span class="badge bg-danger">Tükendi</span>`}</td>
                <td><button class="btn btn-primary" onclick="oduncAl(${k.id})" ${k.adet <= 0 ? 'disabled' : ''}>Ödünç Al</button></td>
            </tr>`).join('');
    } else {
        tbody.innerHTML = "<tr><td colspan='5' style='color:red'>Veri yüklenemedi.</td></tr>";
    }
}

window.oduncAl = function(kitapId) {
    executeAction(
        '/emanet/odunc-al',
        'POST',
        { kitapId: kitapId },
        "Bu kitabı ödünç almak istiyor musunuz?",
        "✅ Kitap ödünç alındı!",
        [loadKitaplar, loadOzet] // Başarılı olursa bu fonksiyonları çalıştır
    );
}

// ============================================================
// 4. EMANET İŞLEMLERİ
// ============================================================
async function loadEmanetler() {
    const tbody = document.querySelector("#table-emanetler tbody");
    if (!tbody) return;
    tbody.innerHTML = "<tr><td colspan='5'>Yükleniyor...</td></tr>";

    const res = await safeRequest('/emanet/benim-emanetlerim');
    if (res && res.ok) {
        const data = await res.json();
        if (!data.length) { tbody.innerHTML = "<tr><td colspan='5'>Emanet kaydı yok.</td></tr>"; return; }

        tbody.innerHTML = data.map(e => {
            const aktif = (e.gercekTeslimTarihi === null);
            return `<tr>
                <td>${e.kitap ? e.kitap.ad : 'Bilinmiyor'}</td>
                <td>${e.emanetTarihi || '-'}</td>
                <td>${e.beklenenTeslimTarihi || '-'}</td>
                <td>${!aktif ? '<span class="badge bg-success">İade Edildi</span>' : '<span class="badge bg-warning">Okunuyor</span>'}</td>
                <td>${aktif ? `<button class="btn btn-sm" style="background:#e67e22; color:white" onclick="kitapIadeEt(${e.id})">📚 İade Et</button>` : '-'}</td>
            </tr>`;
        }).join('');

        // Tabloyu yüklemişken özeti de güncelle (ekstra fetch yapmadan)
        const el = getEl("ozet-emanet");
        if(el) el.innerText = data.filter(e => e.gercekTeslimTarihi === null).length;

    } else {
        tbody.innerHTML = "<tr><td colspan='5' style='color:red'>Veri yüklenemedi.</td></tr>";
    }
}

window.kitapIadeEt = function(id) {
    executeAction(
        `/emanet/iade-et/${id}`,
        'PUT',
        null,
        "Kitabı iade etmek istiyor musunuz?",
        "✅ İade işlemi başarılı.",
        [loadEmanetler, loadOzet, loadCezalar]
    );
}

// ============================================================
// 5. CEZA İŞLEMLERİ
// ============================================================
async function loadCezalar() {
    const tbody = document.querySelector("#table-cezalar tbody");
    if (!tbody) return;
    tbody.innerHTML = "<tr><td colspan='4'>Yükleniyor...</td></tr>";

    const res = await safeRequest('/ceza/benim-cezalar');
    if (res && res.ok) {
        const data = await res.json();
        if (!data.length) { tbody.innerHTML = "<tr><td colspan='4'>Cezanız yok. 🎉</td></tr>"; return; }

        tbody.innerHTML = data.map(c => {
            const durum = c.durum ? c.durum.toString().toUpperCase() : "";
            const isPaid = (durum === 'ODENDI' || durum === 'ÖDENDİ' || c.odendiMi === true);
            const kitap = c.emanet?.kitap?.ad || c.kitap?.ad || '-';

            return `<tr>
                <td>${kitap}</td>
                <td>${c.tutar || c.cezaMiktari || 0} TL</td>
                <td>${isPaid ? '<span class="badge bg-success">Ödendi</span>' : '<span class="badge bg-danger">Ödenmedi</span>'}</td>
                <td>${!isPaid ? `<button class="btn" style="background:#27ae60;" onclick="cezaOde(${c.id})">💸 Öde</button>` : '<i class="fas fa-check" style="color:green"></i>'}</td>
            </tr>`;
        }).join('');

        // Özeti güncelle (Ekstra fetch yapmadan)
        let toplam = data.reduce((acc, c) => {
            const d = c.durum ? c.durum.toString().toUpperCase() : "";
            return (d !== 'ODENDI' && !c.odendiMi) ? acc + (c.tutar || c.cezaMiktari || 0) : acc;
        }, 0);
        const el = getEl("ozet-ceza");
        if(el) el.innerText = toplam + " TL";

    } else {
        tbody.innerHTML = "<tr><td colspan='4' style='color:red'>Veri yüklenemedi.</td></tr>";
    }
}

window.cezaOde = function(id) {
    executeAction(
        `/ceza/ode/${id}`,
        'POST',
        null,
        "Ödeme yapmak istiyor musunuz?",
        "✅ Ödeme Başarılı!",
        [loadCezalar, loadOzet]
    );
}