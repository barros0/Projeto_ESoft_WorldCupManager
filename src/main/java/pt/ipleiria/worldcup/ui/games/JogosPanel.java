package pt.ipleiria.worldcup.ui.games;

import javax.swing.*;
import java.awt.Component;

public class JogosPanel extends JTabbedPane {
    public interface Atualizavel { void atualizar(); }

    public JogosPanel() {
        addTab("Campeonato", new ConfigurarCampeonatoPanel());
        addTab("Equipas", new RegistoEquipasPanel());
        addTab("Sorteio", new SorteioPanel());
        addTab("Calendário", new CalendarioPanel());
        addTab("Classificação Grupos", new ClassificacaoPanel());
        addTab("Eliminatórias", new pt.ipleiria.worldcup.ui.guest.GuestPanel.EliminatoriasView());
        addTab("Criar Jogo", new CriarJogoPanel());
        addTab("Resultados", new RegistarResultadoPanel());
        addTab("Eventos", new EventosPanel());
        addTab("Estatísticas do Jogo", new EstatisticasJogoPanel());
        addTab("Árbitros", new ArbitrosPanel());
        addTab("Arbitragem", new EquipaArbitragemPanel());
        addTab("Estádios", new EstadiosPanel());
        addChangeListener(e -> atualizarTodas());
    }

    /**
     * Atualiza TODAS as tabs, não apenas a selecionada.
     * Necessário porque ações numa tab (ex.: criar jogo) podem afetar
     * o conteúdo de outras tabs (ex.: Eliminatórias, Classificação) mesmo
     * que estas não estejam visíveis no momento.
     */
    private void atualizarTodas() {
        for (int i = 0; i < getTabCount(); i++) {
            Component c = getComponentAt(i);
            if (c instanceof Atualizavel a) a.atualizar();
        }
    }
}
