package pt.ipleiria.worldcup.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Grupo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final String nome; // A, B, C...
    private final List<Equipa> equipas = new ArrayList<>();
    private final List<Jogo> jogos = new ArrayList<>();

    public Grupo(String nome) { this.nome = nome; }

    public String getNome() { return nome; }
    public List<Equipa> getEquipas() { return equipas; }
    public List<Jogo> getJogos() { return jogos; }

    /** Linha de classificação de uma equipa no grupo. */
    public static class Linha {
        public final Equipa equipa;
        public int jogos, vitorias, empates, derrotas, golosMarcados, golosSofridos, pontos;
        public Linha(Equipa e) { this.equipa = e; }
    }

    /** Classificação: vitória 3, empate 1, derrota 0; desempate por golos marcados. */
    public List<Linha> classificacao() {
        List<Linha> linhas = new ArrayList<>();
        for (Equipa e : equipas) {
            Linha l = new Linha(e);
            for (Jogo j : jogos) {
                if (!j.isRealizado()) continue;
                boolean casa = j.getEquipa1() == e, fora = j.getEquipa2() == e;
                if (!casa && !fora) continue;
                int gm = casa ? j.getGolos1() : j.getGolos2();
                int gs = casa ? j.getGolos2() : j.getGolos1();
                l.jogos++; l.golosMarcados += gm; l.golosSofridos += gs;
                if (gm > gs) { l.vitorias++; l.pontos += 3; }
                else if (gm == gs) { l.empates++; l.pontos += 1; }
                else l.derrotas++;
            }
            linhas.add(l);
        }
        linhas.sort(Comparator.comparingInt((Linha l) -> l.pontos).reversed()
                .thenComparing(Comparator.comparingInt((Linha l) -> l.golosMarcados).reversed()));
        return linhas;
    }

    @Override public String toString() { return "Grupo " + nome; }
}
