

public class SeguimientoImpacto {
    private String efecto; // "Positivo" o "Negativo"

    public SeguimientoImpacto(String efecto) {
        this.efecto = efecto;
    }

    // Sobrecarga 
    public SeguimientoImpacto(boolean positivo) {
        this.efecto = positivo ? "Positivo" : "Negativo";
    }

    // Getters y Setters
    public String getEfecto() {
        return efecto;
    }

    public void setEfecto(String efecto) {
        this.efecto = efecto;
    }
}

