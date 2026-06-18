package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.EstadoJogador;
import java.time.LocalDate;

public class Jogador implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome;
    private final LocalDate dataNascimento;
    private final String posicao;
    private final int numeroCamisola; // único por equipa
    private final Equipa equipa;
    private EstadoJogador estado = EstadoJogador.DISPONIVEL;

    public Jogador(String nome, LocalDate dataNascimento,
                   String posicao, int numeroCamisola, Equipa equipa) {
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.posicao = posicao;
        this.numeroCamisola = numeroCamisola;
        this.equipa = equipa;
    }

    public String getNome() { return nome; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public String getPosicao() { return posicao; }
    public int getNumeroCamisola() { return numeroCamisola; }
    public Equipa getEquipa() { return equipa; }
    public EstadoJogador getEstado() { return estado; }
    public void setEstado(EstadoJogador estado) { this.estado = estado; }

    @Override public String toString() { return nome + " (" + numeroCamisola + ")"; }
}
