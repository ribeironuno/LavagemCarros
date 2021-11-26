package sharedobjects;

public class SharedMainAspersoresSecadores {
    public enum PedidoMain {
        ASPIRAR, SECAR;
    }

    /**
     * Duração em segundos da utilização dos secadores
     */
    private int duracaoSecadores;

    /**
     * Duração em segundos da utilização dos aspersores.
     */
    private int duracaoAspersores;

    /**
     * Ultimo pedido da main.
     */
    private PedidoMain pedidoMain;

    public void setPedidoMain(PedidoMain pedidoMain) {
        this.pedidoMain = pedidoMain;
    }

    public PedidoMain getPedidoMain() {
        return this.pedidoMain;
    }

    public int getDuracaoSecadores() {
        return this.duracaoSecadores;
    }

    public int getDuracaoAspersores() {
        return this.duracaoAspersores;
    }

    public void setDuracaoSecadores(int duracao) {
        this.duracaoSecadores = duracao;
    }

    public void setDuracaoAspersores(int duracao) {
        this.duracaoAspersores = duracao;
    }

}
