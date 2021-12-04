package lavagem;

import enumerations.EstadoAspersoresSecadores;
import sharedobjects.SharedMainAspersoresSecadores;

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

public class AspersoresSecadores implements Runnable {

    /**
     * Estado dos aspersores e secadores
     */
    private EstadoAspersoresSecadores estado;


    /**
     * Semáforo que irá sinalizar quando a main der ordem.
     */
    private Semaphore semaphoreRecebeOrdem;

    /**
     * Semáforo que irá notificar a main que o processo acabou.
     */
    private Semaphore semaphoreDaSinal;

    /**
     * Semáforo que irá dizer quando o método de suspender acabar.
     */
    private Semaphore semaphoreSuspender;

    /**
     * Objeto de troca de mensagens com a main.
     */
    private SharedMainAspersoresSecadores sharedObj;

    JLabel labelEstado;

    /**
     * Instancia Aspersores iniciando com o estado PARADO
     */
    public AspersoresSecadores(Semaphore semaphoreRecebeOrdem, Semaphore getSemaphoreDaSinal, Semaphore semaphoreSuspender, SharedMainAspersoresSecadores sharedObj) {
        this.estado = EstadoAspersoresSecadores.PARADO;
        this.sharedObj = sharedObj;
        this.semaphoreSuspender = semaphoreSuspender;
        this.semaphoreRecebeOrdem = semaphoreRecebeOrdem;
        this.semaphoreDaSinal = getSemaphoreDaSinal;
    }

    /**
     * Obtem estado atual.
     *
     * @return Estado atual.
     */
    public EstadoAspersoresSecadores getEstado() {
        return estado;
    }

    private void mostrarJanela() {
        JFrame janela = new JFrame("Aspersores e Secadores");
        janela.getContentPane().setLayout(new FlowLayout());

        labelEstado = new JLabel("<html><p style=\"text-align:center;\">Estado Aspersor/Secador = " + this.estado + "</p><br></html>");
        labelEstado.setForeground(Color.RED);

        janela.add(labelEstado);

        try {
            janela.setIconImage(ImageIO.read(new File("images/icon.jpg")));
        } catch (IOException e) {
        }
        janela.pack();
        janela.setSize(300, 95);
        janela.setLocation(740, 510);
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
        if (this.estado == EstadoAspersoresSecadores.PARADO) {
            labelEstado.setForeground(Color.RED);
        } else {
            labelEstado.setForeground(Color.green);
        }
        labelEstado.setText("<html><p style=\"text-align:center;\">Estado Aspersor/Secador = " + this.estado + "</p><br></html>");
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
                case ASPIRAR:
                    this.estado = EstadoAspersoresSecadores.EM_ASPIRACAO;
                    System.out.println(Thread.currentThread().getName() + ": Aspersores ligados");
                    this.atualizarLabel();

                    long tempoInicio = System.currentTimeMillis(); //Regista a hora que iniciou caso seja interrompido saber o que falta de espera

                    try {
                        Thread.sleep(sharedObj.getDuracaoAspersores() * 1000L);
                    } catch (InterruptedException e) {

                        if (sharedObj.getPedidoMain() == SharedMainAspersoresSecadores.PedidoMain.SUSPENDER) { //Caso a interrupção seja por pedido de suspensão
                            this.estado = EstadoAspersoresSecadores.PARADO;
                            this.atualizarLabel();
                            long tempoRestante = (sharedObj.getDuracaoAspersores() * 1000L) - (System.currentTimeMillis() - tempoInicio); //Obtem duracao que resta
                            System.out.println(Thread.currentThread().getName() + ": Suspenso. Fica a faltar " + tempoRestante + "ms para acabar após retoma");
                            try {
                                semaphoreSuspender.acquire(); //Espera que a main liberte o recurso, ou seja, tire do botao de emergencia
                                System.out.println(Thread.currentThread().getName() + ": Retomaram");
                                this.estado = EstadoAspersoresSecadores.EM_ASPIRACAO;
                                this.atualizarLabel();
                                Thread.sleep(tempoRestante);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        this.estado = EstadoAspersoresSecadores.PARADO;
                        System.out.println(Thread.currentThread().getName() + ": Aspersores desligados");
                        this.atualizarLabel();
                    }
                    this.estado = EstadoAspersoresSecadores.PARADO;
                    this.atualizarLabel();
                    semaphoreDaSinal.release();
                    break;

                case SECAR:
                    this.estado = EstadoAspersoresSecadores.EM_SECAGEM;
                    System.out.println(Thread.currentThread().getName() + ": Secadores ativados");
                    this.atualizarLabel();

                    tempoInicio = System.currentTimeMillis(); //Regista a hora que iniciou caso seja interrompido saber o que falta de espera

                    try {
                        Thread.sleep(sharedObj.getDuracaoSecadores() * 1000L);
                    } catch (InterruptedException e) {

                        if (sharedObj.getPedidoMain() == SharedMainAspersoresSecadores.PedidoMain.SUSPENDER) { //Caso a interrupção seja por pedido de suspensão
                            this.estado = EstadoAspersoresSecadores.PARADO;
                            this.atualizarLabel();
                            long tempoRestante = (sharedObj.getDuracaoAspersores() * 1000L) - (System.currentTimeMillis() - tempoInicio); //Obtem duracao que resta
                            System.out.println(Thread.currentThread().getName() + ": Suspenso. Fica a faltar " + tempoRestante + "ms para acabar após retoma");
                            try {
                                semaphoreSuspender.acquire(); //Espera que a main liberte o recurso, ou seja, tire do botao de emergencia
                                System.out.println(Thread.currentThread().getName() + ": Retomaram");
                                this.estado = EstadoAspersoresSecadores.EM_SECAGEM;
                                this.atualizarLabel();
                                Thread.sleep(tempoRestante);
                            } catch (InterruptedException ignored) {
                            }
                        }

                    }
                    System.out.println(Thread.currentThread().getName() + ": Secadores terminaram");
                    this.estado = EstadoAspersoresSecadores.PARADO;
                    this.atualizarLabel();
                    semaphoreDaSinal.release();
                    break;
            }
        }
    }
}
