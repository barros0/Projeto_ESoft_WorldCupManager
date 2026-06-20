package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.ResultadoTeste;
import java.time.LocalDate;

public class TesteDoping implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final Jogador jogador;
    private final Jogo jogo;
    private final LocalDate data;
    private final ResultadoTeste resultado;
    private final Substancia substancia; // só se positivo
    private String castigoAplicado = "-";

    public TesteDoping(Jogador jogador, Jogo jogo, LocalDate data, ResultadoTeste resultado, Substancia substancia) {
        this.jogador = jogador;
        this.jogo = jogo;
        this.data = data;
        this.resultado = resultado;
        this.substancia = substancia;
    }

    public Jogador getJogador() { return jogador; }
    public Jogo getJogo() { return jogo; }
    public LocalDate getData() { return data; }
    public ResultadoTeste getResultado() { return resultado; }
    public Substancia getSubstancia() { return substancia; }
    public String getCastigoAplicado() { return castigoAplicado; }
    public void setCastigoAplicado(String castigo) { this.castigoAplicado = castigo; }
}
