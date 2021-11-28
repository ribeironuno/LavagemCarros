package lavagem;

import sharedobjects.SharedMainInterface;

import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import static lavagem.Main.semaphoreLog;
import static lavagem.Main.sharedMainLog;

public class Moedeiro implements ActionListener, Runnable {
    JFrame janela;

    private Semaphore semDarOrdem; //Semaforo que irá dar ordem ao main e aguadar pelo processamento do mesmo
    private Semaphore semReceberOrdem; //Semaforo que
    private SharedMainInterface sharedObj;

    private JLabel labelB = new JLabel("<html><body>Introduzido - 0.00 Euros <br></body></html>");
    private JLabel labelT = new JLabel(" ");
    private JLabel labelEstado = new JLabel("<html><body>Notificacao: Estado Ativo <br></body></html>");

    private JButton imageButton1;
    private JButton imageButton2;
    private JButton imageButton3;
    private JButton imageButton4;
    private JButton imageButton5;

    private JButton botaoI;
    private JButton botaoC;
    private JButton botaoE;
    private JButton botaoR;
    private JButton botaoAF;
    private JButton botaoAdd;
    private JButton botaoAbrirLog;


    public Moedeiro(Semaphore semDarOdem, Semaphore semReceberOrdem, SharedMainInterface buffer) {
        this.semDarOrdem = semDarOdem;
        this.sharedObj = buffer;
        this.semReceberOrdem = semReceberOrdem;
    }

    public void mostraJanela() {
        janela = new JFrame("Moedeiro");

        janela.getContentPane().setLayout(new FlowLayout());

        JLabel labelA = new JLabel("<html><body>Lavagem " + String.format("%.1f", sharedObj.getValorLavagem()) + " Euros <br></body></html>");

        ImageIcon image = new ImageIcon("images/10.jpeg");
        ImageIcon image2 = new ImageIcon("images/20.jpeg");
        ImageIcon image3 = new ImageIcon("images/50.jpeg");
        ImageIcon image4 = new ImageIcon("images/1.jpeg");
        ImageIcon image5 = new ImageIcon("images/2.jpeg");

        JPanel panelLabels = new JPanel();
        JPanel panelLabels2 = new JPanel();
        JPanel panelLabels3 = new JPanel();
        JPanel panelMoedas = new JPanel();
        JPanel panelMoedas2 = new JPanel();
        JPanel panelButoes1 = new JPanel();
        JPanel panelButoes2 = new JPanel();

        botaoI = new JButton("I");
        botaoC = new JButton("C");
        botaoE = new JButton("E");
        botaoR = new JButton("R");
        botaoAF = new JButton("A/F");
        botaoAdd = new JButton("Adicionar carro");
        botaoAbrirLog = new JButton("Ver logs");

        panelButoes1.add(botaoI);
        panelButoes1.add(botaoC);
        panelButoes1.add(botaoE);
        panelButoes1.add(botaoR);
        panelButoes2.add(botaoAF);
        panelButoes2.add(botaoAdd);
        panelButoes2.add(botaoAbrirLog);

        this.imageButton1 = new JButton("0.10");
        imageButton1.setIcon(image);
        this.imageButton2 = new JButton("0.20");
        imageButton2.setIcon(image2);
        this.imageButton3 = new JButton("0.50");
        imageButton3.setIcon(image3);
        this.imageButton4 = new JButton("1");
        imageButton4.setIcon(image4);
        this.imageButton5 = new JButton("2");
        imageButton5.setIcon(image5);

        panelMoedas.add(imageButton1);
        panelMoedas.add(imageButton2);
        panelMoedas2.add(imageButton3);
        panelMoedas2.add(imageButton4);
        panelMoedas2.add(imageButton5);
        panelLabels.add(labelA);
        panelLabels2.add(labelB);
        panelLabels3.add(labelEstado);

        janela.add(panelLabels);
        janela.add(panelLabels2);
        janela.add(panelLabels3);
        janela.add(panelMoedas);
        janela.add(panelMoedas2);
        janela.add(panelButoes1);
        janela.add(panelButoes2);

        imageButton1.addActionListener(this);
        imageButton2.addActionListener(this);
        imageButton3.addActionListener(this);
        imageButton4.addActionListener(this);
        imageButton5.addActionListener(this);

        botaoI.addActionListener(this);
        botaoC.addActionListener(this);
        botaoR.addActionListener(this);
        botaoR.addActionListener(this);
        botaoAF.addActionListener(this);
        botaoAdd.addActionListener(this);
        botaoAbrirLog.addActionListener(this);

        try {
            janela.setIconImage(ImageIO.read(new File("images/icon.jpg")));
        } catch (IOException ignored) {
        }
        janela.pack();
        janela.setSize(340, 320);
        janela.setLocation(400, 400);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                semaphoreLog.release();
                sharedMainLog.setMessage("close");
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();

        // este código pode ser melhorado
        if (action.equals("0.10")) {
            sharedObj.setValorIntroduzido(0.10);
            labelB.setText("<html><body>Introduzido - " + String.format("%.1f", sharedObj.getValorIntroduzido()) + "Euros <br></body></html>");
        } else if (action.equals("0.20")) {
            sharedObj.setValorIntroduzido(0.20);
            labelB.setText("<html><body>Introduzido - " + String.format("%.1f", sharedObj.getValorIntroduzido()) + "Euros <br></body></html>");
        } else if (action.equals("0.50")) {
            sharedObj.setValorIntroduzido(0.50);
            labelB.setText("<html><body>Introduzido - " + String.format("%.1f", sharedObj.getValorIntroduzido()) + "Euros <br></body></html>");
        } else if (action.equals("1")) {
            sharedObj.setValorIntroduzido(1);
            labelB.setText("<html><body>Introduzido - " + String.format("%.1f", sharedObj.getValorIntroduzido()) + "Euros <br></body></html>");
        } else if (action.equals("2")) {
            sharedObj.setValorIntroduzido(2);
            labelB.setText("<html><body>Introduzido - " + String.format("%.1f", sharedObj.getValorIntroduzido()) + "Euros <br></body></html>");
        } else if (action.equals("I")) {
            sharedObj.setBotao("I");
            semDarOrdem.release();
        } else if (action.equals("C")) {
            sharedObj.setBotao("C");
            semDarOrdem.release();
            labelB.setText("<html><body>Introduzido - " + String.format("%.1f", sharedObj.getValorIntroduzido()) + "Euros <br></body></html>");
        } else if (action.equals("E")) {
            sharedObj.setBotao("E");
            semDarOrdem.release();
        } else if (action.equals("R")) {
            sharedObj.setBotao("R");
            semDarOrdem.release();
        } else if (action.equals("A/F")) {
            sharedObj.setBotao("A/F");
            semDarOrdem.release();
        } else if (action.equals("Adicionar carro")) {
            sharedObj.setBotao("Adicionar carro");
            semDarOrdem.release();
        } else if (action.equals("Ver logs")) {
            sharedObj.setBotao("Ver logs");
            semDarOrdem.release();
        }
    }

