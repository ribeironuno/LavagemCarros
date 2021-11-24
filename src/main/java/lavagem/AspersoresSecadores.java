package lavagem;

import enumerations.EstadoAspersoresSecadores;
import enumerations.EstadoTapete;
import jdk.swing.interop.SwingInterOpUtils;

import java.util.concurrent.Semaphore;

public class AspersoresSecadores implements Runnable {


    /**
     * Estado dos aspersores e secadores
     */
    private EstadoAspersoresSecadores estado;

    /**
     * Duração em segundos da utilização dos secadores
     */
    private int duracaoSecadores;

    /**
     * Duração em segundos da utilização dos aspersores.
     */
    private int duracaoAspersores;

    /**
     * Tipo de pedidos da main
     */
    public enum PedidoMain {
        ASPIRAR, SECAR;
    }

    /**
     * Tipo de pedido de comunicação da main.
     */
    private AspersoresSecadores.PedidoMain pedidoMain;

    /**
     * Semáforo de comunicação com a main.
     */
    Semaphore sem;

    /**
     * Instancia Aspersores iniciando com o estado PARADO
     */
    public AspersoresSecadores(int duracaoAspersores, Semaphore semaphore) {
        this.estado = EstadoAspersoresSecadores.PARADO;
        this.duracaoAspersores = duracaoAspersores;
        this.sem = semaphore;
    }

    /**
     * Obtem estado atual.
     *
     * @return
     */
    public EstadoAspersoresSecadores getEstado() {
        return estado;
    }

    /**
     * Ordem para iniciar aspersores.
     */
    public void iniciarAspersor() {
        this.pedidoMain = PedidoMain.ASPIRAR;
    }

    /**
     * Ordem para iniciar secador
     */
    public void iniciarSecador(int duracao) {
        this.pedidoMain = PedidoMain.SECAR;
        this.duracaoSecadores = duracao;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (this.pedidoMain != null) {
                switch (this.pedidoMain) {
                    case ASPIRAR:
                        this.estado = EstadoAspersoresSecadores.EM_ASPIRACAO;
                        try {
                            Thread.sleep(this.duracaoAspersores * 1000L);
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
