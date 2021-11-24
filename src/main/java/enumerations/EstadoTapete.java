package enumerations;

public enum EstadoTapete {
    MOV_FRENTE, MOV_TRAS, PARADO;

    public static String estadoToString(EstadoTapete estado) {
        if (estado.equals(PARADO)) {
            return "Parado";
        } else if (estado.equals(MOV_FRENTE)) {
            return "Movimento em frente";
        } else {
            return "Movimento para trás";
        }
    }

}
