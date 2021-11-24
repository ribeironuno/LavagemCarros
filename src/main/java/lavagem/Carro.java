package lavagem;

public class Carro {

    public String nome;

    public Carro(String name) {
        this.nome = name;
    }

    public String getNome() {
        return this.nome;
    }

    @Override
    public String toString(){
        return this.nome;
    }
}
