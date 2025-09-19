package multas;

import javax.swing.SwingUtilities;
import multas.ui.MainFrame;

//
//  Clase principal de la aplicación FineTrack.
//  Se encarga de iniciar la ejecución del programa y mostrar el MainFrame.
//  Pertenece a la capa de arranque del sistema.


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

