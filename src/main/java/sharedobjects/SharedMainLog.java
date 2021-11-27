package sharedobjects;

public class SharedMainLog {

    /**
     * Mensagem log a ser escrita para o txt
     */
    private String message;

    /**
     * Define a mensagem a ser escrita no txt
     * @param message mensagem a ser escrita
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Retorna a mensagem a se escrita no txt
     * @return mensagem a ser escrita
     */
    public String getMessage() {
        return this.message;
    }
}
