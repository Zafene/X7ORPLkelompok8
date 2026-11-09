package com.bikerent.ui.panel;

import com.bikerent.dao.PenyewaanDAO;
import com.bikerent.model.Penyewaan;
import com.bikerent.ui.Muat;
import com.bikerent.ui.comp.Banner;
import com.bikerent.ui.comp.Kartu;
import com.bikerent.ui.comp.KartuStatistik;
import com.bikerent.ui.comp.TabelRapi;
import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Fmt;
import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

public class PanelBeranda extends JPanel implements Muat {

    private final PenyewaanDAO dao = new PenyewaanDAO();

    private final KartuStatistik kTotal, kDisewa, kSelesai, kPendapatan;
    private final JPanel daftarJenis = new JPanel();
    private final DefaultTableModel modelTerbaru =
            TabelRapi.model("No.", "Nama Pelanggan", "Jenis Sepeda", "Tanggal", "Status");

    private static final Color[] WARNA_JENIS = { Theme.PURPLE, Theme.GREEN, Theme.BLUE, Theme.ORANGE };

    public PanelBeranda() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));

        Banner banner = new Banner();
        kunci(banner, 140);
        isi.add(banner);
        isi.add(Box.createVerticalStrut(14));

        kTotal      = new KartuStatistik("Total Orderan", "0", Ikon.papan(19, Theme.ORANGE), Theme.ORANGE, Theme.ORANGE_SOFT);
        kDisewa     = new KartuStatistik("Sedang Disewa", "0", Ikon.kotak(19, Theme.BLUE), Theme.BLUE, Theme.BLUE_SOFT);
        kSelesai    = new KartuStatistik("Selesai", "0", Ikon.centang(19, Theme.GREEN), Theme.GREEN, Theme.GREEN_SOFT);
        kPendapatan = new KartuStatistik("Pendapatan", "Rp 0", Ikon.dompet(19, Theme.ORANGE), Theme.ORANGE, Theme.ORANGE_SOFT);

        JPanel grid = new JPanel(new GridLayout(1, 4, 12, 0));
        grid.setOpaque(false);
        grid.add(kTotal);
        grid.add(kDisewa);
        grid.add(kSelesai);
        grid.add(kPendapatan);

        Kartu ringkasan = new Kartu(new BorderLayout(0, 12));
        ringkasan.padding(14);
        ringkasan.add(Kartu.judul("Ringkasan Rental"), BorderLayout.NORTH);
        ringkasan.add(grid, BorderLayout.CENTER);
        kunci(ringkasan, 132);
        isi.add(ringkasan);
        isi.add(Box.createVerticalStrut(14));

        JPanel baris = new JPanel(new GridLayout(1, 2, 14, 0));
        baris.setOpaque(false);
        baris.add(kartuJenis());
        baris.add(kartuTerbaru());
        baris.setAlignmentX(Component.LEFT_ALIGNMENT);
        baris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        isi.add(baris);

        add(isi, BorderLayout.CENTER);
    }

    private void kunci(JComponent c, int tinggi) {
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, tinggi));
        c.setPreferredSize(new Dimension(10, tinggi));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private Kartu kartuJenis() {
        Kartu k = new Kartu(new BorderLayout(0, 10));
        k.padding(14);
        k.add(Kartu.judul("Orderan per Jenis Sepeda"), BorderLayout.NORTH);

        daftarJenis.setOpaque(false);
        daftarJenis.setLayout(new BoxLayout(daftarJenis, BoxLayout.Y_AXIS));
        daftarJenis.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel bungkus = new JPanel(new BorderLayout());
        bungkus.setOpaque(false);
        bungkus.add(daftarJenis, BorderLayout.NORTH);
        k.add(bungkus, BorderLayout.CENTER);
        return k;
    }

    private Kartu kartuTerbaru() {
        Kartu k = new Kartu(new BorderLayout(0, 10));
        k.padding(14);
        k.add(Kartu.judul("Orderan Terbaru"), BorderLayout.NORTH);

        JTable tabel = TabelRapi.buat(modelTerbaru);
        TabelRapi.lebarKolom(tabel, 40, 130, 110, 90, 80);
        TabelRapi.kolomBadge(tabel, 4);
        k.add(TabelRapi.gulir(tabel), BorderLayout.CENTER);
        return k;
    }

    @Override
    public void muatData() {
        kTotal.setNilai(String.valueOf(dao.totalOrderan()));
        kDisewa.setNilai(String.valueOf(dao.jumlahStatus("Disewa")));
        kSelesai.setNilai(String.valueOf(dao.jumlahStatus("Selesai")));
        kPendapatan.setNilai(Fmt.rupiah(dao.totalPendapatan()));

        Map<String, Integer> perJenis = dao.orderanPerJenis();
        int total = 0;
        for (int v : perJenis.values()) total += v;

        daftarJenis.removeAll();
        int i = 0;
        for (Map.Entry<String, Integer> e : perJenis.entrySet()) {
            Color w = WARNA_JENIS[i % WARNA_JENIS.length];
            int persen = total == 0 ? 0 : Math.round(e.getValue() * 100f / total);

            JPanel baris = new JPanel(new BorderLayout(8, 0));
            baris.setOpaque(false);
            baris.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            baris.add(new JLabel(Ikon.titik(10, w)), BorderLayout.WEST);
            baris.add(Kartu.label(e.getKey(), 12, Theme.TEXT, false), BorderLayout.CENTER);
            baris.add(Kartu.label(e.getValue() + " (" + persen + "%)", 12, Theme.TEXT_MUTED, false), BorderLayout.EAST);
            baris.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
            baris.setAlignmentX(Component.LEFT_ALIGNMENT);
            daftarJenis.add(baris);
            i++;
        }
        daftarJenis.revalidate();
        daftarJenis.repaint();

        modelTerbaru.setRowCount(0);
        List<Penyewaan> terbaru = dao.terbaru(5);
        int no = 1;
        for (Penyewaan p : terbaru) {
            modelTerbaru.addRow(new Object[] {
                    no++, p.getNamaPelanggan(), p.getNamaSepeda(),
                    Fmt.dbKeTampil(p.getTanggalSewa()), p.getStatus()
            });
        }
    }
}
