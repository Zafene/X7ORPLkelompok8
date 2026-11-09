package com.bikerent.ui.comp;

import com.bikerent.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

public final class TabelRapi {

    private TabelRapi() { }

    public static DefaultTableModel model(String... kolom) {
        return new DefaultTableModel(kolom, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    public static JTable buat(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(Theme.font(12));
        t.setForeground(Theme.TEXT);
        t.setRowHeight(30);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(Theme.GRID);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setSelectionBackground(Theme.ORANGE_SOFT);
        t.setSelectionForeground(Theme.TEXT);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFillsViewportHeight(true);
        t.setBackground(Color.WHITE);
        t.setBorder(BorderFactory.createEmptyBorder());

        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(10, 32));
        h.setDefaultRenderer(new RendererHeader());
        h.setBorder(BorderFactory.createEmptyBorder());

        DefaultTableCellRenderer isi = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel,
                                                           boolean fok, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, sel, false, r, c);
                l.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                l.setFont(Theme.font(12));
                if (!sel) l.setBackground(Color.WHITE);
                return l;
            }
        };
        for (int i = 0; i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setCellRenderer(isi);
        }
        return t;
    }

    public static JScrollPane gulir(JTable t) {
        JScrollPane sp = new JScrollPane(t);

        sp.setColumnHeaderView(t.getTableHeader());
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1, true));
        sp.getViewport().setBackground(Color.WHITE);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    public static void lebarKolom(JTable t, int... lebar) {
        for (int i = 0; i < lebar.length && i < t.getColumnCount(); i++) {
            t.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
            if (lebar[i] <= 60) t.getColumnModel().getColumn(i).setMaxWidth(lebar[i] + 30);
        }
    }

    public static void kolomBadge(JTable t, int kolom) {
        t.getColumnModel().getColumn(kolom).setCellRenderer(new RendererBadge());
    }

    private static class RendererHeader extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tb, Object v, boolean s, boolean f, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(tb, v, s, f, r, c);
            l.setFont(Theme.fontBold(11));
            l.setForeground(Theme.TEXT_MUTED);
            l.setBackground(new Color(0xF9FAFB));
            l.setOpaque(true);
            l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER),
                    BorderFactory.createEmptyBorder(0, 8, 0, 8)));
            return l;
        }
    }

    public static class RendererBadge implements TableCellRenderer {
        private final Badge badge = new Badge();

        @Override
        public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean f, int r, int c) {
            badge.setText(v == null ? "" : v.toString());
            badge.latarBaris = sel ? Theme.ORANGE_SOFT : Color.WHITE;
            return badge;
        }
    }

    public static class Badge extends JLabel {
        Color latarBaris = Color.WHITE;

        Badge() {
            setFont(Theme.fontBold(11));
            setHorizontalAlignment(CENTER);
            setOpaque(false);
        }

        static Color[] warnaDari(String s) {
            String t = s == null ? "" : s.toLowerCase();
            if (t.contains("selesai"))  return new Color[] { Theme.GREEN_SOFT,  new Color(0x15803D) };
            if (t.contains("disewa"))   return new Color[] { Theme.ORANGE_SOFT, new Color(0xB45309) };
            if (t.contains("batal"))    return new Color[] { new Color(0xFDE8E8), new Color(0xB91C1C) };
            return new Color[] { new Color(0xF1F3F5), Theme.TEXT_MUTED };
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Theme.aa(g);
            g.setColor(latarBaris);
            g.fillRect(0, 0, getWidth(), getHeight());

            Color[] c = warnaDari(getText());
            int tw = g.getFontMetrics(getFont()).stringWidth(getText()) + 20;
            tw = Math.min(tw, getWidth() - 8);
            int x = (getWidth() - tw) / 2, y = (getHeight() - 20) / 2;
            g.setColor(c[0]);
            g.fill(new RoundRectangle2D.Double(x, y, tw, 20, 10, 10));
            setForeground(c[1]);
            g.dispose();
            super.paintComponent(g0);
        }
    }
}
