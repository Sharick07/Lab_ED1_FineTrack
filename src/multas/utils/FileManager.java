package multas.utils;

import multas.models.Multa;
import java.io.*;
import static java.nio.file.Files.lines;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;

public class FileManager {
    private static final String FILE_MULTAS = "src/archivos/Multas_Registradas.txt";
    private static final String FILE_PAGOS = "src/archivos/Pagos_Multas.txt";
    private static final String FILE_ELIMINADAS = "src/archivos/Reporte_Eliminadas.txt";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

   // Guardar una multa en archivo secuencial con validación de duplicados
    public static boolean saveMulta(Multa m) {
        try {
            // 1. Cargar todas las multas actuales
            List<Multa> multasExistentes = loadMultas();

            // 2. Validar duplicado exacto
            for (Multa existente : multasExistentes) {
                if (existente.getPlaca().equalsIgnoreCase(m.getPlaca()) &&
                    existente.getCedula().equals(m.getCedula()) &&
                    existente.getTipo().equalsIgnoreCase(m.getTipo()) &&
                    sdf.format(existente.getFecha()).equals(sdf.format(m.getFecha())) &&
                    existente.getMonto() == m.getMonto()) {

                    // 🚫 Ya existe la misma multa → no guardar
                    JOptionPane.showMessageDialog(null,
                            "Ya existe una multa registrada con los mismos datos.\nNo se puede duplicar.",
                            "Registro duplicado",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // 3. Si no existe, guardar en el archivo
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_MULTAS, true))) {
                String line = String.join(";",
                        m.getCodigo(),
                        m.getPlaca(),
                        m.getCedula(),
                        m.getNombre(),
                        m.getTipo(),
                        sdf.format(m.getFecha()),
                        String.valueOf(m.getMonto()),
                        m.getEstado()
                );
                bw.write(line);
                bw.newLine();
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Cargar todas las multas
    public static List<Multa> loadMultas() {
        List<Multa> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_MULTAS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length == 8) {
                    Multa m = new Multa(
                            p[0], p[1], p[2], p[3], p[4],
                            sdf.parse(p[5]),
                            Double.parseDouble(p[6]),
                            p[7]
                    );
                    list.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static int contarPagos(String code) {
        int count = 0;
        File f = new File(FILE_PAGOS);
        if (!f.exists()) return 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(";");
                // Esperamos formato: codigo;placa;fecha;monto
                if (parts.length >= 4) {
                    String codigoPago = parts[0].trim();
                    if (codigoPago.equalsIgnoreCase(code)) {
                        count++;
                    }
                }
                // si el archivo tiene líneas antiguas (placa;fecha;monto) las ignoramos aquí
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }


    
//    public static boolean registerPayment(String code, String placa, Date fecha, double monto) {
//        try {
//             // 1) Contar pagos previos
//            int pagosPrevios = contarPagos(code);
//
//            // 2) Cargar multas para ver saldo actual
//            List<Multa> multas = loadMultas();
//            Multa target = null;
//            for (Multa m : multas) {
//                if (m.getCodigo().equals(code)) {
//                    target = m;
//                    break;
//                }
//            }
//            if (target == null) return false;
//
//            double saldoPendiente = target.getMonto();
//
//            // 3) Validar la regla de máximo 3 pagos
//            if (pagosPrevios >= 3) {
//                JOptionPane.showMessageDialog(null,
//                    "Esta multa ya alcanzó el máximo de 3 pagos permitidos.",
//                    "Límite de pagos", JOptionPane.ERROR_MESSAGE);
//                return false;
//            }
//
//            if (pagosPrevios == 2) {
//                // Último pago: debe ser igual al saldo restante
//                if (Math.abs(monto - saldoPendiente) > 0.01) {
//                    JOptionPane.showMessageDialog(null,
//                        "El tercer pago debe ser exactamente por el saldo restante: " + saldoPendiente,
//                        "Pago inválido", JOptionPane.WARNING_MESSAGE);
//                    return false;
//                }
//            } else {
//                // Primer o segundo pago: no puede ser mayor al saldo
//                if (monto > saldoPendiente) {
//                    JOptionPane.showMessageDialog(null,
//                        "El monto ingresado excede el saldo pendiente: " + saldoPendiente,
//                        "Pago inválido", JOptionPane.WARNING_MESSAGE);
//                    return false;
//                }
//            }
//
//            // 1) Guardar pago en FILE_PAGOS con formato: codigo;placa;fecha;monto
//            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PAGOS, true))) {
//                String pagoLine = String.join(";", code == null ? "" : code, placa, sdf.format(fecha), String.valueOf(monto));
//                bw.write(pagoLine);
//                bw.newLine();
//                bw.flush();
//            }
//
//            // 2) Leer Multas_Registradas y actualizar la multa correspondiente
//            File file = new File(FILE_MULTAS);
//            if (!file.exists()) {
//                System.out.println("FileManager.registerPayment: no existe FILE_MULTAS -> " + FILE_MULTAS);
//                return false;
//            }
//
//            List<String> updated = new ArrayList<>();
//            boolean found = false;
//
//            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
//                String line;
//                while ((line = br.readLine()) != null) {
//                    String[] parts = line.split(";");
//                    if (parts.length < 8) {
//                        updated.add(line);
//                        continue;
//                    }
//
//                    String codFile = parts[0];
//                    String placaFile = parts[1];
//
//                    // Si se pasó código usarlo; si código vacío usar primera multa pendiente de la placa
//                    boolean match = false;
//                    if (code != null && !code.isEmpty()) {
//                        match = codFile.equals(code) && placaFile.equalsIgnoreCase(placa);
//                    } else {
//                        // sin código -> aplicar al primer registro pendiente de esa placa
//                        match = placaFile.equalsIgnoreCase(placa) && parts[7].equalsIgnoreCase("Pendiente") && !found;
//                    }
//
//                    if (match) {
//                        double montoActual;
//                        try {
//                            montoActual = Double.parseDouble(parts[6]);
//                        } catch (NumberFormatException ex) {
//                            montoActual = 0.0;
//                        }
//
//                        double nuevoMonto = montoActual - monto;
//
//                        if (nuevoMonto <= 0.0) {
//                            parts[6] = "0";
//                            parts[7] = "Pagada";
//                        } else {
//                            // formatear con sin notación científica
//                            parts[6] = String.format(Locale.US, "%.2f", nuevoMonto);
//                            parts[7] = "Pendiente";
//                        }
//
//                        String newLine = String.join(";", parts);
//                        updated.add(newLine);
//                        found = true;
//                        System.out.println("FileManager: Multa actualizada -> " + newLine);
//                    } else {
//                        updated.add(line);
//                    }
//                }
//            }
//
//            // 3) Reescribir Multas_Registradas.txt con los cambios
//            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
//                for (String l : updated) {
//                    bw.write(l);
//                    bw.newLine();
//                }
//                bw.flush();
//            }
//
//
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    public static boolean registerPayment(String code, String placa, Date fecha, double monto) {
        try {
            // --- 0) sanity trim
            if (code == null) code = "";
            code = code.trim();
            placa = placa == null ? "" : placa.trim();

            // 1) contar pagos previos (por CÓDIGO)
            int pagosPrevios = contarPagos(code);
            


            // 2) cargar MULTA objetivo desde archivo
            List<Multa> multas = loadMultas();
            Multa target = null;
            for (Multa m : multas) {
                if (m.getCodigo().equalsIgnoreCase(code) && m.getPlaca().equalsIgnoreCase(placa)) {
                    target = m;
                    break;
                }
            }
            if (target == null) {
                JOptionPane.showMessageDialog(null, "No se encontró la multa especificada (código=" + code + ").", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            double saldoPendiente = target.getMonto();

            // 3) Validaciones de cuotas (usar BigDecimal para exactitud si quieres)
            if (pagosPrevios >= 3) {
                JOptionPane.showMessageDialog(null, "Esta multa ya alcanzó el máximo de 3 pagos permitidos.", "Límite de pagos", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            if (pagosPrevios == 2) {
                // tercer pago: debe ser exactamente el saldo restante
                if (Math.abs(monto - saldoPendiente) > 0.01) {
                    JOptionPane.showMessageDialog(null, "El tercer pago debe ser exactamente por el saldo restante: " + String.format(Locale.US, "%.2f", saldoPendiente), "Pago inválido", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } else {
                // primer o segundo pago: no mayor al saldo
                if (monto > saldoPendiente + 0.0001) {
                    JOptionPane.showMessageDialog(null, "El monto ingresado excede el saldo pendiente: " + String.format(Locale.US, "%.2f", saldoPendiente), "Pago inválido", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }

            // 4) guardar pago — Asegúrate que EL FORMATO es: codigo;placa;fecha;monto
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PAGOS, true))) {
                String pagoLine = String.join(";", code, placa, sdf.format(fecha), String.format(Locale.US, "%.2f", monto));
                bw.write(pagoLine);
                bw.newLine();
                bw.flush();
            }

            // 5) actualizar Multas_Registradas (por el mismo código)
            File file = new File(FILE_MULTAS);
            if (!file.exists()) {
                System.out.println("FileManager.registerPayment: no existe FILE_MULTAS -> " + FILE_MULTAS);
                return false;
            }

            List<String> updated = new ArrayList<>();
            boolean found = false;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length < 8) {
                        updated.add(line);
                        continue;
                    }
                    String codFile = parts[0].trim();
                    String placaFile = parts[1].trim();

                    if (codFile.equalsIgnoreCase(code) && placaFile.equalsIgnoreCase(placa)) {
                        double montoActual;
                        try { montoActual = Double.parseDouble(parts[6]); }
                        catch (NumberFormatException ex) { montoActual = 0.0; }

                        double nuevoMonto = montoActual - monto;
                        if (nuevoMonto <= 0.0) {
                            parts[6] = "0";
                            parts[7] = "Pagada";
                        } else {
                            parts[6] = String.format(Locale.US, "%.2f", nuevoMonto);
                            parts[7] = "Pendiente";
                        }

                        String newLine = String.join(";", parts);
                        updated.add(newLine);
                        found = true;
                        System.out.println("FileManager: Multa actualizada -> " + newLine);
                    } else {
                        updated.add(line);
                    }
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, false))) {
                for (String l : updated) {
                    bw.write(l);
                    bw.newLine();
                }
                bw.flush();
            }

            if (!found) {
                System.out.println("FileManager.registerPayment: no se encontró multa para actualizar (code=" + code + ", placa=" + placa + ")");
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }




    public static List<Multa> buscarPorPlaca(String placa) {
        List<Multa> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_MULTAS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 8 && parts[1].equalsIgnoreCase(placa)) {
                    list.add(parseMulta(parts));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 🔹 Buscar por cédula
    public static List<Multa> buscarPorCedula(String cedula) {
        List<Multa> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_MULTAS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 8 && parts[2].equals(cedula)) {
                    list.add(parseMulta(parts));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 🔹 Convertir línea en objeto Multa
    private static Multa parseMulta(String[] parts) {
        try {
            String codigo = parts[0];
            String placa = parts[1];
            String cedula = parts[2];
            String nombre = parts[3];
            String tipo = parts[4];
            Date fecha = new SimpleDateFormat("yyyy-MM-dd").parse(parts[5]);
            double monto = Double.parseDouble(parts[6]);
            String estado = parts[7];

            return new Multa(codigo, placa, cedula, nombre, tipo, fecha, monto, estado);

        } catch (ParseException | NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
//    public static void updateEstados() {
//        try {
//            // 1) Cargar pagos acumulados por código de multa
//            Map<String, Double> pagosPorCodigo = new HashMap<>();
//            File pagosFile = new File(FILE_PAGOS);
//            if (pagosFile.exists()) {
//                try (BufferedReader br = new BufferedReader(new FileReader(pagosFile))) {
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        String[] p = line.split(";");
//                        if (p.length < 4) continue;
//                        String codigoPago = p[0];
//                        String montoStr = p[3];
//                        double montoPago = 0.0;
//                        try {
//                            montoPago = Double.parseDouble(montoStr);
//                        } catch (NumberFormatException ex) {
//                            montoPago = 0.0;
//                        }
//                        if (codigoPago != null && !codigoPago.isEmpty()) {
//                            pagosPorCodigo.put(codigoPago, pagosPorCodigo.getOrDefault(codigoPago, 0.0) + montoPago);
//                        }
//                    }
//                }
//            }
//
//            // 2) Cargar multas y aplicar pagos por código
//            List<Multa> multas = loadMultas();
//            for (Multa m : multas) {
//                String codigo = m.getCodigo();
//                if (pagosPorCodigo.containsKey(codigo)) {
//                    double totalPagado = pagosPorCodigo.get(codigo);
//                    if (totalPagado >= m.getMonto()) {
//                        m.setMonto(0.0);
//                        m.setEstado("Pagada");
//                    } else {
//                        m.setMonto(m.getMonto() - totalPagado);
//                        m.setEstado("Pendiente");
//                    }
//                }
//            }
//
//            // 3) Reescribir Multas_Registradas con los nuevos estados
//            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_MULTAS, false))) {
//                for (Multa m : multas) {
//                    String line = String.join(";",
//                            m.getCodigo(),
//                            m.getPlaca(),
//                            m.getCedula(),
//                            m.getNombre(),
//                            m.getTipo(),
//                            sdf.format(m.getFecha()),
//                            String.format(Locale.US, "%.2f", m.getMonto()),
//                            m.getEstado()
//                    );
//                    bw.write(line);
//                    bw.newLine();
//                }
//                bw.flush();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    public static void updateEstados() {
        try {
            // leer pagos y acumular por CÓDIGO de multa
            Map<String, Double> pagosPorCodigo = new HashMap<>();
            File pagosFile = new File(FILE_PAGOS);
            if (pagosFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(pagosFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] p = line.split(";");
                        if (p.length < 4) continue;
                        String codigoPago = p[0].trim();
                        String montoStr = p[3].trim();
                        double montoPago = 0.0;
                        try { montoPago = Double.parseDouble(montoStr); } catch (NumberFormatException ex) { montoPago = 0.0; }
                        if (!codigoPago.isEmpty()) {
                            pagosPorCodigo.put(codigoPago, pagosPorCodigo.getOrDefault(codigoPago, 0.0) + montoPago);
                        }
                    }
                }
            }

            // aplicar pagos por CÓDIGO a las multas
            List<Multa> multas = loadMultas();
            for (Multa m : multas) {
                String codigo = m.getCodigo();
                if (pagosPorCodigo.containsKey(codigo)) {
                    double totalPagado = pagosPorCodigo.get(codigo);
                    if (totalPagado >= m.getMonto()) {
                        m.setMonto(0.0);
                        m.setEstado("Pagada");
                    } else {
                        m.setMonto(m.getMonto() - totalPagado);
                        m.setEstado("Pendiente");
                    }
                }
            }

            // reescribir archivo Multas_Registradas
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_MULTAS, false))) {
                for (Multa m : multas) {
                    String line = String.join(";",
                            m.getCodigo(),
                            m.getPlaca(),
                            m.getCedula(),
                            m.getNombre(),
                            m.getTipo(),
                            sdf.format(m.getFecha()),
                            String.format(Locale.US, "%.2f", m.getMonto()),
                            m.getEstado());
                    bw.write(line);
                    bw.newLine();
                }
                bw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    
    public static void exportExpiredReport() throws IOException {
        List<Multa> multas = loadMultas();
        Date today = new Date();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Reporte_Multas_Vencidas.txt"))) {
            for (Multa m : multas) {
                if (m.getEstado().equalsIgnoreCase("Pendiente")) {
                    long diff = today.getTime() - m.getFecha().getTime();
                    long days = diff / (1000 * 60 * 60 * 24);
                    if (days > 90) {
                        bw.write(m.toString());
                        bw.newLine();
                    }
                }
            }
        }
    }
    
    public static void deletePagadas(List<String> codigosEliminar) {
        List<Multa> multas = loadMultas();
        List<Multa> eliminadas = new ArrayList<>();

        Iterator<Multa> it = multas.iterator();
        while (it.hasNext()) {
            Multa m = it.next();
            if (m.getEstado().equalsIgnoreCase("Pagada") && codigosEliminar.contains(m.getCodigo())) {
                eliminadas.add(m);
                it.remove();
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_MULTAS))) {
            for (Multa m : multas) {
                String line = String.join(";",
                        m.getCodigo(),
                        m.getPlaca(),
                        m.getCedula(),
                        m.getNombre(),
                        m.getTipo(),
                        sdf.format(m.getFecha()),
                        String.valueOf(m.getMonto()),
                        m.getEstado()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
            // 3. Quitar también del archivo de Pagos_Multas.txt
        try {
            File pagosFile = new File(FILE_PAGOS);
            if (pagosFile.exists()) {
                List<String> pagosActualizados = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(pagosFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(";");
                        if (parts.length >= 4) {
                            String codPago = parts[0]; // código multa asociado al pago
                            if (!codigosEliminar.contains(codPago)) {
                                pagosActualizados.add(line);
                            }
                        }
                    }
                }

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(pagosFile))) {
                    for (String l : pagosActualizados) {
                        bw.write(l);
                        bw.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Guardar reporte de eliminadas
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_ELIMINADAS, true))) {
                for (Multa m : eliminadas) {
                    String line = String.join(";",
                            m.getCodigo(),
                            m.getPlaca(),
                            m.getCedula(),
                            m.getNombre(),
                            m.getTipo(),
                            sdf.format(m.getFecha()),
                            String.valueOf(m.getMonto()),
                            m.getEstado()
                    );
                    bw.write(line);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static String generarCodigoMulta() {
        int max = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_MULTAS))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length > 0 && parts[0].startsWith("M")) {
                    try {
                        int num = Integer.parseInt(parts[0].substring(1));
                        if (num > max) max = num;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.out.println("Archivo no encontrado, se empieza desde M001.");
        }
        // generar el siguiente
        return String.format("M%03d", max + 1);
    }
    
    
    public static List<Multa> searchByPlaca(String placa) {
        List<Multa> resultados = new ArrayList<>();
        File file = new File(FILE_MULTAS);
        if (!file.exists()) return resultados;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 8) continue;

                String codigo = parts[0];
                String placaFile = parts[1];
                String cedula = parts[2];
                String nombre = parts[3];
                String tipo = parts[4];
                Date fecha = sdf.parse(parts[5]);
                double monto = Double.parseDouble(parts[6]);
                String estado = parts[7];

                if (placaFile.equalsIgnoreCase(placa)) {
                    Multa m = new Multa(codigo, placaFile, cedula, nombre, tipo, fecha, monto, estado);
                    resultados.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultados;
    }
    
    
   
    public static List<Multa> loadEliminadas() {
        List<Multa> list = new ArrayList<>();
        File file = new File(FILE_ELIMINADAS);
        if (!file.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(";");
                if (p.length == 8) {
                    Multa m = new Multa(
                            p[0], p[1], p[2], p[3], p[4],
                            sdf.parse(p[5]),
                            Double.parseDouble(p[6]),
                            p[7]
                    );
                    list.add(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


}

