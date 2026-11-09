package com.bikerent.dao;

import com.bikerent.db.Koneksi;
import com.bikerent.model.UkuranSepeda;
import com.bikerent.model.VarianSepeda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VarianSepedaDAO {

    private VarianSepeda dariBaris(ResultSet rs, int sepedaId) throws SQLException {
        return new VarianSepeda(rs.getInt("id"), sepedaId,
                rs.getString("nama_model"), rs.getDouble("harga_sewa"));
    }

    public List<VarianSepeda> daftarVarian(int sepedaId) {
        List<VarianSepeda> h = new ArrayList<>();
        try (PreparedStatement ps = Koneksi.get().prepareStatement(
                "SELECT * FROM varian_sepeda WHERE sepeda_id=? ORDER BY id")) {
            ps.setInt(1, sepedaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) h.add(dariBaris(rs, sepedaId));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return h;
    }

    public void simpanSemuaVarian(int sepedaId, List<VarianSepeda> variants) {
        try {
            try (PreparedStatement d = Koneksi.get().prepareStatement(
                    "DELETE FROM varian_sepeda WHERE sepeda_id=?")) {
                d.setInt(1, sepedaId); d.executeUpdate();
            }
            try (PreparedStatement ps = Koneksi.get().prepareStatement(
                    "INSERT INTO varian_sepeda(sepeda_id,nama_model,harga_sewa) VALUES(?,?,?)")) {
                for (VarianSepeda v : variants) {
                    ps.setInt(1, sepedaId); ps.setString(2, v.getNamaModel().trim());
                    ps.setDouble(3, v.getHargaSewa()); ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<UkuranSepeda> daftarUkuran(int sepedaId) {
        List<UkuranSepeda> h = new ArrayList<>();
        try (PreparedStatement ps = Koneksi.get().prepareStatement(
                "SELECT * FROM ukuran_sepeda WHERE sepeda_id=? ORDER BY id")) {
            ps.setInt(1, sepedaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) h.add(new UkuranSepeda(
                    rs.getInt("id"), sepedaId, rs.getString("ukuran"),
                    rs.getInt("tinggi_min"), rs.getInt("tinggi_max")));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return h;
    }

    public void simpanSemuaUkuran(int sepedaId, List<UkuranSepeda> list) {
        try {
            try (PreparedStatement d = Koneksi.get().prepareStatement(
                    "DELETE FROM ukuran_sepeda WHERE sepeda_id=?")) {
                d.setInt(1, sepedaId); d.executeUpdate();
            }
            try (PreparedStatement ps = Koneksi.get().prepareStatement(
                    "INSERT INTO ukuran_sepeda(sepeda_id,ukuran,tinggi_min,tinggi_max) VALUES(?,?,?,?)")) {
                for (UkuranSepeda u : list) {
                    ps.setInt(1, sepedaId); ps.setString(2, u.getUkuran());
                    ps.setInt(3, u.getTinggiMin()); ps.setInt(4, u.getTinggiMax()); ps.addBatch();
                }
                ps.executeBatch();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
