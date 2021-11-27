package lavagem;

import sharedobjects.SharedMainLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.concurrent.Semaphore;

public class Log implements Runnable {

    private Semaphore sem;

    private SharedMainLog sharedObj;

    private File file;

    private FileWriter fw;

    private PrintWriter pw;

    private DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    ;

    public Log(Semaphore sem, SharedMainLog sharedObj) throws IOException {
        this.sem = sem;
        this.sharedObj = sharedObj;
        this.file = new File("log.txt");
        this.fw = new FileWriter(this.file, true);
        this.pw = new PrintWriter(this.fw);
    }

    @Override
    public void run() {
        while (true) {
            try {
                sem.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sharedObj.getMessage().equals("close")) {
                this.pw.close();
            }
            this.pw.println(date.format(LocalDateTime.now()) + " " +sharedObj.getMessage());
            sem.release();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
