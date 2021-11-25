package lavagem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.Semaphore;

public class Moedeiro implements ActionListener, Runnable {
    JFrame janela;
    Semaphore semMT;
    Buffer buffer;
    static JLabel labelB = new JLabel("<html><body>Introduzido - 0.00€ <br></body></html>");
    static JLabel labelT = new JLabel(" ");

    public Moedeiro(Semaphore semMT, Buffer buffer) {
        this.semMT = semMT;
        this.buffer = buffer;
    }

    public void mostraJanela() {
        janela = new JFrame("Nova janela");

        // define layout para janela
        janela.getContentPane().setLayout(new FlowLayout());

        JLabel labelA = new JLabel("<html><body>Lavagem - 3.00€ <br></body></html>");
        //JLabel labelB = new JLabel();
        ImageIcon image = new ImageIcon("10.jpeg");
        ImageIcon image2 = new ImageIcon("20.jpeg");
        ImageIcon image3 = new ImageIcon("50.jpeg");
        ImageIcon image4 = new ImageIcon("1.jpeg");
        ImageIcon image5 = new ImageIcon("2.jpeg");

        JButton botaoA = new JButton("I");
        JButton botaoC = new JButton("C");
        JButton botaoD = new JButton("E");
        JButton botaoE = new JButton("R");
        JButton botaoF = new JButton("A/F");

        JButton imageButton1 = new JButton("0.10€");
        imageButton1.setIcon(image);
        JButton imageButton2 = new JButton("0.20€");
        imageButton2.setIcon(image2);
        JButton imageButton3 = new JButton("0.50€");
        imageButton3.setIcon(image3);
        JButton imageButton4 = new JButton("1€");
        imageButton4.setIcon(image4);
        JButton imageButton5 = new JButton("2€");
        imageButton5.setIcon(image5);

        // define listeners para botões
        imageButton1.addActionListener(this);
        imageButton2.addActionListener(this);
        imageButton3.addActionListener(this);
        imageButton4.addActionListener(this);
        imageButton5.addActionListener(this);

        botaoA.addActionListener(this);
        botaoC.addActionListener(this);
        botaoD.addActionListener(this);
        botaoE.addActionListener(this);
        botaoF.addActionListener(this);

        // adiciona botões à janela


        janela.add(labelA);
        janela.add(labelB);
        janela.add(labelT);
        janela.add(imageButton1);
        janela.add(imageButton2);
        janela.add(imageButton3);
        janela.add(imageButton4);
        janela.add(imageButton5);

        janela.add(botaoA);
        janela.add(botaoC);
        janela.add(botaoD);
        janela.add(botaoE);
        janela.add(botaoF);

        janela.pack();
        janela.setSize(150, 600);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();

        // este código pode ser melhorado
        if (action.equals("0.10€")) {
            buffer.setBotao("0.10€");
            semMT.release();
        } else if (action.equals("0.20€")) {
            buffer.setBotao("0.20€");
            semMT.release();
        } else if (action.equals("0.50€")) {
            buffer.setBotao("0.50€");
            semMT.release();
        } else if (action.equals("1€")) {
            buffer.setBotao("1€");
            semMT.release();
        } else if (action.equals("2€")) {
            buffer.setBotao("2€");
            semMT.release();
        } else if (action.equals("I")) {
            buffer.setBotao("I");
            semMT.release();
        } else if (action.equals("C")) {
            buffer.setBotao("C");
            semMT.release();
        } else if (action.equals("E")) {
            buffer.setBotao("E");
            semMT.release();
        } else if (action.equals("R")) {
            buffer.setBotao("R");
            semMT.release();
        } else if (action.equals("A/F")) {
            buffer.setBotao("A/F");
            semMT.release();
        }
    }

    @Override
    public void run() {
        mostraJanela();
    }
}

