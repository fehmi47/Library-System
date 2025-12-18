package dao;

import core.Database;
import entity.Kategori;
import entity.Uye;
import entity.Yazar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class YazarDao {
    private Connection connection;
    private YazarDao yazarDao;

    public YazarDao(){
        this.connection= Database.getInstance();
    }

    public ArrayList<Yazar> findAll(){
        ArrayList<Yazar> yazarlar = new ArrayList<>();
        try {
            ResultSet rs = this.connection.createStatement().executeQuery("SELECT * FROM yazar");
            while(rs.next()){
                yazarlar.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return yazarlar;
    }

    public boolean save(Yazar yazar){
        String query = "INSERT INTO yazar " +
                "(" +
                "ad, " +
                "soyad "+
                ")"+
                " VALUES (?,?)";

        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setString(1,yazar.getAd());
            pr.setString(2,yazar.getSoyad());

            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Yazar getByID(int id){
        Yazar yazar = null;
        String query = "SELECT * FROM yazar WHERE ID = ?";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1,id);
            ResultSet rs = pr.executeQuery();
            if(rs.next()){
                yazar = this.match(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  yazar;
    }



    public boolean update(Yazar yazar){
        String query = "UPDATE yazar SET " +
                "ad = ? ," +
                "soyad = ? "+
                "WHERE id = ?";

        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setString(1,yazar.getAd());;
            pr.setString(2,yazar.getSoyad());
            pr.setInt(3,yazar.getID());
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean delete(int id){
        String query = "DELETE FROM yazar WHERE ID = ?";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1,id);
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public  ArrayList<Yazar> query(String query){
        ArrayList<Yazar> yazarlar = new ArrayList<>();
        try {
            ResultSet rs = this.connection.createStatement().executeQuery(query);
            while (rs.next()){
                yazarlar.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return yazarlar;
    }

    public Yazar match (ResultSet rs) throws SQLException {
        Yazar yazar = new Yazar();
        yazar.setID((rs.getInt("ID")));
        yazar.setAd(rs.getString("ad"));
        yazar.setSoyad(rs.getString("soyad"));
        return yazar;
    }


}
