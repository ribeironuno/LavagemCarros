package sharedobjects;

public class SharedMainInterface {

    public enum Notificacao {
        VALOR_INSUFICIENTE, LAVAGEM_ACEITE_COM_TROCO, LAVAGEM_ACEITE_SEM_TROCO;
    }

    private String botao;

    private double valorLavagem = 0;

    private Notificacao notificacao;

    private double valorIntroduzido = 0;

    private double troco = 0;

    public void setValorLavagem(double valorLavagem) {
        this.valorLavagem = valorLavagem;
    }

    public double getValorLavagem() {
        return this.valorLavagem;
    }

    public void setValorIntroduzido(double valorIntroduzido) {
        this.valorIntroduzido += valorIntroduzido;
    }

    public double getValorIntroduzido() {
        return this.valorIntroduzido;
    }

    public void resetValor() {
        this.troco = this.valorIntroduzido = 0;
    }

    public void setBotao(String botao) {
        this.botao = botao;
    }

    public void darNotificacao(Notificacao notificacao) {
        this.notificacao = notificacao;
    }

    public Notificacao getNotificacao() {
        return this.notificacao;
    }

    public void setTroco(double troco) {
        this.troco = troco;
    }

    public double getTroco() {
        return troco;
    }

    public String getBotao() {
        return this.botao;
    }
}
