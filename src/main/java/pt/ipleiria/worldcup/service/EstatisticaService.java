package pt.ipleiria.worldcup.service;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.model.Enums.*;

import java.util.*;
import java.util.stream.Collectors;

/** Cálculo de estatísticas (acesso guest e acesso restrito). */
public class EstatisticaService {

    private final DataStore ds = DataStore.getInstance();

    private List<Jogo> realizados() { return ds.getJogos().stream().filter(Jogo::isRealizado).toList(); }

    // ---------- Jogos (guest) ----------
    public long jogosComResultado() { return realizados().size(); }

    public Map<Fase, Long> jogosPorFase() {
        Map<Fase, Long> m = new LinkedHashMap<>();
        for (Fase f : Fase.values())
            m.put(f, ds.getJogos().stream().filter(j -> j.getFase() == f).count());
        return m;
    }

    public long jogosComProlongamento() { return realizados().stream().filter(Jogo::isProlongamento).count(); }
    public long jogosComPenaltis() { return realizados().stream().filter(Jogo::isPenaltis).count(); }

    public double mediaGolosPorJogo() {
        return realizados().stream().mapToInt(j -> j.getGolos1() + j.getGolos2()).average().orElse(0);
    }

    private double mediaStat(java.util.function.ToIntFunction<EstatisticasJogo> f) {
        return realizados().stream().map(Jogo::getEstatisticas)
                .filter(EstatisticasJogo::isRegistadas).mapToInt(f).average().orElse(0);
    }

    public double mediaFaltas() { return mediaStat(EstatisticasJogo::totalFaltas); }
    public double mediaCantos() { return mediaStat(EstatisticasJogo::totalCantos); }
    public double mediaRemates() { return mediaStat(EstatisticasJogo::totalRemates); }
    public double mediaPasses() { return mediaStat(EstatisticasJogo::totalPasses); }
    public double mediaForasDeJogo() { return mediaStat(EstatisticasJogo::totalForas); }

    public Map<Equipa, Double> mediaPossePorEquipa() {
        Map<Equipa, List<Integer>> tmp = new HashMap<>();
        for (Jogo j : realizados()) {
            if (!j.getEstatisticas().isRegistadas()) continue;
            tmp.computeIfAbsent(j.getEquipa1(), k -> new ArrayList<>()).add(j.getEstatisticas().getPosseBola(0));
            tmp.computeIfAbsent(j.getEquipa2(), k -> new ArrayList<>()).add(j.getEstatisticas().getPosseBola(1));
        }
        Map<Equipa, Double> out = new LinkedHashMap<>();
        tmp.forEach((e, l) -> out.put(e, l.stream().mapToInt(Integer::intValue).average().orElse(0)));
        return out;
    }

    // ---------- Jogadores (guest) ----------
    public int totalJogadores() { return ds.getEquipas().stream().mapToInt(e -> e.getJogadores().size()).sum(); }

    public Map<Jogador, Long> melhoresMarcadores(int top) {
        return contarEventosPorJogador(TipoEvento.GOLO, top);
    }

    public Map<Jogador, Long> melhoresAssistentes(int top) {
        return contarEventosPorJogador(TipoEvento.ASSISTENCIA, top);
    }

    public Map<Jogador, Long> cartoesPorJogador(TipoEvento tipo) {
        return contarEventosPorJogador(tipo, Integer.MAX_VALUE);
    }

