package multas.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import multas.models.Multa;
import multas.utils.FileManager;
import multas.utils.UIUtils;
import java.util.List;

//  Esta clase es el panel de eliminación de multas pagadas.
//  Muestra las multas ya pagadas y permite eliminarlas del archivo principal,
//  registrándolas en el archivo de Reporte_Eliminadas.
// 

public class DeletePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public DeletePanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.APP_BG);

        JLabel header = new JLabel("Eliminar multas pagadas");
        header.setFont(UIUtils.TITLE_FONT);
        add(header, BorderLayout.NORTH);

        String[] cols = {"Código","Placa","Cédula","Nombre","Tipo","Fecha","Monto","Estado"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        JButton btnLoad = new JButton("Cargar pagadas");
        btnLoad.addActionListener(e -> loadPagadas());
        JButton btnDelete = new JButton("Eliminar seleccionadas");
        btnDelete.addActionListener(e -> deleteSelected());

        JPanel bottom = new JPanel();
        bottom.add(btnLoad);
        bottom.add(btnDelete);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadPagadas() {
        model.setRowCount(0);
        List<Multa> multas = FileManager.loadMultas();
        for (Multa m : multas) {
            if (m.getEstado().equalsIgnoreCase("Pagada")) {
                model.addRow(new Object[]{
                        m.getCodigo(), m.getPlaca(), m.getCedula(), m.getNombre(),
                        m.getTipo(), new SimpleDateFormat("yyyy-MM-dd").format(m.getFecha()),
                        m.getMonto(), m.getEstado()
                });
            }
        }
    }
    //Eliminar las multas seleccionadas
    private void deleteSelected() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Seleccione al menos una multa para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> codigosEliminar = new ArrayList<>();
        for (int r : rows) {
            codigosEliminar.add((String) model.getValueAt(r, 0));
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea eliminar " + codigosEliminar.size() + " multa(s)?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            FileManager.deletePagadas(codigosEliminar); // 🔹 este ya reescribe Multas_Registradas y genera el reporte
            JOptionPane.showMessageDialog(this, "Multas eliminadas y registradas en reporte.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            loadPagadas(); // refrescar tabla
        }
    }

    
}
