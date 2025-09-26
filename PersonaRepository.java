import exceptions.RutInvalidoException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonaRepository {
    private static Map<String, Persona> registros = new HashMap<>();

    // Agregar general
    public static boolean agregar(Persona p) {
        if (p == null) return false;
        if (registros.containsKey(p.getRut())) return false;
        registros.put(p.getRut(), p);
        return true;
    }

    // Sobrecargas (por tipo) - cumplen SIA1.6
    public static boolean agregar(Beneficiario b) {
        return agregar((Persona) b);
    }

    public static boolean agregar(Funcionario f) {
        return agregar((Persona) f);
    }

    // Editar: actualiza campos comunes y específicos si son del mismo tipo
    public static boolean editar(Persona p) {
        if (p == null) return false;
        Persona existente = registros.get(p.getRut());
        if (existente == null) return false;
        existente.setNombre(p.getNombre());
        existente.setFechaNacimiento(p.getFechaNacimiento());
        if (p instanceof Beneficiario && existente instanceof Beneficiario) {
            ((Beneficiario) existente).setDiscapacidad(((Beneficiario) p).getDiscapacidad());
        } else if (p instanceof Funcionario && existente instanceof Funcionario) {
            ((Funcionario) existente).setAreaTrabajo(((Funcionario) p).getAreaTrabajo());
        }
        return true;
    }

    public static boolean editar(Beneficiario b) {
        return editar((Persona) b);
    }

    public static boolean editar(Funcionario f) {
        return editar((Persona) f);
    }

    public static Persona buscarPorRut(String rut) {
        return registros.get(rut);
    }

    public static boolean eliminar(String rut) {
        return registros.remove(rut) != null;
    }

    public static List<String> listarTodos() {
        List<String> lista = new ArrayList<>();
        if (registros.isEmpty()) {
            lista.add("No hay personas registradas.");
            return lista;
        }
        for (Persona p : registros.values()) {
            lista.add(p.obtenerDetalle());
            lista.add("---------------------------");
        }
        return lista;
    }

    public static List<String> listarBeneficiariosTexto() {
        List<String> lista = new ArrayList<>();
        for (Persona p : registros.values()) {
            if (p instanceof Beneficiario) {
                lista.add(p.obtenerDetalle());
                lista.add("---------------------------");
            }
        }
        if (lista.isEmpty()) lista.add("No hay beneficiarios registrados.");
        return lista;
    }

    public static List<String> listarFuncionariosTexto() {
        List<String> lista = new ArrayList<>();
        for (Persona p : registros.values()) {
            if (p instanceof Funcionario) {
                lista.add(p.obtenerDetalle());
                lista.add("---------------------------");
            }
        }
        if (lista.isEmpty()) lista.add("No hay funcionarios registrados.");
        return lista;
    }

    // Filtros / búsquedas a nivel aplicación (SIA2.5, SIA2.13)
    public static List<Beneficiario> listarBeneficiariosPorDiscapacidad(String discapacidad) {
        List<Beneficiario> res = new ArrayList<>();
        if (discapacidad == null) return res;
        for (Persona p : registros.values()) {
            if (p instanceof Beneficiario) {
                Beneficiario b = (Beneficiario) p;
                if (b.getDiscapacidad() != null && b.getDiscapacidad().equalsIgnoreCase(discapacidad)) {
                    res.add(b);
                }
            }
        }
        return res;
    }

    public static List<Beneficiario> listarBeneficiariosConNotasPositivas() {
        List<Beneficiario> res = new ArrayList<>();
        for (Persona p : registros.values()) {
            if (p instanceof Beneficiario) {
                Beneficiario b = (Beneficiario) p;
                boolean tienePositiva = b.getSeguimientoImpacto().stream()
                        .anyMatch(n -> n.getEfecto().equalsIgnoreCase("Positivo"));
                if (tienePositiva) res.add(b);
            }
        }
        return res;
    }

    // Buscar servicios por tipo entre todos los beneficiarios (SIA2.13)
    public static List<String> buscarServiciosPorTipo(String tipoServicio) {
        List<String> res = new ArrayList<>();
        for (Persona p : registros.values()) {
            if (p instanceof Beneficiario) {
                Beneficiario b = (Beneficiario) p;
                b.getServiciosDeApoyo().stream()
                        .filter(s -> s.getTipoServicio().equalsIgnoreCase(tipoServicio))
                        .forEach(s -> res.add("Beneficiario: " + b.getNombre() + " (" + b.getRut() + ") - " + s.getTipoServicio() + ": " + s.getDescripcion()));
            }
        }
        return res;
    }

    // Persistencia delegada a DataStore
    public static void cargarDesde(String carpetaData) {
        Map<String, Persona> cargado = DataStore.cargar(carpetaData);
        if (cargado != null) {
            registros.clear();
            registros.putAll(cargado);
        }
    }

    public static void guardarEn(String carpetaData) {
        DataStore.guardar(registros, carpetaData);
    }

    public static Map<String, Persona> getRegistros() {
        return registros;
    }

    // Inicializar datos de prueba (si no hay archivos)
    public static void inicializarDatosPrueba() {
        try {
            // RUTs corregidos para pasar el validador
            Beneficiario b1 = new Beneficiario("12345678-5", "Juan Perez", "15/04/1980", "Visual");
            Beneficiario b2 = new Beneficiario("98765432-5", "Maria Gomez", "22/11/1990", "Auditiva");
            Beneficiario b3 = new Beneficiario("11111111-1", "Pedro Martinez", "05/06/1975", "Motriz");
            Beneficiario b4 = new Beneficiario("22222222-2", "Ana Torres", "30/09/1985", "Cognitiva");
            Funcionario f1 = new Funcionario("44444444-4", "Carlos Jara", "01/01/1985", "Recursos Humanos");

            b1.agregarServicio("Fisioterapia", "Sesiones semanales");
            b1.agregarNota("Positivo");
            b2.agregarServicio("Logopedia", "Mejora en pronunciación");
            b3.agregarNota("Negativo");

            agregar(b1);
            agregar(b2);
            agregar(b3);
            agregar(b4);
            agregar(f1);
        } catch (RutInvalidoException e) {
            e.printStackTrace();
        }
    }
}