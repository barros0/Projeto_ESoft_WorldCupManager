package pt.ipleiria.worldcup.service;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;

import java.time.LocalDate;

public class EquipaService {

    private final DataStore ds = DataStore.getInstance();

    public void adicionarJogador(Equipa equipa, String nome, LocalDate nascimento,
                                 String posicao, int camisola) {
        boolean repetida = equipa.getJogadores().stream().anyMatch(j -> j.getNumeroCamisola() == camisola);
        if (repetida) throw new IllegalStateException("O nº de camisola " + camisola + " já está em uso nessa equipa.");
        equipa.getJogadores().add(new Jogador(nome, nascimento, posicao, camisola, equipa));
    }

    public void removerJogador(Jogador j) {
        boolean comEventos = ds.getJogos().stream()
                .flatMap(g -> g.getEventos().stream())
                .anyMatch(e -> e.getJogador() == j);
        if (comEventos) throw new IllegalStateException("Não é permitido remover um jogador com eventos associados.");
        j.getEquipa().getJogadores().remove(j);
    }

    public void adicionarStaff(Equipa equipa, String nome, String cargo) {
        equipa.getStaff().add(new Staff(nome, cargo, equipa));
    }

    public void removerStaff(Staff s) { s.getEquipa().getStaff().remove(s); }

    /** @return true se já existe outra equipa nesse hotel (requer confirmação). */
    public boolean hotelOcupado(Hotel h, Equipa equipa) {
        return ds.getAlojamentos().stream().anyMatch(a -> a.getHotel() == h && a.getEquipa() != equipa);
    }

    public void atribuirAlojamento(Equipa equipa, Jogo jogo, Hotel hotel) {
        ds.getAlojamentos().add(new Alojamento(equipa, jogo, hotel));
    }

    /** Edita um alojamento já atribuído (muda o jogo e/ou o hotel). */
    public void editarAlojamento(Alojamento a, Jogo jogo, Hotel hotel) {
        if (jogo == null) throw new IllegalArgumentException("Selecione o jogo.");
        if (hotel == null) throw new IllegalArgumentException("Selecione o hotel.");
        a.setJogo(jogo);
        a.setHotel(hotel);
    }

    public void removerAlojamento(Alojamento a) {
        ds.getAlojamentos().remove(a);
    }
}
