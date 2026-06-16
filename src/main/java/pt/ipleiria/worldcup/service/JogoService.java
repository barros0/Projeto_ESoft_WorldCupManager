package pt.ipleiria.worldcup.service;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.model.Enums.*;

import java.util.List;
import java.util.Map;

public class JogoService {

    private final DataStore ds = DataStore.getInstance();

    /** Regista o resultado. Nas eliminatórias permite prolongamento e penáltis em caso de empate. */
    public void registarResultado(Jogo j, int g1, int g2,
                                  Integer prol1, Integer prol2, Integer pen1, Integer pen2) {
        if (j.isRealizado()) throw new IllegalStateException("O resultado deste jogo já foi registado.");
        j.setResultado(g1, g2);
        if (j.getFase() != Fase.GRUPOS && g1 == g2) {
            if (prol1 != null && prol2 != null) j.setProlongamento(prol1, prol2);
            int t1 = g1 + (prol1 == null ? 0 : prol1), t2 = g2 + (prol2 == null ? 0 : prol2);
            if (t1 == t2) {
                if (pen1 == null || pen2 == null || pen1.intValue() == pen2.intValue())
                    throw new IllegalArgumentException("Empate nas eliminatórias: registe penáltis com vencedor.");
                j.setPenaltis(pen1, pen2);
            }
        }
        avancarVencedor(j);
    }

    /** Passa automaticamente o vencedor para o jogo seguinte da fase eliminatória. */
    private void avancarVencedor(Jogo j) {
        if (j.getFase() == Fase.GRUPOS || j.getFase() == Fase.FINAL) return;
        Equipa vencedor = j.getVencedor();
        if (vencedor == null) return;
        Fase proxima = switch (j.getFase()) {
            case OITAVOS -> Fase.QUARTOS;
            case QUARTOS -> Fase.MEIAS;
            case MEIAS -> Fase.FINAL;
            default -> null;
        };
        if (proxima == null) return;
        for (Jogo next : ds.getJogos()) {
            if (next.getFase() != proxima || next.isRealizado()) continue;
            if (next.getEquipa1() == null) { next.setEquipa1(vencedor); return; }
            if (next.getEquipa2() == null) { next.setEquipa2(vencedor); return; }
        }
    }

    /** Regista um evento; aplica suspensão automática (2 amarelos no jogo / vermelho direto). */
    public void registarEvento(Jogo j, TipoEvento tipo, Equipa equipa, Jogador jogador, int minuto) {
        j.getEventos().add(new EventoJogo(tipo, equipa, jogador, minuto));
        if (jogador == null) return;
        if (tipo == TipoEvento.CARTAO_VERMELHO) {
            jogador.setEstado(EstadoJogador.SUSPENSO_JOGO);
        } else if (tipo == TipoEvento.CARTAO_AMARELO) {
            long amarelos = j.getEventos().stream()
                    .filter(e -> e.getJogador() == jogador && e.getTipo() == TipoEvento.CARTAO_AMARELO).count();
            if (amarelos >= 2) jogador.setEstado(EstadoJogador.SUSPENSO_JOGO);
        }
    }

    /** Atribui equipa de arbitragem (5 árbitros, sem repetidos, nacionalidade != equipas). */
    public void atribuirEquipaArbitragem(Jogo j, Map<PapelArbitro, Arbitro> equipa) {
        if (j.isRealizado()) throw new IllegalStateException("A equipa de arbitragem é atribuída antes do jogo.");
        if (equipa.size() != PapelArbitro.values().length || equipa.containsValue(null))
            throw new IllegalArgumentException("Cada jogo tem obrigatoriamente 5 árbitros.");
        long distintos = equipa.values().stream().distinct().count();
        if (distintos != 5) throw new IllegalArgumentException("Não é permitido repetir árbitros na equipa de arbitragem.");
        for (Arbitro a : equipa.values()) {
            if (mesmaNacionalidade(a, j.getEquipa1()) || mesmaNacionalidade(a, j.getEquipa2()))
                throw new IllegalArgumentException("O árbitro " + a.getNome()
                        + " tem a nacionalidade de uma das equipas do jogo.");
        }
        j.getEquipaArbitragem().clear();
        j.getEquipaArbitragem().putAll(equipa);
    }

    private boolean mesmaNacionalidade(Arbitro a, Equipa e) {
        if (e == null) return false;
        return a.getNacionalidade().equalsIgnoreCase(e.getPais())
                || a.getNacionalidade().equalsIgnoreCase(e.getSigla());
    }

    public void atribuirRating(Jogo j, int rating) {
        if (!j.isRealizado()) throw new IllegalStateException("O rating só pode ser atribuído após o resultado.");
        if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating entre 1 e 5.");
        j.setRatingArbitragem(rating);
    }

    public void adicionarArbitro(String nome, String nacionalidade) {
        boolean existe = ds.getArbitros().stream().anyMatch(a -> a.getNome().equalsIgnoreCase(nome));
        if (existe) throw new IllegalStateException("Esse árbitro já existe no sistema.");
        ds.getArbitros().add(new Arbitro(nome, nacionalidade));
    }

    public void removerArbitro(Arbitro a) {
        boolean atribuido = ds.getJogos().stream().anyMatch(j -> j.getEquipaArbitragem().containsValue(a));
        if (atribuido) throw new IllegalStateException("O árbitro está atribuído a um jogo.");
        ds.getArbitros().remove(a);
    }

    public void adicionarEstadio(Estadio e) {
        boolean existe = ds.getEstadios().stream().anyMatch(x -> x.getNome().equalsIgnoreCase(e.getNome()));
        if (existe) throw new IllegalStateException("Esse estádio já existe no sistema.");
        ds.getEstadios().add(e);
    }

    public void removerEstadio(Estadio e) {
        boolean emUso = ds.getJogos().stream().anyMatch(j -> j.getEstadio() == e);
        if (emUso) throw new IllegalStateException("Não é permitido remover um estádio atribuído a um jogo.");
        ds.getEstadios().remove(e);
    }

    public List<Jogo> jogosPorFase(Fase f) {
        return ds.getJogos().stream().filter(j -> j.getFase() == f).toList();
    }
}
