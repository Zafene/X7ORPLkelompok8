package com.bikerent.ui.panel;

import com.bikerent.dao.PenyewaanDAO;
import com.bikerent.dao.SepedaDAO;
import com.bikerent.dao.VarianSepedaDAO;
import com.bikerent.model.Penyewaan;
import com.bikerent.model.Sepeda;
import com.bikerent.model.UkuranSepeda;
import com.bikerent.model.VarianSepeda;
import com.bikerent.ui.Muat;
import com.bikerent.ui.comp.Kartu;
import com.bikerent.ui.comp.Medan;
import com.bikerent.ui.comp.Penambah;
import com.bikerent.ui.comp.Saklar;
import com.bikerent.ui.comp.TabelRapi;
import com.bikerent.ui.comp.Tombol;
import com.bikerent.ui.icon.GambarSepeda;
import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Fmt;
import com.bikerent.util.Theme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PanelPenyewaan extends JPanel implements Muat {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String VARIAN_DEFAULT = "(Model Standar)";
    private static final String UKURAN_KOSONG  = "(Pilih Ukuran)";
    private static final double JAM_GRACE      = 2;
    private static final double DENDA_PER_JAM  = 0.05;

    private final SepedaDAO       sepedaDAO    = new SepedaDAO();
    private final PenyewaanDAO    penyewaanDAO = new PenyewaanDAO();
    private final VarianSepedaDAO varianDAO    = new VarianSepedaDAO();

    private List<Sepeda> daftarSepeda = new ArrayList<>();
    private Sepeda        terpilih;

    private final DefaultTableModel modelSepeda =
            TabelRapi.model("No.", "Jenis Sepeda", "Stok", "Harga Default / Hari");
    private final JTable       tabelSepeda = TabelRapi.buat(modelSepeda);
    private final GambarSepeda pratinjau   = new GambarSepeda(GambarSepeda.MTB);
    private final JLabel pvNama   = Kartu.label("-", 13, Theme.TEXT, true);
    private final JLabel pvUkuran = Kartu.label("-", 10, Theme.TEXT_MUTED, false);
    private final JLabel pvTipe   = Kartu.label("-", 10, Theme.TEXT_MUTED, false);
    private final Medan.Combo<Object> cbVarian = Medan.combo(new Object[]{VARIAN_DEFAULT});
    private final JLabel lblHargaVarian = Kartu.label("Rp 0 / hari", 11, Theme.ORANGE, true);
    private final Medan.Combo<Object> cbUkuran = Medan.combo(new Object[]{UKURAN_KOSONG});

    private final Medan.Teks txtNama    = new Medan.Teks("Nama lengkap pelanggan");
    private final Medan.Teks txtTanggal = new Medan.Teks("dd/MM/yyyy");
    private final Penambah   durasi     = new Penambah(1, 1, 30);
    private final JLabel lblHargaHari   = Kartu.label("Rp 0 / hari", 12, Theme.TEXT_MUTED, false);
    private final JLabel lblTotalSewa   = Kartu.label("Rp 0", 12, Theme.TEXT, true);
    private final Saklar saklarOngkir   = new Saklar(false);
    private final Medan.Teks txtJarak   = new Medan.Teks("0");
    private final Medan.Teks txtKmL     = new Medan.Teks("0");
    private final Medan.Teks txtBensin  = new Medan.Teks("0");
    private final JLabel lblOngkir      = Kartu.label("Rp 0", 15, Theme.TEXT, true);
    private final Medan.Teks txtUangLelah = new Medan.Teks("0");
    private JPanel panelOngkir;

    private final JRadioButton rbCash     = radio("Cash");
    private final JRadioButton rbTransfer = radio("Transfer");
    private final Medan.Teks txtJumlahBayar = new Medan.Teks("Rp 0");
    private final Medan.Combo<String> cbBank =
            Medan.combo(new String[]{"BCA","BNI","BRI","Mandiri","BSI","CIMB Niaga"});
    private final Medan.Teks txtRekening = new Medan.Teks("1234 5678 9012");
    private final Medan.Teks txtPemilik  = new Medan.Teks("PT. FAR BikeRent");
    private JPanel panelCash, panelTransfer;

    private final JPanel panelKartuAktif = new JPanel();

    private Runnable saatTersimpan;
    public void saatTersimpan(Runnable r) { this.saatTersimpan = r; }

    public PanelPenyewaan() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.fontBold(12));
        tabs.setOpaque(false);

        JPanel tabBaru = new JPanel(new GridBagLayout());
        tabBaru.setOpaque(false);
        tabBaru.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH; c.gridy = 0; c.weighty = 1;
        c.insets = new Insets(0, 0, 0, 12);
        c.gridx = 0; c.weightx = 0.32; tabBaru.add(lebar(kolomPilihSepeda(), 296), c);
        c.gridx = 1; c.weightx = 0.36; tabBaru.add(lebar(kolomDetail(), 310), c);
        c.insets = new Insets(0, 0, 0, 0);
        c.gridx = 2; c.weightx = 0.32; tabBaru.add(lebar(kolomPembayaran(), 290), c);
        tabs.addTab("  Sewa Baru  ", tabBaru);
        tabs.addTab("  Sewa Aktif  ", buatTabSewaAktif());
        tabs.addChangeListener(e -> { if (tabs.getSelectedIndex() == 1) muatSewaAktif(); });

        add(tabs, BorderLayout.CENTER);
        pasangPendengar();
    }

    private JPanel lebar(JPanel p, int lMin) {
        p.setPreferredSize(new Dimension(lMin, 10));
        p.setMinimumSize(new Dimension(lMin - 30, 10));
        return p;
    }

    private JPanel kolomPilihSepeda() {
        JPanel kolom = new JPanel(new BorderLayout(0, 12));
        kolom.setOpaque(false);

        Kartu k = new Kartu(new BorderLayout(0, 10));
        k.padding(14);
        k.add(Kartu.judul("1. Pilih Sepeda"), BorderLayout.NORTH);
        tabelSepeda.setRowHeight(34);
        TabelRapi.lebarKolom(tabelSepeda, 40, 96, 40, 108);
        tabelSepeda.getColumnModel().getColumn(0).setCellRenderer(new RendererRadio());
        tabelSepeda.getSelectionModel().addListSelectionListener(
                e -> { if (!e.getValueIsAdjusting()) pilihBaris(tabelSepeda.getSelectedRow()); });
        JScrollPane sp = TabelRapi.gulir(tabelSepeda);
        sp.setPreferredSize(new Dimension(10, 172));
        k.add(sp, BorderLayout.CENTER);
        k.setPreferredSize(new Dimension(10, 232));
        kolom.add(k, BorderLayout.NORTH);

        Kartu pv = new Kartu(new BorderLayout(10, 0));
        pv.padding(12);
        pratinjau.setPreferredSize(new Dimension(90, 84));
        pv.add(pratinjau, BorderLayout.WEST);
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        pvNama.setAlignmentX(Component.LEFT_ALIGNMENT);
        info.add(pvNama); info.add(Box.createVerticalStrut(6));
        info.add(ikonBaris(Ikon.penggaris(11, Theme.TEXT_LIGHT), pvUkuran));
        info.add(ikonBaris(Ikon.label(11, Theme.TEXT_LIGHT), pvTipe));
        pv.add(info, BorderLayout.CENTER);
        pv.setPreferredSize(new Dimension(10, 96));

        JPanel varPanel = new JPanel();
        varPanel.setOpaque(false);
        varPanel.setLayout(new BoxLayout(varPanel, BoxLayout.Y_AXIS));
        varPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        Medan.tinggi(cbVarian, 32);
        JPanel rowVarian = new JPanel(new BorderLayout(8, 0));
        rowVarian.setOpaque(false);
        rowVarian.add(cbVarian, BorderLayout.CENTER);
        lblHargaVarian.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        rowVarian.add(lblHargaVarian, BorderLayout.EAST);
        varPanel.add(grupKecil("Model / Varian & Harga", rowVarian));

        JPanel rowUkuran = new JPanel(new BorderLayout(6, 0));
        rowUkuran.setOpaque(false);
        Medan.tinggi(cbUkuran, 32);
        Tombol btnInfo = new Tombol("Info Ukuran");
        btnInfo.gaya(Tombol.Gaya.GARIS).padding(4, 10, 4, 10);
        btnInfo.addActionListener(e -> tampilkanInfoUkuran());
        rowUkuran.add(cbUkuran, BorderLayout.CENTER);
        rowUkuran.add(btnInfo, BorderLayout.EAST);
        varPanel.add(grupKecil("Ukuran", rowUkuran));

        JPanel bungkus = new JPanel(new BorderLayout(0, 10));
        bungkus.setOpaque(false);
        bungkus.add(pv, BorderLayout.NORTH);
        bungkus.add(varPanel, BorderLayout.CENTER);
        kolom.add(bungkus, BorderLayout.CENTER);
        return kolom;
    }

    private JPanel ikonBaris(javax.swing.Icon ikon, JLabel teks) {
        JPanel p = new JPanel(new BorderLayout(5, 0));
        p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        p.add(new JLabel(ikon), BorderLayout.WEST); p.add(teks, BorderLayout.CENTER);
        return p;
    }

    private JPanel grupKecil(String lbl, JComponent input) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = Medan.labelInput(lbl, false);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(3)); p.add(input);
        p.add(Box.createVerticalStrut(8));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, input.getPreferredSize().height + 32));
        return p;
    }

    private void tampilkanInfoUkuran() {
        if (terpilih == null) { pesan("Pilih jenis sepeda terlebih dahulu.", JOptionPane.INFORMATION_MESSAGE); return; }
        List<UkuranSepeda> ukuranList = varianDAO.daftarUkuran(terpilih.getId());
        if (ukuranList.isEmpty()) { pesan("Belum ada data ukuran untuk " + terpilih.getNama() + ".", JOptionPane.INFORMATION_MESSAGE); return; }
        DefaultTableModel tm = new DefaultTableModel(new String[]{"Ukuran","Tinggi Badan (cm)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (UkuranSepeda u : ukuranList) tm.addRow(new Object[]{u.getUkuran(), u.getTinggiMin() + " – " + u.getTinggiMax() + " cm"});
        JTable tbl = TabelRapi.buat(tm); tbl.setRowHeight(28);
        JScrollPane sc = new JScrollPane(tbl);
        sc.setPreferredSize(new Dimension(340, Math.min(tm.getRowCount() * 28 + 34, 240)));
        JPanel pnl = new JPanel(new BorderLayout(0, 8)); pnl.setOpaque(false);
        pnl.add(Kartu.label("Panduan Ukuran — " + terpilih.getNama(), 13, Theme.TEXT, true), BorderLayout.NORTH);
        pnl.add(sc, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, pnl, "FAR BikeRent", JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel kolomDetail() {
        Kartu k = new Kartu(new BorderLayout(0, 10));
        k.padding(14);
        k.add(Kartu.judul("2. Detail Penyewaan"), BorderLayout.NORTH);
        JPanel form = new JPanel(); form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        Medan.tinggi(txtNama, 34);
        form.add(grup("Nama Pelanggan", true, txtNama));

        JPanel tgl = new JPanel(new BorderLayout(6,0)); tgl.setOpaque(false);
        txtTanggal.setText(Fmt.keTampil(LocalDate.now())); Medan.tinggi(txtTanggal, 34);
        Tombol pilihTgl = new Tombol("", Ikon.kalender(14, Theme.TEXT_MUTED));
        pilihTgl.gaya(Tombol.Gaya.GARIS).padding(7,9,7,9);
        pilihTgl.addActionListener(e -> pilihTanggal());
        tgl.add(txtTanggal, BorderLayout.CENTER); tgl.add(pilihTgl, BorderLayout.EAST);
        form.add(grup("Tanggal Sewa", true, tgl));

        Medan.tinggi(durasi, 34);
        form.add(grup("Durasi (hari)", true, durasi));

        JPanel harga = kotakInfo(new Color(0xF8F9FA));
        harga.add(lblHargaHari, BorderLayout.WEST); harga.add(lblTotalSewa, BorderLayout.EAST);
        Medan.tinggi(harga, 34);
        form.add(grup("Harga Sewa", false, harga));

        JPanel judulOngkir = new JPanel(new BorderLayout()); judulOngkir.setOpaque(false);
        judulOngkir.add(Kartu.label("Hitung Ongkir (Opsional)", 12, Theme.TEXT, true), BorderLayout.WEST);
        judulOngkir.add(saklarOngkir, BorderLayout.EAST);
        judulOngkir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
        judulOngkir.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(Box.createVerticalStrut(4)); form.add(judulOngkir); form.add(Box.createVerticalStrut(6));

        panelOngkir = new JPanel(); panelOngkir.setOpaque(false);
        panelOngkir.setLayout(new BoxLayout(panelOngkir, BoxLayout.Y_AXIS));
        panelOngkir.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel duaKol = new JPanel(new GridLayout(1,2,10,0)); duaKol.setOpaque(false);
        Medan.tinggi(txtJarak,32); Medan.tinggi(txtKmL,32);
        duaKol.add(grup("Jarak (km)",false,txtJarak)); duaKol.add(grup("Km/L",false,txtKmL));
        duaKol.setAlignmentX(Component.LEFT_ALIGNMENT); duaKol.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panelOngkir.add(duaKol);
        Medan.tinggi(txtBensin,32); panelOngkir.add(grup("Harga Bensin (Rp/L)",false,txtBensin));
        JPanel kotakOngkir = kotakInfo(Theme.ORANGE_SOFT);
        kotakOngkir.add(labelKecil("Estimasi Ongkir (PP)", 10), BorderLayout.NORTH);
        kotakOngkir.add(lblOngkir, BorderLayout.SOUTH);
        kotakOngkir.setAlignmentX(Component.LEFT_ALIGNMENT);
        kotakOngkir.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panelOngkir.add(Box.createVerticalStrut(4)); panelOngkir.add(kotakOngkir);
        form.add(panelOngkir);
        aktifkanOngkir(false);

        Medan.tinggi(txtUangLelah, 32);
        form.add(grup("Service Charge (Rp)", false, txtUangLelah));

        JPanel bungkus = new JPanel(new BorderLayout()); bungkus.setOpaque(false);
        bungkus.add(form, BorderLayout.NORTH);
        k.add(bungkus, BorderLayout.CENTER);
        return k;
    }

    private JPanel grup(String label, boolean wajib, JComponent input) {
        JPanel p = new JPanel(); p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = Medan.labelInput(label, wajib); l.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(4)); p.add(input); p.add(Box.createVerticalStrut(9));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, input.getPreferredSize().height + 34));
        return p;
    }
    private JPanel kotakInfo(Color bg) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create(); Theme.aa(g);
                g.setColor(bg); g.fill(new RoundRectangle2D.Double(0,0,getWidth()-1,getHeight()-1,8,8)); g.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(8,11,8,11));
        return p;
    }
    private JLabel labelKecil(String t, int sz) {
        JLabel l = new JLabel(t); l.setFont(Theme.font(sz)); l.setForeground(Theme.TEXT_MUTED); return l;
    }

    private JPanel kolomPembayaran() {
        Kartu k = new Kartu(new BorderLayout(0, 10)); k.padding(14);
        k.add(Kartu.judul("3. Metode Pembayaran"), BorderLayout.NORTH);
        JPanel isi = new JPanel(); isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));

        ButtonGroup bg = new ButtonGroup(); bg.add(rbCash); bg.add(rbTransfer); rbCash.setSelected(true);

        JPanel brCash = new JPanel(new BorderLayout()); brCash.setOpaque(false);
        brCash.add(rbCash, BorderLayout.WEST); brCash.add(new JLabel(Ikon.tunai(16, Theme.TEXT_LIGHT)), BorderLayout.EAST);
        brCash.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28)); brCash.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(brCash); isi.add(Box.createVerticalStrut(6));
        panelCash = new JPanel(); panelCash.setOpaque(false);
        panelCash.setLayout(new BoxLayout(panelCash, BoxLayout.Y_AXIS)); panelCash.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtJumlahBayar.setEnabled(false); Medan.tinggi(txtJumlahBayar, 34);
        panelCash.add(grup("Jumlah Bayar", false, txtJumlahBayar));
        isi.add(panelCash);

        JPanel brTf = new JPanel(new BorderLayout()); brTf.setOpaque(false);
        brTf.add(rbTransfer, BorderLayout.WEST); brTf.add(new JLabel(Ikon.bank(16, Theme.TEXT_LIGHT)), BorderLayout.EAST);
        brTf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28)); brTf.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(brTf); isi.add(Box.createVerticalStrut(6));
        panelTransfer = new JPanel(); panelTransfer.setOpaque(false);
        panelTransfer.setLayout(new BoxLayout(panelTransfer, BoxLayout.Y_AXIS)); panelTransfer.setAlignmentX(Component.LEFT_ALIGNMENT);
        Medan.tinggi(cbBank,34); Medan.tinggi(txtRekening,34); Medan.tinggi(txtPemilik,34);
        panelTransfer.add(grup("Bank Tujuan",false,cbBank));
        panelTransfer.add(grup("No. Rekening",false,txtRekening));
        panelTransfer.add(grup("Nama Pemilik",false,txtPemilik));
        isi.add(panelTransfer);

        Tombol btnSimpan = new Tombol("Simpan Transaksi", Ikon.sepeda(16, Color.WHITE));
        btnSimpan.padding(11,14,11,14); btnSimpan.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSimpan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSimpan.addActionListener(e -> simpan());
        isi.add(Box.createVerticalStrut(6)); isi.add(btnSimpan); isi.add(Box.createVerticalStrut(10));

        JPanel infoBox = kotakInfo(Theme.BLUE_SOFT);
        infoBox.add(new JLabel(Ikon.info(14, Theme.BLUE)), BorderLayout.WEST);
        JLabel ti = new JLabel("<html><body style='width:160px'>Total = harga x hari + ongkir + uang lelah.<br>Denda keterlambatan dihitung saat pengembalian.</body></html>");
        ti.setFont(Theme.font(10)); ti.setForeground(new Color(0x1E40AF));
        ti.setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        infoBox.add(ti, BorderLayout.CENTER);
        infoBox.setAlignmentX(Component.LEFT_ALIGNMENT); infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        isi.add(infoBox);

        JPanel bungkus = new JPanel(new BorderLayout()); bungkus.setOpaque(false);
        bungkus.add(isi, BorderLayout.NORTH);
        k.add(bungkus, BorderLayout.CENTER);
        ubahMetode(); return k;
    }

    private JRadioButton radio(String teks) {
        JRadioButton r = new JRadioButton(teks);
        r.setOpaque(false); r.setFocusPainted(false);
        r.setFont(Theme.fontBold(13)); r.setForeground(Theme.TEXT); r.setIconTextGap(8);
        r.setIcon(ikonRadio(false)); r.setSelectedIcon(ikonRadio(true));
        return r;
    }
    private javax.swing.Icon ikonRadio(boolean sel) {
        return new Ikon(18, sel ? Theme.ORANGE : new Color(0xC7CAD1), 1.8f, g -> {
            g.draw(new Ellipse2D.Double(3.5,3.5,17,17));
            if (sel) { g.setColor(Theme.ORANGE); g.fill(new Ellipse2D.Double(8,8,8,8)); }
        });
    }

    private JPanel buatTabSewaAktif() {
        JPanel tab = new JPanel(new BorderLayout(0, 10));
        tab.setOpaque(false);
        tab.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lbl = Kartu.label("Sewa Aktif — Pantau & Kembalikan", 14, Theme.TEXT, true);
        Tombol btnRefresh = new Tombol("Muat Ulang");
        btnRefresh.gaya(Tombol.Gaya.GARIS).padding(6,12,6,12);
        btnRefresh.addActionListener(e -> muatSewaAktif());
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        header.add(lbl, BorderLayout.WEST); header.add(btnRefresh, BorderLayout.EAST);
        tab.add(header, BorderLayout.NORTH);

        panelKartuAktif.setOpaque(false);
        panelKartuAktif.setLayout(new BoxLayout(panelKartuAktif, BoxLayout.Y_AXIS));
        JScrollPane sp = new JScrollPane(panelKartuAktif);
        sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder());
        tab.add(sp, BorderLayout.CENTER);

        JPanel infoDenda = kotakInfo(new Color(0xFFF7ED));
        infoDenda.add(new JLabel(Ikon.info(14, new Color(0xD97706))), BorderLayout.WEST);
        JLabel td = new JLabel("<html><body style='width:520px'>Denda keterlambatan: grace period 2 jam, "
            + "lalu 5% x harga sewa/hari untuk setiap jam terlambat (dibulatkan ke atas).</body></html>");
        td.setFont(Theme.font(11)); td.setForeground(new Color(0x92400E));
        td.setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
        infoDenda.add(td, BorderLayout.CENTER);
        tab.add(infoDenda, BorderLayout.SOUTH);
        return tab;
    }

    private void muatSewaAktif() {
        panelKartuAktif.removeAll();
        List<Penyewaan> aktif = penyewaanDAO.daftarAktif();
        if (aktif.isEmpty()) {
            JLabel kosong = Kartu.label("Tidak ada sewa aktif saat ini.", 13, Theme.TEXT_MUTED, false);
            kosong.setHorizontalAlignment(JLabel.CENTER);
            kosong.setBorder(BorderFactory.createEmptyBorder(40,0,40,0));
            panelKartuAktif.add(kosong);
        } else {
            for (Penyewaan p : aktif) {
                panelKartuAktif.add(kartuSewaAktif(p));
                panelKartuAktif.add(Box.createVerticalStrut(10));
            }
        }
        panelKartuAktif.revalidate(); panelKartuAktif.repaint();
    }

    private JPanel kartuSewaAktif(Penyewaan p) {
        Kartu k = new Kartu(new BorderLayout(0, 8));
        k.padding(14).radius(12);
        k.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        k.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblNama = Kartu.label("#" + p.getId() + "  " + p.getNamaPelanggan(), 13, Theme.TEXT, true);
        k.add(lblNama, BorderLayout.NORTH);

        String infoParts = p.getNamaSepeda()
            + (p.getNamaVarian() != null ? " · " + p.getNamaVarian() : "")
            + (p.getUkuranTerpilih() != null ? " · Ukuran " + p.getUkuranTerpilih() : "");
        JLabel lblSepeda = Kartu.label(infoParts, 11, Theme.TEXT_MUTED, false);

        LocalDateTime due = hitungDueTime(p);
        String dueStr = due != null ? due.format(DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm")) : "-";
        JLabel lblDue = Kartu.label("Jatuh tempo: " + dueStr + "  ·  " + Fmt.rupiah(p.getHargaSewa()) + "/hari", 11, Theme.TEXT_MUTED, false);

        JPanel midPanel = new JPanel(); midPanel.setOpaque(false);
        midPanel.setLayout(new BoxLayout(midPanel, BoxLayout.Y_AXIS));
        lblSepeda.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDue.setAlignmentX(Component.LEFT_ALIGNMENT);
        midPanel.add(lblSepeda); midPanel.add(Box.createVerticalStrut(3)); midPanel.add(lblDue);
        k.add(midPanel, BorderLayout.CENTER);

        JLabel lblStatus = new JLabel(statusWaktuTeks(due));
        lblStatus.setFont(Theme.fontBold(13));
        lblStatus.setForeground(due == null || !due.isBefore(LocalDateTime.now()) ? new Color(0x059669) : new Color(0xDC2626));

        Tombol btnKembali = new Tombol("Kembalikan Sekarang");
        btnKembali.padding(7,12,7,12);
        btnKembali.addActionListener(e -> prosesKembalikan(p));

        JPanel bawah = new JPanel(new BorderLayout(10, 0));
        bawah.setOpaque(false);
        bawah.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0, Theme.BORDER),
            BorderFactory.createEmptyBorder(8,0,0,0)));
        bawah.add(lblStatus, BorderLayout.WEST);
        bawah.add(btnKembali, BorderLayout.EAST);
        k.add(bawah, BorderLayout.SOUTH);
        return k;
    }

    private String statusWaktuTeks(LocalDateTime due) {
        if (due == null) return "-";
        Duration diff = Duration.between(LocalDateTime.now(), due);
        long secs = diff.abs().getSeconds();
        long jam = secs / 3600, menit = (secs % 3600) / 60;
        if (diff.isNegative()) return String.format("Terlambat %dj %dm", jam, menit);
        return String.format("Sisa %dj %dm", jam, menit);
    }

    private LocalDateTime hitungDueTime(Penyewaan p) {
        if (p.getDatetimeSewa() != null && !p.getDatetimeSewa().isEmpty()) {
            try { return LocalDateTime.parse(p.getDatetimeSewa(), DT_FMT).plusDays(p.getDurasi()); } catch (Exception ignored) {}
        }
        try { return LocalDate.parse(p.getTanggalSewa()).atStartOfDay().plusDays(p.getDurasi()); } catch (Exception ignored) {}
        return null;
    }

    private void prosesKembalikan(Penyewaan p) {
        LocalDateTime actual = LocalDateTime.now();
        LocalDateTime due    = hitungDueTime(p);
        double denda = 0;
        String pesanDenda;

        if (due != null) {
            Duration terlambat = Duration.between(due, actual);
            if (!terlambat.isNegative()) {
                double jamExact = terlambat.toMinutes() / 60.0;
                if (jamExact > JAM_GRACE) {
                    int jamKena = (int) Math.ceil(jamExact - JAM_GRACE);
                    denda = jamKena * DENDA_PER_JAM * p.getHargaSewa();
                    pesanDenda = String.format(
                        "Terlambat <b>%.1f jam</b> (grace %.0f jam).<br>Jam kena denda: %d jam<br>Denda: %d x 5%% x %s = <b>%s</b>",
                        jamExact, JAM_GRACE, jamKena, jamKena, Fmt.rupiah(p.getHargaSewa()), Fmt.rupiah(denda));
                } else {
                    pesanDenda = String.format("Terlambat %.1f jam — masih dalam grace period %.0f jam.<br><b>Tidak ada denda.</b>", jamExact, JAM_GRACE);
                }
            } else {
                pesanDenda = "Dikembalikan tepat waktu. <b>Tidak ada denda.</b>";
            }
        } else {
            pesanDenda = "Data waktu mulai tidak tersedia. Denda tidak dapat dihitung.";
        }

        String msg = String.format(
            "<html><body style='width:310px'><b>Konfirmasi Pengembalian</b><br><br>" +
            "Pelanggan: %s<br>Sepeda: %s%s<br>Waktu sekarang: %s<br><br>%s<br><br>Total tambahan: <b>%s</b></body></html>",
            p.getNamaPelanggan(),
            p.getNamaSepeda(), p.getNamaVarian() != null ? " (" + p.getNamaVarian() + ")" : "",
            actual.format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss")),
            pesanDenda, Fmt.rupiah(denda));

        double finalDenda = denda;
        int ok = JOptionPane.showConfirmDialog(this, msg, "FAR BikeRent — Kembalikan Sepeda",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            penyewaanDAO.kembalikan(p.getId(), actual.format(DT_FMT), finalDenda);
            sepedaDAO.ubahStok(p.getSepedaId(), 1);
            muatSewaAktif();
            if (saatTersimpan != null) saatTersimpan.run();
            pesan("<html>Sepeda berhasil dikembalikan!<br>"
                + (finalDenda > 0 ? "Denda dibayar: " + Fmt.rupiah(finalDenda) : "Tidak ada denda.") + "</html>",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void pasangPendengar() {
        DocumentListener hitung = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { hitungUlang(); }
            public void removeUpdate(DocumentEvent e)  { hitungUlang(); }
            public void changedUpdate(DocumentEvent e) { hitungUlang(); }
        };
        txtJarak.getDocument().addDocumentListener(hitung);
        txtKmL.getDocument().addDocumentListener(hitung);
        txtBensin.getDocument().addDocumentListener(hitung);
        txtUangLelah.getDocument().addDocumentListener(hitung);
        durasi.saatBerubah(this::hitungUlang);
        saklarOngkir.saatBerubah(() -> { aktifkanOngkir(saklarOngkir.isNyala()); hitungUlang(); });
        rbCash.addActionListener(e -> ubahMetode());
        rbTransfer.addActionListener(e -> ubahMetode());
        cbVarian.addActionListener(e -> { hitungUlang(); updateLabelHargaVarian(); });
    }

    private double hitungHargaEfektif() {
        if (terpilih == null) return 0;
        Object sel = cbVarian.getSelectedItem();
        if (sel instanceof VarianSepeda) {
            double h = ((VarianSepeda) sel).getHargaSewa();
            if (h > 0) return h;
        }
        return terpilih.getHargaSewa();
    }

    private void updateLabelHargaVarian() {
        double h = hitungHargaEfektif();
        Object sel = cbVarian.getSelectedItem();
        boolean isVarian = sel instanceof VarianSepeda && ((VarianSepeda) sel).getHargaSewa() > 0;
        lblHargaVarian.setText(Fmt.rupiah(h) + (isVarian ? "/hari" : "/hari (default)"));
        lblHargaVarian.setForeground(isVarian ? Theme.ORANGE : Theme.TEXT_MUTED);
    }

    private void aktifkanOngkir(boolean aktif) {
        for (Component c : panelOngkir.getComponents()) aktifRekursif(c, aktif);
    }
    private void aktifRekursif(Component c, boolean aktif) {
        c.setEnabled(aktif);
        if (c instanceof Container) for (Component a : ((Container)c).getComponents()) aktifRekursif(a, aktif);
    }
    private void ubahMetode() {
        boolean cash = rbCash.isSelected();
        for (Component c : panelCash.getComponents())     aktifRekursif(c, cash);
        for (Component c : panelTransfer.getComponents()) aktifRekursif(c, !cash);
        txtJumlahBayar.setEnabled(false);
    }

    private void pilihBaris(int baris) {
        if (daftarSepeda == null || baris < 0 || baris >= daftarSepeda.size()) return;
        terpilih = daftarSepeda.get(baris);
        pratinjau.setTipe(terpilih.getKode()); pratinjau.setGambar(terpilih.getGambar());
        pvNama.setText(terpilih.getNama());
        pvUkuran.setText("Ukuran : " + terpilih.getUkuran());
        pvTipe.setText("Tipe : " + terpilih.getTipe());

        cbVarian.removeAllItems();
        cbVarian.addItem(VARIAN_DEFAULT);
        for (VarianSepeda v : varianDAO.daftarVarian(terpilih.getId())) cbVarian.addItem(v);

        cbUkuran.removeAllItems();
        cbUkuran.addItem(UKURAN_KOSONG);
        for (UkuranSepeda u : varianDAO.daftarUkuran(terpilih.getId())) cbUkuran.addItem(u);

        updateLabelHargaVarian();
        hitungUlang();
        tabelSepeda.repaint();
    }

    private double hitungOngkir() {
        if (!saklarOngkir.isNyala()) return 0;
        double jarak = Fmt.angka(txtJarak.getText()), kml = Fmt.angka(txtKmL.getText()),
               harga = Fmt.angka(txtBensin.getText());
        if (jarak <= 0 || kml <= 0 || harga <= 0) return 0;
        return (jarak / kml) * harga * 2;
    }

    private void hitungUlang() {
        double perHari = hitungHargaEfektif();
        double totalSewa = perHari * durasi.getNilai();
        double ongkir = hitungOngkir(), uangLelah = Fmt.angka(txtUangLelah.getText());
        lblHargaHari.setText(Fmt.rupiah(perHari) + " / hari");
        lblTotalSewa.setText(Fmt.rupiah(totalSewa));
        lblOngkir.setText(Fmt.rupiah(ongkir));
        txtJumlahBayar.setText(Fmt.rupiah(totalSewa + ongkir + uangLelah));
    }

    private void pilihTanggal() {
        LocalDate d = Fmt.dariTampil(txtTanggal.getText()); if (d == null) d = LocalDate.now();
        Integer[] tgl = new Integer[31]; for (int i=0;i<31;i++) tgl[i]=i+1;
        String[]  bln = new String[12];  for (int i=0;i<12;i++) bln[i]=Fmt.namaBulan(i+1);
        Integer[] thn = new Integer[7];  for (int i=0;i<7;i++)  thn[i]=d.getYear()-3+i;
        Medan.Combo<Integer> cT=Medan.combo(tgl); Medan.Combo<String> cB=Medan.combo(bln); Medan.Combo<Integer> cH=Medan.combo(thn);
        cT.setSelectedItem(d.getDayOfMonth()); cB.setSelectedIndex(d.getMonthValue()-1); cH.setSelectedItem(d.getYear());
        JPanel pp = new JPanel(new GridLayout(1,3,8,0)); pp.setPreferredSize(new Dimension(280,40));
        pp.add(cT); pp.add(cB); pp.add(cH);
        if (JOptionPane.showConfirmDialog(this,pp,"Pilih Tanggal Sewa",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE)==JOptionPane.OK_OPTION) {
            try { txtTanggal.setText(Fmt.keTampil(LocalDate.of((Integer)cH.getSelectedItem(),cB.getSelectedIndex()+1,(Integer)cT.getSelectedItem()))); }
            catch (Exception ignored) { pesan("Tanggal tidak valid.", JOptionPane.WARNING_MESSAGE); }
        }
    }

    private void simpan() {
        if (terpilih == null) { pesan("Pilih sepeda terlebih dahulu.", JOptionPane.WARNING_MESSAGE); return; }
        if (terpilih.getStok() <= 0) { pesan("Stok " + terpilih.getNama() + " habis.", JOptionPane.WARNING_MESSAGE); return; }
        if (txtNama.getText().trim().isEmpty()) { pesan("Nama pelanggan wajib diisi.", JOptionPane.WARNING_MESSAGE); txtNama.requestFocus(); return; }
        LocalDate tanggal = Fmt.dariTampil(txtTanggal.getText());
        if (tanggal == null) { pesan("Format tanggal harus dd/MM/yyyy.", JOptionPane.WARNING_MESSAGE); return; }
        if (rbTransfer.isSelected() && txtRekening.getText().trim().isEmpty()) { pesan("Nomor rekening wajib diisi.", JOptionPane.WARNING_MESSAGE); return; }

        double hargaEf = hitungHargaEfektif(), ongkir = hitungOngkir(), uangLelah = Fmt.angka(txtUangLelah.getText());
        double total = hargaEf * durasi.getNilai() + ongkir + uangLelah;

        Object selV = cbVarian.getSelectedItem();
        String namaVarian = selV instanceof VarianSepeda ? ((VarianSepeda) selV).getNamaModel() : null;
        Object selU = cbUkuran.getSelectedItem();
        String ukuranTerpilih = selU instanceof UkuranSepeda ? ((UkuranSepeda) selU).getUkuran() : null;

        Penyewaan p = new Penyewaan();
        p.setNamaPelanggan(txtNama.getText().trim()); p.setSepedaId(terpilih.getId()); p.setNamaSepeda(terpilih.getNama());
        p.setTanggalSewa(Fmt.keDb(tanggal)); p.setDurasi(durasi.getNilai());
        p.setHargaSewa(hargaEf); p.setOngkir(ongkir); p.setUangLelah(uangLelah); p.setTotalBayar(total);
        p.setNamaVarian(namaVarian); p.setUkuranTerpilih(ukuranTerpilih);
        p.setDatetimeSewa(LocalDateTime.now().format(DT_FMT));
        p.setStatus("Disewa");
        if (rbCash.isSelected()) { p.setMetode("Cash"); }
        else { p.setMetode("Transfer"); p.setBank((String)cbBank.getSelectedItem());
               p.setNoRekening(txtRekening.getText().trim()); p.setNamaPemilik(txtPemilik.getText().trim()); }

        int idBaru = penyewaanDAO.simpan(p); p.setId(idBaru);
        sepedaDAO.ubahStok(terpilih.getId(), -1);
        String modelInfo = namaVarian != null ? " (" + namaVarian + (ukuranTerpilih != null ? ", " + ukuranTerpilih : "") + ")" : "";
        pesan("<html>Transaksi berhasil disimpan.<br><br><b>" + p.getNamaPelanggan() + "</b><br>" +
              terpilih.getNama() + modelInfo + " · " + p.getDurasi() + " hari<br>" +
              "Total: <b>" + Fmt.rupiah(total) + "</b> (" + p.getMetode() + ")<br><br>" +
              "<small>Jatuh tempo: " + LocalDateTime.parse(p.getDatetimeSewa(), DT_FMT).plusDays(p.getDurasi())
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")) + "</small></html>",
              JOptionPane.INFORMATION_MESSAGE);

        int cetak = JOptionPane.showConfirmDialog(this, "Cetak struk untuk pelanggan?",
                "FAR BikeRent", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (cetak == JOptionPane.YES_OPTION) cetakStruk(p);

        kosongkanForm(); muatData();
        if (saatTersimpan != null) saatTersimpan.run();
    }

    private void cetakStruk(Penyewaan p) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Struk FAR BikeRent #" + p.getId());
        job.setPrintable(new CetakStruk(p));
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Gagal mencetak struk: " + e.getMessage(),
                        "FAR BikeRent", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void kosongkanForm() {
        txtNama.setText(""); txtTanggal.setText(Fmt.keTampil(LocalDate.now()));
        durasi.setNilai(1); saklarOngkir.setNyala(false);
        txtJarak.setText(""); txtKmL.setText(""); txtBensin.setText(""); txtUangLelah.setText("0");
        cbVarian.removeAllItems(); cbVarian.addItem(VARIAN_DEFAULT);
        cbUkuran.removeAllItems(); cbUkuran.addItem(UKURAN_KOSONG);
        lblHargaVarian.setText("Rp 0 / hari");
        rbCash.setSelected(true); ubahMetode();
    }

    private void pesan(String teks, int tipe) {
        JOptionPane.showMessageDialog(this, teks, "FAR BikeRent", tipe);
    }

    @Override
    public void muatData() {
        daftarSepeda = sepedaDAO.semua();
        modelSepeda.setRowCount(0);
        int no = 1;
        for (Sepeda s : daftarSepeda)
            modelSepeda.addRow(new Object[]{no++, s.getNama(), s.getStok(), Fmt.rupiah(s.getHargaSewa())});
        if (!daftarSepeda.isEmpty()) {
            int baris = 0;
            if (terpilih != null)
                for (int i = 0; i < daftarSepeda.size(); i++)
                    if (daftarSepeda.get(i).getId() == terpilih.getId()) { baris = i; break; }
            tabelSepeda.setRowSelectionInterval(baris, baris); pilihBaris(baris);
        }
    }

    public void pilihSepeda(int idSepeda) {
        muatData();
        for (int i = 0; i < daftarSepeda.size(); i++)
            if (daftarSepeda.get(i).getId() == idSepeda) { tabelSepeda.setRowSelectionInterval(i,i); pilihBaris(i); break; }
        txtNama.requestFocusInWindow();
    }

    private class RendererRadio extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean fok, int b, int kol) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, false, b, kol);
            l.setFont(Theme.font(12)); l.setHorizontalAlignment(LEFT);
            l.setIcon(ikonRadio(sel)); l.setIconTextGap(8);
            l.setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
            if (!sel) l.setBackground(Color.WHITE);
            return l;
        }
    }

    private static class CetakStruk implements Printable {
        private static final int LEBAR = 300;
        private final Penyewaan p;

        CetakStruk(Penyewaan p) { this.p = p; }

        @Override
        public int print(Graphics g0, PageFormat pf, int halaman) throws PrinterException {
            if (halaman > 0) return NO_SUCH_PAGE;
            Graphics2D g = (Graphics2D) g0;
            Theme.aa(g);
            g.setColor(Color.BLACK);
            int x0 = (int) pf.getImageableX() + (int) (pf.getImageableWidth() - LEBAR) / 2;
            int y = (int) pf.getImageableY() + 20;
            String fam = Theme.family();

            g.setFont(new Font(fam, Font.BOLD, 15));
            y = tengah(g, "FAR BikeRent", x0, y);
            g.setFont(new Font(fam, Font.PLAIN, 9));
            y = tengah(g, "Ride More, Worry Less", x0, y + 2) + 6;
            y = putus(g, x0, y);
            g.setFont(new Font(fam, Font.BOLD, 11));
            y = tengah(g, "BUKTI SEWA SEPEDA", x0, y + 12) + 4;
            y = putus(g, x0, y);

            g.setFont(new Font(fam, Font.PLAIN, 10));
            LocalDateTime waktuSewa = p.getDatetimeSewa() != null
                    ? LocalDateTime.parse(p.getDatetimeSewa(), DT_FMT) : LocalDateTime.now();
            y = baris(g, x0, y, "No. Transaksi", "#" + p.getId());
            y = baris(g, x0, y, "Tanggal", waktuSewa.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            y = baris(g, x0, y, "Pelanggan", p.getNamaPelanggan());
            y = putus(g, x0, y + 4);

            y = baris(g, x0, y, "Sepeda", p.getNamaSepeda());
            if (p.getNamaVarian() != null)     y = baris(g, x0, y, "Model", p.getNamaVarian());
            if (p.getUkuranTerpilih() != null) y = baris(g, x0, y, "Ukuran", p.getUkuranTerpilih());
            y = baris(g, x0, y, "Durasi", p.getDurasi() + " hari x " + Fmt.rupiah(p.getHargaSewa()));
            y = putus(g, x0, y + 4);

            y = baris(g, x0, y, "Harga Sewa", Fmt.rupiah(p.getHargaSewa() * p.getDurasi()));
            if (p.getOngkir() > 0)    y = baris(g, x0, y, "Ongkir", Fmt.rupiah(p.getOngkir()));
            if (p.getUangLelah() > 0) y = baris(g, x0, y, "Service Charge", Fmt.rupiah(p.getUangLelah()));
            y = putus(g, x0, y + 4);

            g.setFont(new Font(fam, Font.BOLD, 13));
            y = baris(g, x0, y + 2, "TOTAL BAYAR", Fmt.rupiah(p.getTotalBayar()));
            g.setFont(new Font(fam, Font.PLAIN, 10));
            y += 4;
            String metode = p.getMetode();
            if ("Transfer".equals(metode)) metode += " - " + p.getBank() + " a.n " + p.getNamaPemilik();
            y = baris(g, x0, y, "Metode Bayar", metode);
            y = putus(g, x0, y + 4);

            y = baris(g, x0, y, "Jatuh Tempo", waktuSewa.plusDays(p.getDurasi())
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            y = putus(g, x0, y + 4);

            g.setFont(new Font(fam, Font.BOLD, 10));
            y = tengah(g, "Terima kasih telah menyewa!", x0, y + 12);
            g.setFont(new Font(fam, Font.PLAIN, 8));
            drawWrapped(g, "Denda keterlambatan 5% per jam berlaku setelah grace period 2 jam.",
                    x0, y + 12, LEBAR);
            return PAGE_EXISTS;
        }

        private int baris(Graphics2D g, int x0, int y, String label, String nilai) {
            if (nilai == null) nilai = "-";
            g.drawString(label, x0, y);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(nilai, x0 + LEBAR - fm.stringWidth(nilai), y);
            return y + 15;
        }

        private int tengah(Graphics2D g, String teks, int x0, int y) {
            FontMetrics fm = g.getFontMetrics();
            g.drawString(teks, x0 + (LEBAR - fm.stringWidth(teks)) / 2, y);
            return y + fm.getHeight();
        }

        private int putus(Graphics2D g, int x0, int y) {
            Graphics2D gd = (Graphics2D) g.create();
            gd.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f,
                    new float[]{3f, 3f}, 0f));
            gd.drawLine(x0, y, x0 + LEBAR, y);
            gd.dispose();
            return y + 14;
        }

        private void drawWrapped(Graphics2D g, String teks, int x0, int y, int lebar) {
            FontMetrics fm = g.getFontMetrics();
            StringBuilder baris = new StringBuilder();
            int yy = y;
            for (String kata : teks.split(" ")) {
                String coba = baris.length() == 0 ? kata : baris + " " + kata;
                if (fm.stringWidth(coba) > lebar) {
                    tengah(g, baris.toString(), x0, yy);
                    yy += fm.getHeight();
                    baris = new StringBuilder(kata);
                } else {
                    baris = new StringBuilder(coba);
                }
            }
            if (baris.length() > 0) tengah(g, baris.toString(), x0, yy);
        }
    }
}
