package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.TipoEvento;

public class EventoJogo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final TipoEvento tipo;
    private final Equipa equipa;
    private final Jogador jogador;
    private final int minuto;

    public EventoJogo(TipoEvento tipo, Equipa equipa, Jogador jogador, int minuto) {
        this.tipo = tipo;
        this.equipa = equipa;
        this.jogador = jogador;
        this.minuto = minuto;
    }

    public TipoEvento getTipo() { return tipo; }
    public Equipa getEquipa() { return equipa; }
    public Jogador getJogador() { return jogador; }
    public int getMinuto() { return minuto; }
}
