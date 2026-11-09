package com.bikerent.dao;

import com.bikerent.db.Koneksi;
import com.bikerent.model.Sepeda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SepedaDAO {

    private Sepeda dariBaris(ResultSet rs) throws SQLException {
        return new Sepeda(
            rs.getInt("id"), rs.getString("nama"), rs.getString("kode"),
            rs.getString("tipe"), rs.getString("ukuran"), rs.getDouble("harga_sewa"),
            rs.getInt("stok"), rs.getBytes("gambar"));
    }

    public List<Sepeda> semua() {
        List<Sepeda> h = new ArrayList<>();
        try (PreparedStatement ps = Koneksi.get().prepareStatement("SELECT * FROM sepeda ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) h.add(dariBaris(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return h;
    }

    public List<Sepeda> cari(String kata, String kode) {
        List<Sepeda> h = new ArrayList<>();
        boolean pakaiKode = kode != null && !kode.isEmpty() && !"Semua Jenis".equals(kode);
        String sql = "SELECT * FROM sepeda WHERE (nama LIKE ? OR tipe LIKE ? OR kode LIKE ?)"
                   + (pakaiKode ? " AND kode = ?" : "") + " ORDER BY id";
        try (PreparedStatement ps = Koneksi.get().prepareStatement(sql)) {
            String k = "%" + (kata == null ? "" : kata.trim()) + "%";
            ps.setString(1,k); ps.setString(2,k); ps.setString(3,k);
            if (pakaiKode) ps.setString(4, kode);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) h.add(dariBaris(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return h;
    }

    public Sepeda byId(int id) {
        try (PreparedStatement ps = Koneksi.get().prepareStatement("SELECT * FROM sepeda WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return dariBaris(rs); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    public int simpan(Sepeda s) {
        String sql = "INSERT INTO sepeda(nama,kode,tipe,ukuran,harga_sewa,stok,gambar) VALUES(?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Koneksi.get().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,s.getNama()); ps.setString(2,s.getKode());
            ps.setString(3,s.getTipe()); ps.setString(4,s.getUkuran());
            ps.setDouble(5,s.getHargaSewa()); ps.setInt(6,s.getStok());
            ps.setBytes(7,s.getGambar());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return -1;
    }

    public void ubah(Sepeda s) {
        String sql = "UPDATE sepeda SET nama=?,kode=?,tipe=?,ukuran=?,harga_sewa=?,stok=?,gambar=? WHERE id=?";
        try (PreparedStatement ps = Koneksi.get().prepareStatement(sql)) {
            ps.setString(1,s.getNama()); ps.setString(2,s.getKode());
            ps.setString(3,s.getTipe()); ps.setString(4,s.getUkuran());
            ps.setDouble(5,s.getHargaSewa()); ps.setInt(6,s.getStok());
            ps.setBytes(7,s.getGambar());
            ps.setInt(8,s.getId()); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void hapus(int id) {
        try (PreparedStatement ps = Koneksi.get().prepareStatement("DELETE FROM sepeda WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void ubahStok(int id, int selisih) {
        try (PreparedStatement ps = Koneksi.get().prepareStatement(
                "UPDATE sepeda SET stok=MAX(0,stok+?) WHERE id=?")) {
            ps.setInt(1,selisih); ps.setInt(2,id); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<String> semuaKode() {
        List<String> h = new ArrayList<>();
        try (PreparedStatement ps = Koneksi.get().prepareStatement(
                "SELECT DISTINCT kode FROM sepeda ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) h.add(rs.getString(1));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return h;
    }
}
