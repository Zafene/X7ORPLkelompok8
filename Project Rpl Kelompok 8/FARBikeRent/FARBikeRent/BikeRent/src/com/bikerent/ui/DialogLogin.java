package com.bikerent.ui;

import com.bikerent.dao.AdminDAO;
import com.bikerent.model.Admin;
import com.bikerent.ui.comp.Kartu;
import com.bikerent.ui.comp.Medan;
import com.bikerent.ui.comp.Tombol;
import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class DialogLogin extends JFrame {

    private final AdminDAO adminDAO = new AdminDAO();
    private final CardLayout card = new CardLayout();
    private final JPanel wadahKartu = new JPanel(card);

    private final Medan.Teks  loginUsername = new Medan.Teks("Username");
    private final Medan.Sandi loginPassword = new Medan.Sandi("Password");

    private final Medan.Teks  daftarUsername = new Medan.Teks("Username baru");
    private final Medan.Sandi daftarPassword = new Medan.Sandi("Password");
    private final Medan.Sandi daftarKonfirmasi = new Medan.Sandi("Konfirmasi password");

    private Consumer<Admin> saatBerhasil;

    public DialogLogin() {
        setTitle("FAR BikeRent - Masuk");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(420, 600);
        setLocationRelativeTo(null);

        JPanel akar = new JPanel(new BorderLayout());
        akar.setBackground(Theme.BG);
        akar.setBorder(BorderFactory.createLineBorder(new Color(0xD8DADF)));
        akar.add(tombolTutup(), BorderLayout.NORTH);

        JPanel tengah = new JPanel(new GridBagLayout());
        tengah.setOpaque(false);
        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.add(logo());
        isi.add(Box.createVerticalStrut(22));

        wadahKartu.setOpaque(false);
        wadahKartu.add(kartuLogin(), "login");
        wadahKartu.add(kartuDaftar(), "daftar");
        wadahKartu.setMaximumSize(new Dimension(320, 340));
        wadahKartu.setPreferredSize(new Dimension(320, 340));
        wadahKartu.setAlignmentX(Component.CENTER_ALIGNMENT);
        isi.add(wadahKartu);

        tengah.add(isi, new GridBagConstraints());
        akar.add(tengah, BorderLayout.CENTER);
        setContentPane(akar);
    }

    public void saatBerhasil(Consumer<Admin> aksi) { this.saatBerhasil = aksi; }

    private JPanel tombolTutup() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        p.setOpaque(false);
        JButton b = new JButton(Ikon.tutup(15, Theme.TEXT_MUTED)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Theme.aa(g);
                if (getModel().isRollover()) {
                    g.setColor(new Color(0xFDE2E2));
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                }
                g.dispose();
                super.paintComponent(g0);
            }
        };
        b.setPreferredSize(new Dimension(30, 26));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.addActionListener(e -> { com.bikerent.db.Koneksi.tutup(); System.exit(0); });
        p.add(b);
        return p;
    }

    private JPanel logo() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel ikon = new JLabel(Ikon.sepeda(42, Theme.ORANGE));
        ikon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel nama = new JLabel("FAR BikeRent");
        nama.setFont(Theme.fontBold(22));
        nama.setForeground(Theme.TEXT);
        nama.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel slogan = new JLabel("Ride More, Worry Less");
        slogan.setFont(Theme.font(12));
        slogan.setForeground(Theme.TEXT_MUTED);
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(ikon); p.add(Box.createVerticalStrut(8)); p.add(nama);
        p.add(Box.createVerticalStrut(2)); p.add(slogan);
        return p;
    }

    private Kartu kartuLogin() {
        Kartu k = new Kartu();
        k.padding(26);
        k.setLayout(new BoxLayout(k, BoxLayout.Y_AXIS));

        JLabel judul = Kartu.label("Masuk sebagai Admin", 16, Theme.TEXT, true);
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        k.add(judul);
        k.add(Box.createVerticalStrut(18));

        Medan.tinggi(loginUsername, 40);
        JPanel bu = Medan.denganIkon(loginUsername, Ikon.pengguna(15, Theme.TEXT_LIGHT), true);
        Medan.tinggi(bu, 40);
        bu.setAlignmentX(Component.LEFT_ALIGNMENT);
        k.add(bu);
        k.add(Box.createVerticalStrut(10));

        Medan.tinggi(loginPassword, 40);
        JPanel bp = Medan.denganIkon(loginPassword, Ikon.kunci(15, Theme.TEXT_LIGHT), true);
        Medan.tinggi(bp, 40);
        bp.setAlignmentX(Component.LEFT_ALIGNMENT);
        k.add(bp);
        k.add(Box.createVerticalStrut(18));

        Tombol masuk = new Tombol("Masuk");
        masuk.setAlignmentX(Component.LEFT_ALIGNMENT);
        masuk.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        masuk.addActionListener(e -> prosesLogin());
        loginPassword.addActionListener(e -> prosesLogin());
        k.add(masuk);
        k.add(Box.createVerticalStrut(16));

        k.add(tautan("Belum punya akun? Daftar di sini", () -> {
            daftarUsername.setText(loginUsername.getText());
            card.show(wadahKartu, "daftar");
        }));
        return k;
    }

    private Kartu kartuDaftar() {
        Kartu k = new Kartu();
        k.padding(26);
        k.setLayout(new BoxLayout(k, BoxLayout.Y_AXIS));

        JLabel judul = Kartu.label("Daftar Admin Baru", 16, Theme.TEXT, true);
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        k.add(judul);
        k.add(Box.createVerticalStrut(16));

        Medan.tinggi(daftarUsername, 38);
        JPanel bu = Medan.denganIkon(daftarUsername, Ikon.pengguna(15, Theme.TEXT_LIGHT), true);
        Medan.tinggi(bu, 38);
        bu.setAlignmentX(Component.LEFT_ALIGNMENT);
        k.add(bu);
        k.add(Box.createVerticalStrut(8));

        Medan.tinggi(daftarPassword, 38);
        JPanel bp = Medan.denganIkon(daftarPassword, Ikon.kunci(15, Theme.TEXT_LIGHT), true);
        Medan.tinggi(bp, 38);
        bp.setAlignmentX(Component.LEFT_ALIGNMENT);
        k.add(bp);
        k.add(Box.createVerticalStrut(8));

        Medan.tinggi(daftarKonfirmasi, 38);
        JPanel bk = Medan.denganIkon(daftarKonfirmasi, Ikon.kunci(15, Theme.TEXT_LIGHT), true);
        Medan.tinggi(bk, 38);
        bk.setAlignmentX(Component.LEFT_ALIGNMENT);
        k.add(bk);
        k.add(Box.createVerticalStrut(16));

        Tombol daftar = new Tombol("Daftar Akun");
        daftar.setAlignmentX(Component.LEFT_ALIGNMENT);
        daftar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        daftar.addActionListener(e -> prosesDaftar());
        daftarKonfirmasi.addActionListener(e -> prosesDaftar());
        k.add(daftar);
        k.add(Box.createVerticalStrut(16));

        k.add(tautan("Sudah punya akun? Masuk di sini", () -> {
            loginUsername.setText(daftarUsername.getText());
            card.show(wadahKartu, "login");
        }));
        return k;
    }

    private JLabel tautan(String teks, Runnable aksi) {
        JLabel l = new JLabel(teks);
        l.setFont(Theme.font(12));
        l.setForeground(Theme.ORANGE);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setCursor(new Cursor(Cursor.HAND_CURSOR));
        l.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { aksi.run(); }
        });
        return l;
    }

    private void prosesLogin() {
        String u = loginUsername.getText().trim();
        String p = new String(loginPassword.getPassword());
        if (u.isEmpty() || p.isEmpty()) { pesan("Username dan password wajib diisi."); return; }
        Admin a = adminDAO.login(u, p);
        if (a == null) { pesan("Username atau password salah."); return; }
        if (saatBerhasil != null) saatBerhasil.accept(a);
        dispose();
    }

    private void prosesDaftar() {
        String u = daftarUsername.getText().trim();
        String p = new String(daftarPassword.getPassword());
        String kp = new String(daftarKonfirmasi.getPassword());
        if (u.isEmpty() || p.isEmpty()) { pesan("Username dan password wajib diisi."); return; }
        if (u.length() < 3) { pesan("Username minimal 3 karakter."); return; }
        if (p.length() < 4) { pesan("Password minimal 4 karakter."); return; }
        if (!p.equals(kp)) { pesan("Konfirmasi password tidak cocok."); return; }
        if (adminDAO.adaUsername(u)) { pesan("Username sudah dipakai, pilih yang lain."); return; }

        adminDAO.daftar(u, p);
        JOptionPane.showMessageDialog(this, "Akun berhasil dibuat. Silakan masuk.",
                "FAR BikeRent", JOptionPane.INFORMATION_MESSAGE);
        loginUsername.setText(u);
        daftarUsername.setText(""); daftarPassword.setText(""); daftarKonfirmasi.setText("");
        card.show(wadahKartu, "login");
    }

    private void pesan(String teks) {
        JOptionPane.showMessageDialog(this, teks, "FAR BikeRent", JOptionPane.WARNING_MESSAGE);
    }
}
