import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

public class Main {

    private static final String DATA_DIR = "data";

    public static void main(String[] args) {
        // Si se pasa "console" se usa la interfaz de consola; por defecto abrimos la GUI
        boolean modoConsola = args != null && args.length > 0 && (args[0].equalsIgnoreCase("console") || args[0].equalsIgnoreCase("--console"));

        // Cargar datos (la GUI también vuelve a cargar en su constructor, pero no hace daño)
        PersonaRepository.cargarDesde(DATA_DIR);
        if (PersonaRepository.getRegistros().isEmpty()) {
            PersonaRepository.inicializarDatosPrueba();
        }

        if (!modoConsola) {
            // Lanzar GUI en el hilo de eventos Swing
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal vp = new VentanaPrincipal();
                vp.setVisible(true);
            });
            return;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        boolean salir = false;

        while (!salir) {
            try {
                mostrarMenuPrincipal();
                String linea = br.readLine();
                int opcion = Integer.parseInt(linea.trim());
                switch (opcion) {
                    case 1:
                        menuBeneficiarios(br);
                        break;
                    case 2:
                        menuServiciosYNotas(br);
                        break;
                    case 3:
                        menuReportesYFiltros(br);
                        break;
                    case 4:
                        System.out.println("Saliendo... Guardando datos...");
                        PersonaRepository.guardarEn(DATA_DIR);
                        salir = true;
                        break;
                    default:
                        System.out.println("Opción no válida. Intente nuevamente.");
                }
            } catch (NumberFormatException nfe) {
                System.out.println("Entrada inválida. Ingrese un número.");
            } catch (IOException ioe) {
                System.out.println("Error de entrada/salida: " + ioe.getMessage());
            } catch (Exception ex) {
                System.out.println("Ocurrió un error: " + ex.getMessage());
            }
            System.out.println();
        }
        System.out.println("Programa finalizado.");
    }

    private static void mostrarMenuPrincipal() {
        System.out.println("=== Menu Principal ===");
        System.out.println("1) Beneficiarios");
        System.out.println("2) Servicios y Notas");
        System.out.println("3) Reportes y Filtros");
        System.out.println("4) Salir");
        System.out.print("Seleccione una opción: ");
    }

    // -------------------- Beneficiarios --------------------
    private static void menuBeneficiarios(BufferedReader br) throws IOException {
        while (true) {
            System.out.println("\n--- Menú Beneficiarios ---");
            System.out.println("1) Agregar Beneficiario");
            System.out.println("2) Modificar Beneficiario");
            System.out.println("3) Eliminar Beneficiario");
            System.out.println("4) Listar Beneficiarios");
            System.out.println("5) Volver");
            System.out.print("Opción: ");
            String linea = br.readLine();
            int opcion;
            try {
                opcion = Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    agregarBeneficiarioInteractive(br);
                    break;
                case 2:
                    modificarBeneficiarioInteractive(br);
                    break;
                case 3:
                    eliminarBeneficiarioInteractive(br);
                    break;
                case 4:
                    listarBeneficiarios();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void agregarBeneficiarioInteractive(BufferedReader br) throws IOException {
        try {
            System.out.print("Ingrese RUT (formato 11111111-4): ");
            String rut = br.readLine().trim();
            System.out.print("Ingrese Nombre: ");
            String nombre = br.readLine().trim();
            System.out.print("Ingrese Fecha de Nacimiento (dd/mm/yyyy): ");
            String fecha = br.readLine().trim();
            System.out.print("Ingrese Discapacidad: ");
            String discapacidad = br.readLine().trim();

            Beneficiario b = new Beneficiario(rut, nombre, fecha, discapacidad);
            boolean agregado = PersonaRepository.agregar(b);
            if (agregado) {
                System.out.println("Beneficiario agregado correctamente.");
            } else {
                System.out.println("No se pudo agregar. El RUT ya existe.");
            }
        } catch (Exception e) {
            System.out.println("Error al agregar beneficiario: " + e.getMessage());
        }
    }

    private static void modificarBeneficiarioInteractive(BufferedReader br) throws IOException {
        System.out.print("Ingrese RUT del beneficiario a modificar: ");
        String rut = br.readLine().trim();
        Persona p = PersonaRepository.buscarPorRut(rut);
        if (p == null || !(p instanceof Beneficiario)) {
            System.out.println("Beneficiario no encontrado.");
            return;
        }
        Beneficiario b = (Beneficiario) p;

        System.out.print("Nuevo nombre (ENTER para mantener '" + b.getNombre() + "'): ");
        String nombre = br.readLine();
        System.out.print("Nueva fecha de nacimiento (ENTER para mantener '" + b.getFechaNacimiento() + "'): ");
        String fecha = br.readLine();
        System.out.print("Nueva discapacidad (ENTER para mantener '" + b.getDiscapacidad() + "'): ");
        String discapacidad = br.readLine();

        if (nombre != null && !nombre.trim().isEmpty()) b.setNombre(nombre.trim());
        if (fecha != null && !fecha.trim().isEmpty()) b.setFechaNacimiento(fecha.trim());
        if (discapacidad != null && !discapacidad.trim().isEmpty()) b.setDiscapacidad(discapacidad.trim());

        boolean ok = PersonaRepository.editar(b);
        if (ok) {
            System.out.println("Beneficiario modificado correctamente.");
        } else {
            System.out.println("No se pudo modificar el beneficiario.");
        }
    }

    private static void eliminarBeneficiarioInteractive(BufferedReader br) throws IOException {
        System.out.print("Ingrese RUT del beneficiario a eliminar: ");
        String rut = br.readLine().trim();
        boolean ok = PersonaRepository.eliminar(rut);
        if (ok) System.out.println("Beneficiario eliminado correctamente.");
        else System.out.println("Beneficiario no encontrado.");
    }

    private static void listarBeneficiarios() {
        List<String> listado = PersonaRepository.listarBeneficiariosTexto();
        for (String s : listado) {
            System.out.println(s);
        }
    }

    // ----------------- Servicios y Notas -----------------
    private static void menuServiciosYNotas(BufferedReader br) throws IOException {
        while (true) {
            System.out.println("\n--- Menú Servicios y Notas ---");
            System.out.println("1) Agregar Servicio de Apoyo");
            System.out.println("2) Agregar Nota");
            System.out.println("3) Listar Servicios de Apoyo");
            System.out.println("4) Listar Notas");
            System.out.println("5) Editar/Eliminar Servicio");
            System.out.println("6) Editar/Eliminar Nota");
            System.out.println("7) Volver");
            System.out.print("Opción: ");
            String linea = br.readLine();
            int opcion;
            try {
                opcion = Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    agregarServicioInteractive(br);
                    break;
                case 2:
                    agregarNotaInteractive(br);
                    break;
                case 3:
                    listarServiciosApoyo();
                    break;
                case 4:
                    listarNotas();
                    break;
                case 5:
                    editarEliminarServicioInteractive(br);
                    break;
                case 6:
                    editarEliminarNotaInteractive(br);
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void agregarServicioInteractive(BufferedReader br) throws IOException {
        System.out.print("Ingrese RUT del beneficiario: ");
        String rut = br.readLine().trim();
        Persona p = PersonaRepository.buscarPorRut(rut);
        if (p == null || !(p instanceof Beneficiario)) {
            System.out.println("Beneficiario no encontrado.");
            return;
        }
        Beneficiario b = (Beneficiario) p;

        System.out.print("Ingrese tipo de servicio: ");
        String tipo = br.readLine().trim();
        System.out.print("Ingrese descripción del servicio: ");
        String descripcion = br.readLine().trim();

        b.agregarServicio(tipo, descripcion);
        System.out.println("Servicio agregado al beneficiario.");
    }

    private static void agregarNotaInteractive(BufferedReader br) throws IOException {
        System.out.print("Ingrese RUT del beneficiario: ");
        String rut = br.readLine().trim();
        Persona p = PersonaRepository.buscarPorRut(rut);
        if (p == null || !(p instanceof Beneficiario)) {
            System.out.println("Beneficiario no encontrado.");
            return;
        }
        Beneficiario b = (Beneficiario) p;

        System.out.print("Ingrese efecto de la nota (Positivo/Negativo): ");
        String efecto = br.readLine().trim();

        b.agregarNota(efecto);
        System.out.println("Nota agregada al beneficiario.");
    }

    private static void listarServiciosApoyo() {
        Map<String, Persona> registros = PersonaRepository.getRegistros();
        boolean any = false;
        for (Persona p : registros.values()) {
            if (!(p instanceof Beneficiario)) continue;
            Beneficiario b = (Beneficiario) p;
            System.out.println("Beneficiario: " + b.getNombre() + " (" + b.getRut() + ")");
            if (b.getServiciosDeApoyo().isEmpty()) {
                System.out.println("  No tiene servicios de apoyo.");
            } else {
                b.getServiciosDeApoyo().forEach(s -> System.out.println("  - " + s.getTipoServicio() + ": " + s.getDescripcion()));
            }
            System.out.println("---------------------------");
            any = true;
        }
        if (!any) System.out.println("No hay beneficiarios con servicios registrados.");
    }

    private static void listarNotas() {
        Map<String, Persona> registros = PersonaRepository.getRegistros();
        boolean any = false;
        for (Persona p : registros.values()) {
            if (!(p instanceof Beneficiario)) continue;
            Beneficiario b = (Beneficiario) p;
            System.out.println("Beneficiario: " + b.getNombre() + " (" + b.getRut() + ")");
            if (b.getSeguimientoImpacto().isEmpty()) {
                System.out.println("  No tiene notas registradas.");
            } else {
                b.getSeguimientoImpacto().forEach(n -> System.out.println("  - Nota: " + n.getEfecto()));
            }
            System.out.println("---------------------------");
            any = true;
        }
        if (!any) System.out.println("No hay beneficiarios con notas registradas.");
    }

    private static void editarEliminarServicioInteractive(BufferedReader br) throws IOException {
        System.out.print("Ingrese RUT del beneficiario: ");
        String rut = br.readLine().trim();
        Persona p = PersonaRepository.buscarPorRut(rut);
        if (p == null || !(p instanceof Beneficiario)) {
            System.out.println("Beneficiario no encontrado.");
            return;
        }
        Beneficiario b = (Beneficiario) p;
        System.out.println("Servicios actuales:");
        for (int i = 0; i < b.getServiciosDeApoyo().size(); i++) {
            ServiciodeApoyo s = b.getServiciosDeApoyo().get(i);
            System.out.println("[" + i + "] " + s.getTipoServicio() + " - " + s.getDescripcion());
        }
        System.out.println("Opciones: 1) Editar por índice  2) Eliminar por índice  3) Eliminar por tipo  4) Volver");
        System.out.print("Opción: ");
        String opStr = br.readLine().trim();
        int op;
        try {
            op = Integer.parseInt(opStr);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }
        switch (op) {
            case 1:
                System.out.print("Ingrese índice a editar: ");
                int idxE = Integer.parseInt(br.readLine().trim());
                System.out.print("Nuevo tipo (ENTER para mantener): ");
                String nt = br.readLine();
                System.out.print("Nueva descripción (ENTER para mantener): ");
                String nd = br.readLine();
                boolean edited = b.editarServicio(idxE, nt, nd);
                System.out.println(edited ? "Servicio editado." : "Índice inválido.");
                break;
            case 2:
                System.out.print("Ingrese índice a eliminar: ");
                int idxD = Integer.parseInt(br.readLine().trim());
                boolean deleted = b.eliminarServicioPorIndice(idxD);
                System.out.println(deleted ? "Servicio eliminado." : "Índice inválido.");
                break;
            case 3:
                System.out.print("Ingrese tipo a eliminar: ");
                String tipo = br.readLine().trim();
                int count = b.eliminarServiciosPorTipo(tipo);
                System.out.println("Servicios eliminados: " + count);
                break;
            case 4:
                return;
            default:
                System.out.println("Opción no válida.");
        }
    }

    private static void editarEliminarNotaInteractive(BufferedReader br) throws IOException {
        System.out.print("Ingrese RUT del beneficiario: ");
        String rut = br.readLine().trim();
        Persona p = PersonaRepository.buscarPorRut(rut);
        if (p == null || !(p instanceof Beneficiario)) {
            System.out.println("Beneficiario no encontrado.");
            return;
        }
        Beneficiario b = (Beneficiario) p;
        System.out.println("Notas actuales:");
        for (int i = 0; i < b.getSeguimientoImpacto().size(); i++) {
            SeguimientoImpacto s = b.getSeguimientoImpacto().get(i);
            System.out.println("[" + i + "] " + s.getEfecto());
        }
        System.out.println("Opciones: 1) Editar por índice  2) Eliminar por índice  3) Eliminar por efecto  4) Volver");
        System.out.print("Opción: ");
        String opStr = br.readLine().trim();
        int op;
        try {
            op = Integer.parseInt(opStr);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }
        switch (op) {
            case 1:
                System.out.print("Ingrese índice a editar: ");
                int idxE = Integer.parseInt(br.readLine().trim());
                System.out.print("Nuevo efecto (Positivo/Negativo): ");
                String ne = br.readLine().trim();
                boolean edited = b.editarNota(idxE, ne);
                System.out.println(edited ? "Nota editada." : "Índice inválido.");
                break;
            case 2:
                System.out.print("Ingrese índice a eliminar: ");
                int idxD = Integer.parseInt(br.readLine().trim());
                boolean deleted = b.eliminarNotaPorIndice(idxD);
                System.out.println(deleted ? "Nota eliminada." : "Índice inválido.");
                break;
            case 3:
                System.out.print("Ingrese efecto a eliminar (Positivo/Negativo): ");
                String ef = br.readLine().trim();
                int count = b.eliminarNotasPorEfecto(ef);
                System.out.println("Notas eliminadas: " + count);
                break;
            case 4:
                return;
            default:
                System.out.println("Opción no válida.");
        }
    }

    // -------------------- Reportes y Filtros --------------------
    private static void menuReportesYFiltros(BufferedReader br) throws IOException {
        while (true) {
            System.out.println("\n--- Reportes y Filtros ---");
            System.out.println("1) Listar beneficiarios por discapacidad");
            System.out.println("2) Listar beneficiarios con notas positivas");
            System.out.println("3) Buscar servicios por tipo");
            System.out.println("4) Exportar reporte TXT");
            System.out.println("5) Volver");
            System.out.print("Opción: ");
            String linea = br.readLine();
            int opcion;
            try {
                opcion = Integer.parseInt(linea.trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido.");
                continue;
            }
            switch (opcion) {
                case 1:
                    listarPorDiscapacidadInteractive(br);
                    break;
                case 2:
                    listarConNotasPositivas();
                    break;
                case 3:
                    buscarServiciosPorTipoInteractive(br);
                    break;
                case 4:
                    exportarReporte();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private static void listarPorDiscapacidadInteractive(BufferedReader br) throws IOException {
        System.out.print("Ingrese discapacidad a filtrar: ");
        String d = br.readLine().trim();
        List<Beneficiario> lista = PersonaRepository.listarBeneficiariosPorDiscapacidad(d);
        if (lista.isEmpty()) {
            System.out.println("No se encontraron beneficiarios con discapacidad: " + d);
            return;
        }
        for (Beneficiario b : lista) {
            System.out.println(b.obtenerDetalle());
            System.out.println("---------------------------");
        }
    }

    private static void listarConNotasPositivas() {
        List<Beneficiario> lista = PersonaRepository.listarBeneficiariosConNotasPositivas();
        if (lista.isEmpty()) {
            System.out.println("No se encontraron beneficiarios con notas positivas.");
            return;
        }
        for (Beneficiario b : lista) {
            System.out.println(b.obtenerDetalle());
            System.out.println("---------------------------");
        }
    }

    private static void buscarServiciosPorTipoInteractive(BufferedReader br) throws IOException {
        System.out.print("Ingrese tipo de servicio a buscar: ");
        String tipo = br.readLine().trim();
        List<String> res = PersonaRepository.buscarServiciosPorTipo(tipo);
        if (res.isEmpty()) {
            System.out.println("No se encontraron servicios del tipo: " + tipo);
            return;
        }
        res.forEach(System.out::println);
    }

    private static void exportarReporte() {
        PersonaRepository.guardarEn(DATA_DIR); // guardamos csv también
        DataStore.exportarReporteTxt(PersonaRepository.getRegistros(), DATA_DIR, "reporte.txt");
    }
}