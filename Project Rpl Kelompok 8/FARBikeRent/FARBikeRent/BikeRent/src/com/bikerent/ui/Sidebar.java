package com.bikerent.ui;

import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Sidebar extends JPanel {

    private final List<ItemMenu> item = new ArrayList<>();
    private final JLabel lblNama = new JLabel("Admin");
    private BiConsumer<String, String> saatPilih;
    private Runnable saatUbahPassword, saatKeluar;

    public Sidebar() {
        setLayout(new BorderLayout());
        setBackground(Theme.SIDEBAR);
        setPreferredSize(new Dimension(204, 100));

        add(logo(), BorderLayout.NORTH);

        JPanel menu = new JPanel();
        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(BorderFactory.createEmptyBorder(6, 12, 12, 12));

        menu.add(buat("beranda",  "Beranda",     Ikon.beranda(18, Color.WHITE)));
        menu.add(Box.createVerticalStrut(4));
        menu.add(buat("sepeda",   "Menu Sepeda", Ikon.sepeda(18, Color.WHITE)));
        menu.add(Box.createVerticalStrut(4));
        menu.add(buat("penyewaan","Penyewaan",   Ikon.dokumen(18, Color.WHITE)));
        menu.add(Box.createVerticalStrut(4));
        menu.add(buat("laporan",  "Laporan",     Ikon.grafik(18, Color.WHITE)));

        JPanel pembungkus = new JPanel(new BorderLayout());
        pembungkus.setOpaque(false);
        pembungkus.add(menu, BorderLayout.NORTH);
        add(pembungkus, BorderLayout.CENTER);

        add(pengguna(), BorderLayout.SOUTH);
    }

    public void saatPilih(BiConsumer<String, String> aksi)      { this.saatPilih = aksi; }
    public void saatUbahPassword(Runnable aksi)                 { this.saatUbahPassword = aksi; }
    public void saatKeluar(Runnable aksi)                       { this.saatKeluar = aksi; }
    public void setUsername(String username)                   { lblNama.setText(username); }

    public void pilih(String kunci) {
        for (ItemMenu i : item) i.setAktif(i.kunci.equals(kunci));
        if (saatPilih != null) {
            for (ItemMenu i : item) if (i.kunci.equals(kunci)) saatPilih.accept(kunci, i.judul);
        }
    }

    private ItemMenu buat(String kunci, String judul, Icon ikon) {
        ItemMenu m = new ItemMenu(kunci, judul, ikon);
        m.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { pilih(kunci); }
        });
        item.add(m);
        return m;
    }

    private JPanel logo() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(18, 16, 14, 16));

        JLabel ikon = new JLabel(Ikon.sepeda(30, Theme.ORANGE));

        JPanel teks = new JPanel();
        teks.setOpaque(false);
        teks.setLayout(new BoxLayout(teks, BoxLayout.Y_AXIS));
        JLabel nama = new JLabel("FAR BikeRent");
        nama.setFont(Theme.fontBold(17));
        nama.setForeground(Color.WHITE);
        JLabel slogan = new JLabel("Ride More, Worry Less");
        slogan.setFont(Theme.font(9));
        slogan.setForeground(new Color(0x9AA0A6));
        nama.setAlignmentX(Component.LEFT_ALIGNMENT);
        slogan.setAlignmentX(Component.LEFT_ALIGNMENT);
        teks.add(nama);
        teks.add(slogan);

        p.add(ikon, BorderLayout.WEST);
        p.add(teks, BorderLayout.CENTER);
        return p;
    }

    private JPanel pengguna() {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        p.setCursor(new Cursor(Cursor.HAND_CURSOR));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(0x2E2E33)),
                BorderFactory.createEmptyBorder(14, 16, 16, 14)));

        JLabel avatar = new JLabel(Ikon.pengguna(20, Theme.SIDEBAR)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Theme.aa(g);
                g.setColor(Color.WHITE);
                g.fillOval(0, 0, 30, 30);
                g.dispose();
                super.paintComponent(g0);
            }
        };
        avatar.setPreferredSize(new Dimension(30, 30));
        avatar.setHorizontalAlignment(JLabel.CENTER);

        lblNama.setFont(Theme.font(13));
        lblNama.setForeground(Color.WHITE);

        JLabel panah = new JLabel(Ikon.panahBawah(14, new Color(0x9AA0A6)));

        p.add(avatar, BorderLayout.WEST);
        p.add(lblNama, BorderLayout.CENTER);
        p.add(panah, BorderLayout.EAST);

        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        JMenuItem ubahSandi = new JMenuItem("Ubah Password", Ikon.kunci(14, Theme.TEXT_MUTED));
        ubahSandi.setFont(Theme.font(13));
        ubahSandi.addActionListener(e -> { if (saatUbahPassword != null) saatUbahPassword.run(); });
        JMenuItem keluar = new JMenuItem("Keluar", Ikon.keluar(14, Theme.TEXT_MUTED));
        keluar.setFont(Theme.font(13));
        keluar.addActionListener(e -> { if (saatKeluar != null) saatKeluar.run(); });
        menu.add(ubahSandi);
        menu.add(keluar);

        p.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { menu.show(p, 0, -menu.getPreferredSize().height - 4); }
        });
        return p;
    }

    private static class ItemMenu extends JPanel {
        final String kunci, judul;
        private final JLabel label;
        private boolean aktif, hover;

        ItemMenu(String kunci, String judul, Icon ikon) {
            super(new BorderLayout());
            this.kunci = kunci;
            this.judul = judul;
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            label = new JLabel(judul, ikon, JLabel.LEFT);
            label.setIconTextGap(10);
            label.setFont(Theme.font(13));
            label.setForeground(new Color(0xC8CBD0));
            add(label, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
        }

        void setAktif(boolean a) {
            this.aktif = a;
            label.setFont(a ? Theme.fontBold(13) : Theme.font(13));
            label.setForeground(a ? Color.WHITE : new Color(0xC8CBD0));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Theme.aa(g);
            if (aktif || hover) {
                g.setColor(aktif ? Theme.ORANGE : Theme.SIDEBAR_HOVER);
                g.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
            }
            g.dispose();
        }
    }
}
