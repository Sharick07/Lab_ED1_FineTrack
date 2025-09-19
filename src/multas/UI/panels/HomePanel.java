package multas.ui.panels;

import javax.swing.*;
import java.awt.*;
import multas.utils.UIUtils;
import multas.utils.UIUtils;


// Esta clase es el panel de inicio de la aplicación.
// Muestra información general, menús y mensajes de bienvenida al usuario.
//
public class HomePanel extends JPanel {

    public HomePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.APP_BG);

        // Panel central
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(UIUtils.APP_BG);
        center.setBorder(BorderFactory.createEmptyBorder(60, 20, 20, 20));

        // Logo opcional
        JLabel logo = new JLabel(UIUtils.getScaledIcon("/imagenes/Logo (2).png", 150, 150));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Título principal
        JLabel title = new JLabel("Bienvenido a FineTrack");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(25, 25, 25));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));

        // Mensaje de bienvenida
        JTextArea message = new JTextArea(
            "FineTrack es un sistema diseñado para la gestión eficiente de multas de tránsito.\n\n" +
            "Con esta aplicación podrás:\n" +
            " - Registrar nuevas multas con todos sus detalles.\n" +
            " - Consultar información de conductores y vehículos.\n" +
            " - Registrar pagos y mantener el historial actualizado.\n" +
            " - Identificar multas vencidas y generar reportes.\n" +
            " - Eliminar registros de multas pagadas.\n\n" +
            ""
        );
        message.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        message.setForeground(new Color(60, 60, 60));
        message.setBackground(UIUtils.APP_BG);
        message.setEditable(false);
        message.setFocusable(false);
        message.setOpaque(false);
        message.setAlignmentX(Component.CENTER_ALIGNMENT);
        message.setBorder(BorderFactory.createEmptyBorder(15, 40, 0, 40));
        message.setLineWrap(true);
        message.setWrapStyleWord(true);

        // Agregar todo al panel central
        center.add(logo);
        center.add(title);
        center.add(message);

        add(center, BorderLayout.CENTER);
    }
}

