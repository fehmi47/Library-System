package entity;

public class Gorevli {
    private  int ID;
    private String ad;
    private String soyad;
    private String unvan;
    private String sifre;
    private String eposta;

    public Gorevli() {

    }

    public String getEposta() {
        return eposta;
    }

    public String getSifre() {
        return sifre;
    }

    public String getUnvan() {
        return unvan;
    }

    public String getSoyad() {
        return soyad;
    }

    public String getAd() {
        return ad;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    public void setSoyad(String soyad) {
        this.soyad = soyad;
    }

    public void setUnvan(String unvan) {
        this.unvan = unvan;
    }

    public void setSifre(String sifre) {
        this.sifre = sifre;
    }

    public void setEposta(String eposta) {
        this.eposta = eposta;
    }

    @Override
    public String toString() {
        return "Gorevli{" +
                "ID=" + ID +
                ", ad='" + ad + '\'' +
                ", soyad='" + soyad + '\'' +
                ", unvan='" + unvan + '\'' +
                ", sifre='" + sifre + '\'' +
                ", eposta='" + eposta + '\'' +
                '}';
    }
}
