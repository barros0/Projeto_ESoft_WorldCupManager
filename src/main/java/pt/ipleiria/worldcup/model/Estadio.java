package pt.ipleiria.worldcup.model;

import java.util.List;

public class Estadio implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome;
    private final String endereco;
    private String foto;
    private final List<Setor> setores; // obrigatoriamente 4

    public Estadio(String nome, String endereco, String foto, List<Setor> setores) {
        if (setores.size() != 4) throw new IllegalArgumentException("Um estádio tem obrigatoriamente 4 setores.");
        this.nome = nome;
        this.endereco = endereco;
        this.foto = foto;
        this.setores = setores;
    }

    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public List<Setor> getSetores() { return setores; }
    public int getCapacidadeTotal() { return setores.stream().mapToInt(Setor::getCapacidade).sum(); }

    @Override public String toString() { return nome; }
}
