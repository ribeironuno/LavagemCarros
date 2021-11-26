package lavagem;

import enumerations.EstadoAspersoresSecadores;
import enumerations.EstadoRolos;
import sharedobjects.SharedMainAspersoresSecadores;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;

public class AspersoresSecadores implements Runnable {


    /**
     * Estado dos aspersores e secadores
     */
    private EstadoAspersoresSecadores estado;


    /**
     * Semáforo que irá sinalizar que o aspirador/secador acabaram os seus processos.
     */
    private Semaphore sem;

    /**
     * Objeto de troca de mensagens com a main.
     */
    private SharedMainAspersoresSecadores sharedObj;

    JLabel labelEstado;

    /**
     * Instancia Aspersores iniciando com o estado PARADO
     */
    public AspersoresSecadores(Semaphore sem, SharedMainAspersoresSecadores sharedObj) {
        this.estado = EstadoAspersoresSecadores.PARADO;
        this.sharedObj = sharedObj;
        this.sem = sem;
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
        JFrame janela = new JFrame("Rolos");
        janela.getContentPane().setLayout(new FlowLayout());

        labelEstado = new JLabel("<html><p style=\"text-align:center;\">Estado Aspersor/Secador = " + this.estado + "</p><br></html>");
        labelEstado.setForeground(Color.RED);

        janela.add(labelEstado);

        janela.pack();
        janela.setSize(300, 95);
        janela.setLocation(740, 510);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (sharedObj.getPedidoMain()) {
                case ASPIRAR:
                    this.estado = EstadoAspersoresSecadores.EM_ASPIRACAO;
                    System.out.println("Aspersores ligados");
                    this.atualizarLabel();
                    try {
                        Thread.sleep(sharedObj.getDuracaoAspersores() * 1000L);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    this.estado = EstadoAspersoresSecadores.PARADO;
                    System.out.println("Aspersores desligados");
                    this.atualizarLabel();
                    sem.release();
                    break;

                case SECAR:
                    this.estado = EstadoAspersoresSecadores.EM_SECAGEM;
                    System.out.println("Secadores ativados");
                    this.atualizarLabel();
                    try {
                        Thread.sleep(sharedObj.getDuracaoSecadores() * 1000L);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                    System.out.println("Secadores terminaram");
                    this.estado = EstadoAspersoresSecadores.PARADO;
                    this.atualizarLabel();
                    sem.release();
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
