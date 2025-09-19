package multas.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import multas.models.Multa;
import multas.utils.FileManager;
import multas.utils.UIUtils;


//  Panel de registro de pagos.
//  Permite seleccionar una multa pendiente e ingresar uno o más pagos,
//  validando la regla de máximo tres cuotas
//  y actualizando el archivo de multas.
// 


public class RegisterPaymentPanel extends JPanel {
    private JTextField txtPlate;
    private JComboBox<String> comboMultas;
    private JSpinner dateSpinner;
    private JFormattedTextField txtMonto;
    private List<Multa> multasPendientes;
    private JLabel lblSaldo;


    public RegisterPaymentPanel() {
        setLayout(new GridBagLayout());
        setBackground(UIUtils.APP_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // --- Buscar por placa ---
        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Placa:"), gbc);
        txtPlate = new JTextField(15); gbc.gridx=1; add(txtPlate, gbc);
        JButton btnBuscar = new JButton("Buscar multas");
        gbc.gridx=2; add(btnBuscar, gbc);
        y++;

        // --- ComboBox de multas ---
        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Multa pendiente:"), gbc);
        comboMultas = new JComboBox<>();
        comboMultas.setPreferredSize(new Dimension(280, 30));
        gbc.gridx=1; gbc.gridwidth=2; add(comboMultas, gbc);
        y++; gbc.gridwidth=1;

        // --- Fecha ---
        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Fecha de pago:"), gbc);
        dateSpinner = new JSpinner(new javax.swing.SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        gbc.gridx=1; add(dateSpinner, gbc);
        y++;

        // --- Monto ---
        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Monto a pagar:"), gbc);
        java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
        javax.swing.text.NumberFormatter nfF = new javax.swing.text.NumberFormatter(nf);
        nfF.setAllowsInvalid(false);
        txtMonto = new JFormattedTextField(nfF);
        txtMonto.setColumns(10);
        gbc.gridx=1; add(txtMonto, gbc);
        y++;

        // --- Botón Registrar ---
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=3;
        JPanel btns = new JPanel(); btns.setBackground(UIUtils.APP_BG);
        JButton btnReg = new JButton("Registrar pago");
        btnReg.setPreferredSize(new Dimension(160,36));
        btns.add(btnReg);
        add(btns, gbc);
        
        gbc.gridx=0; gbc.gridy=y; add(new JLabel("Saldo pendiente:"), gbc);
        lblSaldo = new JLabel("0");  // por defecto en cero
        gbc.gridx=1; add(lblSaldo, gbc); 
        y++;


        // --- Acciones ---
        btnBuscar.addActionListener(e -> buscarMultas());
        btnReg.addActionListener(e -> registrarPago());
        comboMultas.addActionListener(e -> actualizarSaldo());
    }  

    private void actualizarSaldo() {
        int index = comboMultas.getSelectedIndex();
        if (index < 0 || multasPendientes == null || multasPendientes.isEmpty()) {
            lblSaldo.setText("-");
            return;
        }

        // Obtener multa seleccionada
        Multa seleccionada = multasPendientes.get(index);

        // Buscar saldo actualizado desde archivo (por si se cambió antes)
        List<Multa> todas = FileManager.loadMultas();
        for (Multa m : todas) {
            if (m.getCodigo().equals(seleccionada.getCodigo())) {
                seleccionada = m;
                break;
            }
        }

        // Contar pagos previos
        int pagosPrevios = FileManager.contarPagos(seleccionada.getCodigo());

        // Mostrar saldo
        lblSaldo.setText("$" + String.format("%,.0f", seleccionada.getMonto()));

        // Validación para el 3er pago
        if (pagosPrevios == 2) {
            // 🔹 Bloqueamos el campo de monto y forzamos al saldo
            txtMonto.setValue(seleccionada.getMonto());
            txtMonto.setEditable(false);
            JOptionPane.showMessageDialog(this,
                    "Este es el 3er pago. Debe cancelar el saldo completo: $" + seleccionada.getMonto(),
                    "Última cuota", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // 🔹 Primer o segundo pago: monto libre
            txtMonto.setEditable(true);
            txtMonto.setValue(null);
        }
    }


    private void buscarMultas() {
        String placa = txtPlate.getText().trim();
        if (placa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una placa.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Buscar multas desde archivo
        multasPendientes = FileManager.searchByPlaca(placa);

        comboMultas.removeAllItems();
        if (multasPendientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay multas pendientes para esta placa.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Multa m : multasPendientes) {
                if (m.getEstado().equalsIgnoreCase("Pendiente")) {
                    comboMultas.addItem(m.getCodigo() + " - " + m.getTipo() + " - $" + m.getMonto() + " - " + sdf.format(m.getFecha()));
                }
            }
            actualizarSaldo(); // 🔹 Muestra saldo de la multa seleccionada
        }
    }



    private void registrarPago() {
        if (multasPendientes == null || multasPendientes.isEmpty() || comboMultas.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una multa para registrar el pago.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Multa seleccionada = multasPendientes.get(comboMultas.getSelectedIndex());
        Number montoN = (Number) txtMonto.getValue();
        Date fecha = (Date) dateSpinner.getValue();

        if (montoN == null || montoN.doubleValue() <= 0) {
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Confirmación
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea registrar este pago a la multa " + seleccionada.getCodigo() + "?",
                "Confirmar pago",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = FileManager.registerPayment(seleccionada.getCodigo(), seleccionada.getPlaca(), fecha, montoN.doubleValue());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Pago registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtMonto.setValue(null);
                buscarMultas(); // refrescar combo
            } else {
                JOptionPane.showMessageDialog(this, "Error registrando pago.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "El pago fue cancelado.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}


