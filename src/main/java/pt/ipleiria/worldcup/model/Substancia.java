package pt.ipleiria.worldcup.model;

public class Substancia implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private final String nome;
    private final String castigo;

    public Substancia(String nome, String castigo){
        this.nome = nome;
        this.castigo = castigo;
    }

    public String getNome() { return nome; }
    public String getCastigo() { return castigo; }

    @Override
    public String toString(){
        return nome;
    }
q

}
