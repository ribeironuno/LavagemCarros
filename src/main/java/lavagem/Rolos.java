package lavagem;

import enumerations.EstadoRolos;
import enumerations.EstadoTapete;
import sharedobjects.SharedMainRolos;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import static lavagem.Main.semaphoreLogDaOrdem;
import static lavagem.Main.sharedMainLog;

public class Rolos implements Runnable {

    /**
     * Enumeração correspondente aos estados possiveis associados aos rolos
     */
    private EstadoRolos estado;

    /**
     * Semáforo que serve de comunicação entre main e rolos.
     */
    private Semaphore semaphoreRecebeOrdem;

    /**
     * Semaforo que da sinal a main que acabou
     */
    private Semaphore semaphoreDaSinal;

    /**
     * Semaforo que da indicacao para quando acabar a suspensao
     */
    private Semaphore semaphoreSuspender;

    private SharedMainRolos sharedObj;

    JLabel labelEstado;

    /**
     * Instancia os rolos com a duração enviada por parametero lida
     * atraves do ficheiro de configuração
     */
    public Rolos(Semaphore semaphoreRecebeOrdem, Semaphore semaphoreDaSinal, Semaphore semaphoreSuspender, SharedMainRolos sharedObj) {
        this.semaphoreRecebeOrdem = semaphoreRecebeOrdem;
        this.semaphoreSuspender = semaphoreSuspender;
        this.semaphoreDaSinal = semaphoreDaSinal;
        this.estado = EstadoRolos.PARADO;
        this.sharedObj = sharedObj;
    }

    /**
     * Obtem estado dos rolos.
     *
     * @return Retorna o estado atual dos rolos
     */
    public EstadoRolos getEstado() {
        return this.estado;
    }

    private void mostrarJanela() {
        JFrame janela = new JFrame("Rolos");
        janela.getContentPane().setLayout(new FlowLayout());

        labelEstado = new JLabel("<html><p style=\"text-align:center;\">Estado Rolos = " + this.estado + "</p><br></html>");
        labelEstado.setForeground(Color.RED);

        janela.add(labelEstado);

        try {
            janela.setIconImage(ImageIO.read(new File("images/icon.jpg")));
        } catch (IOException ignored) {
        }
        janela.pack();
        janela.setSize(300, 95);
        janela.setLocation(740, 620);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                semaphoreLogDaOrdem.release();
                sharedMainLog.setMessage("close");
            }
        });
    }

    private void atualizarLabel() {
        if (this.estado == EstadoRolos.PARADO) {
            labelEstado.setForeground(Color.RED);
        } else {
            labelEstado.setForeground(Color.green);
        }
        labelEstado.setText("<html><p style=\"text-align:center;\">Estado Rolos = " + this.estado + "</p><br></html>");
    }

    @Override
    public void run() {
        this.mostrarJanela();
        while (true) {
            try {
                semaphoreRecebeOrdem.acquire();
            } catch (InterruptedException ignored) {
            }

            switch (sharedObj.getPedidoMain()) {
                case LIGAR:
                    this.estado = EstadoRolos.ATIVO; //Ativa os rolos
                    System.out.println(Thread.currentThread().getName() + ": Rolos ativaram");
                    this.atualizarLabel();
                    long tempoInicio = System.currentTimeMillis(); //Regista a hora que iniciou caso seja interrompido saber o que falta de espera

                    try {
                        Thread.sleep(sharedObj.getDuracao() * 1000L); //Aguarda o tempo dado pela main
                    } catch (InterruptedException e) {

                        if (sharedObj.getPedidoMain() == SharedMainRolos.PedidoMain.SUSPENDER) { //Caso a interrupção seja por pedido de suspensão
                            estado = EstadoRolos.PARADO;
                            this.atualizarLabel();
                            long tempoRestante = (sharedObj.getDuracao() * 1000L) - (System.currentTimeMillis() - tempoInicio); //Obtem duracao que resta
                            System.out.println(Thread.currentThread().getName() + ": Suspenso. Fica a faltar " + tempoRestante + "ms para acabar após retoma");
                            try {
                                semaphoreSuspender.acquire(); //Espera que a main liberte o recurso, ou seja, tire do botao de emergencia
                                System.out.println(Thread.currentThread().getName() + ": Retomaram");
                                estado = EstadoRolos.ATIVO;
                                this.atualizarLabel();
                                Thread.sleep(tempoRestante);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    this.estado = EstadoRolos.PARADO; //Para rolos
                    System.out.println(Thread.currentThread().getName() + ": Rolos acabaram");
                    this.atualizarLabel();
                    semaphoreDaSinal.release();
                    break;
                case PARAR:
                    this.estado = EstadoRolos.PARADO;
                    System.out.println(Thread.currentThread().getName() + ": Rolo parou");
                    this.atualizarLabel();
                    break;
            }
        }
    }
}