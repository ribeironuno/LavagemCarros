package sharedobjects;

public class SharedMainRolos {
    /**
     * Tipo de pedidos da main.
     */
    public enum PedidoMain {
        LIGAR, SUSPENDER, RETOMAR, PARAR;
    }

    /**
     * Atual pedido da main.
     */
    private PedidoMain pedidoMain;

    /**
     * Duração em segundos do movimento dos rolos
     */
    private int duracao;

    public void setPedidoMain(PedidoMain pedidoMain) {
        this.pedidoMain = pedidoMain;
    }

    public PedidoMain getPedidoMain() {
        return this.pedidoMain;
    }

    public int getDuracao() {
        return this.duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }
}
