package multas.utils;

import multas.models.Multa;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileManager {
    private static final String FILE_MULTAS = "src/archivos/Multas_Registradas.txt";
    private static final String FILE_PAGOS = "src/archivos/Pagos_Multas.txt";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // Guardar una multa en archivo secuencial
    public static boolean saveMulta(Multa m) {
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

    // Guardar un pago
    public static boolean registerPayment(String placa, String code, Date fecha, double monto) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PAGOS, true))) {
            String line = placa + ";" + sdf.format(fecha) + ";" + monto;
            bw.write(line);
            bw.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

//    // Buscar multas por placa o cédula (indexado: recorremos y filtramos)
//    public static List<Multa> searchByPlacaOrCedula(String query) {
//        List<Multa> result = new ArrayList<>();
//        for (Multa m : loadMultas()) {
//            if (m.getPlaca().equalsIgnoreCase(query) || m.getCedula().equals(query)) {
//                result.add(m);
//            }
//        }
//        return result;
//    }
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
    // Actualizar estados (ejemplo: marcar pagadas si están en Pagos_Multas.txt)
    public static void updateEstados() {
        try {
            // Cargar pagos
            Map<String, Double> pagosPorPlaca = new HashMap<>();
            try (BufferedReader br = new BufferedReader(new FileReader(FILE_PAGOS))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split(";");
                    String placa = p[0];
                    double monto = Double.parseDouble(p[2]);
                    pagosPorPlaca.put(placa, pagosPorPlaca.getOrDefault(placa, 0.0) + monto);
                }
            }

            // Cargar multas
            List<Multa> multas = loadMultas();
            for (Multa m : multas) {
                if (pagosPorPlaca.containsKey(m.getPlaca())) {
                    double pagado = pagosPorPlaca.get(m.getPlaca());
                    if (pagado >= m.getMonto()) {
                        m.setEstado("Pagada");
                        m.setMonto(0);
                    } else {
                        m.setMonto(m.getMonto() - pagado);
                        m.setEstado("Pendiente");
                    }
                }
            }

            // Guardar de nuevo todas las multas
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

        // Guardar reporte de eliminadas
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("Reporte_Eliminadas.txt", true))) {
            for (Multa m : eliminadas) {
                bw.write(m.toString());
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


}

