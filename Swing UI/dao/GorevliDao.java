package dao;

import core.Database;
import entity.Gorevli;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GorevliDao {
    private Connection connection;

    public GorevliDao(){
        this.connection= Database.getInstance();
    }

    public Gorevli findByLogin(String mail,String password){
        Gorevli gorevli = new Gorevli();

        String query = "SELECT * FROM GOREVLI WHERE eposta = ? AND sifre = ? ";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setString(1,mail);
            pr.setString(2,password);
            ResultSet rs = pr.executeQuery();
            if(rs.next()){
                gorevli =  this.match(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return gorevli;
    }
    public ArrayList<Gorevli> findAll(){
        ArrayList<Gorevli> gorevliler = new ArrayList<>();
        try {
            ResultSet rs = this.connection.createStatement().executeQuery("SELECT * FROM GOREVLI");
            while(rs.next()){
                gorevliler.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return gorevliler;

    }

    public Gorevli match (ResultSet rs) throws SQLException {
        Gorevli gorevli = new Gorevli();
        gorevli.setID((rs.getInt("ID")));
        gorevli.setAd(rs.getString("ad"));
        gorevli.setSoyad(rs.getString("soyad"));
        gorevli.setUnvan(rs.getString("unvan"));
        gorevli.setSifre(rs.getString("sifre"));
        gorevli.setEposta(rs.getString("eposta"));
        return gorevli;
    }
}