    /**
     * Coloca botoes desativados
     */
    public void desativarBotoes() {
        this.imageButton1.setEnabled(false);
        this.imageButton2.setEnabled(false);
        this.imageButton3.setEnabled(false);
        this.imageButton4.setEnabled(false);
        this.imageButton5.setEnabled(false);


        this.botaoI.setEnabled(false);
        this.botaoC.setEnabled(false);
        this.botaoE.setEnabled(false);
        this.botaoR.setEnabled(false);
    }

    public void ativarBotoes() {
        this.imageButton1.setEnabled(true);
        this.imageButton2.setEnabled(true);
        this.imageButton3.setEnabled(true);
        this.imageButton4.setEnabled(true);
        this.imageButton5.setEnabled(true);

        this.botaoI.setEnabled(true);
        this.botaoC.setEnabled(true);
        this.botaoE.setEnabled(true);
        this.botaoR.setEnabled(true);
    }

    @Override
    public void run() {
        mostraJanela();
        desativarBotoes();
        while (true) {

            if (sharedObj.getNotificacao() != SharedMainInterface.Notificacao.VALOR_INSUFICIENTE) {
                labelB.setText("<html><body>Introduzido - " + String.format("%.1f", sharedObj.getValorIntroduzido()) + "Euros <br></body></html>");
            }

            try {
                semReceberOrdem.acquire(); //Espera pela ordem do main
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (sharedObj.getNotificacao()) {
                case CHEGOU_PRIMEIRO_CARRO:
                    ativarBotoes();
                    break;
                case SEM_CARROS:
                    desativarBotoes();
                    labelEstado.setText("<html><body>Notificacao: Sem carros <br></body></html>");
                    break;
                case VALOR_INSUFICIENTE:
                    labelEstado.setText("<html><body>Notificacao: Valor insuficiente <br></body></html>");
                    break;
                case LAVAGEM_ACEITE_COM_TROCO:
                    labelEstado.setText("<html><body>Notificacao: Lavagem aceite com troco: " + String.format("%.1f", sharedObj.getTroco()) + " Euros<br></body></html>");
                    sharedObj.resetValor(); //Reseta o valor no shared object para o prox carro
                    break;
                case LAVAGEM_ACEITE_SEM_TROCO:
                    labelEstado.setText("<html><body>Notificacao: Lavagem aceite sem troco <br></body></html>");
                    sharedObj.resetValor(); //Reseta o valor no shared object para o prox carro
                    break;
                case RETIRADA_CARRO_COM_DEVOLUCAO:
                    labelEstado.setText("<html><body>Notificacao: Cancelamento com devolucao: " + String.format("%.1f", sharedObj.getValorIntroduzido()) + " Euros<br></body></html>");
                    sharedObj.resetValor(); //Reseta o valor no shared object para o prox carro
                    break;
                case RETIRADA_CARRO_SEM_DEVOLUCAO:
                    labelEstado.setText("<html><body>Notificacao: Cancelamento sem devolucao <br></body></html>");
                    sharedObj.resetValor(); //Reseta o valor no shared object para o prox carro
                    break;
                case FECHO_SEM_DEVOLUCAO:
                    labelEstado.setText("<html><body>Notificacao: Fecho sem devolucao <br></body></html>");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    desativarBotoes();
                    botaoAdd.setEnabled(false);
                    botaoAF.setEnabled(true);
                    break;
                case FECHO_COM_DEVOLUCAO:
                    desativarBotoes();
                    labelEstado.setText("<html><body>Notificacao: Fecho com devolucao: " + String.format("%.1f", sharedObj.getValorIntroduzido()) + " Euros<br></body></html>");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sharedObj.resetValor(); //Reseta o valor no shared object para o prox carro
                    //No método desativarBotoes estes 2 botoes nao desativam
                    botaoAdd.setEnabled(false);
                    botaoAF.setEnabled(true);
                    break;
                case RETOMAR_SISTEMA:
                    labelEstado.setText("<html><body>Notificacao: Sem carros <br></body></html>");
                    //No método desativarBotoes estes 2 botoes nao ativam
                    botaoAdd.setEnabled(true);
                    botaoAF.setEnabled(true);
                    break;
            }
        }
    }
}


