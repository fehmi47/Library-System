package entity;

public class Kategori {
    private int ID;
    private String ad;

    public Kategori(){

    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getAd() {
        return ad;
    }

    public void setAd(String ad) {
        this.ad = ad;
    }

    @Override
    public String toString() {
        return "Kategori{" +
                "ID=" + ID +
                ", ad='" + ad + '\'' +
                '}';
    }
}
