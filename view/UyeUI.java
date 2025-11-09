package view;

import business.UyeController;
import core.Helper;
import entity.Uye;

import javax.swing.*;
import java.awt.*;

public class UyeUI  extends JFrame {

    private JPanel container;
    private JLabel lbl_title;
    private JLabel lbl_uye_name;
    private JTextField fld_uye_name;
    private JLabel lbl_uye_surname;
    private JTextField fld_uye_surname;
    private JLabel lbl_uye_telNo;
    private JTextField fld_uye_telNo;
    private JLabel lbl_uye_eposta;
    private JTextField fld_uye_eposta;
    private JLabel lbl_uye_gender;
    private JComboBox<Uye.CINSIYET> cmb_uye_gender;
    private JButton btn_uye_save;
    private Uye uye;
    private UyeController uyeController;

    public UyeUI(Uye uye){
        this.uye = uye;
        this.uyeController = new UyeController();

        this.add(container);
        this.setTitle("Üye Ekle/Düzenle");
        this.setSize(300,500);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height-this.getSize().height) / 2;
        this.setLocation(x,y);
        this.setVisible(true);

        this.cmb_uye_gender.setModel(new DefaultComboBoxModel<>(Uye.CINSIYET.values()));

        if(this.uye.getID() == 0){
            this.lbl_title.setText("Üye Ekle");
        }else{
            this.lbl_title.setText("üye Güncelle");
            this.fld_uye_name.setText(this.uye.getAd());
            this.fld_uye_surname.setText(this.uye.getSoyad());
            this.fld_uye_telNo.setText((this.uye.getTelefonNo()));
            this.fld_uye_eposta.setText(this.uye.getEposta());
            this.cmb_uye_gender.getModel().setSelectedItem(this.uye.getCinsiyet());
        }


        btn_uye_save.addActionListener(e -> {
            JTextField[] checkList = {this.fld_uye_name, this.fld_uye_telNo};
            if(Helper.isFieldListEmpty(checkList)){
                Helper.showMsg("fill");
            }else if(!Helper.isFieldEmpty(this.fld_uye_eposta) && !Helper.isEmailValid(this.fld_uye_eposta.getText())){
                Helper.showMsg("Lütfen geçerli bir mail adresi girin !");
            }else{
                boolean result = false;
                this.uye.setAd(this.fld_uye_name.getText());
                this.uye.setSoyad(this.fld_uye_surname.getText());
                this.uye.setTelefonNo(this.fld_uye_telNo.getText());
                this.uye.setEposta(this.fld_uye_eposta.getText());
                this.uye.setCinsiyet((Uye.CINSIYET)this.cmb_uye_gender.getSelectedItem());

                if(this.uye.getID() == 0){
                    result = this.uyeController.save(this.uye);
                }else{
                    result = this.uyeController.update(this.uye);
                }

                if(result){
                    Helper.showMsg("done");
                    dispose();
                }else{
                    Helper.showMsg("error");
                }
            }

        });
    }
}





















