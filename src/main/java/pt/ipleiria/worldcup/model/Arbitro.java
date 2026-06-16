package pt.ipleiria.worldcup.model;

public class Arbitro implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome;
    private final String nacionalidade;

    public Arbitro(String nome, String nacionalidade) {
        this.nome = nome;
        this.nacionalidade = nacionalidade;
    }

    public String getNome() { return nome; }
    public String getNacionalidade() { return nacionalidade; }

    @Override public String toString() { return nome + " (" + nacionalidade + ")"; }
}
