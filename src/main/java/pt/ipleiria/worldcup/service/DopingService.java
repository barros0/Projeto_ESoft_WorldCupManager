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
     * Máximo de 2 testes por jogo (identificado pela data).
     */
    public TesteDoping registarTeste(Jogador jogador, LocalDate data,
                                     ResultadoTeste resultado, Substancia substancia) {
        if (jogador == null)
            throw new IllegalArgumentException("Selecione o jogador.");
        if (data == null)
            throw new IllegalArgumentException("Indique a data do teste.");
        if (resultado == ResultadoTeste.POSITIVO && substancia == null)
            throw new IllegalArgumentException("Selecione a substância detetada.");

        // Máximo 2 testes por data (por jogo)
        long testesMesmaData = ds.getTestes().stream()
                .filter(t -> t.getData().equals(data))
                .count();
        if (testesMesmaData >= 2)
            throw new IllegalStateException(
                    "Já existem 2 testes registados para a data " + data.format(
                    java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    + ". Máximo 2 testes por jogo.");

        TesteDoping t = new TesteDoping(jogador, data, resultado,
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
