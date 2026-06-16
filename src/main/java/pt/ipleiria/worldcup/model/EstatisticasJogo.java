package pt.ipleiria.worldcup.model;

/** Estatísticas de um jogo por equipa (índice 0 = equipa1, 1 = equipa2). */
public class EstatisticasJogo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final int[] faltas = new int[2];
    private final int[] cantos = new int[2];
    private final int[] remates = new int[2];
    private final int[] passes = new int[2];
    private final int[] posseBola = new int[2];
    private final int[] forasDeJogo = new int[2];
    private boolean registadas = false;

    public void registar(int idx, int faltas, int cantos, int remates, int passes, int posse, int foras) {
        this.faltas[idx] = faltas; this.cantos[idx] = cantos; this.remates[idx] = remates;
        this.passes[idx] = passes; this.posseBola[idx] = posse; this.forasDeJogo[idx] = foras;
        this.registadas = true;
    }

    public boolean isRegistadas() { return registadas; }
    public int getFaltas(int i) { return faltas[i]; }
    public int getCantos(int i) { return cantos[i]; }
    public int getRemates(int i) { return remates[i]; }
    public int getPasses(int i) { return passes[i]; }
    public int getPosseBola(int i) { return posseBola[i]; }
    public int getForasDeJogo(int i) { return forasDeJogo[i]; }
    public int totalFaltas() { return faltas[0] + faltas[1]; }
    public int totalCantos() { return cantos[0] + cantos[1]; }
    public int totalRemates() { return remates[0] + remates[1]; }
    public int totalPasses() { return passes[0] + passes[1]; }
    public int totalForas() { return forasDeJogo[0] + forasDeJogo[1]; }
}
