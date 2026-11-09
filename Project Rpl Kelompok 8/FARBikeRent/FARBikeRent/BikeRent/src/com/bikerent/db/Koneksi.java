package com.bikerent.db;

import java.sql.*;

public final class Koneksi {

    public static final String NAMA_APLIKASI = "FAR BikeRent";
    private static final String URL = "jdbc:sqlite:bikerent.db";
    private static Connection conn;

    private Koneksi() {}

    public static Connection get() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection(URL);
                try (Statement st = conn.createStatement()) {
                    st.execute("PRAGMA foreign_keys = ON");
                }
            }
            return conn;
        } catch (Exception e) {
            throw new RuntimeException("Gagal membuka database: " + e.getMessage(), e);
        }
    }

    public static void tutup() {
        try { if (conn != null && !conn.isClosed()) conn.close(); } catch (SQLException ignored) {}
    }

    public static void siapkan() {
        buatTabel();
        isiAdminAwal();
        isiDataAwal();
    }

    private static void buatTabel() {
        try (Statement st = get().createStatement()) {
            st.execute(
                "CREATE TABLE IF NOT EXISTS admin (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  username TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
            st.execute(
                "CREATE TABLE IF NOT EXISTS sepeda (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  nama TEXT NOT NULL, kode TEXT NOT NULL," +
                "  tipe TEXT NOT NULL, ukuran TEXT NOT NULL," +
                "  harga_sewa REAL NOT NULL, stok INTEGER NOT NULL DEFAULT 0," +
                "  gambar BLOB)");
            st.execute(
                "CREATE TABLE IF NOT EXISTS varian_sepeda (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  sepeda_id INTEGER NOT NULL, nama_model TEXT NOT NULL, harga_sewa REAL NOT NULL DEFAULT 0," +
                "  FOREIGN KEY (sepeda_id) REFERENCES sepeda(id) ON DELETE CASCADE)");
            st.execute(
                "CREATE TABLE IF NOT EXISTS ukuran_sepeda (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  sepeda_id INTEGER NOT NULL, ukuran TEXT NOT NULL," +
                "  tinggi_min INTEGER NOT NULL, tinggi_max INTEGER NOT NULL," +
                "  FOREIGN KEY (sepeda_id) REFERENCES sepeda(id) ON DELETE CASCADE)");
            st.execute(
                "CREATE TABLE IF NOT EXISTS penyewaan (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  nama_pelanggan TEXT NOT NULL, sepeda_id INTEGER NOT NULL," +
                "  tanggal_sewa TEXT NOT NULL, durasi INTEGER NOT NULL," +
                "  harga_sewa REAL NOT NULL, ongkir REAL NOT NULL DEFAULT 0," +
                "  uang_lelah REAL NOT NULL DEFAULT 0, denda REAL NOT NULL DEFAULT 0, total_bayar REAL NOT NULL," +
                "  metode TEXT NOT NULL, bank TEXT, no_rekening TEXT, nama_pemilik TEXT," +
                "  nama_varian TEXT, ukuran_terpilih TEXT," +
                "  datetime_sewa TEXT, datetime_kembali TEXT," +
                "  status TEXT NOT NULL," +
                "  FOREIGN KEY (sepeda_id) REFERENCES sepeda(id))");
        } catch (SQLException e) {
            throw new RuntimeException("Gagal membuat tabel: " + e.getMessage(), e);
        }
    }

    private static boolean kosong(String tbl) {
        try (Statement st = get().createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tbl)) {
            return rs.next() && rs.getInt(1) == 0;
        } catch (SQLException e) { return false; }
    }

    private static void isiAdminAwal() {
        if (!kosong("admin")) return;
        try (PreparedStatement ps = get().prepareStatement("INSERT INTO admin(username,password) VALUES(?,?)")) {
            ps.setString(1, "admin");
            ps.setString(2, com.bikerent.util.Hash.sha256("admin123"));
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private static void isiDataAwal() {
        if (kosong("sepeda")) {
            Object[][] data = {
                {"Mountain Bike","MTB","Hardtail / Full Suspension","26\"/27.5\"/29\"",75000.0,8},
                {"Road Bike","Road","Road Bike","46/50/54/58",100000.0,6},
                {"City Bike","City","City Bike","26\"",50000.0,12},
                {"Sepeda Listrik","E-Bike","Electric Bike","20\"",80000.0,5}
            };
            String sql = "INSERT INTO sepeda(nama,kode,tipe,ukuran,harga_sewa,stok) VALUES(?,?,?,?,?,?)";
            try (PreparedStatement ps = get().prepareStatement(sql)) {
                for (Object[] d : data) {
                    ps.setString(1,(String)d[0]); ps.setString(2,(String)d[1]);
                    ps.setString(3,(String)d[2]); ps.setString(4,(String)d[3]);
                    ps.setDouble(5,(Double)d[4]); ps.setInt(6,(Integer)d[5]);
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException e) { throw new RuntimeException(e); }
        }
        isiVarian();
        isiUkuran();
    }

    private static void isiVarian() {
        if (!kosong("varian_sepeda")) return;
        Object[][] d = {
            {1,"Polygon Xtrada 5",100000.0},{1,"Polygon Xtrada 6",120000.0},{1,"Polygon Xtrada 7",140000.0},
            {1,"Polygon Siskiu D5",180000.0},{1,"Polygon Siskiu T8",300000.0},
            {2,"Polygon Strattos S2",100000.0},{2,"Polygon Strattos S3",115000.0},{2,"Polygon Strattos S4",140000.0},
            {2,"Polygon Strattos S4D",170000.0},{2,"Polygon Strattos 7",250000.0},
            {3,"Polygon Urbano 3",70000.0},{3,"Polygon Oosten 26",70000.0},{3,"Polygon Zenith Active i3",80000.0},
            {3,"Polygon Zenith Town i3",85000.0},{3,"Polygon Urbano 5",100000.0},
            {4,"United Milano V1",100000.0},{4,"Polygon Gili Velo",160000.0},{4,"Polygon Kalosi Prime",180000.0},
            {4,"Polygon Kalosi Miles High/Low Step",200000.0},{4,"Polygon Kalosi Prime+",350000.0}
        };
        try (PreparedStatement ps = get().prepareStatement(
                "INSERT INTO varian_sepeda(sepeda_id,nama_model,harga_sewa) VALUES(?,?,?)")) {
            for (Object[] r : d) {
                ps.setInt(1,(Integer)r[0]); ps.setString(2,(String)r[1]); ps.setDouble(3,(Double)r[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException ignored) {}
    }

    private static void isiUkuran() {
        if (!kosong("ukuran_sepeda")) return;
        Object[][] d = {
            {1,"XS",145,155},{1,"S",155,165},{1,"M",165,175},{1,"L",175,185},{1,"XL",185,200},
            {2,"46 cm",155,163},{2,"50 cm",163,170},{2,"54 cm",170,178},{2,"58 cm",178,185},{2,"62 cm",185,200},
            {3,"S",150,165},{3,"M",165,178},{3,"L",178,195},
            {4,"S",150,165},{4,"M",165,178},{4,"L",178,195}
        };
        try (PreparedStatement ps = get().prepareStatement(
                "INSERT INTO ukuran_sepeda(sepeda_id,ukuran,tinggi_min,tinggi_max) VALUES(?,?,?,?)")) {
            for (Object[] r : d) {
                ps.setInt(1,(Integer)r[0]); ps.setString(2,(String)r[1]);
                ps.setInt(3,(Integer)r[2]); ps.setInt(4,(Integer)r[3]); ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException ignored) {}
    }
}
