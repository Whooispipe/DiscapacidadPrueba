public class ValidadorRut {
    public static boolean validarRut(String rut) {
        rut = rut.replace(".", "").replace("-", "").toUpperCase();
        if (rut.length() < 2) return false;

        String numero = rut.substring(0, rut.length() - 1);
        char dv = rut.charAt(rut.length() - 1);

        try {
            int rutAux = Integer.parseInt(numero);
            char dvCalculado = calcularDV(rutAux);
            return dv == dvCalculado;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static char calcularDV(int rut) {
        int m = 0, s = 1;
        for (; rut != 0; rut /= 10) {
            s = (s + rut % 10 * (9 - m++ % 6)) % 11;
        }
        return (char) (s != 0 ? s + 47 : 75); // Retorna '0'-'9' o 'K'
    }
}

