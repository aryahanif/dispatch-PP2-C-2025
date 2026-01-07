package id.ac.unpas.Controller;

import javax.swing.JOptionPane;
import java.awt.Component;

final class UiUtil {

    private UiUtil() {
    }

    static boolean confirm(Component parent, String message) {
        return JOptionPane.showConfirmDialog(parent, message, "Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    static Long tryParseLong(String text) {
        if (text == null) {
            return null;
        }
        String t = text.trim();
        if (t.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(t);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}