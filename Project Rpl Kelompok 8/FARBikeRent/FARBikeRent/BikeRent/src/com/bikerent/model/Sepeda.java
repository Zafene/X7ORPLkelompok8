package com.bikerent.model;

public class Sepeda {
    private int id;
    private String nama;
    private String kode;
    private String tipe;
    private String ukuran;
    private double hargaSewa;
    private int stok;
    private byte[] gambar;

    public Sepeda() {}
    public Sepeda(int id, String nama, String kode, String tipe, String ukuran,
                  double hargaSewa, int stok, byte[] gambar) {
        this.id = id; this.nama = nama; this.kode = kode; this.tipe = tipe;
        this.ukuran = ukuran; this.hargaSewa = hargaSewa; this.stok = stok; this.gambar = gambar;
    }

    public int getId()                  { return id; }
    public void setId(int id)           { this.id = id; }
    public String getNama()             { return nama; }
    public void setNama(String v)       { this.nama = v; }
    public String getKode()             { return kode; }
    public void setKode(String v)       { this.kode = v; }
    public String getTipe()             { return tipe; }
    public void setTipe(String v)       { this.tipe = v; }
    public String getUkuran()           { return ukuran; }
    public void setUkuran(String v)     { this.ukuran = v; }
    public double getHargaSewa()        { return hargaSewa; }
    public void setHargaSewa(double v)  { this.hargaSewa = v; }
    public int getStok()                { return stok; }
    public void setStok(int v)          { this.stok = v; }
    public byte[] getGambar()           { return gambar; }
    public void setGambar(byte[] v)     { this.gambar = v; }

    @Override public String toString()  { return nama; }
}
