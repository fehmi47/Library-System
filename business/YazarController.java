package business;

import core.Helper;
import dao.KategoriDao;
import dao.YazarDao;
import entity.Kategori;
import entity.Uye;
import entity.Yazar;

import java.util.ArrayList;

public class YazarController {
    private final YazarDao yazarDao = new YazarDao();

    public ArrayList<Yazar> findAll(){
        return this.yazarDao.findAll();
    }

    public boolean save(Yazar yazar){
        return this.yazarDao.save(yazar);
    }

    public Yazar getByID(int id){
        return this.yazarDao.getByID(id);
    }

    public boolean delete(int id){
        if(this.getByID(id) == null){
            Helper.showMsg(id + " ID kayıtlı yazar bulunamadı");
            return false;
        }
        return  this.yazarDao.delete(id);
    }

    public boolean update(Yazar yazar){
        if ( this.getByID(yazar.getID())==null){
            Helper.showMsg(yazar.getID() + " ID kayıtlı yazar bulunamadı");
            return false;
        }
        return this.yazarDao.update(yazar);
    }

    public ArrayList<Yazar> filter(String name, String surname){
        String query = "SELECT * FROM yazar";
        ArrayList<String> whereList = new ArrayList<>();
        if(name.length() > 0){
            whereList.add("ad LIKE '%" + name +"%'");
        }
        if(surname.length() > 0){
            whereList.add("soyad LIKE '%" + surname +"%'");
        }
        if(whereList.size() > 0 ){
            String whereQuery = String.join(" AND ",whereList);
            query += " WHERE " + whereQuery;
        }
        return this.yazarDao.query(query);
    }
}
