package lavagem;

public enum EstadoRolos {
    ATIVO, PARADO;

    /**
     * Converte a enumeração para uma String formatada
     * @param estado estado a ser convertido
     * @return resultado em string do estado
     */
    public static String estadoToString(EstadoRolos estado) {
        if (estado.equals(PARADO)) {
            return "Parado";
        } else {
            return "Ativo";
        }
    }
}
