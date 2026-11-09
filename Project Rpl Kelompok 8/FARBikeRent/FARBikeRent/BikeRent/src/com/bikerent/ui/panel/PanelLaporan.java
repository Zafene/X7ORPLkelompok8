package com.bikerent.ui.panel;

import com.bikerent.dao.PenyewaanDAO;
import com.bikerent.model.Penyewaan;
import com.bikerent.ui.Muat;
import com.bikerent.ui.comp.Kartu;
import com.bikerent.ui.comp.KartuStatistik;
import com.bikerent.ui.comp.Medan;
import com.bikerent.ui.comp.TabelRapi;
import com.bikerent.ui.comp.Tombol;
import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Fmt;
import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelLaporan extends JPanel implements Muat {

    private static final int BARIS_PER_HALAMAN = 6;

    private final PenyewaanDAO dao = new PenyewaanDAO();

    private final Medan.Teks txtPeriode = new Medan.Teks("dd/MM/yyyy - dd/MM/yyyy");
    private final Medan.Combo<String> cbJenis = Medan.combo(new String[] { "Semua", "Disewa", "Selesai" });

    private final KartuStatistik kTotal, kPendapatan, kSelesai, kAktif;
    private final DefaultTableModel modelJenis = TabelRapi.model("Jenis Sepeda", "Orderan", "Pendapatan");
    private final DefaultTableModel modelDetail = TabelRapi.model(
            "No.", "Nama Pelanggan", "Jenis Sepeda", "Tanggal", "Hari", "Total", "Status");
    private final JPanel panelHalaman = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));

    private List<Penyewaan> data = new ArrayList<>();
    private int halaman = 1;

    public PanelLaporan() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(bilahFilter(), BorderLayout.NORTH);

        kTotal      = mini("Total Orderan", "0", Ikon.papan(17, Theme.ORANGE), Theme.ORANGE_SOFT);
        kPendapatan = mini("Pendapatan", "Rp 0", Ikon.dompet(17, Theme.BLUE), Theme.BLUE_SOFT);
        kSelesai    = mini("Orderan Selesai", "0", Ikon.centang(17, Theme.GREEN), Theme.GREEN_SOFT);
        kAktif      = mini("Orderan Aktif", "0", Ikon.kotak(17, Theme.PURPLE), Theme.PURPLE_SOFT);

        JPanel isi = new JPanel(new GridBagLayout());
        isi.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;

        c.gridy = 0; c.weighty = 0; c.gridx = 0; c.gridwidth = 2; c.weightx = 1;
        c.insets = new Insets(0, 0, 12, 0);
        isi.add(kartuRingkasan(), c);

        c.gridy = 1; c.weighty = 1; c.gridwidth = 1;
        c.gridx = 0; c.weightx = 0.38; c.insets = new Insets(0, 0, 0, 12);
        isi.add(kartuPerJenis(), c);
        c.gridx = 1; c.weightx = 0.62; c.insets = new Insets(0, 0, 0, 0);
        isi.add(kartuDetail(), c);

        add(isi, BorderLayout.CENTER);
    }

    private KartuStatistik mini(String label, String nilai, javax.swing.Icon ikon, Color latar) {
        return new KartuStatistik(label, nilai, ikon, Theme.TEXT, latar);
    }

    private JPanel bilahFilter() {
        Kartu k = new Kartu(new GridBagLayout());
        k.padding(12, 14, 12, 14);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;

        String[] rentang = dao.rentangTanggal();
        txtPeriode.setText(Fmt.dbKeTampil(rentang[0]) + " - " + Fmt.dbKeTampil(rentang[1]));
        Medan.tinggi(txtPeriode, 36);
        Medan.tinggi(cbJenis, 36);

        c.gridx = 0; c.weightx = 0.55; c.insets = new Insets(0, 0, 0, 12);
        k.add(blokFilter("Periode", txtPeriode), c);
        c.gridx = 1; c.weightx = 0.30;
        k.add(blokFilter("Jenis Laporan", cbJenis), c);

        Tombol tampilkan = new Tombol("Tampilkan");
        tampilkan.padding(10, 20, 10, 20);
        tampilkan.addActionListener(e -> { halaman = 1; muatData(); });

        Tombol export = new Tombol("Export PDF", Ikon.unduh(15, Theme.ORANGE));
        export.gaya(Tombol.Gaya.GARIS).teksWarna(Theme.ORANGE).padding(10, 16, 10, 16);
        export.addActionListener(e -> exportPdf());

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setOpaque(false);
        tombol.add(tampilkan);
        tombol.add(export);

        c.gridx = 2; c.weightx = 0.15; c.insets = new Insets(14, 0, 0, 0);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        k.add(tombol, c);

        k.setPreferredSize(new Dimension(10, 74));
        return k;
    }

    private JPanel blokFilter(String label, javax.swing.JComponent input) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.add(Medan.labelInput(label, false), BorderLayout.NORTH);
        p.add(input, BorderLayout.CENTER);
        return p;
    }

    private Kartu kartuRingkasan() {
        Kartu k = new Kartu(new BorderLayout());
        k.padding(14);
        JPanel grid = new JPanel(new GridLayout(1, 4, 10, 0));
        grid.setOpaque(false);
        grid.add(kTotal);
        grid.add(kPendapatan);
        grid.add(kSelesai);
        grid.add(kAktif);
        k.add(grid, BorderLayout.CENTER);
        k.setPreferredSize(new Dimension(10, 100));
        return k;
    }

    private Kartu kartuPerJenis() {
        Kartu k = new Kartu(new BorderLayout(0, 10));
        k.padding(14);
        k.add(Kartu.judul("Pendapatan per Jenis Sepeda"), BorderLayout.NORTH);
        JTable t = TabelRapi.buat(modelJenis);
        TabelRapi.lebarKolom(t, 130, 70, 110);
        k.add(TabelRapi.gulir(t), BorderLayout.CENTER);
        k.setPreferredSize(new Dimension(10, 280));
        return k;
    }

    private Kartu kartuDetail() {
        Kartu k = new Kartu(new BorderLayout(0, 10));
        k.padding(14);
        k.add(Kartu.judul("Detail Orderan"), BorderLayout.NORTH);

        JTable t = TabelRapi.buat(modelDetail);
        TabelRapi.lebarKolom(t, 40, 140, 116, 92, 50, 96, 78);
        TabelRapi.kolomBadge(t, 6);
        JScrollPane sp = TabelRapi.gulir(t);
        sp.setPreferredSize(new Dimension(10, 218));
        k.add(sp, BorderLayout.CENTER);

        panelHalaman.setOpaque(false);
        k.add(panelHalaman, BorderLayout.SOUTH);
        k.setPreferredSize(new Dimension(10, 280));
        return k;
    }

    private LocalDate[] periode() {
        String[] bagian = txtPeriode.getText().split("-");
        if (bagian.length == 2) {
            LocalDate a = Fmt.dariTampil(bagian[0]);
            LocalDate b = Fmt.dariTampil(bagian[1]);
            if (a != null && b != null && !a.isAfter(b)) return new LocalDate[] { a, b };
        }
        String[] rentang = dao.rentangTanggal();
        return new LocalDate[] { Fmt.dariDb(rentang[0]), Fmt.dariDb(rentang[1]) };
    }

    @Override
    public void muatData() {
        LocalDate[] p = periode();
        String dari = Fmt.keDb(p[0]), sampai = Fmt.keDb(p[1]);
        String status = (String) cbJenis.getSelectedItem();

        data = dao.periode(dari, sampai, status);

        kTotal.setNilai(String.valueOf(dao.totalOrderan(dari, sampai)));
        kPendapatan.setNilai(Fmt.rupiah(dao.totalPendapatan(dari, sampai)));
        kSelesai.setNilai(String.valueOf(dao.jumlahStatus("Selesai", dari, sampai)));
        kAktif.setNilai(String.valueOf(dao.jumlahStatus("Disewa", dari, sampai)));

        modelJenis.setRowCount(0);
        Map<String, Double> perJenis = dao.pendapatanPerJenis(dari, sampai);
        Map<String, Integer> jumlahPerJenis = dao.orderanPerJenis();
        for (Map.Entry<String, Double> e : perJenis.entrySet()) {
            Integer jml = jumlahPerJenis.get(e.getKey());
            modelJenis.addRow(new Object[]{ e.getKey(), jml == null ? 0 : jml, Fmt.rupiah(e.getValue()) });
        }

        tampilHalaman();
    }

    private void tampilHalaman() {
        int totalHalaman = Math.max(1, (int) Math.ceil(data.size() / (double) BARIS_PER_HALAMAN));
        halaman = Math.max(1, Math.min(halaman, totalHalaman));

        modelDetail.setRowCount(0);
        int mulai = (halaman - 1) * BARIS_PER_HALAMAN;
        int akhir = Math.min(mulai + BARIS_PER_HALAMAN, data.size());
        for (int i = mulai; i < akhir; i++) {
            Penyewaan p = data.get(i);
            modelDetail.addRow(new Object[] {
                    i + 1, p.getNamaPelanggan(), p.getNamaSepeda(),
                    Fmt.dbKeTampil(p.getTanggalSewa()), p.getDurasi() + " hari",
                    Fmt.rupiah(p.getTotalBayar()), p.getStatus()
            });
        }

        panelHalaman.removeAll();
        panelHalaman.add(tombolHalaman(Ikon.panahKiri(12, Theme.TEXT_MUTED), () -> { halaman--; tampilHalaman(); },
                halaman > 1));
        int dariH = Math.max(1, Math.min(halaman - 2, totalHalaman - 4));
        for (int h = dariH; h <= Math.min(totalHalaman, dariH + 4); h++) {
            final int target = h;
            panelHalaman.add(tombolAngka(String.valueOf(h), () -> { halaman = target; tampilHalaman(); },
                    h == halaman));
        }
        panelHalaman.add(tombolHalaman(Ikon.panahKanan(12, Theme.TEXT_MUTED), () -> { halaman++; tampilHalaman(); },
                halaman < totalHalaman));
        panelHalaman.revalidate();
        panelHalaman.repaint();
    }

    private Tombol tombolAngka(String teks, Runnable aksi, boolean aktif) {
        Tombol t = new Tombol(teks);
        t.gaya(aktif ? Tombol.Gaya.ISI : Tombol.Gaya.GARIS).radius(7).padding(5, 11, 5, 11);
        t.setFont(Theme.fontBold(12));
        t.addActionListener(e -> aksi.run());
        return t;
    }

    private Tombol tombolHalaman(javax.swing.Icon ikon, Runnable aksi, boolean hidup) {
        Tombol t = new Tombol("", ikon);
        t.gaya(Tombol.Gaya.GARIS).radius(7).padding(6, 10, 6, 10);
        t.setEnabled(hidup);
        t.addActionListener(e -> aksi.run());
        return t;
    }

    private void exportPdf() {
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada data untuk diexport pada periode ini.",
                    "FAR BikeRent", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Laporan FAR BikeRent");
        job.setPrintable(new CetakLaporan(new ArrayList<>(data), txtPeriode.getText()));
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Gagal mencetak: " + e.getMessage(),
                        "FAR BikeRent", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class CetakLaporan implements Printable {
        private final List<Penyewaan> isi;
        private final String periode;
        private static final int BARIS_PER_KERTAS = 28;

        CetakLaporan(List<Penyewaan> isi, String periode) {
            this.isi = isi;
            this.periode = periode;
        }

        @Override
        public int print(Graphics g0, PageFormat pf, int halaman) throws PrinterException {
            int mulai = halaman * BARIS_PER_KERTAS;
            if (mulai >= isi.size()) return NO_SUCH_PAGE;

            Graphics2D g = (Graphics2D) g0;
            Theme.aa(g);
            g.translate(pf.getImageableX(), pf.getImageableY());
            int lebar = (int) pf.getImageableWidth();
            int y = 24;

            g.setColor(Color.BLACK);
            g.setFont(new Font(Theme.family(), Font.BOLD, 16));
            g.drawString("Laporan Penyewaan Sepeda - FAR BikeRent", 0, y);
            y += 18;
            g.setFont(new Font(Theme.family(), Font.PLAIN, 10));
            g.drawString("Periode: " + periode + "   |   Dicetak: " + Fmt.keTampil(LocalDate.now()), 0, y);
            y += 16;
            g.drawLine(0, y, lebar, y);
            y += 16;

            int[] kolom = { 0, 30, 170, 280, 360, 410, 510 };
            String[] judul = { "No", "Nama Pelanggan", "Jenis Sepeda", "Tanggal", "Durasi", "Total", "Status" };
            g.setFont(new Font(Theme.family(), Font.BOLD, 10));
            for (int i = 0; i < judul.length; i++) g.drawString(judul[i], kolom[i], y);
            y += 6;
            g.drawLine(0, y, lebar, y);
            y += 14;

            g.setFont(new Font(Theme.family(), Font.PLAIN, 10));
            int akhir = Math.min(mulai + BARIS_PER_KERTAS, isi.size());
            double total = 0;
            for (int i = mulai; i < akhir; i++) {
                Penyewaan p = isi.get(i);
                total += p.getTotalBayar();
                String[] sel = {
                        String.valueOf(i + 1), p.getNamaPelanggan(), p.getNamaSepeda(),
                        Fmt.dbKeTampil(p.getTanggalSewa()), p.getDurasi() + " hari",
                        Fmt.rupiah(p.getTotalBayar()), p.getStatus()
                };
                for (int j = 0; j < sel.length; j++) g.drawString(sel[j], kolom[j], y);
                y += 15;
            }

            y += 6;
            g.drawLine(0, y, lebar, y);
            y += 16;
            g.setFont(new Font(Theme.family(), Font.BOLD, 11));
            g.drawString("Subtotal halaman ini: " + Fmt.rupiah(total), 0, y);
            return PAGE_EXISTS;
        }
    }
}
