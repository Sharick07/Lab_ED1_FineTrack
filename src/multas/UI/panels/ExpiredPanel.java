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


//Esta clase permite mostrar las multas vencidas
//(Aquellas que tienen mas de 90 dias sin ser pagadas)
// Las obntiene del archivo Multas_Registradas.

public class ExpiredPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ExpiredPanel() {
        setLayout(new BorderLayout());
        setBackground(UIUtils.APP_BG);

        JLabel header = new JLabel("Multas vencidas (+90 días sin pagar)");
        header.setFont(UIUtils.TITLE_FONT);
        add(header, BorderLayout.NORTH);

        String[] cols = {"Código","Placa","Cédula","Nombre","Tipo","Fecha","Monto","Estado"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        JButton btnLoad = new JButton("Cargar vencidas");
        btnLoad.addActionListener(e -> loadExpired());

        JPanel bottom = new JPanel();
        bottom.add(btnLoad);
        add(bottom, BorderLayout.SOUTH);
    }

    private void loadExpired() {
        model.setRowCount(0);
        List<Multa> multas = FileManager.loadMultas();
        Date today = new Date();
        for (Multa m : multas) {
            if (m.getEstado().equalsIgnoreCase("Pendiente")) {
                long diff = today.getTime() - m.getFecha().getTime();
                long days = diff / (1000 * 60 * 60 * 24);
                if (days > 90) {
                    model.addRow(new Object[]{
                            m.getCodigo(), m.getPlaca(), m.getCedula(), m.getNombre(),
                            m.getTipo(), new SimpleDateFormat("yyyy-MM-dd").format(m.getFecha()),
                            m.getMonto(), "Vencida"
                    });
                }
            }
        }
    }
}

