package com.bikerent.model;

public class VarianSepeda {
    private int id;
    private int sepedaId;
    private String namaModel;
    private double hargaSewa;

    public VarianSepeda() {}
    public VarianSepeda(int id, int sepedaId, String namaModel, double hargaSewa) {
        this.id = id; this.sepedaId = sepedaId; this.namaModel = namaModel; this.hargaSewa = hargaSewa;
    }

    public int getId()                 { return id; }
    public void setId(int v)           { this.id = v; }
    public int getSepedaId()           { return sepedaId; }
    public void setSepedaId(int v)     { this.sepedaId = v; }
    public String getNamaModel()       { return namaModel; }
    public void setNamaModel(String v) { this.namaModel = v; }
    public double getHargaSewa()       { return hargaSewa; }
    public void setHargaSewa(double v) { this.hargaSewa = v; }

    @Override public String toString() { return namaModel; }
}
