package entity;

public class Uye {
    private int ID;
    private String ad;
    private String soyad;
    private String telefonNo;
    private String eposta;
    private CINSIYET cinsiyet;
    public enum CINSIYET {
        E,
        K,
        B
    }

    public Uye() {
    }

    public int getID() {
        return ID;
    }

    public String getEposta() {
        return eposta;
    }

    public String getTelefonNo() {
        return telefonNo;
    }

    public String getAd() {
        return ad;
    }

    public String getSoyad() {
        return soyad;
    }

    public CINSIYET getCinsiyet() {
        return cinsiyet;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

    public void setTelefonNo(String telefonNo) {
        this.telefonNo = telefonNo;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setCinsiyet(CINSIYET cinsiyet) {
        this.cinsiyet = cinsiyet;
    }

    @Override
    public String toString() {
        return "Uyeler{" +
                "ID=" + ID +
                ", ad='" + ad + '\'' +
                ", soyad='" + soyad + '\'' +
                ", telefonNo=" + telefonNo +
                ", eposta='" + eposta + '\'' +
                ", cinsiyet=" + cinsiyet +
                '}';
    }
}
