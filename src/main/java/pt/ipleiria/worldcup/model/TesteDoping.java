package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.ResultadoTeste;
import java.time.LocalDate;


public class TesteDoping implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    private final Jogador jogador;
    private final LocalDate data;
    private final ResultadoTeste resultado;
    private final Substancia substancia;
    private String castigoAplicado = "-";

    public TesteDoping(Jogador jogador, LocalDate data, ResultadoTeste resultado, Substancia substancia) {
        this.jogador = jogador;
        this.data = data;
        this.resultado = resultado;
        this.substancia = substancia;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public LocalDate getData() {
        return data;
    }

    public ResultadoTeste getResultado() {
        return resultado;
    }

    public Substancia getSubstancia() {
        return substancia;
    }

    public String getCastigoAplicado() {
        return castigoAplicado;
    }

    public void setCastigoAplicado(String castigoAplicado) {
        this.castigoAplicado = castigoAplicado;
    }
}
