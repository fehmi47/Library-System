package view;

import business.UyeController;
import core.Helper;
import entity.Gorevli;
import entity.Uye;

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
    private Gorevli gorevli;
    private UyeController uyeController;
    private DefaultTableModel mdl_uye_table = new DefaultTableModel();
    private JPopupMenu popup_uye = new JPopupMenu();

    public DashboardUI (Gorevli gorevli){
        this.gorevli=gorevli;
        this.uyeController = new UyeController();
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

        loadUyeTable(null);
        loadUyePopupMenu();
        loadUyeButtonEvent();


    }

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
                    this.fld_f_uyeler_ad.getText()
            );
            loadUyeTable(filteredUyeler);
        });

        btn_uyeler_filter_reset.addActionListener(e -> {
            loadUyeTable(null);
            this.fld_f_uyeler_ad.setText(null);
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
