package pt.ipleiria.worldcup.ui.games;

import javax.swing.*;

public class JogosPanel extends JTabbedPane {
    public interface Atualizavel { void atualizar(); }

    public JogosPanel() {
        addTab("Campeonato", new ConfigurarCampeonatoPanel());
        addTab("Equipas", new RegistoEquipasPanel());
        addTab("Sorteio", new SorteioPanel());
        addTab("Calendário", new CalendarioPanel());
        addTab("Classificação", new ClassificacaoPanel());
        addTab("Criar Jogo", new CriarJogoPanel());
        addTab("Resultados", new RegistarResultadoPanel());
        addTab("Eventos", new EventosPanel());
        addTab("Estatísticas do Jogo", new EstatisticasJogoPanel());
        addTab("Árbitros", new ArbitrosPanel());
        addTab("Arbitragem", new EquipaArbitragemPanel());
        addTab("Estádios", new EstadiosPanel());
        addChangeListener(e -> {
            var c = getSelectedComponent();
            if (c instanceof Atualizavel a) a.atualizar();
        });
    }
}
