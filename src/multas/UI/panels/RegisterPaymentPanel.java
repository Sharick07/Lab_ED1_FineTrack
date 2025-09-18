package multas.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import multas.utils.UIUtils;
import multas.utils.FileManager;

public class RegisterPaymentPanel extends JPanel {
    private JTextField txtPlate, txtCode;
    private JSpinner dateSpinner;
    private JFormattedTextField txtMonto;
    public RegisterPaymentPanel() {
        setLayout(new GridBagLayout());
        setBackground(UIUtils.APP_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y=0;

        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Placa:"), gbc);
        txtPlate = new JTextField(20); gbc.gridx=1; add(txtPlate, gbc); y++;

        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Código multa (opcional):"), gbc);
        txtCode = new JTextField(20); gbc.gridx=1; add(txtCode, gbc); y++;

        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Fecha pago:"), gbc);
        dateSpinner = new JSpinner(new javax.swing.SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        gbc.gridx=1; add(dateSpinner, gbc); y++;

        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Monto pagado:"), gbc);
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
        javax.swing.text.NumberFormatter nfF = new javax.swing.text.NumberFormatter(nf);
        nfF.setAllowsInvalid(false);
        txtMonto = new JFormattedTextField(nfF); txtMonto.setColumns(10);
        gbc.gridx=1; add(txtMonto, gbc); y++;

        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=2;
        JPanel btns = new JPanel(); btns.setBackground(UIUtils.APP_BG);
        JButton btnReg = new JButton("Registrar pago");
        btnReg.setPreferredSize(new Dimension(160,36));
        btnReg.addActionListener(e -> registerPayment());
        btns.add(btnReg);
        add(btns, gbc);
    }

    private void registerPayment() {
        String placa = txtPlate.getText().trim();
        String code = txtCode.getText().trim();
        Number montoN = (Number) txtMonto.getValue();
        Date fecha = (Date) dateSpinner.getValue();

        // Validaciones primero
        if (placa.isEmpty() && code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese placa o código de la multa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (montoN == null || montoN.doubleValue() <= 0) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmación
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea registrar este pago?",
                "Confirmar pago",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = FileManager.registerPayment(placa, code, fecha, montoN.doubleValue());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Pago registrado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtPlate.setText("");
                txtCode.setText("");
                txtMonto.setValue(null);
            } else {
                JOptionPane.showMessageDialog(this, "Error registrando pago.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "El pago fue cancelado.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}

