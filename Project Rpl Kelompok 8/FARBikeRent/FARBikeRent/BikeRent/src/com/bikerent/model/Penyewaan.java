package com.bikerent.model;

public class Penyewaan {
    private int id;
    private String namaPelanggan;
    private int sepedaId;
    private String namaSepeda;
    private String kodeSepeda;
    private String tanggalSewa;
    private int durasi;
    private double hargaSewa;
    private double ongkir;
    private double uangLelah;
    private double totalBayar;
    private String metode;
    private String bank;
    private String noRekening;
    private String namaPemilik;
    private String namaVarian;
    private String ukuranTerpilih;
    private String datetimeSewa;
    private String datetimeKembali;
    private double denda;
    private String status;

    public int getId()                      { return id; }
    public void setId(int v)                { this.id = v; }
    public String getNamaPelanggan()        { return namaPelanggan; }
    public void setNamaPelanggan(String v)  { this.namaPelanggan = v; }
    public int getSepedaId()                { return sepedaId; }
    public void setSepedaId(int v)          { this.sepedaId = v; }
    public String getNamaSepeda()           { return namaSepeda; }
    public void setNamaSepeda(String v)     { this.namaSepeda = v; }
    public String getKodeSepeda()           { return kodeSepeda; }
    public void setKodeSepeda(String v)     { this.kodeSepeda = v; }
    public String getTanggalSewa()          { return tanggalSewa; }
    public void setTanggalSewa(String v)    { this.tanggalSewa = v; }
    public int getDurasi()                  { return durasi; }
    public void setDurasi(int v)            { this.durasi = v; }
    public double getHargaSewa()            { return hargaSewa; }
    public void setHargaSewa(double v)      { this.hargaSewa = v; }
    public double getOngkir()               { return ongkir; }
    public void setOngkir(double v)         { this.ongkir = v; }
    public double getUangLelah()            { return uangLelah; }
    public void setUangLelah(double v)      { this.uangLelah = v; }
    public double getTotalBayar()           { return totalBayar; }
    public void setTotalBayar(double v)     { this.totalBayar = v; }
    public String getMetode()               { return metode; }
    public void setMetode(String v)         { this.metode = v; }
    public String getBank()                 { return bank; }
    public void setBank(String v)           { this.bank = v; }
    public String getNoRekening()           { return noRekening; }
    public void setNoRekening(String v)     { this.noRekening = v; }
    public String getNamaPemilik()          { return namaPemilik; }
    public void setNamaPemilik(String v)    { this.namaPemilik = v; }
    public String getNamaVarian()           { return namaVarian; }
    public void setNamaVarian(String v)     { this.namaVarian = v; }
    public String getUkuranTerpilih()       { return ukuranTerpilih; }
    public void setUkuranTerpilih(String v) { this.ukuranTerpilih = v; }
    public String getDatetimeSewa()          { return datetimeSewa; }
    public void setDatetimeSewa(String v)    { this.datetimeSewa = v; }
    public String getDatetimeKembali()       { return datetimeKembali; }
    public void setDatetimeKembali(String v) { this.datetimeKembali = v; }
    public double getDenda()                 { return denda; }
    public void setDenda(double v)           { this.denda = v; }
    public String getStatus()               { return status; }
    public void setStatus(String v)         { this.status = v; }
}
