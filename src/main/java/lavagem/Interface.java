package lavagem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.Semaphore;

public class Interface implements ActionListener, Runnable {
    JFrame janela;
    Semaphore sem;
    Buffer buffer;


    public Interface(Semaphore semMT, Buffer buffer) {
        this.sem = semMT;
        this.buffer = buffer;
    }

    public void mostraJanela() {
        janela = new JFrame("Nova janela");

        janela.getContentPane().setLayout(new FlowLayout());

        JButton botaoA = new JButton("Adicionar Carro");
        JButton botaoB = new JButton("Botao emergencia");
        JButton botaoC = new JButton("Iniciar Lavagem");

        botaoA.addActionListener(this);
        botaoB.addActionListener(this);
        botaoC.addActionListener(this);

        janela.add(botaoA);
        janela.add(botaoC);
        janela.add(botaoB);

        janela.pack();
        janela.setLocationRelativeTo(null);
        janela.setVisible(true);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();

        if (action.equals("Adicionar Carro")) {
            buffer.setBotao("Adicionar Carro");
            sem.release();
        } else if (action.equals("Iniciar Lavagem")) {
            buffer.setBotao("Iniciar Lavagem");
            sem.release();
        } else if (action.equals("Botao emergencia")) {
            buffer.setBotao("Botao emergencia");
            sem.release();
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        mostraJanela();
    }
}