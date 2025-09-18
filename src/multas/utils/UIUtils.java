package multas.utils;

import java.awt.*;
import javax.swing.*;
import java.net.URL;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class UIUtils {
    // Colors
    public static final Color APP_BG = Color.decode("#F5F7FA");
    public static final Color LIGHT_BLUE = Color.decode("#64B5F6");
    public static final Color SIDEBAR_HOVER_BG = Color.decode("#08377a");
    public static final Color PRIMARY = Color.decode("#1976D2");
    public static final Color SUCCESS = Color.decode("#2E7D32");
    public static final Color WARNING = Color.decode("#F9A825");
    public static final Color DANGER = Color.decode("#C62828");
    public static final Color WHITE = Color.white;

    // Fonts
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font MENU_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // Icon loader utility
    public static ImageIcon getScaledIcon(String resourcePath, int w, int h) {
        URL url = UIUtils.class.getResource(resourcePath);
        if (url == null) {
            // fallback blank icon
            return new ImageIcon(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
        }
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // hover effect factory
    public static MouseAdapter createHoverEffect(JComponent comp, Color base, Color hover) {
        return new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                comp.setBackground(hover);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                comp.setBackground(base);
            }
        };
    }
}

