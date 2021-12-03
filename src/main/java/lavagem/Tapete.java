package lavagem;

import enumerations.EstadoTapete;
import sharedobjects.SharedMainTapete;

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

public class Tapete implements Runnable {

    /**
     * Semáforo de comunicação entre main e tapete.
     */
    private Semaphore semaphoreRecebeOrdem;

    private Semaphore semaphoreDaSinal;

    private Semaphore semaphoreSuspender;

    /**
     * Estado atual do tapete.
     */
    private EstadoTapete estado;

    /**
     * Objeto de partilha de ordens entre main e tapete.
     */
    private SharedMainTapete sharedObj;

    /**
     * Label que diz o estado do objeto.
     */
    JLabel labelEstado;

    /**
     * Cria uma instância de tapete,
     */
    public Tapete(Semaphore semaphoreRecebeOrdem, Semaphore semaphoreDaSinal, Semaphore semaphoreSuspender, SharedMainTapete sharedMainTapete) {
        this.estado = EstadoTapete.PARADO;
        this.semaphoreSuspender = semaphoreSuspender;
        this.semaphoreRecebeOrdem = semaphoreRecebeOrdem;
        this.semaphoreDaSinal = semaphoreDaSinal;
        this.sharedObj = sharedMainTapete;

    }

    /**
     * Obter estado do tapete.
     *
     * @return Estado atual do tapete.
     */
    public EstadoTapete getEstado() {
        return this.estado;
    }

    private void mostrarJanela() {
        JFrame janela = new JFrame("Tapete");
        janela.getContentPane().setLayout(new FlowLayout());

        labelEstado = new JLabel("<html><p style=\"text-align:center;\">Estado Tapete = " + this.estado + "</p><br></html>");
        labelEstado.setForeground(Color.RED);

        janela.add(labelEstado);

        try {
            janela.setIconImage(ImageIO.read(new File("images/icon.jpg")));
        } catch (IOException ignored) {
        }
        janela.pack();
        janela.setSize(300, 95);
        janela.setLocation(740, 400);
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
        if (this.estado == EstadoTapete.PARADO) {
            labelEstado.setForeground(Color.RED);
        } else {
            labelEstado.setForeground(Color.green);
        }
        labelEstado.setText("<html><p style=\"text-align:center;\">Estado Tapete = " + this.estado + "</p><br></html>");
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
                case LIGAR_FRENTE:
                    try {
                        Thread.sleep(sharedObj.getDelayInicial() * 1000L); //Faz a espera inicial programada
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.estado = EstadoTapete.MOV_FRENTE;
                    this.atualizarLabel();
                    System.out.println(Thread.currentThread().getName() + ": Tapete ligado");
                    semaphoreDaSinal.release();

                    break;
                case PARAR:
                    this.estado = EstadoTapete.PARADO;
                    System.out.println(Thread.currentThread().getName() + ": Tapete parou");
                    this.atualizarLabel();
                    break;
                case SUSPENDER:
                    if (estado != EstadoTapete.PARADO) {
                        this.estado = EstadoTapete.PARADO;
                        System.out.println(Thread.currentThread().getName() + ": Suspensos");
                        this.atualizarLabel();
                        try {
                            semaphoreSuspender.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(Thread.currentThread().getName() + ": Retomaram");
                        this.estado = EstadoTapete.MOV_FRENTE;
                        this.atualizarLabel();
                    }
                    break;
            }
        }
    }
}

