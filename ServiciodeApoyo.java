
public class ServiciodeApoyo {
    private String tipoServicio;
    private String descripcion;

    public ServiciodeApoyo(String tipoServicio, String descripcion) {
        this.tipoServicio = tipoServicio;
        this.descripcion = descripcion;
    }

    // Getters y Setters
    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
