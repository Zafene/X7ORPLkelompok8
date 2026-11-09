package com.bikerent.ui;

import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Fmt;
import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;

public class BilahJudul extends JPanel {

    private final JLabel label = new JLabel();
    private final JLabel lblJam = new JLabel();
    private final JFrame frame;
    private Point titikKlik;

    public BilahJudul(JFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                BorderFactory.createEmptyBorder(10, 18, 10, 8)));

        label.setFont(Theme.fontBold(15));
        label.setForeground(Theme.TEXT);
        label.setIconTextGap(10);

        lblJam.setFont(Theme.font(11));
        lblJam.setForeground(Theme.TEXT_MUTED);
        updateJam();

        Timer timer = new Timer(1000, e -> updateJam());
        timer.setInitialDelay(0);
        timer.start();

        add(label, BorderLayout.WEST);

        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        kanan.setOpaque(false);
        kanan.add(lblJam);
        kanan.add(tombolJendela());
        add(kanan, BorderLayout.EAST);

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { titikKlik = e.getPoint(); }
            @Override public void mouseClicked(MouseEvent e)  {
                if (e.getClickCount() == 2) toggleMaksimal();
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (titikKlik == null || frame.getExtendedState() == Frame.MAXIMIZED_BOTH) return;
                Point layar = e.getLocationOnScreen();
                frame.setLocation(layar.x - titikKlik.x, layar.y - titikKlik.y);
            }
        });
    }

    public void set(String judul, Icon ikon) { label.setText(judul); label.setIcon(ikon); }

    private void updateJam() { lblJam.setText(Fmt.formatJam(LocalDateTime.now())); }

    private void toggleMaksimal() {
        frame.setExtendedState(frame.getExtendedState() == Frame.MAXIMIZED_BOTH
                ? Frame.NORMAL : Frame.MAXIMIZED_BOTH);
    }

    private JPanel tombolJendela() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        p.setOpaque(false);
        p.add(tombol(Ikon.minimize(16, Theme.TEXT_MUTED), new Color(0xF0F1F3),
                () -> frame.setState(Frame.ICONIFIED)));
        p.add(tombol(Ikon.maximize(14, Theme.TEXT_MUTED), new Color(0xF0F1F3), this::toggleMaksimal));
        p.add(tombol(Ikon.tutup(16, Theme.TEXT_MUTED), new Color(0xFDE2E2), () -> {
            com.bikerent.db.Koneksi.tutup();
            System.exit(0);
        }));
        return p;
    }

    private JButton tombol(Icon ikon, Color warnaHover, Runnable aksi) {
        JButton b = new JButton(ikon) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Theme.aa(g);
                if (getModel().isRollover()) {
                    g.setColor(warnaHover);
                    g.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                }
                g.dispose();
                super.paintComponent(g0);
            }
        };
        b.setPreferredSize(new Dimension(34, 26));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.addActionListener(e -> aksi.run());
        return b;
    }
}
