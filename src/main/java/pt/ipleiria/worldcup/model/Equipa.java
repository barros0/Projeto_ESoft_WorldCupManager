package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.Confederacao;

import java.util.ArrayList;
import java.util.List;

public class Equipa implements java.io.Serializable{

    private static final long serialVersionUID = 1L;

    private String pais;
    private String bandeira;
    private String sigla;
    private Confederacao confederacao;
    private int pote;
    private Grupo grupo;
    private final List<Jogador> jogadores = new ArrayList<>();
    private final List<Staff> staff = new ArrayList<>();

    public Equipa(String pais, String bandeira, String sigla, Confederacao confederacao, int pote) {
        this.pais = pais;
        this.bandeira = bandeira;
        this.sigla = sigla;
        this.confederacao = confederacao;
        this.pote = pote;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getBandeira() {
        return bandeira;
    }

    public void setBandeira(String bandeira) {
        this.bandeira = bandeira;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public Confederacao getConfederacao() {
        return confederacao;
    }

    public void setConfederacao(Confederacao confederacao) {
        this.confederacao = confederacao;
    }

    public int getPote() {
        return pote;
    }

    public void setPote(int pote) {
        this.pote = pote;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public List<Staff> getStaff() {
        return staff;
    }

    @Override
    public String toString(){
        return pais + " (" + sigla + ")";
    }


}
