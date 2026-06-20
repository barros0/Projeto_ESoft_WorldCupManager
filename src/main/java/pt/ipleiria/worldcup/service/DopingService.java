package pt.ipleiria.worldcup.service;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.model.Enums.EstadoJogador;
import pt.ipleiria.worldcup.model.Enums.ResultadoTeste;

import java.time.LocalDate;

public class DopingService {

    private final DataStore ds = DataStore.getInstance();

    /**
     * Guarda o teste; em caso positivo, o sistema atribui automaticamente
     * o castigo apropriado consoante a substância. Dois resultados duvidosos
     * do mesmo jogador implicam castigo automático.
     * Máximo de 2 testes por jogo.
     */
    public TesteDoping registarTeste(Jogador jogador, Jogo jogo,
                                     ResultadoTeste resultado, Substancia substancia) {
        if (jogador == null)
            throw new IllegalArgumentException("Selecione o jogador.");
        if (jogo == null)
            throw new IllegalArgumentException("Selecione o jogo.");
        if (resultado == ResultadoTeste.POSITIVO && substancia == null)
            throw new IllegalArgumentException("Selecione a substância detetada.");

        // Máximo 2 testes por jogo (não por data — pode haver vários jogos no mesmo dia)
        long testesNesseJogo = ds.getTestes().stream()
                .filter(t -> t.getJogo() == jogo)
                .count();
        if (testesNesseJogo >= 2)
            throw new IllegalStateException(
                    "Já existem 2 testes registados para este jogo. Máximo 2 testes por jogo.");

        TesteDoping t = new TesteDoping(jogador, jogo, jogo.getData(), resultado,
                resultado == ResultadoTeste.POSITIVO ? substancia : null);
        ds.getTestes().add(t);

        if (resultado == ResultadoTeste.POSITIVO) {
            t.setCastigoAplicado(substancia.getCastigo());
            jogador.setEstado(EstadoJogador.SUSPENSO_DOPING);
        } else if (resultado == ResultadoTeste.DUVIDOSO) {
            long duvidosos = ds.getTestes().stream()
                    .filter(x -> x.getJogador() == jogador && x.getResultado() == ResultadoTeste.DUVIDOSO)
                    .count();
            if (duvidosos >= 2) {
                t.setCastigoAplicado("Suspensão automática (2 resultados duvidosos)");
                jogador.setEstado(EstadoJogador.SUSPENSO_DOPING);
            }
        }
        return t;
    }
}
