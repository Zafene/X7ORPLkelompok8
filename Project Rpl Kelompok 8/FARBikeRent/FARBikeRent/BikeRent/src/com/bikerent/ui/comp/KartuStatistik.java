package com.bikerent.ui.comp;

import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

public class KartuStatistik extends Kartu {

    private final JLabel lblNilai;
    private final JLabel lblTren;

    public KartuStatistik(String label, String nilai, Icon ikon, Color warnaIkon, Color latarIkon) {
        super(new BorderLayout(12, 0));
        radius(12).padding(14, 14, 14, 14);

        JPanel pembungkusIkon = new JPanel(new BorderLayout());
        pembungkusIkon.setOpaque(false);
        pembungkusIkon.add(new KotakIkon(ikon, latarIkon), BorderLayout.NORTH);
        add(pembungkusIkon, BorderLayout.WEST);

        JPanel teks = new JPanel();
        teks.setOpaque(false);
        teks.setLayout(new BoxLayout(teks, BoxLayout.Y_AXIS));
        JPanel bungkusTeks = new JPanel(new BorderLayout());
        bungkusTeks.setOpaque(false);

        JLabel lbl = Kartu.label(label, 12, Theme.TEXT_MUTED, false);
        lblNilai = Kartu.label(nilai, 22, Theme.TEXT, true);
        lblTren  = Kartu.label(" ", 11, Theme.GREEN, false);

        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNilai.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTren.setAlignmentX(Component.LEFT_ALIGNMENT);

        teks.add(lbl);
        teks.add(Box.createVerticalStrut(2));
        teks.add(lblNilai);
        teks.add(lblTren);
        bungkusTeks.add(teks, BorderLayout.NORTH);
        add(bungkusTeks, BorderLayout.CENTER);
    }

    public KartuStatistik tren(String teks, Color warna) {
        lblTren.setText(teks);
        lblTren.setForeground(warna);
        return this;
    }

    public void setNilai(String v) { lblNilai.setText(v); }

    public static class KotakIkon extends JPanel {
        private final Icon ikon;
        private final Color latar;

        public KotakIkon(Icon ikon, Color latar) { this(ikon, latar, 38); }

        public KotakIkon(Icon ikon, Color latar, int size) {
            this.ikon = ikon;
            this.latar = latar;
            setOpaque(false);
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
            setBorder(BorderFactory.createEmptyBorder());
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Theme.aa(g);
            g.setColor(latar);
            g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
            if (ikon != null) {
                ikon.paintIcon(this, g,
                        (getWidth() - ikon.getIconWidth()) / 2,
                        (getHeight() - ikon.getIconHeight()) / 2);
            }
            g.dispose();
        }
    }
}
