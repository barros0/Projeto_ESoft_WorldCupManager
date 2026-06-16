package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.Fase;
import pt.ipleiria.worldcup.model.Enums.PapelArbitro;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class Jogo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private static int contador = 1;
    /** Ressincroniza o contador após carregar dados do disco. */
    public static void sincronizarContador(int proximo) {
        if (proximo > contador) contador = proximo;
    }


    private final int id;
    private Equipa equipa1;
    private Equipa equipa2;
    private LocalDate data;
    private LocalTime hora;
    private Estadio estadio;
    private final Fase fase;
    private Grupo grupo; // só na fase de grupos
    private double precoBase;

    private boolean realizado = false;
    private int golos1, golos2;
    private boolean prolongamento = false;
    private int golosProl1, golosProl2;
    private boolean penaltis = false;
    private int penaltis1, penaltis2;

    private final List<EventoJogo> eventos = new ArrayList<>();
    private final EstatisticasJogo estatisticas = new EstatisticasJogo();
    private final Map<PapelArbitro, Arbitro> equipaArbitragem = new EnumMap<>(PapelArbitro.class);
    private Integer ratingArbitragem; // 1..5, após resultado

    // bilhetes vendidos por setor
    private final Map<Setor, Integer> lugaresVendidos = new java.util.HashMap<>();

    public Jogo(Equipa e1, Equipa e2, LocalDate data, LocalTime hora, Estadio estadio, Fase fase, double precoBase) {
        this.id = contador++;
        this.equipa1 = e1; this.equipa2 = e2;
        this.data = data; this.hora = hora;
        this.estadio = estadio; this.fase = fase;
        this.precoBase = precoBase;
    }

    public int getId() { return id; }
    public Equipa getEquipa1() { return equipa1; }
    public Equipa getEquipa2() { return equipa2; }
    public void setEquipa1(Equipa e) { this.equipa1 = e; }
    public void setEquipa2(Equipa e) { this.equipa2 = e; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
    public Estadio getEstadio() { return estadio; }
    public void setEstadio(Estadio estadio) { this.estadio = estadio; }
    public Fase getFase() { return fase; }
    public Grupo getGrupo() { return grupo; }
    public void setGrupo(Grupo grupo) { this.grupo = grupo; }
    public double getPrecoBase() { return precoBase; }
    public void setPrecoBase(double precoBase) { this.precoBase = precoBase; }

    public boolean isRealizado() { return realizado; }
    public void setRealizado(boolean r) { this.realizado = r; }
    public int getGolos1() { return golos1; }
    public int getGolos2() { return golos2; }
    public void setResultado(int g1, int g2) { this.golos1 = g1; this.golos2 = g2; this.realizado = true; }
    public boolean isProlongamento() { return prolongamento; }
    public void setProlongamento(int g1, int g2) { this.prolongamento = true; this.golosProl1 = g1; this.golosProl2 = g2; }
    public int getGolosProl1() { return golosProl1; }
    public int getGolosProl2() { return golosProl2; }
    public boolean isPenaltis() { return penaltis; }
    public void setPenaltis(int p1, int p2) { this.penaltis = true; this.penaltis1 = p1; this.penaltis2 = p2; }
    public int getPenaltis1() { return penaltis1; }
    public int getPenaltis2() { return penaltis2; }

    public List<EventoJogo> getEventos() { return eventos; }
    public EstatisticasJogo getEstatisticas() { return estatisticas; }
    public Map<PapelArbitro, Arbitro> getEquipaArbitragem() { return equipaArbitragem; }
    public Integer getRatingArbitragem() { return ratingArbitragem; }
    public void setRatingArbitragem(Integer r) { this.ratingArbitragem = r; }

    public int getLugaresVendidos(Setor s) { return lugaresVendidos.getOrDefault(s, 0); }
    public void vender(Setor s, int qtd) { lugaresVendidos.merge(s, qtd, Integer::sum); }
    public void libertar(Setor s, int qtd) { lugaresVendidos.merge(s, -qtd, Integer::sum); }
    public int lugaresDisponiveis(Setor s) { return s.getCapacidade() - getLugaresVendidos(s); }

    /** Vencedor (eliminatórias), considerando prolongamento e penáltis. */
    public Equipa getVencedor() {
        if (!realizado) return null;
        int t1 = golos1 + golosProl1, t2 = golos2 + golosProl2;
        if (t1 != t2) return t1 > t2 ? equipa1 : equipa2;
        if (penaltis) return penaltis1 > penaltis2 ? equipa1 : equipa2;
        return null;
    }

    public String descricaoCurta() {
        String e1 = equipa1 != null ? equipa1.getPais() : "?";
        String e2 = equipa2 != null ? equipa2.getPais() : "?";
        return e1 + " vs " + e2 + " — " + fase + (data != null
                ? " — " + data.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "");
    }

    @Override public String toString() { return descricaoCurta(); }
}