    private Map<Jogador, Long> contarEventosPorJogador(TipoEvento tipo, int top) {
        return ds.getJogos().stream().flatMap(j -> j.getEventos().stream())
                .filter(e -> e.getTipo() == tipo && e.getJogador() != null)
                .collect(Collectors.groupingBy(EventoJogo::getJogador, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Jogador, Long>comparingByValue().reversed())
                .limit(top)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    // ---------- Equipas (guest) ----------
    public Map<Equipa, Long> golosPorEquipa() {
        return ds.getJogos().stream().flatMap(j -> j.getEventos().stream())
                .filter(e -> e.getTipo() == TipoEvento.GOLO && e.getEquipa() != null)
                .collect(Collectors.groupingBy(EventoJogo::getEquipa, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Equipa, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    public Map<Equipa, Long> cartoesPorEquipa(TipoEvento tipo) {
        return ds.getJogos().stream().flatMap(j -> j.getEventos().stream())
                .filter(e -> e.getTipo() == tipo && e.getEquipa() != null)
                .collect(Collectors.groupingBy(EventoJogo::getEquipa, Collectors.counting()));
    }

    // ---------- Estádios (guest) ----------
    public Estadio estadioComMaisJogos() {
        return realizados().stream().filter(j -> j.getEstadio() != null)
                .collect(Collectors.groupingBy(Jogo::getEstadio, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(null);
    }

    public int totalEstadios() { return ds.getEstadios().size(); }

    // ---------- Árbitros (guest) ----------
    public Map<Arbitro, Long> topArbitrosMaisJogos(int top) {
        return ds.getJogos().stream().flatMap(j -> j.getEquipaArbitragem().values().stream())
                .collect(Collectors.groupingBy(a -> a, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<Arbitro, Long>comparingByValue().reversed()).limit(top)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public Map<Arbitro, Double> topArbitrosMelhorRating(int top) {
        Map<Arbitro, List<Integer>> tmp = new HashMap<>();
        for (Jogo j : ds.getJogos()) {
            if (j.getRatingArbitragem() == null) continue;
            for (Arbitro a : j.getEquipaArbitragem().values())
                tmp.computeIfAbsent(a, k -> new ArrayList<>()).add(j.getRatingArbitragem());
        }
        return tmp.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), e.getValue().stream().mapToInt(Integer::intValue).average().orElse(0)))
                .sorted(Map.Entry.<Arbitro, Double>comparingByValue().reversed()).limit(top)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
    }

    public int totalArbitros() { return ds.getArbitros().size(); }

    // ---------- Doping (restrito) ----------
    public long suspensosPorDoping() {
        return ds.getEquipas().stream().flatMap(e -> e.getJogadores().stream())
                .filter(j -> j.getEstado() == EstadoJogador.SUSPENSO_DOPING).count();
    }

    public long totalTestes() { return ds.getTestes().size(); }

    public long testesPorResultado(ResultadoTeste r) {
        return ds.getTestes().stream().filter(t -> t.getResultado() == r).count();
    }

    // ---------- Bilhetes (restrito) ----------
    private List<Bilhete> ativos() {
        return ds.getBilhetes().stream().filter(b -> b.getEstado() == EstadoBilhete.ATIVO).toList();
    }

    public int bilhetesVendidos() { return ativos().stream().mapToInt(Bilhete::getQuantidade).sum(); }

    public Map<Jogo, Integer> bilhetesPorJogo() {
        Map<Jogo, Integer> m = new LinkedHashMap<>();
        for (Bilhete b : ativos()) m.merge(b.getJogo(), b.getQuantidade(), Integer::sum);
        return m;
    }

    public Map<Jogo, Double> valorPorJogo() {
        Map<Jogo, Double> m = new LinkedHashMap<>();
        for (Bilhete b : ativos()) m.merge(b.getJogo(), b.getValorTotal(), Double::sum);
        return m;
    }

    public double valorTotalArrecadado() { return ativos().stream().mapToDouble(Bilhete::getValorTotal).sum(); }

    public Map<String, Integer> bilhetesPorSetor() {
        Map<String, Integer> m = new TreeMap<>();
        for (Bilhete b : ativos()) m.merge("Setor " + b.getSetor().getNome(), b.getQuantidade(), Integer::sum);
        return m;
    }

    public long bilhetesCancelados() {
        return ds.getBilhetes().stream().filter(b -> b.getEstado() == EstadoBilhete.CANCELADO).count();
    }

    public double valorReembolsado() {
        return ds.getBilhetes().stream().filter(Bilhete::isReembolsado).mapToDouble(Bilhete::getValorTotal).sum();
    }
}
