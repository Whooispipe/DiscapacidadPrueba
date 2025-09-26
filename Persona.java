import exceptions.PersonaNoEncontradaException;
import exceptions.RutInvalidoException;

public abstract class Persona {
    private String rut;
    private String nombre;
    private String fechaNacimiento;

    public Persona(String rut, String nombre, String fechaNacimiento) throws RutInvalidoException {
        if (!ValidadorRut.validarRut(rut)) {
            throw new RutInvalidoException("RUT inválido: " + rut);
        }
        this.rut = rut;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }

    // Getters y Setters
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    // Método abstracto existente
    public abstract String buscarPorRut(String rut) throws PersonaNoEncontradaException;

    // Representación por defecto (puede ser sobrescrita por subclases)
    public String obtenerDetalle() {
        StringBuilder sb = new StringBuilder();
        sb.append("RUT: ").append(getRut()).append("\n");
        sb.append("Nombre: ").append(getNombre()).append("\n");
        sb.append("Fecha de Nacimiento: ").append(getFechaNacimiento()).append("\n");
        return sb.toString();
    }
}