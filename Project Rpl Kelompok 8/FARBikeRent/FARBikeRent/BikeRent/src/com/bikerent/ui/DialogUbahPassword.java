package com.bikerent.ui;

import com.bikerent.dao.AdminDAO;
import com.bikerent.model.Admin;
import com.bikerent.ui.comp.Medan;
import com.bikerent.ui.comp.Tombol;
import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;

public class DialogUbahPassword extends JDialog {

    private final AdminDAO adminDAO = new AdminDAO();
    private final Admin admin;

    private final Medan.Sandi sandiLama       = new Medan.Sandi("Password lama");
    private final Medan.Sandi sandiBaru       = new Medan.Sandi("Password baru");
    private final Medan.Sandi sandiKonfirmasi = new Medan.Sandi("Konfirmasi password baru");

    public DialogUbahPassword(Window parent, Admin admin) {
        super(parent, "Ubah Password", ModalityType.APPLICATION_MODAL);
        this.admin = admin;
        setSize(380, 380);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel isi = new JPanel();
        isi.setOpaque(false);
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setBorder(BorderFactory.createEmptyBorder(22, 22, 18, 22));

        JLabel judul = com.bikerent.ui.comp.Kartu.label("Ubah Password — " + admin.getUsername(), 15, Theme.TEXT, true);
        judul.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(judul);
        isi.add(Box.createVerticalStrut(16));

        isi.add(medan("Password Lama", sandiLama));
        isi.add(medan("Password Baru", sandiBaru));
        isi.add(medan("Konfirmasi Password Baru", sandiKonfirmasi));

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bawah.setOpaque(false);
        bawah.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        Tombol batal  = new Tombol("Batal");
        batal.gaya(Tombol.Gaya.GARIS);
        batal.addActionListener(e -> dispose());
        Tombol simpan = new Tombol("Simpan", Ikon.centang(15, java.awt.Color.WHITE));
        simpan.addActionListener(e -> prosesSimpan());
        bawah.add(batal); bawah.add(simpan);

        JPanel akar = new JPanel(new java.awt.BorderLayout());
        akar.add(isi, java.awt.BorderLayout.CENTER);
        akar.add(bawah, java.awt.BorderLayout.SOUTH);
        setContentPane(akar);
    }

    private JPanel medan(String label, Component input) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel l = Medan.labelInput(label, true);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        javax.swing.JComponent jc = (javax.swing.JComponent) input;
        Medan.tinggi(jc, 38);
        jc.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(4)); p.add(input); p.add(Box.createVerticalStrut(12));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        return p;
    }

    private void prosesSimpan() {
        String lama = new String(sandiLama.getPassword());
        String baru = new String(sandiBaru.getPassword());
        String konfirmasi = new String(sandiKonfirmasi.getPassword());

        if (lama.isEmpty() || baru.isEmpty()) { pesan("Semua kolom wajib diisi."); return; }
        if (adminDAO.login(admin.getUsername(), lama) == null) { pesan("Password lama salah."); return; }
        if (baru.length() < 4) { pesan("Password baru minimal 4 karakter."); return; }
        if (!baru.equals(konfirmasi)) { pesan("Konfirmasi password baru tidak cocok."); return; }

        adminDAO.ubahPassword(admin.getId(), baru);
        JOptionPane.showMessageDialog(this, "Password berhasil diubah.", "FAR BikeRent", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void pesan(String teks) {
        JOptionPane.showMessageDialog(this, teks, "FAR BikeRent", JOptionPane.WARNING_MESSAGE);
    }
}
