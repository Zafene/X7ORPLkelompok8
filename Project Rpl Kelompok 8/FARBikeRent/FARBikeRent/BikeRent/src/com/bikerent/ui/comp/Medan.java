package com.bikerent.ui.comp;

import com.bikerent.ui.icon.Ikon;
import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.DefaultListCellRenderer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public final class Medan {

    private Medan() { }

    public static class Teks extends JTextField {
        private String placeholder = "";
        private boolean fokus;

        public Teks(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(Theme.font(13));
            setForeground(Theme.TEXT);
            setCaretColor(Theme.ORANGE);
            setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { fokus = true;  repaint(); }
                @Override public void focusLost(FocusEvent e)   { fokus = false; repaint(); }
            });
        }

        public void setPlaceholder(String p) { this.placeholder = p; repaint(); }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Theme.aa(g);
            g.setColor(isEnabled() ? Color.WHITE : new Color(0xF6F7F9));
            g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 9, 9));
            g.setColor(fokus ? Theme.ORANGE : Theme.BORDER);
            g.setStroke(new BasicStroke(fokus ? 1.6f : 1f));
            g.draw(new RoundRectangle2D.Double(0.6, 0.6, getWidth() - 2, getHeight() - 2, 9, 9));
            g.dispose();
            super.paintComponent(g0);

            if (getText().isEmpty() && !placeholder.isEmpty()) {
                Graphics2D gp = (Graphics2D) g0.create();
                Theme.aa(gp);
                gp.setColor(Theme.TEXT_LIGHT);
                gp.setFont(Theme.font(13));
                gp.drawString(placeholder, getInsets().left,
                        getHeight() / 2 + gp.getFontMetrics().getAscent() / 2 - 2);
                gp.dispose();
            }
        }
    }

    public static class Sandi extends javax.swing.JPasswordField {
        private String placeholder = "";
        private boolean fokus;

        public Sandi(String placeholder) {
            this.placeholder = placeholder;
            setOpaque(false);
            setFont(Theme.font(13));
            setForeground(Theme.TEXT);
            setCaretColor(Theme.ORANGE);
            setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { fokus = true;  repaint(); }
                @Override public void focusLost(FocusEvent e)   { fokus = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Theme.aa(g);
            g.setColor(isEnabled() ? Color.WHITE : new Color(0xF6F7F9));
            g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 9, 9));
            g.setColor(fokus ? Theme.ORANGE : Theme.BORDER);
            g.setStroke(new BasicStroke(fokus ? 1.6f : 1f));
            g.draw(new RoundRectangle2D.Double(0.6, 0.6, getWidth() - 2, getHeight() - 2, 9, 9));
            g.dispose();
            super.paintComponent(g0);

            if (getPassword().length == 0 && !placeholder.isEmpty()) {
                Graphics2D gp = (Graphics2D) g0.create();
                Theme.aa(gp);
                gp.setColor(Theme.TEXT_LIGHT);
                gp.setFont(Theme.font(13));
                gp.drawString(placeholder, getInsets().left,
                        getHeight() / 2 + gp.getFontMetrics().getAscent() / 2 - 2);
                gp.dispose();
            }
        }
    }

    public static JPanel denganIkon(JTextField field, Icon ikon, boolean kiri) {
        JPanel p = new JPanel(new BorderLayout(6, 0)) {
            @Override protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Theme.aa(g);
                g.setColor(Color.WHITE);
                g.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 9, 9));
                g.setColor(Theme.BORDER);
                g.draw(new RoundRectangle2D.Double(0.6, 0.6, getWidth() - 2, getHeight() - 2, 9, 9));
                g.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        JLabel l = new JLabel(ikon);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(9, 2, 9, 2));
        if (field instanceof Teks) {

            field.setBorder(BorderFactory.createEmptyBorder(9, 2, 9, 2));
        }
        p.add(field, BorderLayout.CENTER);
        p.add(l, kiri ? BorderLayout.WEST : BorderLayout.EAST);
        return p;
    }

    public static JLabel labelInput(String teks, boolean wajib) {
        JLabel l = new JLabel(teks + (wajib ? " *" : ""));
        l.setFont(Theme.font(12));
        l.setForeground(Theme.TEXT_MUTED);
        return l;
    }

    public static class Combo<T> extends JComboBox<T> {
        public Combo(T[] isi) {
            super(isi);
            setFont(Theme.font(13));
            setForeground(Theme.TEXT);
            setOpaque(false);
            setFocusable(false);
            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 2));
            setUI(new BasicComboBoxUI() {
                @Override protected JButton createArrowButton() {
                    JButton b = new JButton(Ikon.panahBawah(14, Theme.TEXT_MUTED));
                    b.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
                    b.setContentAreaFilled(false);
                    b.setBorderPainted(false);
                    b.setFocusable(false);
                    return b;
                }
                @Override protected ComboPopup createPopup() {
                    BasicComboPopup popup = (BasicComboPopup) super.createPopup();
                    popup.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
                    return popup;
                }
                @Override public void paintCurrentValueBackground(Graphics g, java.awt.Rectangle b, boolean f) { }
            });
            setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                        boolean sel, boolean fok) {
                    JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, sel, fok);
                    l.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                    l.setFont(Theme.font(13));
                    l.setBackground(sel ? Theme.ORANGE_SOFT : Color.WHITE);
                    l.setForeground(Theme.TEXT);
                    return l;
                }
            });
        }

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
            super.paintComponent(g0);
        }
    }

    public static <T> Combo<T> combo(T[] isi) { return new Combo<>(isi); }

    public static void tinggi(JComponent c, int h) {
        Dimension d = c.getPreferredSize();
        c.setPreferredSize(new Dimension(d.width, h));
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        c.setMinimumSize(new Dimension(40, h));
        if (c instanceof JLabel) ((JLabel) c).setVerticalAlignment(SwingConstants.CENTER);
    }
}
