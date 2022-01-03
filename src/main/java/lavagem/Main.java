package lavagem;

import collections.implementation.LinkedQueue;
import collections.interfaces.QueueADT;
import enumerations.EstadoSistema;
import org.apache.commons.lang3.SystemUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sharedobjects.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {

    private static JLabel labelTotal;
    private static JLabel labelLavados;
    private static JLabel labelFilaPagamento;
    private static JLabel labelFilaLavagem;
    private static JLabel labelEstadoSistema;


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

    private static Tapete tapete;
    private static Rolos rolos;
    private static AspersoresSecadores aspersoresSecadores;

    private static Semaphore semaphoreMoedeiroRecebeOrdem;
    private static Semaphore semaphoreMoedeiroDarOrdem;

    private static Semaphore semaphoreLavagem;

    public static Semaphore semaphoreLogDaOrdem;
    public static Semaphore semaphoreLogRecebeSinal;

    private static Semaphore semaphoreSuspender;

    private static Semaphore semaphoreTapeteDaOrdem;
    private static Semaphore semaphoreTapeteRecebeOrdem;

    private static Semaphore semaphoreRolosDaOrdem;
    private static Semaphore semaphoreRolosRecebe;

    private static Semaphore semaphoreAspSecadorDaOrdem;
    private static Semaphore semaphoreAspSecadorRecebeSinal;

    private static int contadorSuspensoes = 0; //Conta quantas vezes foi suspenso numa lavagem, o maximo é 1 suspensão

    private static Thread threadAspersoresSecadores;
    private static Thread threadTapete;
    private static Thread threadRolos;

    public static SharedMainLog sharedMainLog;
    private static SharedMainInterface sharedMainInterface;
    private static SharedMainTapete sharedMainTapete;
    private static SharedMainRolos sharedMainRolos;
    private static SharedMainAspersoresSecadores sharedMainAspersoresSecadores;

    private static boolean reset;
    private static boolean estavaEmLavagem = false;

    private static void lerDados(String caminho) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        FileReader reader = null;
        reader = new FileReader(caminho);

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
        labelEstadoSistema.setForeground(Color.green);
        labelLavados = new JLabel();
        labelFilaPagamento = new JLabel();
        labelFilaLavagem = new JLabel();

        janela.add(labelEstadoSistema);
        janela.add(labelTotal);
        janela.add(labelLavados);
        janela.add(labelFilaPagamento);
        janela.add(labelFilaLavagem);

        try {
            janela.setIconImage(ImageIO.read(new File("images/icon.jpg")));
        } catch (IOException e) {
        }
        janela.pack();
        janela.setSize(260, 320);
        janela.setLocation(1040, 400);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                semaphoreLogDaOrdem.release();
                sharedMainLog.setMessage("close");
            }
        });
    }

    public static void atualizarLabels() {
        if (estadoSistema == EstadoSistema.OCUPADO) {
            labelEstadoSistema.setForeground(Color.orange);
        } else if (estadoSistema == EstadoSistema.FECHADO) {
            labelEstadoSistema.setForeground(Color.red);
        } else if (estadoSistema == EstadoSistema.SUSPENSO) {
            labelEstadoSistema.setForeground(Color.YELLOW);
        } else {
            labelEstadoSistema.setForeground(Color.green);
        }
        labelEstadoSistema.setText("<html><p style=\"text-align:center;\">Estado Sistema = " + estadoSistema + "</p><br></html>");
        labelLavados.setText("<html><p style=\"text-align:center;\">Lavagens = " + carrosLavados + "</p><br></html>");
        labelTotal.setText("<html><p style=\"text-align:center;\">Carros que passaram aqui = " + carrosTotais + "</p><br></html>");
        labelFilaPagamento.setText("<html><p style=\"text-align:center;\">Carros na fila de pagamento = " + filaParaPagar.size() + "</p><br></html>");
        labelFilaLavagem.setText("<html><p style=\"text-align:center;\">Carros na fila de lavagem = " + filaLavagem.size() + "</p><br></html>");
    }

    /**
     * Thread que atualiza as labels da Main a cada 200 ms
     */
    private static class ThreadAtualizaLabels extends Thread {
        @Override
        public void run() {
            while (true) {
                atualizarLabels();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Calcula um numero random entre o min e o maximo
     */
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
                try {
                    semaphoreMoedeiroRecebeOrdem.acquire(); //Espera pela ordem do moedeiro
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                switch (sharedMainInterface.getBotao()) {
                    case "Adicionar carro":
                        filaParaPagar.enqueue(new Carro("Carro" + carrosTotais++));
                        System.out.println("Interface: Chegou 1 carro! Fila atual: " + filaParaPagar);
                        if (filaParaPagar.size() == 1) { //Caso seja o primeiro carro a chegar
                            sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.CHEGOU_PRIMEIRO_CARRO); //Notifica o moedeiro que é insuficiente
                            semaphoreMoedeiroDarOrdem.release();
                        }
                        break;
                    case "I":
                        double valorIntroduzido = sharedMainInterface.getValorIntroduzido(); //Vai buscar o valor ao shared object
                        if (valorIntroduzido < valorLavagem) {
                            sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.VALOR_INSUFICIENTE); //Notifica o moedeiro que é insuficiente
                            semaphoreMoedeiroDarOrdem.release(); //Dá permissão a thread moedeiro para conseguir trabalhar e ver as notificações do main
                        } else {
                            filaLavagem.enqueue(filaParaPagar.dequeue()); //Adiciona o carro
                            //Dá permissão a thread moedeiro para conseguir trabalhar e ver as notificações do main
                            if (valorIntroduzido > valorLavagem) {
                                sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.LAVAGEM_ACEITE_COM_TROCO); //Notifica que o carro irá iniciar a lavagem e receber troco
                                sharedMainInterface.setTroco(valorIntroduzido - valorLavagem);
                            } else {
                                sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.LAVAGEM_ACEITE_SEM_TROCO);
                            }
                            semaphoreLavagem.release(); //Acrescenta recursos ao semaforo, para que consiga iniciar lavagem
                            semaphoreMoedeiroDarOrdem.release(); //Dá permissão a thread moedeiro para conseguir trabalhar e ver as notificações do main
                        }
                        if (filaParaPagar.isEmpty() && estadoSistema != EstadoSistema.FECHADO) { //Caso o carro que pagou foi o ultimo da fila
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.SEM_CARROS);
                            semaphoreMoedeiroDarOrdem.release();
                        }
                        break;
                    case "C":
                        if (sharedMainInterface.getValorIntroduzido() > 0) {
                            sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.RETIRADA_CARRO_COM_DEVOLUCAO); //Notifica que deve devolver
                        } else {
                            sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.RETIRADA_CARRO_COM_DEVOLUCAO); //Notifica que não é necessário devolver
                        }
                        if (!filaParaPagar.isEmpty()) {
                            filaParaPagar.dequeue(); //Retira o carro da queue
                        }
                        semaphoreMoedeiroDarOrdem.release();
                        if (filaParaPagar.isEmpty() && estadoSistema != EstadoSistema.FECHADO) { //Caso o carro que cancelou foi o ultimo da fila
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.SEM_CARROS);
                            semaphoreMoedeiroDarOrdem.release();
                        }
                        break;
                    case "A/F":
                        if (estadoSistema == EstadoSistema.LIVRE || estadoSistema == EstadoSistema.OCUPADO) {
                            if (sharedMainInterface.getValorIntroduzido() > 0) {
                                sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.FECHO_COM_DEVOLUCAO);
                            } else {
                                sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.FECHO_SEM_DEVOLUCAO);
                            }
                            filaParaPagar.clear();
                            estadoSistema = EstadoSistema.FECHADO;
                        } else {
                            estadoSistema = EstadoSistema.LIVRE;
                            sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.RETOMAR_SISTEMA);
                        }
                        semaphoreMoedeiroDarOrdem.release();
                        break;
                    case "E":
                        if (estadoSistema == EstadoSistema.SUSPENSO && contadorSuspensoes == 1) {
                            estadoSistema = EstadoSistema.LIVRE;
                            if (estavaEmLavagem) {  //verifica se o botao suspenção foi ativado enquanto estava em lavagem
                                semaphoreSuspender.release(2); //Avisa os thread que estao á espera do sinal do fim da suspensao
                            } else {
                                contadorSuspensoes = 0; //Caso a suspensao tenha occorido sem carros, ele podera zerar o estado
                            }
                            sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.TIRAR_SUSPENCAO); //Avisa o moedeiro que o sistema esta suspenso
                            semaphoreMoedeiroDarOrdem.release();
                        } else if (contadorSuspensoes == 0) {
                            if (estadoSistema == EstadoSistema.LIVRE) { //se nao estiver em lavagem
                                estadoSistema = EstadoSistema.SUSPENSO;
                            } else {
                                estavaEmLavagem = true;
                                estadoSistema = EstadoSistema.SUSPENSO;

                                //Coloca os pedidos de SUSPENDER no shared object
                                sharedMainTapete.setPedidoMain(SharedMainTapete.PedidoMain.SUSPENDER);
                                sharedMainRolos.setPedidoMain(SharedMainRolos.PedidoMain.SUSPENDER);
                                sharedMainAspersoresSecadores.setPedidoMain(SharedMainAspersoresSecadores.PedidoMain.SUSPENDER);

                                //Dá sinal para interromper, caso as thread estejam em sleep saibam
                                threadTapete.interrupt();
                                threadAspersoresSecadores.interrupt();
                                threadRolos.interrupt();
                            }
                            //Avisa o moedeiro que o sistema esta suspenso
                            sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.SUSPENDER);
                            semaphoreMoedeiroDarOrdem.release();
                            contadorSuspensoes++;
                        }
                        break;
                    case "Ver logs":
                        try {
                            //Verifica qual o sistema operativo do utilizador e executa o processo correspondente
                            if (SystemUtils.IS_OS_WINDOWS) {
                                new ProcessBuilder("notepad", "files/log.txt").start();
                            } else if (SystemUtils.IS_OS_MAC) {
                                new ProcessBuilder("open", "files/log.txt").start();
                            } else if (SystemUtils.IS_OS_LINUX) {
                                new ProcessBuilder("vim", "files/log.txt").start();
                            } else { //Caso não seja encontrado é lançado um erro
                                JOptionPane.showMessageDialog(null, "O seu sistema operativo nao e suportado", "InfoBox: " + "Erro ao abrir log", JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (IOException ex) {
                            JOptionPane.showMessageDialog(null, "Nao foi possivel executar o processo, este erro pode estar relacionado com o tipo de sistema operativo.", "Erro ao abrir log", JOptionPane.INFORMATION_MESSAGE);
                        }
                        break;
                    case "R":
                        estadoSistema = EstadoSistema.LIVRE;

                        sharedMainTapete.setPedidoMain(SharedMainTapete.PedidoMain.PARAR);
                        sharedMainRolos.setPedidoMain(SharedMainRolos.PedidoMain.PARAR);
                        sharedMainAspersoresSecadores.setPedidoMain(SharedMainAspersoresSecadores.PedidoMain.PARAR);

                        threadTapete.interrupt();
                        threadAspersoresSecadores.interrupt();
                        threadRolos.interrupt();

                        carrosTotais = 0;
                        carrosLavados = 0;
                        filaParaPagar.clear();
                        filaLavagem.clear();

                        sharedMainInterface.darNotificacao(SharedMainInterface.Notificacao.SEM_CARROS);
                        semaphoreMoedeiroDarOrdem.release();
                        contadorSuspensoes = 0;
                        reset = true;
                        break;
                }
            }
        }
    }

    /**
     * Thread que trata da lavagem
     */
    private static class ThreadLavagem extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    semaphoreLavagem.acquire(); //Tira o recurso da lavagem
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (estadoSistema != EstadoSistema.FECHADO) { //Caso o sistema estaja LIVRE muda para OCUPADO, caso esteja FECHADO continua
                    estadoSistema = EstadoSistema.OCUPADO;
                }
                estavaEmLavagem = true;
                atualizarLabels();

                if (!reset) {
                    //Thread LOG -> ativa e espera acabar
                    sharedMainLog.setMessage(filaLavagem.first().getNome() + " iniciou lavagem");
                    semaphoreLogDaOrdem.release(); //Da sinal para escrever no ficheiro de log a entrada do carro
                    try {
                        semaphoreLogRecebeSinal.acquire(); //Espera que a thread escreva no ficheiro
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!reset) {
                    //Thread tapete -> ativa e espera que o tapete inicie
                    sharedMainTapete.setPedidoMain(SharedMainTapete.PedidoMain.LIGAR_FRENTE);
                    semaphoreTapeteDaOrdem.release();
                    try {
                        semaphoreTapeteRecebeOrdem.acquire(); //Verifica se o tapete já está a correr, pois ele tem delay para começar
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!reset) {
                    //Thread aspersores -> ativa e espera que acabe
                    sharedMainAspersoresSecadores.setPedidoMain(SharedMainAspersoresSecadores.PedidoMain.ASPIRAR);
                    semaphoreAspSecadorDaOrdem.release();
                    try {
                        semaphoreAspSecadorRecebeSinal.acquire(); //Espera que os aspersores terminem
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!reset) {
                    //Thread rolos -> ativa e espera que acabe
                    sharedMainRolos.setPedidoMain(SharedMainRolos.PedidoMain.LIGAR);
                    semaphoreRolosDaOrdem.release();
                    try {
                        semaphoreRolosRecebe.acquire(); //Entra quando os rolos acabarem
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!reset) {
                    //Thread secadores -> ativa e espera acabar
                    sharedMainAspersoresSecadores.setDuracaoSecadores(randomNumber(tempoSecadorMin, tempoSecadorMax));
                    sharedMainAspersoresSecadores.setPedidoMain(SharedMainAspersoresSecadores.PedidoMain.SECAR);
                    semaphoreAspSecadorDaOrdem.release();
                    try {
                        semaphoreAspSecadorRecebeSinal.acquire(); //Entra quando secadores acabarem
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!reset) {
                    //Thread tapete -> finaliza operação
                    try {
                        Thread.sleep(tempoFinalTapete * 1000L); //Espera 3 segundos antes de terminar tapete e finalizar lavagem
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sharedMainTapete.setPedidoMain(SharedMainTapete.PedidoMain.PARAR);
                    semaphoreTapeteDaOrdem.release();

                    //Thread log -> escreve fim de lavagem
                    sharedMainLog.setMessage(filaLavagem.dequeue().getNome() + " terminou lavagem");
                    semaphoreLogDaOrdem.release(); //Da ordem para escrever no ficheiro de log a saida do carro
                    try {
                        semaphoreLogRecebeSinal.acquire(); //Espera que a thread escreva no ficheiro
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    if (estadoSistema != EstadoSistema.FECHADO) {
                        estadoSistema = EstadoSistema.LIVRE;
                    }
                    carrosLavados++;
                    contadorSuspensoes = 0;
                    estavaEmLavagem = false;
                    System.out.println("Fila de espera para lavagem: " + filaLavagem.toString());
                    System.out.println("Fila de espera para pagar: " + filaParaPagar.toString());
                }
                reset = false;
            }
        }
    }

    public static void main(String[] args) {
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

        semaphoreSuspender = new Semaphore(0);
        semaphoreTapeteDaOrdem = new Semaphore(0);
        semaphoreTapeteRecebeOrdem = new Semaphore(0);

        //Tapete
        sharedMainTapete = new SharedMainTapete();
        sharedMainTapete.setDelayInicial(tempoInicialTapete);
        tapete = new Tapete(semaphoreTapeteDaOrdem, semaphoreTapeteRecebeOrdem, semaphoreSuspender, sharedMainTapete);
        threadTapete = new Thread(tapete, "TH[TAPETE]");
        threadTapete.start();

        semaphoreLavagem = new Semaphore(0);

        //Rolos
        semaphoreRolosDaOrdem = new Semaphore(0);
        semaphoreRolosRecebe = new Semaphore(0);
        sharedMainRolos = new SharedMainRolos();
        sharedMainRolos.setDuracao(randomNumber(tempoMinRolos, tempoMaxRolos));
        rolos = new Rolos(semaphoreRolosDaOrdem, semaphoreRolosRecebe, semaphoreSuspender, sharedMainRolos);
        threadRolos = new Thread(rolos, "TH[ROLOS]");
        threadRolos.start();

        //Aspersores e secadores
        semaphoreAspSecadorDaOrdem = new Semaphore(0); //Dá ordem
        semaphoreAspSecadorRecebeSinal = new Semaphore(0); //Dá ordem
        sharedMainAspersoresSecadores = new SharedMainAspersoresSecadores();
        sharedMainAspersoresSecadores.setDuracaoAspersores(tempoAspersores);
        aspersoresSecadores = new AspersoresSecadores(semaphoreAspSecadorDaOrdem, semaphoreAspSecadorRecebeSinal, semaphoreSuspender, sharedMainAspersoresSecadores);
        threadAspersoresSecadores = new Thread(aspersoresSecadores, "TH[ASP E SEC]");
        threadAspersoresSecadores.start();

        //Logs
        semaphoreLogDaOrdem = new Semaphore(0);
        semaphoreLogRecebeSinal = new Semaphore(0);
        sharedMainLog = new SharedMainLog();
        Log log = null;
        try {
            log = new Log(semaphoreLogDaOrdem, semaphoreLogRecebeSinal, sharedMainLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread threadLog = new Thread(log, "TH[LOG]");
        threadLog.start();

        //Moedeiro
        semaphoreMoedeiroRecebeOrdem = new Semaphore(0);
        semaphoreMoedeiroDarOrdem = new Semaphore(0);
        sharedMainInterface = new SharedMainInterface();
        sharedMainInterface.setValorLavagem(valorLavagem);
        Thread guiMoedeiro = new Thread(new Moedeiro(semaphoreMoedeiroRecebeOrdem, semaphoreMoedeiroDarOrdem, sharedMainInterface));
        guiMoedeiro.start();

        Thread threadM = new ThreadTrataMoedeiro();
        threadM.start();

        mostrarJanela();

        Thread threadAtualizaLabels = new ThreadAtualizaLabels();
        threadAtualizaLabels.start();

        Thread threadLavagem = new ThreadLavagem();
        threadLavagem.start();
    }
}
