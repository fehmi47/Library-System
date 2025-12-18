package dao;

import core.Database;
import entity.Kategori;
import entity.Uye;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class KategoriDao {
    private Connection connection;

    public KategoriDao(){
        this.connection= Database.getInstance();
    }

    public ArrayList<Kategori> findAll(){
        ArrayList<Kategori> kategoriler = new ArrayList<>();
        try {
            ResultSet rs = this.connection.createStatement().executeQuery("SELECT * FROM kategori");
            while(rs.next()){
                kategoriler.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return kategoriler;
    }

    public Kategori match (ResultSet rs) throws SQLException {
        Kategori kategori = new Kategori();
        kategori.setID((rs.getInt("ID")));
        kategori.setAd(rs.getString("ad"));
        return kategori;
    }

    public boolean save(Kategori kategori){
        String query = "INSERT INTO kategori " +
                "(" +
                "ad " +
                ")"+
                " VALUES (?)";

        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setString(1,kategori.getAd());

            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Kategori getByID(int id){
        Kategori kategori = null;
        String query = "SELECT * FROM kategori WHERE ID = ?";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1,id);
            ResultSet rs = pr.executeQuery();
            if(rs.next()){
                kategori = this.match(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  kategori;
    }

    public boolean update(Kategori kategori){
        String query = "UPDATE kategori SET " +
                "ad = ? " +
                "WHERE id = ?";

        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setString(1,kategori.getAd());;
            pr.setInt(2,kategori.getID());
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean delete(int id){
        String query = "DELETE FROM kategori WHERE ID = ?";
        try {
            PreparedStatement pr = this.connection.prepareStatement(query);
            pr.setInt(1,id);
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public  ArrayList<Kategori> query(String query){
        ArrayList<Kategori> kategoriler = new ArrayList<>();
        try {
            ResultSet rs = this.connection.createStatement().executeQuery(query);
            while (rs.next()){
                kategoriler.add(this.match(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return kategoriler;
    }


}
