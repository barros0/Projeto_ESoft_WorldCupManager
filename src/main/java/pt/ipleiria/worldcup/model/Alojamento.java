package pt.ipleiria.worldcup.model;

public class Alojamento implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final Equipa equipa;
    private Jogo jogo;
    private Hotel hotel;

    public Alojamento(Equipa equipa, Jogo jogo, Hotel hotel) {
        this.equipa = equipa;
        this.jogo = jogo;
        this.hotel = hotel;
    }

    public Equipa getEquipa() { return equipa; }
    public Jogo getJogo() { return jogo; }
    public void setJogo(Jogo jogo) { this.jogo = jogo; }
    public Hotel getHotel() { return hotel; }
    public void setHotel(Hotel hotel) { this.hotel = hotel; }
}
