package multas.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.NumberFormatter;
import multas.utils.UIUtils;
import multas.models.Multa;
import multas.utils.FileManager;

public class AddFinePanel extends JPanel { 
    //Atributos de la clase AddFinePanel
    private JTextField txtCode, txtPlate, txtCedula, txtName, txtEstado;
    private JComboBox<String> cbType;
    private JSpinner dateSpinner;
    private JFormattedTextField txtMonto;
    private Map<String, Double> typePriceMap;

    
    public AddFinePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.APP_BG);

        JLabel header = new JLabel("Agregar nueva multa");
        header.setFont(UIUtils.TITLE_FONT);
        add(header, BorderLayout.NORTH);
        
        // Init map of types -> prices
        initTypePriceMap();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UIUtils.APP_BG);
        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y=0;

        // Código
        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Código:"), gbc);
        txtCode = new JTextField(20);
        txtCode.setText(FileManager.generarCodigoMulta());
        txtCode.setEditable(false);
        gbc.gridx = 1;
        form.add(txtCode, gbc);
        y++;

        // Placa
        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Placa:"), gbc);
        txtPlate = new JTextField(20);
        gbc.gridx = 1;
        form.add(txtPlate, gbc);
        y++;

        // Cédula
        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Cédula:"), gbc);
        txtCedula = new JTextField(20);
        gbc.gridx = 1;
        form.add(txtCedula, gbc);
        y++;

        // Nombre
        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Nombre propietario:"), gbc);
        txtName = new JTextField(20);
        gbc.gridx = 1;
        form.add(txtName, gbc);
        y++;

       // Tipo infracción (combo)
        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Tipo infracción:"), gbc);
        cbType = new JComboBox<>(new String[] {
            "Exceso de velocidad",
            "Estacionar en sitios no permitidos",
            "No respetar semáforo en rojo",
            "Conducir sin licencia",
            "No tener SOAT vigente",
            "No realizar revisión técnico-mecánica",
            "No usar casco o chaleco (motos)",
            "Transitar en contravía",
            "Conducir en zonas restringidas",
            "Conducir bajo la influencia de alcohol o sustancias"
        });
        gbc.gridx = 1;
        form.add(cbType, gbc);
        y++;

        // Fecha
        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Fecha multa:"), gbc);
        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(editor);
        gbc.gridx = 1;
        form.add(dateSpinner, gbc);
        y++;

        // Monto
        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Monto (COP):"), gbc);
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        NumberFormatter nfFormatter = new NumberFormatter(nf);
        nfFormatter.setAllowsInvalid(false);
        txtMonto = new JFormattedTextField(nfFormatter);
        txtMonto.setColumns(10);
        txtMonto.setEditable(false);
        gbc.gridx = 1;
        form.add(txtMonto, gbc);
        y++;

        // Estado
        gbc.gridx = 0; gbc.gridy = y;
        form.add(new JLabel("Estado:"), gbc);
        txtEstado = new JTextField(20);
        txtEstado.setText("Pendiente");
        txtEstado.setEditable(false);
        gbc.gridx = 1;
        form.add(txtEstado, gbc);
        y++;

        // Botones
        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel btns = new JPanel();
        btns.setBackground(UIUtils.APP_BG);
        JButton btnSave = new JButton("Guardar");
        btnSave.setPreferredSize(new Dimension(120, 36));
        btnSave.addActionListener(e -> saveMulta());
        JButton btnClear = new JButton("Limpiar");
        btnClear.setPreferredSize(new Dimension(120, 36));
        btnClear.addActionListener(e -> clearForm());
        btns.add(btnSave);
        btns.add(btnClear);
        form.add(btns, gbc);

        add(form, BorderLayout.CENTER);
        
                // Cuando se selecciona un tipo, autocompletar monto
        cbType.addActionListener(e -> {
            String sel = (String) cbType.getSelectedItem();
            Double price = typePriceMap.get(sel);
            if (price != null) {
                txtMonto.setValue(price);
            } else {
                txtMonto.setValue(null);
            }
        });

        // set default monto for initial selection
        String current = (String) cbType.getSelectedItem();
        if (current != null && typePriceMap.get(current) != null) {
            txtMonto.setValue(typePriceMap.get(current));
        }
    }
    
    private void initTypePriceMap() {
        typePriceMap = new HashMap<>();
        typePriceMap.put("Exceso de velocidad", 650000.0);
        typePriceMap.put("Estacionar en sitios no permitidos", 400000.0);
        typePriceMap.put("No respetar semáforo en rojo", 1000000.0);
        typePriceMap.put("Conducir sin licencia", 1300000.0);
        typePriceMap.put("No tener SOAT vigente", 1300000.0);
        typePriceMap.put("No realizar revisión técnico-mecánica", 650000.0);
        typePriceMap.put("No usar casco o chaleco (motos)", 650000.0);
        typePriceMap.put("Transitar en contravía", 800000.0);
        typePriceMap.put("Conducir en zonas restringidas", 160000.0);
        typePriceMap.put("Conducir bajo la influencia de alcohol o sustancias", 2000000.0);
    }  

    private String generateCode() {
        // Ejemplo simple. Cambiar por lógica real si se desea
        return "M" + System.currentTimeMillis() % 100000;
    }
    
    //Limpiar todos los campos
    private void clearForm() {
        txtPlate.setText("");
        txtCedula.setText("");
        txtName.setText("");
        cbType.setSelectedIndex(0);
        dateSpinner.setValue(new Date());
        txtMonto.setValue(null);
    }

    private void saveMulta() {
        // Validaciones
        String code = txtCode.getText().trim();
        String placa = txtPlate.getText().trim().toUpperCase();
        String ced = txtCedula.getText().trim();
        String nombre = txtName.getText().trim();
        String tipo = (String) cbType.getSelectedItem();
        Date fecha = (Date) dateSpinner.getValue();
        Number montoN = (Number) txtMonto.getValue();
        String estado = txtEstado.getText().trim() ;

        if (placa.isEmpty() || ced.isEmpty() || nombre.isEmpty() || montoN==null) { // Se validan que los campos no esten vacíos
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // que la cedula sea correcta
        if (!ced.matches("^[0-9]{6,10}$")) {
            JOptionPane.showMessageDialog(this, "Cédula inválida (6 a 10 dígitos).", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!placa.matches("^[A-Z0-9-]{5,8}$")) {
            JOptionPane.showMessageDialog(this, "Placa inválida.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double monto = montoN.doubleValue();
        if (monto <= 0) {
            JOptionPane.showMessageDialog(this, "El monto debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // TODO: llamar a FileManager para guardar la multa en archivo
        Multa m = new Multa(code, placa, ced, nombre, tipo, fecha, monto, estado);
        boolean saved = FileManager.saveMulta(m); // stub: implementarlo
        if (saved) {
            JOptionPane.showMessageDialog(this, "Multa guardada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            txtCode.setText(FileManager.generarCodigoMulta());
        } else {
            JOptionPane.showMessageDialog(this, "Error guardando la multa. Ver logs.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

