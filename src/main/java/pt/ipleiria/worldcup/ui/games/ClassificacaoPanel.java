package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Campeonato;
import pt.ipleiria.worldcup.model.Grupo;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClassificacaoPanel extends JPanel implements JogosPanel.Atualizavel {

    private final JPanel lista = new JPanel();

    public ClassificacaoPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Classificação — Fase de Grupos"), BorderLayout.NORTH);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        add(new JScrollPane(lista), BorderLayout.CENTER);
        atualizar();
    }

    @Override public void atualizar() {
        lista.removeAll();
        Campeonato c = DataStore.getInstance().getCampeonato();
        if (c == null) { lista.add(new JLabel("  Configure primeiro o campeonato.")); }
        else for (Grupo g : c.getGrupos()) {
            DefaultTableModel m = Ui.modelComIcone("", "#", "Equipa", "J", "V", "E", "D", "GM", "GS", "Pts");
            int pos = 1;
            for (Grupo.Linha l : g.classificacao())
                m.addRow(new Object[]{ Ui.imagem(l.equipa.getBandeira(), 28, 18), pos++, l.equipa.getPais(), l.jogos, l.vitorias,
                        l.empates, l.derrotas, l.golosMarcados, l.golosSofridos, l.pontos });
            JTable t = new JTable(m);
            t.setRowHeight(22);
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(BorderFactory.createTitledBorder("Grupo " + g.getNome()
                    + "  (apuram-se os 2 primeiros — desempate: golos marcados)"));
            p.add(t.getTableHeader(), BorderLayout.NORTH);
            p.add(t, BorderLayout.CENTER);
            lista.add(p);
        }
        lista.revalidate();
        lista.repaint();
    }
}
