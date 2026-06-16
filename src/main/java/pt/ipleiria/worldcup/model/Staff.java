package pt.ipleiria.worldcup.model;

public class Staff {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String cargo;
    private Equipa equipa;

    public Staff(String nome, String cargo, Equipa equipa) {
        this.nome = nome;
        this.cargo = cargo;
        this.equipa = equipa;
    }

    public Equipa getEquipa() {
        return equipa;
    }

    public void setEquipa(Equipa equipa) {
        this.equipa = equipa;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
