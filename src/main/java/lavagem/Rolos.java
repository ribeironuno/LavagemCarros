package lavagem;

import enumerations.EstadoRolos;
import sharedobjects.SharedMainRolos;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TransferQueue;

public class Rolos implements Runnable {

    /**
     * Enumeração correspondente aos estados possiveis associados aos rolos
     */
    private EstadoRolos estado;

    /**
     * Semáforo que serve de comunicação entre main e rolos.
     */
    private Semaphore sem;

    private SharedMainRolos sharedObj;

    JLabel labelEstado;

    /**
     * Instancia os rolos com a duração enviada por parametero lida
     * atraves do ficheiro de configuração
     */
    public Rolos(Semaphore sem, SharedMainRolos sharedObj) {
        this.sem = sem;
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

        janela.pack();
        janela.setSize(300, 80);
        janela.setLocation(700, 600);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void atualizarLabel() {
        if (this.estado == EstadoRolos.PARADO){
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
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (sharedObj.getPedidoMain()) {
                case LIGAR:
                    this.estado = EstadoRolos.ATIVO; //Ativa os rolos
                    System.out.println("Rolos ativaram");
                    this.atualizarLabel();
                    long tempoInicio = System.currentTimeMillis(); //Regista a hora que iniciou caso seja interrompido saber o que falta de espera
                    try {
                        Thread.sleep(sharedObj.getDuracao() * 1000L); //Aguarda o tempo dado pela main
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    this.estado = EstadoRolos.PARADO; //Para rolos
                    System.out.println("Rolos acabaram");
                    this.atualizarLabel();
                    sem.release(); //Notifica main
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