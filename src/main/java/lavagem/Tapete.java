package lavagem;

import enumerations.EstadoAspersoresSecadores;
import enumerations.EstadoTapete;
import sharedobjects.SharedMainTapete;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;

public class Tapete implements Runnable {

    /**
     * Semáforo de comunicação entre main e tapete.
     */
    private Semaphore sem;

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
    public Tapete(Semaphore sem, SharedMainTapete sharedMainTapete) {
        this.estado = EstadoTapete.PARADO;
        this.sem = sem;
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

        janela.pack();
        janela.setSize(300, 95);
        janela.setLocation(740, 400);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (sharedObj.getPedidoMain()) {
                case LIGAR_FRENTE:
                    try {
                        Thread.sleep(sharedObj.getDelayInicial() * 1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.estado = EstadoTapete.MOV_FRENTE;
                    this.atualizarLabel();
                    System.out.println("Tapete ligado");
                    sem.release();

                    break;
                case PARAR:
                    this.estado = EstadoTapete.PARADO;
                    System.out.println("Tapete parou");
                    this.atualizarLabel();
                    break;
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

