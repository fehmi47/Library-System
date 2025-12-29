/**
 * Kütüphane Yönetim Sistemi - Merkezi JavaScript (app.js)
 * SADELEŞTİRİLMİŞ & GÜÇLENDİRİLMİŞ VERSİYON
 */

const API_BASE = "http://localhost:8080/api";
const getAuth = () => localStorage.getItem("auth");

// YARDIMCI: Değer kontrolü
const isInvalid = (value) => !value || value.toString().trim() === "";

// YARDIMCI: Güvenli Element Değeri Atama
const setVal = (id, val) => { const el = document.getElementById(id); if (el) el.value = val; };
const getVal = (id) => { const el = document.getElementById(id); return el ? el.value : null; };

// ======================================================
// --- 1. GENEL YARDIMCI FONKSİYONLAR (CORE) ---
// ======================================================

// [YENİ] GENEL KAYDETME FONKSİYONU (Tüm formlar bunu kullanacak)
async function genericSave({ editId, createUrl, updateUrl, data, entityName, refreshFunc, resetFunc }) {
    const method = editId ? 'PUT' : 'POST';
    const url = editId ? `${updateUrl}/${editId}` : createUrl;

    try {
        const res = await fetch(url, {
            method: method,
            headers: { 'Authorization': getAuth(), 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            // Önce Tabloyu Yenile (Hata olsa da veri gittiği için tabloyu güncellemeliyiz)
            try { await refreshFunc(); } catch (e) { console.error("Tablo yenileme hatası:", e); }

            // Sonra Formu Temizle
            try { resetFunc(); } catch (e) { console.warn("Form temizleme hatası:", e); }

            // Başarılı Mesajı (İsteğe bağlı, popup kirliliği olmasın diye kapalı tutabilirsin)
            // alert(`${entityName} ${editId ? 'güncellendi' : 'eklendi'}!`);
        } else {
            const msg = await res.text();
            alert(`Hata: ${msg}`);
        }
    } catch (e) {
        console.error(e);
        alert(`${entityName} işlemi sırasında bağlantı hatası oluştu!`);
    }
}

// [YENİ] GENEL SİLME FONKSİYONU
async function genericDelete(url, refreshFunc, confirmMsg = "Silmek istediğinize emin misiniz?") {
    if (!confirm(confirmMsg)) return;
    try {
        const res = await fetch(url, { method: 'DELETE', headers: { 'Authorization': getAuth() } });
        if (res.ok) await refreshFunc();
        else alert("Silme işlemi başarısız (Bağlı veri olabilir).");
    } catch (e) { alert("Bağlantı hatası!"); }
}

// [YENİ] GENEL FORM SIFIRLAMA (Ortak elementleri yönetir)
function genericResetForm(prefix, titleText, titleDefault, btnText = "💾 Kaydet") {
    // Başlık ve Buton
    const baslik = document.getElementById(`${prefix}Baslik`);
    if (baslik) baslik.innerText = titleText || titleDefault;

    const btn = document.getElementById(`${prefix}Btn`);
    if (btn) {
        btn.innerText = btnText;
        btn.style.background = "#1abc9c";
    }

    // İptal Butonu
    const iptal = document.getElementById(`${prefix}Iptal`);
    if (iptal) iptal.style.display = "none";
}

// ======================================================
// --- 2. AUTH & GİRİŞ ---
// ======================================================

async function login() {
    const eposta = getVal("username");
    const sifre = getVal("password");

    if (!eposta || !sifre) { alert("Lütfen tüm alanları doldurun!"); return; }

    const authHeader = 'Basic ' + btoa(unescape(encodeURIComponent(eposta + ":" + sifre)));

    try {
        const res = await fetch('/api/kitap/liste', { headers: { 'Authorization': authHeader } });

        if (res.status === 401) { alert("❌ Hatalı e-posta veya şifre!"); return; }

        if (res.ok) {
            localStorage.setItem("auth", authHeader);
            sessionStorage.setItem("auth", authHeader);

            const check = await fetch('/api/admin/check', { headers: { 'Authorization': authHeader } });
            window.location.replace(check.status === 200 ? "admin.html" : "uye.html");
        } else {
            alert("Sunucu hatası.");
        }
    } catch (e) { alert("Bağlantı hatası."); }
}

async function register() {
    const data = {
        ad: getVal("regAd"), soyad: getVal("regSoyad"), telefonNo: getVal("regTelefon"),
        eposta: getVal("regEmail"), sifre: getVal("regPass")
    };

    if (!data.ad || !data.eposta || !data.sifre) { alert("Eksik bilgi!"); return; }

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (res.ok) {
            alert("✅ Kayıt başarılı!");
            ['regAd', 'regSoyad', 'regTelefon', 'regEmail', 'regPass'].forEach(id => setVal(id, ""));
            if (typeof toggleForm === 'function') toggleForm('form-login');
        } else {
            alert("Hata: " + await res.text());
        }
    } catch (e) { alert("Sunucuya bağlanılamadı!"); }
}

window.logout = function() {
    if (confirm("Çıkış yapılsın mı?")) {
        localStorage.clear(); sessionStorage.clear();
        window.location.href = "index.html";
    }
};

// ======================================================
// --- 3. MODÜLLER (KİTAP, KATEGORİ, YAZAR, KULLANICI) ---
// ======================================================

// --- GLOBAL VARIABLES ---
let editKitapId = null;
let editKatId = null;
let editYazId = null;
let editKullaniciId = null;

// --- A. KİTAP YÖNETİMİ ---
function kFormSifirla() {
    editKitapId = null;
    ['kAd', 'kSayfa', 'kAdet', 'kKategoriId', 'kYazarId', 'kTarih'].forEach(id => setVal(id, ""));
    genericResetForm('k', null, "📚 Kitap Yönetimi (Ekleme Modu)");
}

function kDuzenleModu(id, ad, sayfa, adet, katId, yazId, tarih) {
    editKitapId = id;
    setVal("kAd", ad); setVal("kSayfa", sayfa); setVal("kAdet", adet);
    setVal("kKategoriId", katId); setVal("kYazarId", yazId); setVal("kTarih", tarih);

    document.getElementById("kBaslik").innerText = `📝 Kitap Düzenle (ID: ${id})`;
    document.getElementById("kBtn").innerText = "🔄 Güncelle";
    document.getElementById("kBtn").style.background = "#f39c12";
    if (document.getElementById("kIptal")) document.getElementById("kIptal").style.display = "block";
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function adminKitapKaydet() {
    const ad = getVal("kAd");
    if (isInvalid(ad)) { alert("Kitap adı boş olamaz!"); return; }

    const tarih = getVal("kTarih") || new Date().toISOString().split('T')[0];
    const data = {
        ad: ad,
        sayfaSayisi: parseInt(getVal("kSayfa")) || 0,
        adet: parseInt(getVal("kAdet")) || 0,
        yayinTarihi: tarih,
        kategori: { id: parseInt(getVal("kKategoriId")) || null },
        yazar: { id: parseInt(getVal("kYazarId")) || null }
    };

    await genericSave({
        editId: editKitapId,
        createUrl: '/api/kitap/ekle',
        updateUrl: '/api/kitap/guncelle',
        data: data,
        entityName: 'Kitap',
        refreshFunc: tumKitaplariGetir,
        resetFunc: kFormSifirla
    });
}

async function tumKitaplariGetir() {
    const body = document.getElementById("kitapTableBody");
    if (!body) return;
    try {
        const res = await fetch('/api/kitap/liste', { headers: { 'Authorization': getAuth() } });
        const data = await res.json();
        body.innerHTML = data.map(k => `
            <tr>
                <td>${k.id}</td><td>${k.ad}</td>
                <td>${k.yazar ? k.yazar.ad + ' ' + k.yazar.soyad : '-'}</td>
                <td>${k.kategori ? k.kategori.ad : '-'}</td>
                <td>${k.adet}</td>
                <td>
                    <button onclick="kDuzenleModu(${k.id}, '${k.ad}', ${k.sayfaSayisi}, ${k.adet}, ${k.kategori?.id}, ${k.yazar?.id}, '${k.yayinTarihi}')" class="btn-sm" style="background:#f39c12; color:white;">Düzenle</button>
                    <button onclick="kSil(${k.id})" class="btn-sm" style="background:#e74c3c; color:white;">Sil</button>
                </td>
            </tr>`).join('');
    } catch (e) {}
}
async function kSil(id) { await genericDelete(`/api/kitap/sil/${id}`, tumKitaplariGetir); }


// --- B. KATEGORİ YÖNETİMİ ---
function katFormSifirla() {
    editKatId = null;
    setVal("katAd", "");
    genericResetForm('kat', null, "📁 Kategori Yönetimi (Ekleme Modu)");
}
function katDuzenleModu(id, ad) {
    editKatId = id;
    setVal("katAd", ad);
    document.getElementById("katBaslik").innerText = `📝 Kategori Düzenle (ID: ${id})`;
    document.getElementById("katBtn").innerText = "🔄 Güncelle";
    document.getElementById("katIptal").style.display = "block";
}
async function adminKategoriKaydet() {
    const ad = getVal("katAd");
    if (isInvalid(ad)) { alert("Kategori adı boş olamaz!"); return; }
    await genericSave({
        editId: editKatId,
        createUrl: '/api/kategori/ekle',
        updateUrl: '/api/kategori/guncelle',
        data: { ad: ad },
        entityName: 'Kategori',
        refreshFunc: tumKategorileriGetir,
        resetFunc: katFormSifirla
    });
}
async function tumKategorileriGetir() {
    const body = document.getElementById("kategoriTableBody");
    if (!body) return;
    try {
        const res = await fetch('/api/kategori/liste', { headers: { 'Authorization': getAuth() } });
        const data = await res.json();
        body.innerHTML = data.map(c => `
            <tr><td>${c.id}</td><td>${c.ad}</td>
            <td><button onclick="katDuzenleModu(${c.id}, '${c.ad}')" class="btn-sm" style="background:#f39c12; color:white;">Düzenle</button>
            <button onclick="katSil(${c.id})" class="btn-sm" style="background:#e74c3c; color:white;">Sil</button></td></tr>`).join('');
    } catch (e) {}
}
async function katSil(id) { await genericDelete(`/api/kategori/sil/${id}`, tumKategorileriGetir); }


// --- C. YAZAR YÖNETİMİ ---
function yazFormSifirla() {
    editYazId = null;
    setVal("yazAd", ""); setVal("yazSoyad", "");
    genericResetForm('yaz', null, "✍️ Yazar Yönetimi (Ekleme Modu)");
}
function yazDuzenleModu(id, ad, soyad) {
    editYazId = id;
    setVal("yazAd", ad); setVal("yazSoyad", soyad);
    document.getElementById("yazBaslik").innerText = `📝 Yazar Düzenle (ID: ${id})`;
    document.getElementById("yazBtn").innerText = "🔄 Güncelle";
    document.getElementById("yazIptal").style.display = "block";
}
async function adminYazarKaydet() {
    const ad = getVal("yazAd"); const soyad = getVal("yazSoyad");
    if (isInvalid(ad) || isInvalid(soyad)) { alert("Ad ve soyad boş olamaz!"); return; }
    await genericSave({
        editId: editYazId,
        createUrl: '/api/yazar/ekle',
        updateUrl: '/api/yazar/guncelle',
        data: { ad: ad, soyad: soyad },
        entityName: 'Yazar',
        refreshFunc: tumYazarlariGetir,
        resetFunc: yazFormSifirla
    });
}
async function tumYazarlariGetir() {
    const body = document.getElementById("yazarTableBody");
    if (!body) return;
    try {
        const res = await fetch('/api/yazar/liste', { headers: { 'Authorization': getAuth() } });
        const data = await res.json();
        body.innerHTML = data.map(y => `
            <tr><td>${y.id}</td><td>${y.ad} ${y.soyad}</td>
            <td><button onclick="yazDuzenleModu(${y.id}, '${y.ad}', '${y.soyad}')" class="btn-sm" style="background:#f39c12; color:white;">Düzenle</button>
            <button onclick="yazarSil(${y.id})" class="btn-sm" style="background:#e74c3c; color:white;">Sil</button></td></tr>`).join('');
    } catch (e) {}
}
async function yazarSil(id) { await genericDelete(`/api/yazar/sil/${id}`, tumYazarlariGetir); }


// --- D. KULLANICI YÖNETİMİ ---
function uFormSifirla() {
    editKullaniciId = null;
    ['uAd', 'uSoyad', 'uEposta', 'uTelefon', 'uSifre'].forEach(id => setVal(id, ""));
    genericResetForm('u', null, "👥 Kullanıcı Yönetimi (Ekleme Modu)");
}
function uDuzenleModu(id, ad, soyad, eposta, tel, tip) {
    editKullaniciId = id;
    setVal("uAd", ad); setVal("uSoyad", soyad); setVal("uEposta", eposta);
    setVal("uTelefon", (tel === 'null' || !tel) ? '' : tel);
    setVal("uRol", tip);
    document.getElementById("uBaslik").innerText = `📝 ${tip === 'MEMBER' ? 'Üye' : 'Görevli'} Düzenle (ID: ${id})`;
    document.getElementById("uBtn").innerText = "🔄 Güncelle";
    document.getElementById("uIptal").style.display = "block";
    window.scrollTo({ top: 0, behavior: 'smooth' });
}
async function adminKullaniciKaydet() {
    const data = {
        ad: getVal("uAd"), soyad: getVal("uSoyad"), eposta: getVal("uEposta"),
        telefon: getVal("uTelefon"), sifre: getVal("uSifre"), rol: getVal("uRol")
    };
    if (!data.ad || !data.eposta) { alert("Ad ve E-posta zorunludur!"); return; }

    let createUrl = (data.rol === "LIBRARIAN") ? "/api/auth/register/gorevli" : "/api/uye/ekle";
    let updateUrl = (data.rol === "LIBRARIAN") ? "/api/admin/gorevli-guncelle" : "/api/uye/guncelle";

    await genericSave({
        editId: editKullaniciId,
        createUrl: createUrl,
        updateUrl: updateUrl,
        data: data,
        entityName: 'Kullanıcı',
        refreshFunc: () => tumKullanicilariGetir('HEPSI'),
        resetFunc: uFormSifirla
    });
}
async function tumKullanicilariGetir(filtre = 'HEPSI') {
    const body = document.getElementById("kullaniciTableBody");
    if (!body) return;
    body.innerHTML = "<tr><td colspan='6'>Yükleniyor...</td></tr>";

    try {
        const [resUye, resGor] = await Promise.all([
            fetch('/api/uye/liste', { headers: { 'Authorization': getAuth() } }),
            fetch('/api/admin/gorevli-liste', { headers: { 'Authorization': getAuth() } })
        ]);
        const uyeler = resUye.ok ? await resUye.json() : [];
        const gorevliler = resGor.ok ? await resGor.json() : [];
        body.innerHTML = "";

        const addRow = (k, tip, color) => {
            const tel = k.telefon || k.tel || k.telefonNo || '-';
            body.innerHTML += `<tr><td>${k.id}</td><td><span style="color:${color}; font-weight:bold;">${tip}</span></td>
            <td>${k.ad} ${k.soyad}</td><td>${k.eposta}</td><td>${tel}</td>
            <td><button onclick="uDuzenleModu(${k.id}, '${k.ad}', '${k.soyad}', '${k.eposta}', '${tel}', '${tip === 'GÖREVLİ' ? 'LIBRARIAN' : 'MEMBER'}')" class="btn-sm" style="background:#f39c12; color:white;">Düzenle</button>
            <button onclick="uSil(${k.id}, '${tip === 'GÖREVLİ' ? 'LIBRARIAN' : 'MEMBER'}')" class="btn-sm" style="background:#e74c3c; color:white;">Sil</button></td></tr>`;
        };

        if (filtre === 'HEPSI' || filtre === 'LIBRARIAN') gorevliler.forEach(g => addRow(g, 'GÖREVLİ', '#e67e22'));
        if (filtre === 'HEPSI' || filtre === 'MEMBER') uyeler.forEach(u => addRow(u, 'ÜYE', '#3498db'));
    } catch (e) { body.innerHTML = "<tr><td colspan='6'>Hata!</td></tr>"; }
}
async function uSil(id, tip) {
    const url = (tip === "MEMBER") ? `/api/uye/sil/${id}` : `/api/admin/gorevli-sil/${id}`;
    await genericDelete(url, () => tumKullanicilariGetir('HEPSI'));
}

// ======================================================
// --- 4. EMANET & CEZA ---
// ======================================================

async function adminEmanetVer() {
    const kId = getVal("eKitapId"); const uId = getVal("eUyeId");
    if (!kId || !uId) { alert("Lütfen ID'leri girin!"); return; }

    await genericSave({
        createUrl: '/api/emanet/odunc-al',
        data: { kitapId: parseInt(kId), uyeId: parseInt(uId) },
        entityName: 'Emanet',
        refreshFunc: emanetleriGetir,
        resetFunc: () => { setVal("eKitapId", ""); setVal("eUyeId", ""); }
    });
}

async function emanetleriGetir() {
    const body = document.getElementById("emanetTableBody");
    if (!body) return;
    try {
        const res = await fetch('/api/emanet/liste', { headers: { 'Authorization': getAuth() } });
        const liste = await res.json();
        const aktif = liste.filter(e => e.gercekTeslimTarihi === null);

        body.innerHTML = aktif.length ? aktif.map(e => `<tr><td>${e.id}</td><td>${e.uye ? e.uye.ad + ' ' + e.uye.soyad : '-'}</td>
        <td>${e.kitap ? e.kitap.ad : '-'}</td><td>${e.gorevli ? e.gorevli.ad : '-'}</td><td>${e.emanetTarihi}</td>
        <td><span style="color:#e67e22; font-weight:bold;">${e.beklenenTeslimTarihi}</span></td>
        <td><button onclick="emanetIadeEt(${e.id})" class="btn-sm" style="background:#27ae60; color:white;">İade Et</button></td></tr>`).join('')
        : "<tr><td colspan='7'>Aktif emanet yok.</td></tr>";
    } catch (e) {}
}

async function emanetIadeEt(id) {
    if (!confirm("İade onaylıyor musunuz?")) return;
    try {
        const res = await fetch(`/api/emanet/iade-et/${id}`, { method: 'PUT', headers: { 'Authorization': getAuth() } });
        alert(await res.text());
        if(res.ok) { emanetleriGetir(); tumCezalariGetir(); }
    } catch (e) { alert("Hata!"); }
}

async function tumCezalariGetir() {
    const body = document.getElementById("cezaTableBody");
    if (!body) return;
    try {
        const res = await fetch('/api/ceza/tum-cezalar', { headers: { 'Authorization': getAuth() } });
        const cezalar = await res.json();

        body.innerHTML = cezalar.length ? cezalar.map(c => {
            const isPaid = (c.durum?.toUpperCase() === 'ODENDI' || c.durum?.toUpperCase() === 'ÖDENDİ');
            return `<tr><td>${c.id}</td><td>${c.uye?.ad || '-'}</td><td><b>${c.tutar || c.cezaMiktari} TL</b></td>
            <td>${isPaid ? '<span style="color:green;">✅ ÖDENDİ</span>' : '<span style="color:red;">❌ ÖDENMEDİ</span>'}</td>
            <td>${!isPaid ? `<button onclick="cezaOde(${c.id})" class="btn-sm" style="background:#f39c12; color:white;">Öde</button>` : '-'}</td></tr>`;
        }).join('') : "<tr><td colspan='5'>Ceza yok.</td></tr>";
    } catch (e) {}
}

async function cezaOde(id) {
    if (!confirm("Ödeme onaylıyor musunuz?")) return;
    try {
        const res = await fetch(`/api/ceza/ode/${id}`, { method: 'POST', headers: { 'Authorization': getAuth() } });
        if(res.ok) { alert("Ödendi!"); tumCezalariGetir(); } else alert(await res.text());
    } catch (e) { alert("Hata!"); }
}

// ======================================================
// --- 5. PANEL YÖNETİMİ ---
// ======================================================

function showPanel(id) {
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));
    const target = document.getElementById(id);
    if (target) target.classList.add('active');

    if (id === 'kitapYonetim') tumKitaplariGetir();
    if (id === 'emanetYonetim') { emanetleriGetir(); tumCezalariGetir(); }
    if (id === 'kullaniciYonetim') tumKullanicilariGetir('HEPSI');
    if (id === 'yazarYonetim') tumYazarlariGetir();
    if (id === 'kategoriYonetim') tumKategorileriGetir();
}

document.addEventListener("DOMContentLoaded", () => {
    if (window.location.pathname.includes("admin.html") && getAuth()) showPanel('kitapYonetim');
});