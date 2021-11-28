package lavagem;

import enumerations.EstadoRolos;
import sharedobjects.SharedMainRolos;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import static lavagem.Main.semaphoreLog;
import static lavagem.Main.sharedMainLog;

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

        try {
            janela.setIconImage(ImageIO.read(new File("images/icon.jpg")));
        } catch (IOException ignored) {
        }
        janela.pack();
        janela.setSize(300, 95);
        janela.setLocation(740, 620);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                semaphoreLog.release();
                sharedMainLog.setMessage("close");
            }
        });
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