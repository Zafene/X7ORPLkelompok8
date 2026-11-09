package com.bikerent.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class Fmt {
    private Fmt() {}

    private static final DecimalFormatSymbols SYM = new DecimalFormatSymbols(new Locale("id","ID"));
    static { SYM.setGroupingSeparator('.'); SYM.setDecimalSeparator(','); }
    private static final DecimalFormat RIBUAN = new DecimalFormat("#,##0", SYM);

    public static String rupiah(double n)  { return "Rp " + RIBUAN.format(Math.round(n)); }
    public static String ribuan(double n)  { return RIBUAN.format(Math.round(n)); }

    public static final DateTimeFormatter DB     = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TAMPIL = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String keDb(LocalDate d)     { return d.format(DB); }
    public static String keTampil(LocalDate d) { return d.format(TAMPIL); }
    public static LocalDate dariDb(String s)   { return LocalDate.parse(s, DB); }

    public static String dbKeTampil(String s) {
        try { return keTampil(dariDb(s)); } catch (Exception e) { return s; }
    }
    public static LocalDate dariTampil(String s) {
        try { return LocalDate.parse(s.trim(), TAMPIL); } catch (Exception e) { return null; }
    }

    private static final String[] BULAN = {
        "Jan","Feb","Mar","Apr","Mei","Jun","Jul","Agu","Sep","Okt","Nov","Des"
    };
    private static final String[] HARI = {
        "Minggu","Senin","Selasa","Rabu","Kamis","Jumat","Sabtu"
    };

    public static String namaBulan(int m) { return BULAN[Math.max(1,Math.min(12,m))-1]; }

    public static String formatJam(LocalDateTime dt) {
        String hari = HARI[dt.getDayOfWeek().getValue() % 7];
        return String.format("%s, %d %s %d  ·  %02d:%02d:%02d",
            hari, dt.getDayOfMonth(), namaBulan(dt.getMonthValue()),
            dt.getYear(), dt.getHour(), dt.getMinute(), dt.getSecond());
    }

    public static double angka(String teks) {
        if (teks == null) return 0;
        String s = teks.replaceAll("[^0-9]","");
        if (s.isEmpty()) return 0;
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 0; }
    }
}
