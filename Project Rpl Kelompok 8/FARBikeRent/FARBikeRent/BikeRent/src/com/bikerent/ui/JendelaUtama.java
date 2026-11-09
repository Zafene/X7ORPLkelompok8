package com.bikerent.ui;

import com.bikerent.model.Admin;
import com.bikerent.ui.icon.Ikon;
import com.bikerent.ui.panel.PanelBeranda;
import com.bikerent.ui.panel.PanelLaporan;
import com.bikerent.ui.panel.PanelMenuSepeda;
import com.bikerent.ui.panel.PanelPenyewaan;
import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class JendelaUtama extends JFrame {

    private final CardLayout card = new CardLayout();
    private final JPanel wadah = new JPanel(card);
    private final Map<String, JPanel> halaman = new HashMap<>();
    private final Sidebar sidebar = new Sidebar();
    private final BilahJudul bilah;

    private final PanelBeranda panelBeranda = new PanelBeranda();
    private final PanelMenuSepeda panelSepeda = new PanelMenuSepeda();
    private final PanelPenyewaan panelPenyewaan = new PanelPenyewaan();
    private final PanelLaporan panelLaporan = new PanelLaporan();

    public JendelaUtama(Admin admin) {
        setTitle("FAR BikeRent - Sistem Informasi Rental Sepeda");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setMinimumSize(new Dimension(1100, 680));
        setSize(1280, 780);
        setLocationRelativeTo(null);

        bilah = new BilahJudul(this);

        wadah.setBackground(Theme.BG);
        daftarHalaman("beranda", panelBeranda);
        daftarHalaman("sepeda", panelSepeda);
        daftarHalaman("penyewaan", panelPenyewaan);
        daftarHalaman("laporan", panelLaporan);

        JPanel kanan = new JPanel(new BorderLayout());
        kanan.setBackground(Theme.BG);
        kanan.add(bilah, BorderLayout.NORTH);
        kanan.add(wadah, BorderLayout.CENTER);

        JPanel akar = new JPanel(new BorderLayout());
        akar.setBackground(Theme.BG);
        akar.setBorder(BorderFactory.createLineBorder(new Color(0xD8DADF)));
        akar.add(sidebar, BorderLayout.WEST);
        akar.add(kanan, BorderLayout.CENTER);
        setContentPane(akar);

        sidebar.saatPilih((kunci, judul) -> {
            card.show(wadah, kunci);
            bilah.set(judul, ikonHalaman(kunci));
            JPanel p = halaman.get(kunci);
            if (p instanceof Muat) ((Muat) p).muatData();
        });

        panelSepeda.saatSewa(s -> {
            sidebar.pilih("penyewaan");
            panelPenyewaan.pilihSepeda(s.getId());
        });

        panelPenyewaan.saatTersimpan(() -> {
            panelBeranda.muatData();
            panelLaporan.muatData();
        });

        sidebar.setUsername(admin.getUsername());
        sidebar.saatUbahPassword(() -> new DialogUbahPassword(this, admin).setVisible(true));
        sidebar.saatKeluar(() -> {
            int ok = javax.swing.JOptionPane.showConfirmDialog(this, "Keluar dari aplikasi?",
                    "FAR BikeRent", javax.swing.JOptionPane.YES_NO_OPTION);
            if (ok == javax.swing.JOptionPane.YES_OPTION) {
                com.bikerent.db.Koneksi.tutup();
                System.exit(0);
            }
        });

        sidebar.pilih("beranda");
    }

    private void daftarHalaman(String kunci, JPanel panel) {
        halaman.put(kunci, panel);
        JScrollPane sp = new JScrollPane(panel);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBackground(Theme.BG);
        sp.getViewport().setBackground(Theme.BG);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        wadah.add(sp, kunci);
    }

    private Icon ikonHalaman(String kunci) {
        switch (kunci) {
            case "sepeda":    return Ikon.sepeda(19, Theme.TEXT);
            case "penyewaan": return Ikon.dokumen(19, Theme.TEXT);
            case "laporan":   return Ikon.grafik(19, Theme.TEXT);
            default:          return Ikon.beranda(19, Theme.TEXT);
        }
    }
}
