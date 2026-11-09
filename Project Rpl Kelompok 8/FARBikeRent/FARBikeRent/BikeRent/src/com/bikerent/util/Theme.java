package com.bikerent.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Theme {

    private Theme() { }

    public static final Color BG            = new Color(0xF4F5F7);
    public static final Color SIDEBAR       = new Color(0x1B1B1D);
    public static final Color SIDEBAR_HOVER = new Color(0x2B2B30);
    public static final Color CARD          = Color.WHITE;
    public static final Color BORDER        = new Color(0xE5E7EB);
    public static final Color GRID          = new Color(0xEFF1F4);

    public static final Color ORANGE      = new Color(0xF97316);
    public static final Color ORANGE_DARK = new Color(0xDD5F08);
    public static final Color ORANGE_SOFT = new Color(0xFFF0E4);
    public static final Color GREEN       = new Color(0x22C55E);
    public static final Color GREEN_SOFT  = new Color(0xE6F8ED);
    public static final Color BLUE        = new Color(0x3B82F6);
    public static final Color BLUE_SOFT   = new Color(0xE7F0FE);
    public static final Color PURPLE      = new Color(0xA855F7);
    public static final Color PURPLE_SOFT = new Color(0xF4E9FE);
    public static final Color RED         = new Color(0xEF4444);

    public static final Color TEXT       = new Color(0x111827);
    public static final Color TEXT_MUTED = new Color(0x6B7280);
    public static final Color TEXT_LIGHT = new Color(0x9CA3AF);

    private static String family;

    public static String family() {
        if (family == null) {
            Set<String> ada = new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            String[] pilihan = { "Segoe UI", "Inter", "Roboto", "Noto Sans", "DejaVu Sans", "SansSerif" };
            family = "SansSerif";
            for (String p : pilihan) {
                if (ada.contains(p)) { family = p; break; }
            }
        }
        return family;
    }

    public static Font font(int size)      { return new Font(family(), Font.PLAIN, size); }
    public static Font fontBold(int size)  { return new Font(family(), Font.BOLD,  size); }

    public static void aa(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    public static Color mix(Color a, Color b, float t) {
        return new Color(
                Math.round(a.getRed()   + (b.getRed()   - a.getRed())   * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t));
    }

    public static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
}
