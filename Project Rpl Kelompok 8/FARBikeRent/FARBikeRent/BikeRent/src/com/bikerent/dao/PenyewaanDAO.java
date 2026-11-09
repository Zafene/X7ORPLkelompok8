package com.bikerent.dao;

import com.bikerent.db.Koneksi;
import com.bikerent.model.Penyewaan;

import java.sql.*;
import java.util.*;

public class PenyewaanDAO {

    private static final String SELECT_JOIN =
        "SELECT p.*, s.nama AS nama_sepeda, s.kode AS kode_sepeda " +
        "FROM penyewaan p JOIN sepeda s ON s.id = p.sepeda_id ";

    private Penyewaan dariBaris(ResultSet rs) throws SQLException {
        Penyewaan p = new Penyewaan();
        p.setId(rs.getInt("id"));
        p.setNamaPelanggan(rs.getString("nama_pelanggan"));
        p.setSepedaId(rs.getInt("sepeda_id"));
        p.setNamaSepeda(rs.getString("nama_sepeda"));
        p.setKodeSepeda(rs.getString("kode_sepeda"));
        p.setTanggalSewa(rs.getString("tanggal_sewa"));
        p.setDurasi(rs.getInt("durasi"));
        p.setHargaSewa(rs.getDouble("harga_sewa"));
        p.setOngkir(rs.getDouble("ongkir"));
        p.setUangLelah(rs.getDouble("uang_lelah"));
        p.setTotalBayar(rs.getDouble("total_bayar"));
        p.setMetode(rs.getString("metode"));
        p.setBank(rs.getString("bank"));
        p.setNoRekening(rs.getString("no_rekening"));
        p.setNamaPemilik(rs.getString("nama_pemilik"));
        p.setNamaVarian(rs.getString("nama_varian"));
        p.setUkuranTerpilih(rs.getString("ukuran_terpilih"));
        p.setDatetimeSewa(rs.getString("datetime_sewa"));
        p.setDatetimeKembali(rs.getString("datetime_kembali"));
        p.setDenda(rs.getDouble("denda"));
        p.setStatus(rs.getString("status"));
        return p;
    }

    public int simpan(Penyewaan p) {
        String sql = "INSERT INTO penyewaan(nama_pelanggan,sepeda_id,tanggal_sewa,durasi," +
                     "harga_sewa,ongkir,uang_lelah,total_bayar,metode,bank,no_rekening," +
                     "nama_pemilik,nama_varian,ukuran_terpilih,datetime_sewa,status) " +
                     "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = Koneksi.get().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1,p.getNamaPelanggan()); ps.setInt(2,p.getSepedaId());
            ps.setString(3,p.getTanggalSewa()); ps.setInt(4,p.getDurasi());
            ps.setDouble(5,p.getHargaSewa()); ps.setDouble(6,p.getOngkir());
            ps.setDouble(7,p.getUangLelah()); ps.setDouble(8,p.getTotalBayar());
            ps.setString(9,p.getMetode()); ps.setString(10,p.getBank());
            ps.setString(11,p.getNoRekening()); ps.setString(12,p.getNamaPemilik());
            ps.setString(13,p.getNamaVarian()); ps.setString(14,p.getUkuranTerpilih());
            ps.setString(15, p.getDatetimeSewa()); ps.setString(16, p.getStatus() == null ? "Disewa" : p.getStatus());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return -1;
    }

