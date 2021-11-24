package lavagem;

import enumerations.EstadoTapete;

import java.util.concurrent.Semaphore;

public class Tapete implements Runnable {

    /**
     * Tipo de pedidos da main
     */
    public enum PedidoMain {
        PARAR, LIGAR_FRENTE, LIGAR_TRAS;
    }

    private PedidoMain pedidoMain;

    Semaphore sem;

    /**
     * Estado atual do tapete.
     */
    private EstadoTapete estado;

    /**
     * Tempo de espera por defeito após ser ativado.
     */
    private static final int DELAY_INICIO = 2;

    /**
     * Cria uma instância de tapete,
     */
    public Tapete(Semaphore sem) {
        this.estado = EstadoTapete.PARADO;
        this.pedidoMain = null;
        this.sem = sem;
    }

    /**
     * Definir estado atual do tapete.
     *
     * @param pedido Estado do tapete.
     */
    public void darOrdem(PedidoMain pedido) {
        this.pedidoMain = pedido;
    }

    /**
     * Obter estado do tapete.
     *
     * @return Estado atual do tapete.
     */
    public EstadoTapete getEstado() {
        return this.estado;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (this.pedidoMain != null) {
                switch (this.pedidoMain) {
                    case PARAR:
                        break;
                    case LIGAR_TRAS:
                        break;
                    case LIGAR_FRENTE:
                        try {
                            Thread.sleep(DELAY_INICIO * 1000);
                        } catch (InterruptedException e) {
                            return;
                        }
                        System.out.println("Tapete iniciou");
                        this.estado = EstadoTapete.MOV_FRENTE;
                        while (pedidoMain != PedidoMain.PARAR){

                        }
                        sem.release();
                        this.pedidoMain = null;
                        break;
                }
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

}
