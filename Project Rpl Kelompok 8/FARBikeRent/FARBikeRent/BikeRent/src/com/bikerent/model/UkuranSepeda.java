package com.bikerent.model;

public class UkuranSepeda {
    private int id;
    private int sepedaId;
    private String ukuran;
    private int tinggiMin;
    private int tinggiMax;

    public UkuranSepeda() {}
    public UkuranSepeda(int id, int sepedaId, String ukuran, int tinggiMin, int tinggiMax) {
        this.id = id; this.sepedaId = sepedaId; this.ukuran = ukuran;
        this.tinggiMin = tinggiMin; this.tinggiMax = tinggiMax;
    }

    public int getId()              { return id; }
    public void setId(int v)        { this.id = v; }
    public int getSepedaId()        { return sepedaId; }
    public void setSepedaId(int v)  { this.sepedaId = v; }
    public String getUkuran()       { return ukuran; }
    public void setUkuran(String v) { this.ukuran = v; }
    public int getTinggiMin()       { return tinggiMin; }
    public void setTinggiMin(int v) { this.tinggiMin = v; }
    public int getTinggiMax()       { return tinggiMax; }
    public void setTinggiMax(int v) { this.tinggiMax = v; }

    @Override public String toString() { return ukuran + " (" + tinggiMin + "-" + tinggiMax + " cm)"; }
}
