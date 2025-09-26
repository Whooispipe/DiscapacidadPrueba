import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/*
 Formato de archivos CSV:
 - personas.csv: tipo,rut,nombre,fechaNacimiento,atributoExtra
     tipo = BENEFICIARIO o FUNCIONARIO
     atributoExtra = discapacidad (para beneficiario) o areaTrabajo (para funcionario)
 - servicios.csv: rut,tipoServicio,descripcion
 - notas.csv: rut,efecto
*/
public class DataStore {

    public static Map<String, Persona> cargar(String carpeta) {
        try {
            Path dir = Path.of(carpeta);
            if (!Files.exists(dir) || !Files.isDirectory(dir)) {
                return null;
            }
            Map<String, Persona> registros = new HashMap<>();

            File personasFile = dir.resolve("personas.csv").toFile();
            if (personasFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(personasFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(",", -1);
                        if (parts.length < 5) continue;
                        String tipo = parts[0].trim();
                        String rut = parts[1].trim();
                        String nombre = parts[2].trim();
                        String fecha = parts[3].trim();
                        String extra = parts[4].trim();
                        try {
                            if ("BENEFICIARIO".equalsIgnoreCase(tipo)) {
                                Beneficiario b = new Beneficiario(rut, nombre, fecha, extra);
                                registros.put(rut, b);
                            } else if ("FUNCIONARIO".equalsIgnoreCase(tipo)) {
                                Funcionario f = new Funcionario(rut, nombre, fecha, extra);
                                registros.put(rut, f);
                            }
                        } catch (Exception e) {
                            System.out.println("Error al crear persona desde archivo: " + e.getMessage());
                        }
                    }
                }
            }

            File serviciosFile = dir.resolve("servicios.csv").toFile();
            if (serviciosFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(serviciosFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(",", -1);
                        if (parts.length < 3) continue;
                        String rut = parts[0].trim();
                        String tipoServicio = parts[1].trim();
                        String descripcion = parts[2].trim();
                        Persona p = registros.get(rut);
                        if (p instanceof Beneficiario) {
                            ((Beneficiario) p).agregarServicio(tipoServicio, descripcion);
                        }
                    }
                }
            }

            File notasFile = dir.resolve("notas.csv").toFile();
            if (notasFile.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(notasFile))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        String[] parts = line.split(",", -1);
                        if (parts.length < 2) continue;
                        String rut = parts[0].trim();
                        String efecto = parts[1].trim();
                        Persona p = registros.get(rut);
                        if (p instanceof Beneficiario) {
                            ((Beneficiario) p).agregarNota(efecto);
                        }
                    }
                }
            }

            return registros;
        } catch (Exception e) {
            System.out.println("Error al cargar datos: " + e.getMessage());
            return null;
        }
    }

    public static void guardar(Map<String, Persona> registros, String carpeta) {
        try {
            Path dir = Path.of(carpeta);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            File personasFile = dir.resolve("personas.csv").toFile();
            File serviciosFile = dir.resolve("servicios.csv").toFile();
            File notasFile = dir.resolve("notas.csv").toFile();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(personasFile))) {
                for (Persona p : registros.values()) {
                    if (p instanceof Beneficiario) {
                        Beneficiario b = (Beneficiario) p;
                        // tipo,rut,nombre,fecha,discapacidad
                        bw.write("BENEFICIARIO," + escapeCsv(b.getRut()) + "," + escapeCsv(b.getNombre()) + "," + escapeCsv(b.getFechaNacimiento()) + "," + escapeCsv(b.getDiscapacidad()));
                        bw.newLine();
                    } else if (p instanceof Funcionario) {
                        Funcionario f = (Funcionario) p;
                        bw.write("FUNCIONARIO," + escapeCsv(f.getRut()) + "," + escapeCsv(f.getNombre()) + "," + escapeCsv(f.getFechaNacimiento()) + "," + escapeCsv(f.getAreaTrabajo()));
                        bw.newLine();
                    }
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(serviciosFile))) {
                for (Persona p : registros.values()) {
                    if (p instanceof Beneficiario) {
                        Beneficiario b = (Beneficiario) p;
                        for (ServiciodeApoyo s : b.getServiciosDeApoyo()) {
                            bw.write(escapeCsv(b.getRut()) + "," + escapeCsv(s.getTipoServicio()) + "," + escapeCsv(s.getDescripcion()));
                            bw.newLine();
                        }
                    }
                }
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(notasFile))) {
                for (Persona p : registros.values()) {
                    if (p instanceof Beneficiario) {
                        Beneficiario b = (Beneficiario) p;
                        for (SeguimientoImpacto n : b.getSeguimientoImpacto()) {
                            bw.write(escapeCsv(b.getRut()) + "," + escapeCsv(n.getEfecto()));
                            bw.newLine();
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error al guardar datos: " + e.getMessage());
        }
    }

    private static String escapeCsv(String s) {
        if (s == null) return "";
        return s.replace("\n", " ").replace("\r", " ").replace(",", ";");
    }

    // Generar reporte TXT (SIA2.10): guarda un archivo report.txt con todos los detalles
    public static void exportarReporteTxt(Map<String, Persona> registros, String carpeta, String nombreArchivo) {
        try {
            Path dir = Path.of(carpeta);
            if (!Files.exists(dir)) Files.createDirectories(dir);
            File out = dir.resolve(nombreArchivo).toFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(out))) {
                for (Persona p : registros.values()) {
                    bw.write(p.obtenerDetalle());
                    bw.write("\n---------------------------\n");
                }
            }
            System.out.println("Reporte exportado a: " + out.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error al exportar reporte: " + e.getMessage());
        }
    }
}