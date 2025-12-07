package business;

import core.Helper;
import dao.KategoriDao;
import entity.Kategori;
import entity.Uye;


import java.util.ArrayList;

public class KategoriController {
    private final KategoriDao kategoriDao = new KategoriDao();

    public ArrayList<Kategori> findAll(){
        return this.kategoriDao.findAll();
    }

    public boolean save(Kategori kategori){
        return this.kategoriDao.save(kategori);
    }

    public Kategori getByID(int id){
        return this.kategoriDao.getByID(id);
    }

    public boolean update(Kategori kategori){
        if ( this.getByID(kategori.getID())==null){
            Helper.showMsg(kategori.getID() + " ID kayıtlı kategori bulunamadı");
            return false;
        }
        return this.kategoriDao.update(kategori);
    }

    public boolean delete(int id){
        if ( this.getByID(id)==null){
            Helper.showMsg(id + " ID kayıtlı kategori bulunamadı");
            return false;
        }
        return this.kategoriDao.delete(id);
    }


    public ArrayList<Kategori> filter(String name){
        String query = "SELECT * FROM kategori";
        ArrayList<String> whereList = new ArrayList<>();
        if(name.length() > 0){
            whereList.add("ad LIKE '%" + name +"%'");
        }

        if(whereList.size() > 0 ){
            String whereQuery = String.join(" AND ",whereList);
            query += " WHERE " + whereQuery;
        }
        return this.kategoriDao.query(query);
    }
}



