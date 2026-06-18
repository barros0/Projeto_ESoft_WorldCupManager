package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.TipoEvento;

public class EventoJogo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final TipoEvento tipo;
    private final Equipa equipa;
    private final Jogador jogador;       // sai (ou marca golo, cartão, etc.)
    private final Jogador jogadorEntra;  // entra — só para SUBSTITUICAO
    private final int minuto;

    public EventoJogo(TipoEvento tipo, Equipa equipa, Jogador jogador, int minuto) {
        this(tipo, equipa, jogador, null, minuto);
    }

    public EventoJogo(TipoEvento tipo, Equipa equipa, Jogador jogador,
                      Jogador jogadorEntra, int minuto) {
        this.tipo = tipo;
        this.equipa = equipa;
        this.jogador = jogador;
        this.jogadorEntra = jogadorEntra;
        this.minuto = minuto;
    }

    public TipoEvento getTipo() { return tipo; }
    public Equipa getEquipa() { return equipa; }
    public Jogador getJogador() { return jogador; }
    public Jogador getJogadorEntra() { return jogadorEntra; }
    public int getMinuto() { return minuto; }
}
