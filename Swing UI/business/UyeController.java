package business;

import core.Helper;
import dao.UyeDao;
import entity.Uye;

import java.util.ArrayList;

public class UyeController {
    private final UyeDao uyeDao = new UyeDao();

    public ArrayList<Uye> findAll(){
        return this.uyeDao.findAll();
    }

    public boolean save(Uye uye){
        return this.uyeDao.save(uye);
    }

    public Uye getByID(int id){
        return this.uyeDao.getByID(id);
    }

    public boolean update(Uye uye){
        if ( this.getByID(uye.getID())==null){
            Helper.showMsg(uye.getID() + " ID kayıtlı kişi bulunamadı");
            return false;
        }
        return this.uyeDao.update(uye);
    }

    public boolean delete(int id){
        if ( this.getByID(id)==null){
            Helper.showMsg(id + " ID kayırlı kişi bulunamadı");
            return false;
        }
        return this.uyeDao.delete(id);
    }

    public ArrayList<Uye> filter(String name, String surname){
        String query = "SELECT * FROM uye";
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
        return this.uyeDao.query(query);
    }
}
