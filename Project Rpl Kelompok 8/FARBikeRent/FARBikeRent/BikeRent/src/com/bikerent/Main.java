package com.bikerent;

import com.bikerent.db.Koneksi;
import com.bikerent.ui.DialogLogin;
import com.bikerent.ui.JendelaUtama;
import com.bikerent.util.Theme;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {

        }

        UIManager.put("ToolTip.background", java.awt.Color.WHITE);
        UIManager.put("OptionPane.background", java.awt.Color.WHITE);
        UIManager.put("Panel.background", java.awt.Color.WHITE);
        UIManager.put("OptionPane.messageFont", Theme.font(13));
        UIManager.put("OptionPane.buttonFont", Theme.font(13));
        UIManager.put("Button.font", Theme.font(13));
        UIManager.put("ComboBox.font", Theme.font(13));

        SwingUtilities.invokeLater(() -> {
            try {
                Koneksi.siapkan();
                DialogLogin login = new DialogLogin();
                login.saatBerhasil(admin -> new JendelaUtama(admin).setVisible(true));
                login.setVisible(true);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null,
                        "Aplikasi gagal dijalankan:\n" + e.getMessage(),
                        "FAR BikeRent", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
