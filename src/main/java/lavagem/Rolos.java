package lavagem;

public class Rolos {

    /**
     * Enumeração correspondente aos estados possiveis associados aos rolos
     */
    private EstadoRolos estado;

    /**
     * Duração em segundos do movimento dos rolos
     */
    private int duracao;

    /**
     * Instancia os rolos com a duração enviada por parametero lida
     * atraves do ficheiro de configuração
     * @param duracao duração em segundos da duração dos rolos
     */
    public Rolos(int duracao) {
        this.duracao = duracao;
        this.estado = EstadoRolos.PARADO;
    }

    public void setEstado(EstadoRolos estado) {
        this.estado = estado;
    }

}
