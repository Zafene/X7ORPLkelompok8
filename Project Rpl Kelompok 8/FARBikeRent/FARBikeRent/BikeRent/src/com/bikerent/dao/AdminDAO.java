package com.bikerent.dao;

import com.bikerent.db.Koneksi;
import com.bikerent.model.Admin;
import com.bikerent.util.Hash;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDAO {

    private Admin dariBaris(ResultSet rs) throws SQLException {
        Admin a = new Admin();
        a.setId(rs.getInt("id"));
        a.setUsername(rs.getString("username"));
        a.setPasswordHash(rs.getString("password"));
        return a;
    }

    public Admin byUsername(String username) {
        try (PreparedStatement ps = Koneksi.get().prepareStatement("SELECT * FROM admin WHERE username=?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return dariBaris(rs); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public Admin login(String username, String password) {
        Admin a = byUsername(username);
        if (a != null && a.getPasswordHash().equals(Hash.sha256(password))) return a;
        return null;
    }

    public boolean adaUsername(String username) {
        return byUsername(username) != null;
    }

    public boolean daftar(String username, String password) {
        if (adaUsername(username)) return false;
        try (PreparedStatement ps = Koneksi.get().prepareStatement(
                "INSERT INTO admin(username,password) VALUES(?,?)")) {
            ps.setString(1, username);
            ps.setString(2, Hash.sha256(password));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void ubahPassword(int id, String passwordBaru) {
        try (PreparedStatement ps = Koneksi.get().prepareStatement("UPDATE admin SET password=? WHERE id=?")) {
            ps.setString(1, Hash.sha256(passwordBaru));
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
