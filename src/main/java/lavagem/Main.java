package lavagem;

import collections.implementation.LinkedQueue;
import collections.interfaces.QueueADT;
import enumerations.EstadoLavagem;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {

    static JLabel labelTotal;
    static JLabel labelLavados;
    static JLabel labelFila;
    static JLabel labelStatusTapete;
    static JLabel labelStatusRolos;
    static JLabel labelStatusAspersorSecador;

    public static void GUI() {
        JFrame janela = new JFrame("Main");
        janela.getContentPane().setLayout(new FlowLayout());

        labelTotal = new JLabel();
        labelLavados = new JLabel();
        labelFila = new JLabel();
        labelStatusTapete = new JLabel();
        labelStatusRolos = new JLabel();
        labelStatusAspersorSecador = new JLabel();

        janela.add(labelTotal);
        janela.add(labelLavados);
        janela.add(labelFila);
        janela.add(labelStatusTapete);
        janela.add(labelStatusRolos);
        janela.add(labelStatusAspersorSecador);


        janela.pack();
        janela.setSize(220, 250);
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static int randomNumber(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static void main(String[] args) throws InterruptedException {
        QueueADT<Carro> filaCarros = new LinkedQueue<>();

        Semaphore semaphoreTapete = new Semaphore(0);
        Tapete tapete = new Tapete(semaphoreTapete);
        Thread threadTapete = new Thread(tapete, "Th[TAPETE]");
        threadTapete.start();

        Semaphore semaphoreLavagem = new Semaphore(0);
        EstadoLavagem estadoLavagem = EstadoLavagem.AINDA_NAO_INICIOU;

        Semaphore semaphoreInterface = new Semaphore(0);
        Buffer buffer = new Buffer();

        Semaphore semaphoreRolos = new Semaphore(0);
        Rolos rolos = new Rolos(semaphoreRolos);
        Thread threadRolos = new Thread(rolos, "TH[ROLOS]");
        threadRolos.start();

        Semaphore semaphoreAspersoresSecadores = new Semaphore(0);
        AspersoresSecadores aspersoresSecadores = new AspersoresSecadores(5, semaphoreAspersoresSecadores);
        Thread threadAspersoresSecadores = new Thread(aspersoresSecadores, "TH[ASPERSORES]");
        threadAspersoresSecadores.start();

        Thread gui = new Thread(new Interface(semaphoreInterface, buffer));
        gui.start();

        int carrosTotais = 0;
        int carrosLavados = 0;
        GUI();

        while (true) {
            labelLavados.setText("<html><p style=\"text-align:center;\">Lavagens = " + carrosLavados + "</p><br></html>");
            labelTotal.setText("<html><p style=\"text-align:center;\">Carros que passaram aqui = " + carrosTotais + "</p><br></html>");
            labelFila.setText("<html><p style=\"text-align:center;\">Carros na fila = " + filaCarros.size() + "</p><br></html>");
            labelStatusTapete.setText("<html><p style=\"text-align:center;\">Tapete = " + tapete.getEstado() + "</p><br></html>");
            labelStatusRolos.setText("<html><p style=\"text-align:center;\">Rolos = " + rolos.getEstado() + "</p><br></html>");
            labelStatusAspersorSecador.setText("<html><p style=\"text-align:center;\">Aspersor/Secador = " + aspersoresSecadores.getEstado() + "</p><br></html>");

            if (semaphoreInterface.availablePermits() == 1) { //Entra se a interface fez pedido
                semaphoreInterface.acquire();

                if (buffer.getBotao().equals("Adicionar Carro")) {
                    filaCarros.enqueue(new Carro("Carro" + carrosTotais++));
                    System.out.println("Interface: Chegou 1 carro! Fila atual: " + filaCarros);

                } else if (buffer.getBotao().equals("Iniciar Lavagem")) {
                    if (semaphoreLavagem.availablePermits() == 1) {//Se a lavagem tem 1 recurso disponivel, não pode iniciar
                        System.out.println("Interface: Acesso negado ao carro '" + filaCarros.first().getNome() + "' Já se encontra uma lavagem em curso");
                    } else {
                        semaphoreLavagem.release(); //Senao abre permissao para a lavagem acontecer
                        System.out.println(filaCarros.first().getNome() + "Interface:  Iniciou lavagem");
                    }
                } else if (buffer.getBotao().equals("Botao emergencia")) {

                }

            } else if (semaphoreLavagem.availablePermits() == 1) { //Entra se a lavagem iniciou ou há permissao para tal
                if (estadoLavagem == EstadoLavagem.AINDA_NAO_INICIOU) {
                    tapete.darOrdem(Tapete.PedidoMain.LIGAR_FRENTE); //Ordem para tapete inciar
                    estadoLavagem = EstadoLavagem.ESPERA_POR_TAPETE;

                } else if (estadoLavagem == EstadoLavagem.ESPERA_POR_TAPETE) {
                    if (semaphoreTapete.availablePermits() == 1) { //Verifica se o tapete já está a correr
                        semaphoreTapete.acquire();
                        System.out.println("Lavagem: Tapete arrancou");
                        aspersoresSecadores.iniciarAspersor();
                        estadoLavagem = EstadoLavagem.ASPERSORES_EM_PROCESSO;
                    }

                } else if (estadoLavagem == EstadoLavagem.ASPERSORES_EM_PROCESSO) {
                    if (semaphoreAspersoresSecadores.availablePermits() == 1) {
                        System.out.println("Lavagem: Aspersores acabaram");
                        rolos.ativarRolos(randomNumber(4, 8));
                        estadoLavagem = EstadoLavagem.ROLOS_EM_PROCESSO;
                        semaphoreAspersoresSecadores.acquire();
                    }

                } else if (estadoLavagem == EstadoLavagem.ROLOS_EM_PROCESSO) {
                    if (semaphoreRolos.availablePermits() == 1) { //Entra quando os rolos acabarem
                        System.out.println("Lavagem: Rolos acabaram");
                        aspersoresSecadores.iniciarSecador(randomNumber(3, 6));
                        estadoLavagem = EstadoLavagem.SECADOR_EM_PROCESSO;
                        semaphoreRolos.acquire();
                    }

                } else if (estadoLavagem == EstadoLavagem.SECADOR_EM_PROCESSO) {
                    if (semaphoreAspersoresSecadores.availablePermits() == 1) { //Entra quando secadores acabarem
                        System.out.println("Lavagem: Secardores acabaram");
                        estadoLavagem = EstadoLavagem.FINALIZADA;
                        semaphoreAspersoresSecadores.acquire();
                    }

                } else if (estadoLavagem == EstadoLavagem.FINALIZADA) {
                    tapete.darOrdem(Tapete.PedidoMain.PARAR);
                    System.out.println(filaCarros.dequeue().getNome() + " acabou a lavagem");
                    carrosLavados++;
                    estadoLavagem = EstadoLavagem.AINDA_NAO_INICIOU;
                    semaphoreLavagem.acquire();
                    System.out.println("Fila de espera: " + filaCarros);
                }
            }
        }
    }
}
