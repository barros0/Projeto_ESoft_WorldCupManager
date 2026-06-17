package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.EstadoBilhete;

public class Bilhete implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private static int contador = 1;
    /** Ressincroniza o contador após carregar dados do disco. */
    public static void sincronizarContador(int proximo) {
        if (proximo > contador) contador = proximo;
    }


    private final String codigo;
    private final String comprador;
    private final Jogo jogo;
    private final Setor setor;
    private final int quantidade;
    private final double valorTotal;
    private EstadoBilhete estado = EstadoBilhete.ATIVO;
    private boolean reembolsado = false;

    public Bilhete(String comprador, Jogo jogo, Setor setor, int quantidade, double valorTotal) {
        this.codigo = "BLH-" + contador++;
        this.comprador = comprador;
        this.jogo = jogo;
        this.setor = setor;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
    }

    public String getCodigo() { return codigo; }
    public String getComprador() { return comprador; }
    public Jogo getJogo() { return jogo; }
    public Setor getSetor() { return setor; }
    public int getQuantidade() { return quantidade; }
    public double getValorTotal() { return valorTotal; }
    public EstadoBilhete getEstado() { return estado; }
    public void cancelar() { this.estado = EstadoBilhete.CANCELADO; this.reembolsado = true; }
    public boolean isReembolsado() { return reembolsado; }
}
