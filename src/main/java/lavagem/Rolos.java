package lavagem;

import enumerations.EstadoRolos;

import java.util.concurrent.Semaphore;

public class Rolos implements Runnable {

    /**
     * Tipo de pedidos da main.
     */
    public enum PedidoMain {
        LIGAR, SUSPENDER, RETOMAR;
    }

    /**
     * Atual pedido da main.
     */
    private Rolos.PedidoMain pedidoMain;

    /**
     * Enumeração correspondente aos estados possiveis associados aos rolos
     */
    private EstadoRolos estado;

    /**
     * Semáforo que serve de comunicação entre main e rolos.
     */
    private Semaphore sem;

    /**
     * Duração em segundos do movimento dos rolos
     */
    private int duracao;

    /**
     * Instancia os rolos com a duração enviada por parametero lida
     * atraves do ficheiro de configuração
     */
    public Rolos(Semaphore sem) {
        this.sem = sem;
        this.estado = EstadoRolos.PARADO;
        this.pedidoMain = null;
    }

    /**
     * Método que corresponde á ordem da main para com os rolos.
     *
     * @param duracao Tempo em segundos que os rolos irão funcionar.
     */
    public void ativarRolos(int duracao) {
        this.duracao = duracao;
        this.pedidoMain = PedidoMain.LIGAR;
    }

    /**
     * Obtem estado dos rolos.
     *
     * @return Retorna o estado atual dos rolos
     */
    public EstadoRolos getEstado() {
        return this.estado;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if (this.pedidoMain != null) {
                switch (this.pedidoMain) {
                    case LIGAR:
                        this.estado = EstadoRolos.ATIVO; //Ativa os rolos
                        try {
                            Thread.sleep(this.duracao * 1000L); //Aguarda o tempo dado pela main
                        } catch (InterruptedException ex) {
                        }
                        this.estado = EstadoRolos.PARADO; //Para rolos
                        sem.release(); //E notifica main
                        this.pedidoMain = null; //Reseta o pedido da main
                        break;
                    case SUSPENDER:
                        break;
                    case RETOMAR:
                        break;
                }
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) { //Verifica se foi interrompido e acaba
                if (this.pedidoMain != PedidoMain.SUSPENDER) {
                    return;
                }
            }
        }
    }
}
