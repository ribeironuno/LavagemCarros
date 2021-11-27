package lavagem;

import collections.implementation.LinkedQueue;
import collections.interfaces.QueueADT;
import enumerations.EstadoLavagem;
import enumerations.EstadoSistema;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sharedobjects.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Semaphore;

public class Main {

    private static JLabel labelTotal;
    private static JLabel labelLavados;
    private static JLabel labelFila;
    private static JLabel labelEstadoSistema;

    private static JButton addCarro;

    private static double valorLavagem;
    private static int tempoInicialTapete;
    private static int tempoFinalTapete;
    private static int tempoMinRolos;
    private static int tempoMaxRolos;
    private static int tempoAspersores;
    private static int tempoSecadorMin;
    private static int tempoSecadorMax;

    private static QueueADT<Carro> filaParaPagar;
    private static QueueADT<Carro> filaLavagem;

    private static EstadoSistema estadoSistema;

    private static int carrosTotais;
    private static int carrosLavados;
    private static boolean sistemaEstaSuspenso;

    private static Tapete tapete;
    private static Rolos rolos;
    private static AspersoresSecadores aspersoresSecadores;
    private static Thread treadTrataMoedeiro; //Thread que irá tratar do processo de pagamento
    private static Semaphore semMoedeiroRecebeOrdem;
    private static SharedMainInterface sharedMainInterface;
    private static Semaphore semMoedeiroDarOrdem;
    private static Semaphore semaphoreLavagem;
    public static Semaphore semaphoreLog;
    public static SharedMainLog sharedMainLog;


    private static void lerDados(String caminho) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        FileReader reader = null;
        reader = new FileReader("files/dados.json");

        JSONObject obj = (JSONObject) parser.parse(reader);

