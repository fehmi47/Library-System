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
            Helper.showMsg(uye.getID() + " ID kayırlı kişi bulunamadı");
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
}
