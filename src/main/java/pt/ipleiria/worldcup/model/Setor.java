package pt.ipleiria.worldcup.model;

public class Setor implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome; // A, B, C, D
    private final int capacidade;

    public Setor(String nome, int capacidade) {
        this.nome = nome;
        this.capacidade = capacidade;
    }

    public String getNome() { return nome; }
    public int getCapacidade() { return capacidade; }

    @Override public String toString() { return "Setor " + nome; }
}
