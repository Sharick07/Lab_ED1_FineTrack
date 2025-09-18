package multas.ui.panels;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import multas.models.Multa;
import multas.utils.FileManager;
import multas.utils.UIUtils;
import java.util.List;

public class ConsultPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> comboCriterio;

    public ConsultPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.APP_BG);

        // Panel superior con criterios de búsqueda
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        top.setBackground(UIUtils.APP_BG);

        comboCriterio = new JComboBox<>(new String[]{"Placa", "Cédula"});
        comboCriterio.setFont(UIUtils.LABEL_FONT);

        JLabel lbl = new JLabel("Buscar por:");
        lbl.setFont(UIUtils.LABEL_FONT);

        searchField = new JTextField(20);
        searchField.setFont(UIUtils.LABEL_FONT);

        JButton btnSearch = new JButton("Buscar");
        btnSearch.setPreferredSize(new Dimension(100, 36));
        btnSearch.addActionListener(e -> doSearch());

        top.add(lbl);
        top.add(comboCriterio);
        top.add(searchField);
        top.add(btnSearch);

        add(top, BorderLayout.NORTH);
        
        String[] cols = {"Código","Placa","Cédula","Nombre","Tipo","Fecha","Monto","Estado","Saldo"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane sp = new JScrollPane(table);
        sp.setPreferredSize(new Dimension(800, 400));
        add(sp, BorderLayout.CENTER);

        
        }

   private void doSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un valor para buscar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Limpiar tabla
        model.setRowCount(0);

        List<Multa> resultados;
        String criterio = (String) comboCriterio.getSelectedItem();

        if ("Placa".equals(criterio)) {
            resultados = FileManager.buscarPorPlaca(q);
        } else {
            // Validación: la cédula debe ser numérica
            if (!q.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "La cédula debe ser numérica.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            resultados = FileManager.buscarPorCedula(q);
        }

        for (Multa m : resultados) {
            model.addRow(new Object[]{
                m.getCodigo(),
                m.getPlaca(),
                m.getCedula(),
                m.getNombre(),
                m.getTipo(),
                new SimpleDateFormat("yyyy-MM-dd").format(m.getFecha()),
                m.getMonto(),
                m.getEstado(),
                (m.getEstado().equals("Pendiente") ? m.getMonto() : 0)
            });
        }

        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron registros.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}

