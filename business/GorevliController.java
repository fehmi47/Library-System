package business;

import core.Helper;
import dao.GorevliDao;
import entity.Gorevli;

public class GorevliController {
    private final GorevliDao gorevliDao = new GorevliDao();

    public Gorevli findByLogin(String mail, String password){
        if (!Helper.isEmailValid(mail)) return null;
        return this.gorevliDao.findByLogin(mail, password);
    }
}