    public List<Penyewaan> terbaru(int limit) {
        List<Penyewaan> h = new ArrayList<>();
        try (PreparedStatement ps = Koneksi.get().prepareStatement(
                SELECT_JOIN + "ORDER BY p.tanggal_sewa DESC, p.id DESC LIMIT ?")) {
            ps.setInt(1,limit);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) h.add(dariBaris(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return h;
    }

    public List<Penyewaan> periode(String dari, String sampai, String status) {
        List<Penyewaan> h = new ArrayList<>();
        boolean ps2 = status != null && !status.isEmpty() && !"Semua".equals(status);
        String sql = SELECT_JOIN + "WHERE p.tanggal_sewa BETWEEN ? AND ? " +
                     (ps2 ? "AND p.status=? " : "") + "ORDER BY p.tanggal_sewa DESC, p.id DESC";
        try (PreparedStatement ps = Koneksi.get().prepareStatement(sql)) {
            ps.setString(1,dari); ps.setString(2,sampai);
            if (ps2) ps.setString(3,status);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) h.add(dariBaris(rs)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return h;
    }

    private double satuAngka(String sql, Object... param) {
        try (PreparedStatement ps = Koneksi.get().prepareStatement(sql)) {
            for (int i = 0; i < param.length; i++) ps.setObject(i+1, param[i]);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getDouble(1); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return 0;
    }

    public int totalOrderan()          { return (int)satuAngka("SELECT COUNT(*) FROM penyewaan"); }
    public int jumlahStatus(String s)  { return (int)satuAngka("SELECT COUNT(*) FROM penyewaan WHERE status=?",s); }
    public double totalPendapatan()    { return satuAngka("SELECT IFNULL(SUM(total_bayar),0) FROM penyewaan"); }
    public int totalOrderan(String a,String b) { return (int)satuAngka("SELECT COUNT(*) FROM penyewaan WHERE tanggal_sewa BETWEEN ? AND ?",a,b); }
    public int jumlahStatus(String s,String a,String b) { return (int)satuAngka("SELECT COUNT(*) FROM penyewaan WHERE status=? AND tanggal_sewa BETWEEN ? AND ?",s,a,b); }
    public double totalPendapatan(String a,String b) { return satuAngka("SELECT IFNULL(SUM(total_bayar),0) FROM penyewaan WHERE tanggal_sewa BETWEEN ? AND ?",a,b); }

    public int jumlahAktifBySepeda(int sepedaId) {
        return (int)satuAngka("SELECT COUNT(*) FROM penyewaan WHERE sepeda_id=? AND status='Disewa'",sepedaId);
    }

    public Map<String,Integer> orderanPerJenis() {
        Map<String,Integer> m = new LinkedHashMap<>();
        try (PreparedStatement ps = Koneksi.get().prepareStatement(
                "SELECT s.nama, COUNT(p.id) FROM sepeda s LEFT JOIN penyewaan p ON p.sepeda_id=s.id GROUP BY s.id ORDER BY s.id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) m.put(rs.getString(1), rs.getInt(2));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return m;
    }

    public Map<String,Double> pendapatanPerJenis(String a, String b) {
        Map<String,Double> m = new LinkedHashMap<>();
        String sql = "SELECT s.kode, IFNULL(SUM(p.total_bayar),0) FROM sepeda s " +
                     "LEFT JOIN penyewaan p ON p.sepeda_id=s.id AND p.tanggal_sewa BETWEEN ? AND ? " +
                     "GROUP BY s.id ORDER BY s.id";
        try (PreparedStatement ps = Koneksi.get().prepareStatement(sql)) {
            ps.setString(1,a); ps.setString(2,b);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) m.put(rs.getString(1), rs.getDouble(2)); }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return m;
    }

    public String[] rentangTanggal() {
        try (PreparedStatement ps = Koneksi.get().prepareStatement(
                "SELECT MIN(tanggal_sewa), MAX(tanggal_sewa) FROM penyewaan");
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getString(1) != null) return new String[]{rs.getString(1),rs.getString(2)};
        } catch (SQLException e) { throw new RuntimeException(e); }
        String t = java.time.LocalDate.now().toString(); return new String[]{t,t};
    }

    public List<Penyewaan> daftarAktif() {
        List<Penyewaan> h = new ArrayList<>();
        try (PreparedStatement ps = Koneksi.get().prepareStatement(
                SELECT_JOIN + "WHERE p.status='Disewa' ORDER BY p.tanggal_sewa ASC, p.id ASC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) h.add(dariBaris(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return h;
    }

    public void kembalikan(int id, String datetimeKembali, double denda) {
        String sql = "UPDATE penyewaan SET status='Selesai', datetime_kembali=?, denda=?, total_bayar=total_bayar+? WHERE id=?";
        try (PreparedStatement ps = Koneksi.get().prepareStatement(sql)) {
            ps.setString(1, datetimeKembali); ps.setDouble(2, denda);
            ps.setDouble(3, denda); ps.setInt(4, id); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
