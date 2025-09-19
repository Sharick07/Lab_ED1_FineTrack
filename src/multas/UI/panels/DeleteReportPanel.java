package multas.ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import multas.models.Multa;
import multas.utils.FileManager;
import multas.utils.UIUtils;

//Es es la clase del panel de multas eliminadas
//que permite ver mediante una tabla los registros
// del archivo de Reporte_eliminadas.

public class DeleteReportPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public DeleteReportPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.APP_BG);

        JLabel header = new JLabel("Reporte de multas eliminadas");
        header.setFont(UIUtils.TITLE_FONT);
        add(header, BorderLayout.NORTH);

        String[] cols = {"Código","Placa","Cédula","Nombre","Tipo","Fecha","Monto","Estado"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        JButton btnLoad = new JButton("Cargar reporte");
        btnLoad.addActionListener(e -> loadEliminadas());

        JPanel bottom = new JPanel();
        bottom.add(btnLoad);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadEliminadas() {
        model.setRowCount(0);
        List<Multa> eliminadas = FileManager.loadEliminadas();
        for (Multa m : eliminadas) {
            model.addRow(new Object[]{
                    m.getCodigo(), m.getPlaca(), m.getCedula(), m.getNombre(),
                    m.getTipo(), new SimpleDateFormat("yyyy-MM-dd").format(m.getFecha()),
                    m.getMonto(), m.getEstado()
            });
        }
    }
}

