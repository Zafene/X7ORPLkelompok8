package com.bikerent.ui.comp;

import com.bikerent.util.Theme;

import javax.swing.JComponent;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Saklar extends JComponent {

    private boolean nyala;
    private float posisi;
    private Timer timer;
    private final List<Runnable> pendengar = new ArrayList<>();

    public Saklar(boolean nyala) {
        this.nyala = nyala;
        this.posisi = nyala ? 1f : 0f;
        setPreferredSize(new Dimension(42, 22));
        setMaximumSize(new Dimension(42, 22));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        timer = new Timer(12, e -> {
            float target = this.nyala ? 1f : 0f;
            posisi += (target - posisi) * 0.28f;
            if (Math.abs(target - posisi) < 0.01f) { posisi = target; timer.stop(); }
            repaint();
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { setNyala(!Saklar.this.nyala); }
        });
    }

    public boolean isNyala() { return nyala; }

    public void setNyala(boolean n) {
        if (this.nyala == n) return;
        this.nyala = n;
        timer.start();
        for (Runnable r : pendengar) r.run();
    }

    public void saatBerubah(Runnable r) { pendengar.add(r); }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Theme.aa(g);
        int w = getWidth(), h = getHeight();
        g.setColor(Theme.mix(new Color(0xD1D5DB), Theme.ORANGE, posisi));
        g.fill(new RoundRectangle2D.Double(0, 0, w, h, h, h));
        double d = h - 6;
        double x = 3 + (w - d - 6) * posisi;
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(x, 3, d, d));
        g.dispose();
    }
}
