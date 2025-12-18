package dao;

import core.Database;
import entity.Uye;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UyeDao {

    private Connection connection;

    public UyeDao(){
        this.connection= Database.getInstance();
    }

    public ArrayList<Uye> findAll(){
        ArrayList<Uye> uyeler = new ArrayList<>();
        try {
            ResultSet rs = this.connection.createStatement().executeQuery("SELECT * FROM UYE");
            while(rs.next()){
                uyeler.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return uyeler;
    }

    public boolean save(Uye uye){
        String query = "INSERT INTO uye " +
                "(" +
                "ad," +
                "soyad," +
                "telefonNo," +
                "eposta," +
                "cinsiyet" +
                ")"+
                " VALUES (?,?,?,?,?)";

        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setString(1,uye.getAd());
            pr.setString(2,uye.getSoyad());
            pr.setString(3,uye.getTelefonNo());
            pr.setString(4,uye.getEposta());
            pr.setString(5,uye.getCinsiyet().toString());
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Uye getByID(int id){
        Uye uye = null;
        String query = "SELECT * FROM uye WHERE ID = ?";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1,id);
            ResultSet rs = pr.executeQuery();
            if(rs.next()){
                uye = this.match(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  uye;
    }

    public boolean update(Uye uye){
        String query = "UPDATE uye SET " +
                "ad = ? ," +
                "soyad = ? ," +
                "telefonNo = ? ," +
                "eposta = ? ," +
                "cinsiyet = ?" +
                "WHERE id = ?";

        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setString(1,uye.getAd());
            pr.setString(2,uye.getSoyad());
            pr.setString(3,uye.getTelefonNo());
            pr.setString(4,uye.getEposta());
            pr.setString(5,uye.getCinsiyet().toString());
            pr.setInt(6,uye.getID());
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean delete(int id){
        String query = "DELETE FROM uye WHERE ID = ?";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1,id);
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public  ArrayList<Uye> query(String query){
       ArrayList<Uye> uyeler = new ArrayList<>();
        try {
            ResultSet rs = this.connection.createStatement().executeQuery(query);
            while (rs.next()){
                uyeler.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return uyeler;
    }
    public Uye match (ResultSet rs) throws SQLException {
        Uye uye = new Uye();
        uye.setID((rs.getInt("ID")));
        uye.setAd(rs.getString("ad"));
        uye.setSoyad(rs.getString("soyad"));
        uye.setTelefonNo(rs.getString("telefonNo"));
        uye.setEposta(rs.getString("eposta"));
        uye.setCinsiyet(Uye.CINSIYET.valueOf(rs.getString("cinsiyet")));
        return uye;
    }
}
