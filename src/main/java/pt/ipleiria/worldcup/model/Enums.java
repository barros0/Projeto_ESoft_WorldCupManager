package pt.ipleiria.worldcup.model;

public final class Enums {
    private Enums() {}

    public enum Confederacao { UEFA, CONMEBOL, CONCACAF, CAF, AFC, OFC }

    public enum Fase {
        GRUPOS("Fase de Grupos"), OITAVOS("Oitavos de Final"), QUARTOS("Quartos de Final"),
        MEIAS("Meias Finais"), FINAL("Final");
        private final String label;
        Fase(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    public enum EstadoJogador { DISPONIVEL, LESIONADO, SUSPENSO_JOGO, SUSPENSO_DOPING }

    public enum EstadoBilhete { ATIVO, CANCELADO }

    public enum ResultadoTeste { POSITIVO, NEGATIVO, DUVIDOSO }

    public enum TipoEvento { GOLO, ASSISTENCIA, CARTAO_AMARELO, CARTAO_VERMELHO, SUBSTITUICAO }

    public enum Perfil { ADMIN, GESTOR_JOGOS, GESTOR_EQUIPA, GESTOR_DOPING, VENDEDOR_BILHETES, GUEST }

    public enum PapelArbitro { PRINCIPAL, ASSISTENTE_1, ASSISTENTE_2, QUARTO_ARBITRO, VAR }
}
