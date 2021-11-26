package sharedobjects;

public class SharedMainTapete {

    public enum PedidoMain {
        PARAR, LIGAR_FRENTE, LIGAR_TRAS;
    }

    private PedidoMain pedidoMain;

    private int delayInicial;

    public PedidoMain getPedidoMain() {
        return this.pedidoMain;
    }

    public void setPedidoMain(PedidoMain pedidoMain) {
        this.pedidoMain = pedidoMain;
    }

    public void setDelayInicial(int delayInicial) {
        this.delayInicial = delayInicial;
    }

    public int getDelayInicial() {
        return this.delayInicial;
    }


}
