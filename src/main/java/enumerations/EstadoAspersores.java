package enumerations;

public enum EstadoAspersores {
    PARADO, ASPIRACAO;

    /**
     * Converte a enumeração para uma String formatada
     * @param estado estado a ser convertido
     * @return resultado em string do estado
     */
    public static String estadoToString(EstadoAspersores estado) {
        if (estado.equals(PARADO)) {
            return "Parado";
        } else {
            return "Em aspiração";
        }
    }
}
