package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.Fase;
import java.util.ArrayList;
import java.util.List;

public class Campeonato implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    public static final int APURADOS_POR_GRUPO = 2;

    private final String nome;
    private final int numGrupos; // 4 ou 8
    private final List<Grupo> grupos = new ArrayList<>();
    private boolean sorteioRealizado = false;
    private boolean calendarioGerado = false;

    public Campeonato(String nome, int numGrupos) {
        if (numGrupos != 4 && numGrupos != 8)
            throw new IllegalArgumentException("O número de grupos deve ser 4 ou 8.");
        this.nome = nome;
        this.numGrupos = numGrupos;
        for (int i = 0; i < numGrupos; i++) grupos.add(new Grupo(String.valueOf((char) ('A' + i))));
    }

    public String getNome() { return nome; }
    public int getNumGrupos() { return numGrupos; }
    public List<Grupo> getGrupos() { return grupos; }
    public int getTotalEquipas() { return numGrupos * 4; }
    public int getEquipasApuradas() { return numGrupos * APURADOS_POR_GRUPO; }

    /** Fases eliminatórias geradas automaticamente com base nas equipas apuradas. */
    public List<Fase> getFasesEliminatorias() {
        List<Fase> fases = new ArrayList<>();
        if (getEquipasApuradas() == 16) fases.add(Fase.OITAVOS);
        fases.add(Fase.QUARTOS);
        fases.add(Fase.MEIAS);
        fases.add(Fase.FINAL);
        return fases;
    }

    public boolean isSorteioRealizado() { return sorteioRealizado; }
    public void setSorteioRealizado(boolean v) { this.sorteioRealizado = v; }
    public boolean isCalendarioGerado() { return calendarioGerado; }
    public void setCalendarioGerado(boolean v) { this.calendarioGerado = v; }

    @Override public String toString() { return nome; }
}
