package view;

import business.GorevliController;
import core.Helper;
import entity.Gorevli;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame{
    private JPanel container;
    private JPanel pnl_top;
    private JLabel lbl_title;
    private JPanel pnl_bottom;
    private JTextField fld_mail;
    private JButton btn_login;
    private JLabel lbl_mail;
    private JLabel lbl_password;
    private JPasswordField fld_password;

    private GorevliController gorevliController;


    public LoginUI(){

        this.gorevliController= new GorevliController();

        this.add(container);
        this.setTitle("Kütüphane Sistemi");
        this.setSize(400,400);

        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height-this.getSize().height) / 2;

        this.setLocation(x,y);
        this.setVisible(true);

        this.btn_login.addActionListener(e -> {
            JTextField[] checkList = {this.fld_password,this.fld_mail};
            if(!Helper.isEmailValid(this.fld_mail.getText())){
                Helper.showMsg("Geçerli bir e posta giriniz");
            }
            if (Helper.isFieldListEmpty(checkList)){
                Helper.showMsg("fill");
            }else{
                Gorevli gorevli = this.gorevliController.findByLogin(this.fld_mail.getText(),this.fld_password.getText());
                if(gorevli == null){
                    Helper.showMsg("Girdiğiniz bilgilere ait görevli bulunamadı");
                }else{
                    this.dispose();
                    DashboardUI dashboardUI = new DashboardUI(gorevli);
                }
            }

        });
    }
}