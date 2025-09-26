import exceptions.PersonaNoEncontradaException;
import exceptions.RutInvalidoException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Beneficiario extends Persona {
    private String discapacidad;
    private List<ServiciodeApoyo> serviciosDeApoyo;
    private List<SeguimientoImpacto> seguimientoImpacto;

    public Beneficiario(String rut, String nombre, String fechaNacimiento, String discapacidad) throws RutInvalidoException {
        super(rut, nombre, fechaNacimiento);
        this.discapacidad = discapacidad;
        this.serviciosDeApoyo = new ArrayList<>();
        this.seguimientoImpacto = new ArrayList<>();
    }

    // Getters y setters
    public String getDiscapacidad() {
        return discapacidad;
    }

    public void setDiscapacidad(String discapacidad) {
        this.discapacidad = discapacidad;
    }

    public List<ServiciodeApoyo> getServiciosDeApoyo() {
        return serviciosDeApoyo;
    }

    public void setServiciosDeApoyo(List<ServiciodeApoyo> serviciosDeApoyo) {
        this.serviciosDeApoyo = serviciosDeApoyo;
    }

    public List<SeguimientoImpacto> getSeguimientoImpacto() {
        return seguimientoImpacto;
    }

    public void setSeguimientoImpacto(List<SeguimientoImpacto> seguimientoImpacto) {
        this.seguimientoImpacto = seguimientoImpacto;
    }

    // SOBRECARGA: agregar servicio con objeto
    public void agregarServicio(ServiciodeApoyo servicio) {
        if (servicio != null) {
            this.serviciosDeApoyo.add(servicio);
        }
    }

    // SOBRECARGA: agregar servicio por datos (sobrecarga de método)
    public void agregarServicio(String tipoServicio, String descripcion) {
        ServiciodeApoyo s = new ServiciodeApoyo(tipoServicio, descripcion);
        this.serviciosDeApoyo.add(s);
    }

    // SOBRECARGA: agregar nota con objeto
    public void agregarNota(SeguimientoImpacto nota) {
        if (nota != null) {
            this.seguimientoImpacto.add(nota);
        }
    }

    // SOBRECARGA: agregar nota por efecto (string)
    public void agregarNota(String efecto) {
        SeguimientoImpacto n = new SeguimientoImpacto(efecto);
        this.seguimientoImpacto.add(n);
    }

    // Editar un servicio por índice (0-based). Devuelve true si se actualizó.
    public boolean editarServicio(int indice, String nuevoTipo, String nuevaDescripcion) {
        if (indice < 0 || indice >= serviciosDeApoyo.size()) return false;
        ServiciodeApoyo s = serviciosDeApoyo.get(indice);
        if (nuevoTipo != null && !nuevoTipo.trim().isEmpty()) s.setTipoServicio(nuevoTipo);
        if (nuevaDescripcion != null && !nuevaDescripcion.trim().isEmpty()) s.setDescripcion(nuevaDescripcion);
        return true;
    }

    // Eliminar servicio por índice
    public boolean eliminarServicioPorIndice(int indice) {
        if (indice < 0 || indice >= serviciosDeApoyo.size()) return false;
        serviciosDeApoyo.remove(indice);
        return true;
    }

    // Eliminar servicios por tipo (devuelve cantidad eliminada)
    public int eliminarServiciosPorTipo(String tipoServicio) {
        if (tipoServicio == null) return 0;
        int count = 0;
        Iterator<ServiciodeApoyo> it = serviciosDeApoyo.iterator();
        while (it.hasNext()) {
            ServiciodeApoyo s = it.next();
            if (s.getTipoServicio().equalsIgnoreCase(tipoServicio)) {
                it.remove();
                count++;
            }
        }
        return count;
    }

    // Editar nota por índice
    public boolean editarNota(int indice, String nuevoEfecto) {
        if (indice < 0 || indice >= seguimientoImpacto.size()) return false;
        SeguimientoImpacto n = seguimientoImpacto.get(indice);
        if (nuevoEfecto != null && !nuevoEfecto.trim().isEmpty()) n.setEfecto(nuevoEfecto);
        return true;
    }

    // Eliminar nota por índice
    public boolean eliminarNotaPorIndice(int indice) {
        if (indice < 0 || indice >= seguimientoImpacto.size()) return false;
        seguimientoImpacto.remove(indice);
        return true;
    }

    // Eliminar notas por efecto (Positivo/Negativo)
    public int eliminarNotasPorEfecto(String efecto) {
        if (efecto == null) return 0;
        int count = 0;
        Iterator<SeguimientoImpacto> it = seguimientoImpacto.iterator();
        while (it.hasNext()) {
            SeguimientoImpacto s = it.next();
            if (s.getEfecto().equalsIgnoreCase(efecto)) {
                it.remove();
                count++;
            }
        }
        return count;
    }

    @Override
    public String buscarPorRut(String rut) throws PersonaNoEncontradaException {
        if (this.getRut().equalsIgnoreCase(rut)) {
            return "Beneficiario encontrado: " + this.getNombre() + " | Discapacidad: " + discapacidad;
        }
        throw new PersonaNoEncontradaException("No se encontró al beneficiario con RUT: " + rut);
    }

    @Override
    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.obtenerDetalle());
        sb.append("Discapacidad: ").append(discapacidad).append("\n");

        if (!serviciosDeApoyo.isEmpty()) {
            sb.append("Servicios de Apoyo:\n");
            for (int i = 0; i < serviciosDeApoyo.size(); i++) {
                ServiciodeApoyo s = serviciosDeApoyo.get(i);
                sb.append("  [" + i + "] ").append(s.getTipoServicio()).append(": ").append(s.getDescripcion()).append("\n");
            }
        }

        if (!seguimientoImpacto.isEmpty()) {
            sb.append("Notas de Seguimiento:\n");
            for (int i = 0; i < seguimientoImpacto.size(); i++) {
                SeguimientoImpacto n = seguimientoImpacto.get(i);
                sb.append("  [" + i + "] ").append(n.getEfecto()).append("\n");
            }
        }

        return sb.toString();
    }
}