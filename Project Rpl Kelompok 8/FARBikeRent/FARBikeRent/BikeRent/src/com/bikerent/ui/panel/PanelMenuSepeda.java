package com.bikerent.ui.panel;

import com.bikerent.dao.PenyewaanDAO;
import com.bikerent.dao.SepedaDAO;
import com.bikerent.dao.VarianSepedaDAO;
import com.bikerent.model.Sepeda;
import com.bikerent.model.UkuranSepeda;
import com.bikerent.model.VarianSepeda;
import com.bikerent.ui.Muat;
import com.bikerent.ui.comp.Kartu;
import com.bikerent.ui.comp.Medan;
import com.bikerent.ui.comp.TabelRapi;
import com.bikerent.ui.comp.Tombol;
import com.bikerent.ui.icon.GambarSepeda;
import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Fmt;
import com.bikerent.util.Theme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PanelMenuSepeda extends JPanel implements Muat {

    private final SepedaDAO       dao         = new SepedaDAO();
    private final VarianSepedaDAO varianDAO   = new VarianSepedaDAO();
    private final PenyewaanDAO    penyewaanDAO = new PenyewaanDAO();

    private final Medan.Teks cari = new Medan.Teks("Cari jenis sepeda...");
    private final Medan.Combo<String> filter = Medan.combo(new String[]{"Semua Jenis"});
    private final JPanel daftar = new JPanel(new GridLayout(1, 4, 14, 14));
    private Consumer<Sepeda> saatSewa;

    public PanelMenuSepeda() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        add(bilahCari(), BorderLayout.NORTH);
        daftar.setOpaque(false);
        JPanel atas = new JPanel(new BorderLayout());
        atas.setOpaque(false);
        atas.add(daftar, BorderLayout.NORTH);
        add(atas, BorderLayout.CENTER);

        cari.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { muatData(); }
            public void removeUpdate(DocumentEvent e)  { muatData(); }
            public void changedUpdate(DocumentEvent e) { muatData(); }
        });
        filter.addActionListener(e -> muatData());
    }

    public void saatSewa(Consumer<Sepeda> aksi) { this.saatSewa = aksi; }

    private JPanel bilahCari() {
        JPanel p = new JPanel(new BorderLayout(12, 0));
        p.setOpaque(false);
        JPanel kotakCari = Medan.denganIkon(cari, Ikon.cari(15, Theme.TEXT_LIGHT), true);
        kotakCari.setPreferredSize(new Dimension(10, 38));
        filter.setPreferredSize(new Dimension(150, 38));

        Tombol btnTambah = new Tombol("+ Tambah Jenis Sepeda");
        btnTambah.padding(8, 14, 8, 14);
        btnTambah.setPreferredSize(new Dimension(190, 38));
        btnTambah.addActionListener(e -> tampilkanDialog(null));

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        kanan.setOpaque(false);
        kanan.add(filter);
        kanan.add(btnTambah);

        p.add(kotakCari, BorderLayout.CENTER);
        p.add(kanan, BorderLayout.EAST);
        p.setPreferredSize(new Dimension(10, 38));
        return p;
    }

    @Override
    public void muatData() {
        String selectedFilter = (String) filter.getSelectedItem();
        filter.removeAllItems();
        filter.addItem("Semua Jenis");
        for (String kode : dao.semuaKode()) filter.addItem(kode);
        if (selectedFilter != null) filter.setSelectedItem(selectedFilter);

        String kata = cari.getText();
        String kode = (String) filter.getSelectedItem();
        List<Sepeda> data = dao.cari(kata, kode);

        daftar.removeAll();
        daftar.setLayout(new GridLayout(1, Math.max(1, data.size()), 14, 14));
        for (Sepeda s : data) daftar.add(kartuSepeda(s));
        if (data.isEmpty()) {
            JLabel kosong = Kartu.label("Tidak ada sepeda. Klik \"+ Tambah Jenis Sepeda\" untuk mulai.",
                    13, Theme.TEXT_MUTED, false);
            kosong.setHorizontalAlignment(JLabel.CENTER);
            daftar.add(kosong);
        }
        daftar.setPreferredSize(new Dimension(10, 360));
        daftar.revalidate();
        daftar.repaint();
    }

    private Kartu kartuSepeda(Sepeda s) {
        Kartu k = new Kartu(new BorderLayout(0, 10));
        k.padding(12).radius(14);

        JPanel area = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Theme.aa(g);
                g.setColor(new Color(0xFAFAFB));
                g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g.dispose();
            }
        };
        area.setOpaque(false);
        area.setPreferredSize(new Dimension(10, 120));
        area.add(new GambarSepeda(s.getKode(), s.getGambar()), BorderLayout.CENTER);
        k.add(area, BorderLayout.NORTH);

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));

        JLabel nama = Kartu.label(s.getNama(), 14, Theme.TEXT, true);
        nama.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(nama);
        isi.add(Box.createVerticalStrut(6));

        JPanel badge = new PilBadge(s.getKode());
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(badge);
        isi.add(Box.createVerticalStrut(10));

        isi.add(spek(Ikon.label(13, Theme.TEXT_LIGHT),    "Tipe",       s.getTipe()));
        isi.add(spek(Ikon.penggaris(13, Theme.TEXT_LIGHT),"Ukuran",     s.getUkuran()));
        isi.add(spek(Ikon.jam(13, Theme.TEXT_LIGHT),      "Harga sewa", Fmt.rupiah(s.getHargaSewa()) + " / hari"));
        isi.add(Box.createVerticalStrut(4));
        isi.add(spek(Ikon.kotak(13, Theme.TEXT_LIGHT),    "Stok",       s.getStok() + " unit"));
        k.add(isi, BorderLayout.CENTER);

        Tombol btnSewa = new Tombol("Sewa Sekarang", Ikon.sepeda(15, Color.WHITE));
        btnSewa.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnSewa.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSewa.addActionListener(e -> { if (saatSewa != null) saatSewa.accept(s); });

        Tombol btnEdit = new Tombol("Edit");
        btnEdit.gaya(Tombol.Gaya.GARIS);
        btnEdit.addActionListener(e -> tampilkanDialog(s));

        Tombol btnHapus = new Tombol("Hapus");
        btnHapus.gaya(Tombol.Gaya.GARIS);
        btnHapus.addActionListener(e -> hapusSepeda(s));

        JPanel barisAksi = new JPanel(new GridLayout(1, 2, 6, 0));
        barisAksi.setOpaque(false);
        barisAksi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        barisAksi.setAlignmentX(Component.LEFT_ALIGNMENT);
        barisAksi.add(btnEdit);
        barisAksi.add(btnHapus);

        JPanel bawah = new JPanel();
        bawah.setOpaque(false);
        bawah.setLayout(new BoxLayout(bawah, BoxLayout.Y_AXIS));
        bawah.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        bawah.add(btnSewa);
        bawah.add(Box.createVerticalStrut(6));
        bawah.add(barisAksi);
        k.add(bawah, BorderLayout.SOUTH);
        return k;
    }

    private void tampilkanDialog(Sepeda s) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        String judul = (s == null) ? "Tambah Jenis Sepeda" : "Edit: " + s.getNama();
        JDialog dlg = new JDialog(parent, judul, Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(640, 460);
        dlg.setLocationRelativeTo(parent);
        dlg.setResizable(false);

        byte[][] gambarHolder = { s != null ? s.getGambar() : null };
        GambarSepeda gs = new GambarSepeda(s != null ? s.getKode() : GambarSepeda.MTB, gambarHolder[0]);

        JPanel areGambar = new JPanel(new BorderLayout(0, 8));
        areGambar.setOpaque(false);
        areGambar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 14));
        areGambar.setPreferredSize(new Dimension(190, 10));

        JPanel frameGambar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Theme.aa(g);
                g.setColor(new Color(0xF8F9FA));
                g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g.setColor(Theme.BORDER);
                g.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth()-1, getHeight()-1, 10, 10));
                g.dispose();
            }
        };
        frameGambar.setOpaque(false);
        frameGambar.setPreferredSize(new Dimension(180, 140));
        frameGambar.add(gs, BorderLayout.CENTER);

        Tombol btnBrowse = new Tombol("Pilih Gambar...");
        btnBrowse.gaya(Tombol.Gaya.GARIS);
        btnBrowse.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnBrowse.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter("Gambar (PNG, JPG)", "png","jpg","jpeg"));
            if (fc.showOpenDialog(dlg) == JFileChooser.APPROVE_OPTION) {
                try {
                    gambarHolder[0] = Files.readAllBytes(fc.getSelectedFile().toPath());
                    gs.setGambar(gambarHolder[0]);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dlg, "Gagal baca gambar: " + ex.getMessage());
                }
            }
        });

        JPanel btnKelola = new JPanel(new GridLayout(1, 2, 6, 0));
        btnKelola.setOpaque(false);
        if (s != null) {
            Tombol bvarian = new Tombol("Model & Harga");
            bvarian.gaya(Tombol.Gaya.GARIS); bvarian.padding(4, 6, 4, 6);
            bvarian.addActionListener(ev -> { dlg.dispose(); kelolaModel(s); tampilkanDialog(dao.byId(s.getId())); });
            Tombol bukuran = new Tombol("Ukuran");
            bukuran.gaya(Tombol.Gaya.GARIS); bukuran.padding(4, 6, 4, 6);
            bukuran.addActionListener(ev -> { dlg.dispose(); kelolaUkuran(s); tampilkanDialog(dao.byId(s.getId())); });
            btnKelola.add(bvarian); btnKelola.add(bukuran);
        }

        areGambar.add(frameGambar, BorderLayout.NORTH);
        areGambar.add(btnBrowse, BorderLayout.CENTER);
        if (s != null) areGambar.add(btnKelola, BorderLayout.SOUTH);

        JTextField txtNama   = styledField(s != null ? s.getNama() : "");
        JTextField txtKode   = styledField(s != null ? s.getKode() : "");
        JTextField txtTipe   = styledField(s != null ? s.getTipe() : "");
        JTextField txtUkuran = styledField(s != null ? s.getUkuran() : "");
        JTextField txtHarga  = styledField(s != null ? String.valueOf((long)s.getHargaSewa()) : "");
        JSpinner   spnStok   = new JSpinner(new SpinnerNumberModel(s != null ? s.getStok() : 0, 0, 9999, 1));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 0, 5, 8);
        gc.anchor = GridBagConstraints.WEST;

        String[] lbls  = {"Nama *","Kode *","Tipe","Ukuran","Harga/hari (Rp)","Stok"};
        JComponent[] inp = {txtNama, txtKode, txtTipe, txtUkuran, txtHarga, spnStok};
        for (int i = 0; i < lbls.length; i++) {
            gc.gridx=0; gc.gridy=i; gc.weightx=0; gc.fill=GridBagConstraints.NONE;
            JLabel l = new JLabel(lbls[i]); l.setFont(Theme.font(12)); l.setForeground(Theme.TEXT_MUTED);
            form.add(l, gc);
            gc.gridx=1; gc.weightx=1; gc.fill=GridBagConstraints.HORIZONTAL;
            inp[i].setPreferredSize(new Dimension(10, 32));
            form.add(inp[i], gc);
        }

        Tombol btnBatal  = new Tombol("Batal"); btnBatal.gaya(Tombol.Gaya.GARIS);
        Tombol btnSimpan = new Tombol("Simpan");
        btnBatal.addActionListener(ev -> dlg.dispose());
        btnSimpan.addActionListener(ev -> {
            String nm = txtNama.getText().trim();
            String kd = txtKode.getText().trim();
            if (nm.isEmpty() || kd.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Nama dan Kode wajib diisi.", "FAR BikeRent", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Sepeda obj = s != null ? s : new Sepeda();
            obj.setNama(nm); obj.setKode(kd);
            obj.setTipe(txtTipe.getText().trim()); obj.setUkuran(txtUkuran.getText().trim());
            obj.setHargaSewa(Fmt.angka(txtHarga.getText()));
            obj.setStok((Integer)spnStok.getValue());
            if (gambarHolder[0] != null) obj.setGambar(gambarHolder[0]);
            if (s == null) dao.simpan(obj); else dao.ubah(obj);
            dlg.dispose();
            muatData();
        });

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bawah.setOpaque(false);
        bawah.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.BORDER));
        bawah.add(btnBatal); bawah.add(btnSimpan);

        JPanel pusat = new JPanel(new BorderLayout(0, 0));
        pusat.setOpaque(false);
        pusat.setBorder(BorderFactory.createEmptyBorder(16, 20, 0, 20));
        JPanel tengah = new JPanel(new BorderLayout());
        tengah.setOpaque(false);
        tengah.add(areGambar, BorderLayout.WEST);
        tengah.add(form, BorderLayout.CENTER);
        pusat.add(tengah, BorderLayout.CENTER);
        pusat.add(bawah, BorderLayout.SOUTH);

        dlg.add(pusat);
        dlg.setVisible(true);
    }

    private JTextField styledField(String text) {
        JTextField tf = new JTextField(text);
        tf.setFont(Theme.font(12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return tf;
    }

    private void kelolaModel(Sepeda s) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, "Model & Harga — " + s.getNama(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 420); dlg.setLocationRelativeTo(parent); dlg.setResizable(false);

        DefaultTableModel tm = new DefaultTableModel(new String[]{"Nama Model", "Harga Sewa / hari (Rp)"}, 0);
        for (VarianSepeda v : varianDAO.daftarVarian(s.getId()))
            tm.addRow(new Object[]{v.getNamaModel(), (long) v.getHargaSewa()});
        JTable tbl = TabelRapi.buat(tm);
        tbl.setRowHeight(30);
        JScrollPane sp = new JScrollPane(tbl); sp.setPreferredSize(new Dimension(10, 210));

        JLabel hint = new JLabel("Klik sel untuk edit langsung. Harga 0 = pakai harga default jenis sepeda.");
        hint.setFont(Theme.font(10)); hint.setForeground(Theme.TEXT_MUTED);

        Tombol btnHapus = new Tombol("Hapus Baris Terpilih"); btnHapus.gaya(Tombol.Gaya.GARIS);
        btnHapus.addActionListener(e -> { int r = tbl.getSelectedRow(); if (r >= 0) { tbl.clearSelection(); tm.removeRow(r); } });

        JTextField fNama  = styledField("");
        JTextField fHarga = styledField("0");
        fNama.setPreferredSize(new Dimension(220, 30)); fHarga.setPreferredSize(new Dimension(100, 30));

        Tombol btnTambah = new Tombol("+ Tambah");
        btnTambah.addActionListener(e -> {
            String nm = fNama.getText().trim();
            if (!nm.isEmpty()) { tm.addRow(new Object[]{nm, Fmt.angka(fHarga.getText())}); fNama.setText(""); fHarga.setText("0"); }
        });
        fNama.addActionListener(btnTambah.getActionListeners()[0]);

        JPanel bInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0)); bInput.setOpaque(false);
        JLabel lm = new JLabel("Model:"); lm.setFont(Theme.font(12)); lm.setForeground(Theme.TEXT_MUTED);
        JLabel lh = new JLabel("Harga Rp:"); lh.setFont(Theme.font(12)); lh.setForeground(Theme.TEXT_MUTED);
        bInput.add(lm); bInput.add(fNama); bInput.add(lh); bInput.add(fHarga); bInput.add(btnTambah);

        Tombol btnSelesai = new Tombol("Simpan & Tutup");
        btnSelesai.addActionListener(e -> {
            if (tbl.isEditing()) tbl.getCellEditor().stopCellEditing();
            List<VarianSepeda> hasil = new ArrayList<>();
            for (int i = 0; i < tm.getRowCount(); i++) {
                VarianSepeda v = new VarianSepeda();
                v.setSepedaId(s.getId());
                v.setNamaModel(tm.getValueAt(i,0).toString().trim());
                v.setHargaSewa(Fmt.angka(tm.getValueAt(i,1).toString()));
                if (!v.getNamaModel().isEmpty()) hasil.add(v);
            }
            varianDAO.simpanSemuaVarian(s.getId(), hasil);
            dlg.dispose();
        });

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false); panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(sp, BorderLayout.CENTER);
        JPanel bot = new JPanel(new BorderLayout(0, 6)); bot.setOpaque(false);
        bot.add(hint, BorderLayout.NORTH); bot.add(bInput, BorderLayout.CENTER); bot.add(btnHapus, BorderLayout.SOUTH);
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bawah.setOpaque(false); bawah.add(btnSelesai);
        JPanel all = new JPanel(new BorderLayout(0, 6)); all.setOpaque(false);
        all.add(bot, BorderLayout.NORTH); all.add(bawah, BorderLayout.SOUTH);
        panel.add(all, BorderLayout.SOUTH);
        dlg.add(panel); dlg.setVisible(true);
    }

    private void kelolaUkuran(Sepeda s) {
        Window parent = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(parent, "Chart Ukuran — " + s.getNama(), Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(460, 440); dlg.setLocationRelativeTo(parent); dlg.setResizable(false);

        DefaultTableModel tm = new DefaultTableModel(new String[]{"Ukuran","Tinggi Min (cm)","Tinggi Max (cm)"}, 0);
        for (UkuranSepeda u : varianDAO.daftarUkuran(s.getId()))
            tm.addRow(new Object[]{u.getUkuran(), u.getTinggiMin(), u.getTinggiMax()});
        JTable tbl = TabelRapi.buat(tm); tbl.setRowHeight(28);
        JScrollPane sp = new JScrollPane(tbl); sp.setPreferredSize(new Dimension(10, 210));

        JTextField fUkuran = styledField(""); fUkuran.setPreferredSize(new Dimension(70, 30));
        JTextField fMin    = styledField(""); fMin.setPreferredSize(new Dimension(70, 30));
        JTextField fMax    = styledField(""); fMax.setPreferredSize(new Dimension(70, 30));

        Tombol btnTambah = new Tombol("+ Tambah");
        btnTambah.addActionListener(e -> {
            String uk = fUkuran.getText().trim();
            if (uk.isEmpty()) return;
            int mn = (int)Fmt.angka(fMin.getText()), mx = (int)Fmt.angka(fMax.getText());
            tm.addRow(new Object[]{uk, mn, mx});
            fUkuran.setText(""); fMin.setText(""); fMax.setText("");
        });

        Tombol btnHapus = new Tombol("Hapus Baris Terpilih"); btnHapus.gaya(Tombol.Gaya.GARIS);
        btnHapus.addActionListener(e -> { int row = tbl.getSelectedRow(); if (row >= 0) tm.removeRow(row); });

        JLabel lblInput = new JLabel("Ukuran:"); lblInput.setFont(Theme.font(12)); lblInput.setForeground(Theme.TEXT_MUTED);
        JLabel lblMin = new JLabel("Min:"); lblMin.setFont(Theme.font(12)); lblMin.setForeground(Theme.TEXT_MUTED);
        JLabel lblMax = new JLabel("Max:"); lblMax.setFont(Theme.font(12)); lblMax.setForeground(Theme.TEXT_MUTED);

        JPanel barisInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        barisInput.setOpaque(false);
        barisInput.add(lblInput); barisInput.add(fUkuran);
        barisInput.add(lblMin);   barisInput.add(fMin);
        barisInput.add(lblMax);   barisInput.add(fMax);
        barisInput.add(btnTambah);

        Tombol btnSelesai = new Tombol("Simpan & Tutup");
        btnSelesai.addActionListener(e -> {
            List<UkuranSepeda> hasil = new ArrayList<>();
            for (int i = 0; i < tm.getRowCount(); i++) {
                UkuranSepeda u = new UkuranSepeda();
                u.setSepedaId(s.getId());
                u.setUkuran(tm.getValueAt(i,0).toString());
                u.setTinggiMin(Integer.parseInt(tm.getValueAt(i,1).toString()));
                u.setTinggiMax(Integer.parseInt(tm.getValueAt(i,2).toString()));
                hasil.add(u);
            }
            varianDAO.simpanSemuaUkuran(s.getId(), hasil);
            dlg.dispose();
        });

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false); panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        panel.add(sp, BorderLayout.CENTER);
        JPanel bot = new JPanel(new BorderLayout(0, 8)); bot.setOpaque(false);
        bot.add(barisInput, BorderLayout.NORTH); bot.add(btnHapus, BorderLayout.CENTER);
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT)); bawah.setOpaque(false); bawah.add(btnSelesai);
        bot.add(bawah, BorderLayout.SOUTH);
        panel.add(bot, BorderLayout.SOUTH);
        dlg.add(panel); dlg.setVisible(true);
    }

    private void hapusSepeda(Sepeda s) {
        int aktif = penyewaanDAO.jumlahAktifBySepeda(s.getId());
        if (aktif > 0) {
            JOptionPane.showMessageDialog(this,
                "Tidak dapat menghapus — masih ada " + aktif + " sewa aktif untuk " + s.getNama() + ".",
                "FAR BikeRent", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
            "Hapus " + s.getNama() + " beserta semua data model dan ukurannya?",
            "FAR BikeRent", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) { dao.hapus(s.getId()); muatData(); }
    }

    private JPanel spek(javax.swing.Icon ikon, String label, String nilai) {
        JPanel p = new JPanel(new BorderLayout(6, 0));
        p.setOpaque(false); p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        p.add(new JLabel(ikon), BorderLayout.WEST);
        p.add(Kartu.label(label + " : " + nilai, 11, Theme.TEXT_MUTED, false), BorderLayout.CENTER);
        return p;
    }

    private static class PilBadge extends JPanel {
        private final String teks;
        PilBadge(String teks) {
            this.teks = teks; setOpaque(false);
            setPreferredSize(new Dimension(60, 22)); setMaximumSize(new Dimension(90, 22));
        }
        @Override protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create(); Theme.aa(g);
            g.setFont(Theme.fontBold(11));
            int w = g.getFontMetrics().stringWidth(teks) + 18;
            g.setColor(Theme.ORANGE_SOFT);
            g.fill(new RoundRectangle2D.Double(0, 0, w, 21, 8, 8));
            g.setColor(new Color(0xB45309));
            g.drawString(teks, 9, 15); g.dispose();
        }
    }
}
