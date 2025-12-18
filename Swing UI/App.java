import business.GorevliController;
import core.Database;
import core.Helper;
import entity.Gorevli;
import view.DashboardUI;
import view.LoginUI;

import java.sql.Connection;

public class App {


    public static void main(String[] args) {
        Helper.setTheme();
        //LoginUI loginUI = new LoginUI();
        GorevliController gorevliController = new GorevliController();
        Gorevli gorevli = gorevliController.findByLogin("fehmi@kutuphane.com","12345");
        DashboardUI dashboardUI = new DashboardUI(gorevli);
    }
}
