package lavagem;

import sharedobjects.SharedMainInterface;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.Semaphore;

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

        JButton botaoI = new JButton("I");
        JButton botaoC = new JButton("C");
        JButton botaoE = new JButton("E");
        JButton botaoR = new JButton("R");
        JButton botaoAF = new JButton("A/F");
        JButton addCarro = new JButton("Adicionar carro");

        panelButoes1.add(botaoI);
        panelButoes1.add(botaoC);
        panelButoes1.add(botaoE);
        panelButoes1.add(botaoR);
        panelButoes2.add(botaoAF);
        panelButoes2.add(addCarro);

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
        addCarro.addActionListener(this);

        janela.pack();
        janela.setSize(340, 320);
        janela.setLocation(400, 400);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
            this.desativarBotoes();
            sharedObj.setBotao("I");
            semDarOrdem.release();
        } else if (action.equals("C")) {
            sharedObj.setBotao("C");
            sharedObj.resetValor();
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
        }
    }

    public void desativarBotoes() {
        this.imageButton1.setEnabled(false);
        this.imageButton2.setEnabled(false);
        this.imageButton3.setEnabled(false);
        this.imageButton4.setEnabled(false);
        this.imageButton5.setEnabled(false);
    }

    public void ativarButoes() {
        this.imageButton1.setEnabled(true);
        this.imageButton2.setEnabled(true);
        this.imageButton3.setEnabled(true);
        this.imageButton4.setEnabled(true);
        this.imageButton5.setEnabled(true);
    }

    @Override
    public void run() {
        mostraJanela();
        while (true) {

            try {
                semReceberOrdem.acquire(); //Espera pela ordem do main
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            switch (sharedObj.getNotificacao()) {
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
            }
            this.ativarButoes();
        }
    }
}


