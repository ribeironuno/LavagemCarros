package lavagem;

public class Aspersores {

    /**
     * Constante que representa a duração em segundos do movimento dos aspersores
     */
    public static final int duracao = 5;

    /**
     * Estado dos aspersores
     */
    private EstadoAspersores estado;

    /**
     * Instancia Aspersores iniciando com o estado PARADO
     */
    public Aspersores() {
        this.estado = EstadoAspersores.PARADO;
    }

    public void setEstado(EstadoAspersores estado) {
        this.estado = estado;
    }

    public EstadoAspersores getEstado() {
        return estado;
    }
}
