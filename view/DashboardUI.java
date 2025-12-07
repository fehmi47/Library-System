package view;

import business.KategoriController;
import business.UyeController;
import business.YazarController;
import core.Helper;
import entity.Gorevli;
import entity.Kategori;
import entity.Uye;
import entity.Yazar;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DashboardUI extends JFrame {
    private JPanel container;
    private JPanel pnl_top;
    private JLabel lbl_welcome;
    private JButton btn_logout;
    private JTabbedPane tab_menu;
    private JPanel pnl_uyeler;
    private JScrollPane scrl_uyeler;
    private JTable tbl_uyeler;
    private JPanel pnl_uyeler_filter;
    private JButton btn_uyeler_filter;
    private JButton btn_uyeler_filter_reset;
    private JButton btn_uyeler_new;
    private JTextField fld_f_uyeler_ad;
    private JLabel lbl_f_uyeler_ad;
    private JPanel pnl_kategori;
    private JScrollPane scrl_kategori;
    private JTable tbl_kategori;
    private JPanel pnl_kategori_filter;
    private JLabel lbl_f_kategori_ad;
    private JTextField fld_f_kategori_ad;
    private JButton btn_kategori_new;
    private JButton btn_kategori_filter_reset;
    private JButton btn_kategori_filter;
    private JTextField fld_f_uyeler_soyad;
    private JLabel lbl_f_uyeler_soyad;
    private JPanel pnl_yazar;
    private JTable tbl_yazar;
    private JScrollPane scrl_yazar;
    private JTextField fld_f_yazar_ad;
    private JButton btn_yazar_new;
    private JButton btn_yazar_filter_reset;
    private JButton btn_yazar_filter;
    private JLabel lbl_f_yazar_ad;
    private JTextField fld_f_yazar_soyad;
    private JLabel lbl_f_yazar_soyad;
    private JPanel pnl_yazar_filter;
    private Gorevli gorevli;
    private UyeController uyeController;
    private KategoriController kategoriController;
    private YazarController yazarController;
    private DefaultTableModel mdl_uye_table = new DefaultTableModel();
    private DefaultTableModel mdl_kategori_table = new DefaultTableModel();
    private DefaultTableModel mdl_yazar_table = new DefaultTableModel();
    private JPopupMenu popup_uye = new JPopupMenu();
    private JPopupMenu popup_kategori = new JPopupMenu();
    private JPopupMenu popup_yazar = new JPopupMenu();

    public DashboardUI (Gorevli gorevli){
        this.gorevli=gorevli;
        this.uyeController = new UyeController();
        this.kategoriController = new KategoriController();
        this.yazarController = new YazarController();


        if(gorevli == null){
            Helper.showMsg("error");
            dispose();
        }
        this.add(container);
        this.setTitle("Uye Yonetim Sistemi");
        this.setSize(800,500);

        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height-this.getSize().height) / 2;

        this.setLocation(x,y);
        this.setVisible(true);

        this.lbl_welcome.setText("Hoşgeldin : " + this.gorevli.getAd() + " " + this.gorevli.getSoyad());
        btn_logout.addActionListener(e -> {
            dispose();
            LoginUI loginUI = new LoginUI();
        });

        //YAZAAR
        loadYazarTable(null);
        loadYazarPopupMenu();
        loadYazarButtonEvent();

        //CUSTOMER
        loadUyeTable(null);
        loadUyePopupMenu();
        loadUyeButtonEvent();

        //KATEGORİ
        loadKategoriTable(null);
        loadKategoriPopupMenu();
        loadKategoriButtonEvent();


    }

    //YAZAR
    private void loadYazarTable(ArrayList<Yazar> yazarlar){
        Object[] columnYazar = {"ID","Yazar Adı","Yazar Soyadı"};

        if(yazarlar == null){
            yazarlar = this.yazarController.findAll();
        }

        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_yazar.getModel();
        clearModel.setRowCount(0);

        this.mdl_yazar_table.setColumnIdentifiers(columnYazar);
        for(Yazar yazar : yazarlar){
            Object[] rowObject = {
                    yazar.getID(),
                    yazar.getAd(),
                    yazar.getSoyad(),

            };
            this.mdl_yazar_table.addRow(rowObject);
        }
        this.tbl_yazar.setModel(mdl_yazar_table);
        this.tbl_yazar.getTableHeader().setReorderingAllowed(false);
        this.tbl_yazar.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_yazar.setEnabled(false);
    }

    private void loadYazarPopupMenu(){
            this.tbl_yazar.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    int selectedRow = tbl_yazar.rowAtPoint(e.getPoint());
                    tbl_yazar.setRowSelectionInterval(selectedRow,selectedRow);
                }
            });

            this.popup_yazar.add("Güncelle").addActionListener(e ->{
                int selectID = Integer.parseInt(tbl_yazar.getValueAt(tbl_yazar.getSelectedRow(),0).toString());
                YazarUI yazarUI = new YazarUI(this.yazarController.getByID(selectID));
                yazarUI.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        loadYazarTable(null);
                    }
                });
            });


            this.popup_yazar.add("Sil").addActionListener(e ->{
                int selectID = Integer.parseInt(tbl_yazar.getValueAt(tbl_yazar.getSelectedRow(),0).toString());
                if (Helper.confirm("sure")){
                    if(this.yazarController.delete(selectID)){
                        Helper.showMsg("done");
                        loadYazarTable(null);
                    }else{
                        Helper.showMsg("error");
                    }
                }
            });

            this.tbl_yazar.setComponentPopupMenu(this.popup_yazar);
    }

    private void loadYazarButtonEvent(){
        this.btn_yazar_new.addActionListener(e -> {
            YazarUI yazarUI = new YazarUI(new Yazar());
            yazarUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                   loadYazarTable(null);
                }
            });
        });


        this.btn_yazar_filter.addActionListener(e -> {
            ArrayList<Yazar> filteredYazarlar = this.yazarController.filter(
                    this.fld_f_yazar_ad.getText(),
                    this.fld_f_yazar_soyad.getText()
            );
            loadYazarTable(filteredYazarlar);
        });

        btn_yazar_filter_reset.addActionListener(e -> {
            loadYazarTable(null);
            this.fld_f_yazar_ad.setText(null);
            this.fld_f_yazar_soyad.setText(null);

        });

    }

    //KATEGORİ
    private void loadKategoriTable(ArrayList<Kategori> kategoriler){
        Object[] columnKategori = {"ID","Kategori Adı"};

        if(kategoriler == null){
            kategoriler = this.kategoriController.findAll();
        }

        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_kategori.getModel();
        clearModel.setRowCount(0);

        this.mdl_kategori_table.setColumnIdentifiers(columnKategori);
        for(Kategori kategori : kategoriler){
            Object[] rowObject = {
                    kategori.getID(),
                    kategori.getAd(),

            };
            this.mdl_kategori_table.addRow(rowObject);
        }
        this.tbl_kategori.setModel(mdl_kategori_table);
        this.tbl_kategori.getTableHeader().setReorderingAllowed(false);
        this.tbl_kategori.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_kategori.setEnabled(false);
    }

    private  void loadKategoriPopupMenu(){
        this.tbl_kategori.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_kategori.rowAtPoint(e.getPoint());
                tbl_kategori.setRowSelectionInterval(selectedRow,selectedRow);
            }
        });

        this.popup_kategori.add("Guncelle").addActionListener(e ->{
            int selectID = Integer.parseInt(tbl_kategori.getValueAt(tbl_kategori.getSelectedRow(),0).toString());
            KategorUI kategorUI = new KategorUI(this.kategoriController.getByID(selectID));
            kategorUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadKategoriTable(null);
                }
            });
        });

        this.popup_kategori.add("sil").addActionListener(e ->{
        int selectID = Integer.parseInt(tbl_kategori.getValueAt(tbl_kategori.getSelectedRow(),0).toString());
            if(Helper.confirm("sure")){
                if(this.kategoriController.delete(selectID)){
                    Helper.showMsg("done");
                    loadKategoriTable(null);
                }else{
                    Helper.showMsg("error");
                }
            }
        });

        this.tbl_kategori.setComponentPopupMenu(this.popup_kategori);

    }

    private void loadKategoriButtonEvent(){
        this.btn_kategori_new.addActionListener(e -> {
            KategorUI kategorUI = new KategorUI(new Kategori());
            kategorUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadKategoriTable(null);
                }
            });
        });


        this.btn_kategori_filter.addActionListener(e -> {
            ArrayList<Kategori> filteredKategoriler = this.kategoriController.filter(
                    this.fld_f_kategori_ad.getText()
            );
            loadKategoriTable(filteredKategoriler);
        });

        btn_kategori_filter_reset.addActionListener(e -> {
            loadKategoriTable(null);
            this.fld_f_kategori_ad.setText(null);

        });
    }

    //UYE
    private void loadUyeButtonEvent(){
        this.btn_uyeler_new.addActionListener(e -> {
            UyeUI uyeUI = new UyeUI(new Uye());
            uyeUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadUyeTable(null);
                }
            });
        });

        btn_uyeler_filter.addActionListener(e -> {
            ArrayList<Uye> filteredUyeler = this.uyeController.filter(
                    this.fld_f_uyeler_ad.getText(),
                    this.fld_f_uyeler_soyad.getText()
            );
            loadUyeTable(filteredUyeler);

        });

        btn_uyeler_filter_reset.addActionListener(e -> {
            loadUyeTable(null);
            this.fld_f_uyeler_ad.setText(null);
            this.fld_f_uyeler_soyad.setText(null);
        });
    }

    private  void loadUyePopupMenu(){
        this.tbl_uyeler.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = tbl_uyeler.rowAtPoint(e.getPoint());
                tbl_uyeler.setRowSelectionInterval(selectedRow,selectedRow);
            }
        });

        this.popup_uye.add("Güncelle").addActionListener(e ->{
            int selectID = Integer.parseInt(tbl_uyeler.getValueAt(tbl_uyeler.getSelectedRow(),0).toString());
            UyeUI uyeUI = new UyeUI(this.uyeController.getByID(selectID));
            uyeUI.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadUyeTable(null);
                }
            });
        });
        this.popup_uye.add("Sil").addActionListener(e ->{
            int selectID = Integer.parseInt(tbl_uyeler.getValueAt(tbl_uyeler.getSelectedRow(),0).toString());
            if (Helper.confirm("sure")){
                if(this.uyeController.delete(selectID)){
                    Helper.showMsg("done");
                    loadUyeTable(null);
                }else{
                    Helper.showMsg("error");
                }
            }

        });

        this.tbl_uyeler.setComponentPopupMenu(this.popup_uye);
    }

    private void loadUyeTable(ArrayList<Uye> uyeler){
        Object[] columnUye = {"ID","Uye Adı","Uye Soyadı","Telefon No","e-posta","Cinsiyet"};

        if(uyeler == null){
            uyeler = this.uyeController.findAll();
        }

        //tablo sıfırlama
        DefaultTableModel clearModel = (DefaultTableModel) this.tbl_uyeler.getModel();
        clearModel.setRowCount(0);

        this.mdl_uye_table.setColumnIdentifiers(columnUye);
        for(Uye uye : uyeler){
            Object[] rowObject = {
                    uye.getID(),
                    uye.getAd(),
                    uye.getSoyad(),
                    uye.getTelefonNo(),
                    uye.getEposta(),
                    uye.getCinsiyet()
            };
            this.mdl_uye_table.addRow(rowObject);
        }
        this.tbl_uyeler.setModel(mdl_uye_table);
        this.tbl_uyeler.getTableHeader().setReorderingAllowed(false);
        this.tbl_uyeler.getColumnModel().getColumn(0).setMaxWidth(50);
        this.tbl_uyeler.setEnabled(false);
    }

}
