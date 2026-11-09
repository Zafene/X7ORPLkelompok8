package com.bikerent.ui.comp;

import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Penambah extends JPanel {

    private int nilai;
    private final int min, max;
    private final JLabel lbl = new JLabel();
    private final List<Runnable> pendengar = new ArrayList<>();

    public Penambah(int nilaiAwal, int min, int max) {
        super(new BorderLayout());
        this.nilai = nilaiAwal;
        this.min = min;
        this.max = max;
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));

        lbl.setFont(Theme.font(13));
        lbl.setForeground(Theme.TEXT);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));

        Tombol minus = kecil(Ikon.kurang(14, Theme.TEXT_MUTED));
        Tombol plus  = kecil(Ikon.tambah(14, Theme.TEXT_MUTED));
        minus.addActionListener(e -> setNilai(this.nilai - 1));
        plus.addActionListener(e -> setNilai(this.nilai + 1));

        JPanel kanan = new JPanel(new BorderLayout(4, 0));
        kanan.setOpaque(false);
        kanan.add(minus, BorderLayout.WEST);
        kanan.add(plus, BorderLayout.EAST);

        add(lbl, BorderLayout.CENTER);
        add(kanan, BorderLayout.EAST);
        perbarui();
    }

    private Tombol kecil(javax.swing.Icon ikon) {
        Tombol t = new Tombol("", ikon);
        t.gaya(Tombol.Gaya.GARIS).radius(7).padding(4, 8, 4, 8);
        return t;
    }

    public int getNilai() { return nilai; }

    public void setNilai(int v) {
        int baru = Math.max(min, Math.min(max, v));
        if (baru == nilai) return;
        nilai = baru;
        perbarui();
        for (Runnable r : pendengar) r.run();
    }

    public void saatBerubah(Runnable r) { pendengar.add(r); }

    private void perbarui() { lbl.setText(String.valueOf(nilai)); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.aa(g);
        g.setColor(Color.WHITE);
        g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 9, 9));
        g.setColor(Theme.BORDER);
        g.setStroke(new BasicStroke(1f));
        g.draw(new RoundRectangle2D.Double(0.6, 0.6, getWidth() - 2, getHeight() - 2, 9, 9));
        g.dispose();
    }
}
