package lavagem;

import enumerations.EstadoAspersoresSecadores;
import enumerations.EstadoTapete;
import jdk.swing.interop.SwingInterOpUtils;

import java.util.concurrent.Semaphore;

public class AspersoresSecadores implements Runnable {

    /**
     * Constante que representa a duração em segundos do movimento dos aspersores
     */
    public static final int DURACAOASPERSORES = 5;

    /**
     * Estado dos aspersores e secadores
     */
    private EstadoAspersoresSecadores estado;

    /**
     * Duração em segundos da utilização dos secadores
     */
    private int duracaoSecadores;

    /**
     * Tipo de pedidos da main
     */
    public enum PedidoMain {
        ASPIRAR, SECAR;
    }

    private AspersoresSecadores.PedidoMain pedidoMain;

    Semaphore sem;

    /**
     * Instancia Aspersores iniciando com o estado PARADO
     */
    public AspersoresSecadores(int duracaoSecadores, Semaphore semaphore) {
        this.estado = EstadoAspersoresSecadores.PARADO;
        this.duracaoSecadores = duracaoSecadores;
        this.sem = semaphore;
    }

    public void setEstado(EstadoAspersoresSecadores estado) {
        this.estado = estado;
    }

    public EstadoAspersoresSecadores getEstado() {
        return estado;
    }

    /**
     * Definir estado atual do tapete.
     *
     * @param pedido Estado do tapete.
     */
    public void darOrdem(AspersoresSecadores.PedidoMain pedido) {
        this.pedidoMain = pedido;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (this.pedidoMain != null) {
                switch (this.pedidoMain) {
                    case ASPIRAR:
                        this.estado = EstadoAspersoresSecadores.EM_ASPIRACAO;
                        try {
                            Thread.sleep(this.DURACAOASPERSORES * 1000L);
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }
                        this.estado = EstadoAspersoresSecadores.PARADO;
                        sem.release();
                        this.pedidoMain = null;
                        break;
                    case SECAR:
                        this.estado = EstadoAspersoresSecadores.EM_SECAGEM;
                        try {
                            Thread.sleep(this.duracaoSecadores * 1000L);
                        } catch (InterruptedException e) {
                            System.out.println(e);
                        }
                        this.estado = EstadoAspersoresSecadores.PARADO;
                        sem.release();
                        this.pedidoMain = null;
                        break;
                }
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            }
        }
    }
}
