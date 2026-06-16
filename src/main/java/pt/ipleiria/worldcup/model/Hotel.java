package pt.ipleiria.worldcup.model;

public class Hotel implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome;
    public Hotel(String nome) { this.nome = nome; }
    public String getNome() { return nome; }
    @Override public String toString() { return nome; }
}
