package view;

import business.KategoriController;
import core.Helper;
import entity.Kategori;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class KategorUI extends JFrame {
    private JPanel container;
    private JLabel lbl_title;
    private JButton btn_kategori_save;
    private JLabel lbl_kategori_name;
    private JTextField fld_kategori_name;
    private Kategori kategori;
    private KategoriController kategoriController;

    public KategorUI(Kategori kategori){
        this.kategori = kategori;
        this.kategoriController = new  KategoriController();


        this.add(container);
        this.setTitle("Kategori Ekle/Düzenle");
        this.setSize(300,200);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height-this.getSize().height) / 2;
        this.setLocation(x,y);
        this.setVisible(true);

        if(this.kategori.getID() == 0){
            this.lbl_title.setText("Kategori Ekle");
        }else{
            this.lbl_title.setText("Kategori Düzenle");
            this.fld_kategori_name.setText(this.kategori.getAd());
        }
        btn_kategori_save.addActionListener(e -> {
            JTextField[] chekList = {
                    this.fld_kategori_name
            };

            if(Helper.isFieldListEmpty(chekList)){
                Helper.showMsg("fill");
            }else{
                boolean result = false;
                this.kategori.setAd(this.fld_kategori_name.getText());
                if(this.kategori.getID() == 0){
                    result = this.kategoriController.save(this.kategori);
                }else{
                    result = this.kategoriController.update(this.kategori);
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