        valorLavagem = (Double) obj.get("valorLavagem");
        tempoInicialTapete = ((Long) obj.get("tempoInicialTapete")).intValue();
        tempoFinalTapete = ((Long) obj.get("tempoFinalTapete")).intValue();
        tempoMinRolos = ((Long) obj.get("tempoMinRolos")).intValue();
        tempoMaxRolos = ((Long) obj.get("tempoMaxRolos")).intValue();
        tempoAspersores = ((Long) obj.get("tempoAspersores")).intValue();
        tempoSecadorMin = ((Long) obj.get("tempoSecadorMin")).intValue();
        tempoSecadorMax = ((Long) obj.get("tempoSecadorMax")).intValue();
    }

    private static void mostrarJanela() {
        JFrame janela = new JFrame("Main");
        janela.getContentPane().setLayout(new FlowLayout());

        labelTotal = new JLabel();
        labelEstadoSistema = new JLabel();
        labelLavados = new JLabel();
        labelFila = new JLabel();

        addCarro = new JButton("Adicionar carro");

        janela.add(labelEstadoSistema);
        janela.add(labelTotal);
        janela.add(labelLavados);
        janela.add(labelFila);

        janela.pack();
        janela.setSize(260, 320);
        janela.setLocation(1040, 400);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                semaphoreLog.release();
                sharedMainLog.setMessage("close");
            }
        });
    }

    public static void atualizarLabels() {
        labelEstadoSistema.setText("<html><p style=\"text-align:center;\">Estado Sistema = " + estadoSistema + "</p><br></html>");
        labelLavados.setText("<html><p style=\"text-align:center;\">Lavagens = " + carrosLavados + "</p><br></html>");
        labelTotal.setText("<html><p style=\"text-align:center;\">Carros que passaram aqui = " + carrosTotais + "</p><br></html>");
        labelFila.setText("<html><p style=\"text-align:center;\">Carros na fila = " + filaLavagem.size() + "</p><br></html>");
    }

    private static int randomNumber(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    /**
     * Thread que irá tratar do processo da interface (moedeiro e teclado)
     */
    private static class ThreadTrataMoedeiro extends Thread {

        @Override
        public void run() {
            while (true) {
                if (semMoedeiroRecebeOrdem.availablePermits() == 1) {
                    try {
                        semMoedeiroRecebeOrdem.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    switch (sharedMainInterface.getBotao()) {
                        case "Adicionar carro":
                            filaLavagem.enqueue(new Carro("Carro" + carrosTotais++));
                            System.out.println("Interface: Chegou 1 carro! Fila atual: " + filaLavagem);
                            break;
                        case "I":
                            double valorIntroduzido = sharedMainInterface.getValorIntroduzido(); //Vai buscar o valor ao shared object
                            if (valorIntroduzido < valorLavagem) {
                                sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.VALOR_INSUFICIENTE); //Notifica o moedeiro que é insuficiente
                                semMoedeiroDarOrdem.release();
                            } else {
                                filaLavagem.enqueue(new Carro("Carro" + carrosTotais++)); //Adiciona o carro
                                semaphoreLavagem.release();
                                if (valorIntroduzido > valorLavagem) {
                                    sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.LAVAGEM_ACEITE_COM_TROCO); //Notifica que o carro irá iniciar a lavagem e receber troco
                                    sharedMainInterface.setTroco(valorIntroduzido - valorLavagem);
                                    semMoedeiroDarOrdem.release(); //Dá permissão a thread moedeiro para conseguir trabalhar e ver as notificações do main
                                } else {
                                    sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.LAVAGEM_ACEITE_SEM_TROCO);
                                    semMoedeiroDarOrdem.release();
                                    semMoedeiroDarOrdem.release(); //Dá permissão a thread moedeiro para conseguir trabalhar e ver as notificações do main
                                }
                            }
                            break;
                    }
                }

            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        try {
            lerDados("files/Dados.json");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }


        filaParaPagar = new LinkedQueue<>();
        filaLavagem = new LinkedQueue<>();

        estadoSistema = EstadoSistema.LIVRE;

        carrosTotais = 0;
        carrosLavados = 0;
        sistemaEstaSuspenso = false;

        Semaphore semaphoreTapete = new Semaphore(0);
        SharedMainTapete sharedMainTapete = new SharedMainTapete();
        sharedMainTapete.setDelayInicial(tempoInicialTapete);
        tapete = new Tapete(semaphoreTapete, sharedMainTapete);
        Thread threadTapete = new Thread(tapete, "Th[TAPETE]");
        threadTapete.start();

        semaphoreLavagem = new Semaphore(0);
        EstadoLavagem estadoLavagem = EstadoLavagem.AINDA_NAO_INICIOU;

        Semaphore semaphoreRolos = new Semaphore(0);
        SharedMainRolos sharedMainRolos = new SharedMainRolos();
        sharedMainRolos.setDuracao(tempoMaxRolos);
        rolos = new Rolos(semaphoreRolos, sharedMainRolos);
        Thread threadRolos = new Thread(rolos, "TH[ROLOS]");
        threadRolos.start();

        Semaphore semaphoreAspSecador = new Semaphore(0); //Dá ordem
        SharedMainAspersoresSecadores sharedMainAspersoresSecadores = new SharedMainAspersoresSecadores();
        sharedMainAspersoresSecadores.setDuracaoAspersores(tempoAspersores);
        aspersoresSecadores = new AspersoresSecadores(semaphoreAspSecador, sharedMainAspersoresSecadores);
        Thread threadAspersoresSecadores = new Thread(aspersoresSecadores, "TH[ASPERSORES]");
        threadAspersoresSecadores.start();

        semaphoreLog = new Semaphore(0);
        sharedMainLog = new SharedMainLog();
        Log log = new Log(semaphoreLog, sharedMainLog);
        Thread threadLog = new Thread(log, "TH[LOG]");
        threadLog.start();

        semMoedeiroRecebeOrdem = new Semaphore(0);
        semMoedeiroDarOrdem = new Semaphore(0);
        sharedMainInterface = new SharedMainInterface();
        sharedMainInterface.setValorLavagem(valorLavagem);
        Thread guiMoedeiro = new Thread(new Moedeiro(semMoedeiroRecebeOrdem, semMoedeiroDarOrdem, sharedMainInterface));
        guiMoedeiro.start();

        Thread threadM = new ThreadTrataMoedeiro();
        threadM.start();

        mostrarJanela();

        while (true) {
            labelEstadoSistema.setText("<html><p style=\"text-align:center;\">Estado Sistema = " + estadoSistema + "</p><br></html>");
            labelLavados.setText("<html><p style=\"text-align:center;\">Lavagens = " + carrosLavados + "</p><br></html>");
            labelTotal.setText("<html><p style=\"text-align:center;\">Carros que passaram aqui = " + carrosTotais + "</p><br></html>");
            labelFila.setText("<html><p style=\"text-align:center;\">Carros na fila = " + filaLavagem.size() + "</p><br></html>");

            if (semaphoreLavagem.availablePermits() == 1) { //Entra se a lavagem iniciou ou há permissao para tal
                estadoSistema = EstadoSistema.OCUPADO;
                atualizarLabels();

                sharedMainLog.setMessage(filaLavagem.first().getNome() + " iniciou lavagem");
                semaphoreLog.release();
                Thread.sleep(200);
                semaphoreLog.acquire();

                sharedMainTapete.setPedidoMain(SharedMainTapete.PedidoMain.LIGAR_FRENTE);
                estadoLavagem = EstadoLavagem.ESPERA_POR_TAPETE;
                semaphoreTapete.release();

                Thread.sleep(200);

                semaphoreTapete.acquire(); //Verifica se o tapete já está a correr, pois ele tem delay para começar
                sharedMainAspersoresSecadores.setPedidoMain(SharedMainAspersoresSecadores.PedidoMain.ASPIRAR);
                estadoLavagem = EstadoLavagem.ASPERSORES_EM_PROCESSO;
                semaphoreAspSecador.release();

                Thread.sleep(200);

                semaphoreAspSecador.acquire(); //Espera que os aspersores terminem
                sharedMainRolos.setPedidoMain(SharedMainRolos.PedidoMain.LIGAR);
                estadoLavagem = EstadoLavagem.ROLOS_EM_PROCESSO;
                semaphoreRolos.release();

                Thread.sleep(200);

                semaphoreRolos.acquire(); //Entra quando os rolos acabarem
                sharedMainAspersoresSecadores.setDuracaoSecadores(randomNumber(tempoSecadorMin, tempoSecadorMax));
                sharedMainAspersoresSecadores.setPedidoMain(SharedMainAspersoresSecadores.PedidoMain.SECAR);
                estadoLavagem = EstadoLavagem.SECADOR_EM_PROCESSO;
                semaphoreAspSecador.release();

                Thread.sleep(200);

                semaphoreAspSecador.acquire(); //Entra quando secadores acabarem
                estadoLavagem = EstadoLavagem.FINALIZADA;

                Thread.sleep(3000); //Espera 3 segundos antes de terminar tapete e finalizar lavagem
                sharedMainTapete.setPedidoMain(SharedMainTapete.PedidoMain.PARAR);
                semaphoreTapete.release();

                sharedMainLog.setMessage(filaLavagem.dequeue().getNome() + " terminou lavagem");
                semaphoreLog.release();
                Thread.sleep(200);
                semaphoreLog.acquire();

                estadoSistema = EstadoSistema.LIVRE;
                carrosLavados++;
                estadoLavagem = EstadoLavagem.AINDA_NAO_INICIOU;
                semaphoreLavagem.acquire(); //Tira o recurso da lavagem
                System.out.println("Fila de espera: " + filaLavagem);
            }
        }
    }
}
