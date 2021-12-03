package lavagem;

import sharedobjects.SharedMainLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;

public class Log implements Runnable {

    /**
     * Semaforo que faz a comunicação entre a main e a log
     */
    private Semaphore semaphoreRecebeOrdem;

    private Semaphore semaphoreDaSinal;

    /**
     * Objecto partilhado utilizado para enviar a mensagem de escrita
     */
    private SharedMainLog sharedObj;

    private File file;

    private FileWriter fw;

    private PrintWriter pw;

    /**
     * Data utilizada no inicio de cada mensagem log
     */
    private DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Instancia Log com semaforo e objecto partilhado por parametro. Instancia biblioteca de escrita em ficheiro
     *
     * @param sem       Semaforo que faz a comunicação entre a main e a log
     * @param sharedObj Objecto partilhado utilizado para enviar a mensagem de escrita
     * @throws IOException Se ocorrer algum erro na escrita de ficheiro
     */
    public Log(Semaphore semaphoreRecebeOrdem, Semaphore semaphoreDaSinal, SharedMainLog sharedObj) throws IOException {
        this.semaphoreRecebeOrdem = semaphoreRecebeOrdem;
        this.semaphoreDaSinal = semaphoreDaSinal;
        this.sharedObj = sharedObj;
        this.file = new File("files/log.txt");
        this.fw = new FileWriter(this.file, true);
        this.pw = new PrintWriter(this.fw);
    }

    @Override
    public void run() {
        while (true) {
            try {
                semaphoreRecebeOrdem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sharedObj.getMessage().equals("close")) {
                this.pw.close();
            }
            this.pw.println(date.format(LocalDateTime.now()) + " " + sharedObj.getMessage());
            semaphoreDaSinal.release();
        }
    }
}
