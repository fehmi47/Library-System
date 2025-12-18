package view;

import business.YazarController;
import core.Helper;
import entity.Yazar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YazarUI extends JFrame{
    private JPanel container;
    private JLabel lbl_title;
    private JLabel lbl_yazar_name;
    private JTextField fld_yazar_name;
    private JTextField fld_yazar_surname;
    private JLabel lbl_yazar_surname;
    private JButton btn_yazar_save;
    private Yazar yazar;
    private YazarController yazarController;

    public YazarUI(Yazar yazar){
        this.yazar = yazar;
        this.yazarController = new YazarController();

        this.add(container);
        this.setTitle("Yazar Ekle/Düzenle");
        this.setSize(300,300);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height-this.getSize().height) / 2;
        this.setLocation(x,y);
        this.setVisible(true);

        if(this.yazar.getID() == 0){
            this.lbl_title.setText("Yazar Ekle");
        }else{
            this.lbl_title.setText("Yazar Güncelle");
            this.fld_yazar_name.setText(this.yazar.getAd());
            this.fld_yazar_surname.setText(this.yazar.getSoyad());
        }

        btn_yazar_save.addActionListener(e -> {
            JTextField[] chekList = {
                    this.fld_yazar_name,
                    this.fld_yazar_surname
            };

            if(Helper.isFieldListEmpty(chekList)){
                Helper.showMsg("fill");
            }else{
                boolean result = false;
                this.yazar.setAd(this.fld_yazar_name.getText());
                this.yazar.setSoyad(this.fld_yazar_surname.getText());

                if(this.yazar.getID() == 0){
                    result = this.yazarController.save(this.yazar);
                }else{
                    result = this.yazarController.update(this.yazar);
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
