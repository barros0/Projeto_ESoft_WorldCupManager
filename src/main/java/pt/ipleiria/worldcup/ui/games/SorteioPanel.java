package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Campeonato;
import pt.ipleiria.worldcup.model.Equipa;
import pt.ipleiria.worldcup.model.Grupo;
import pt.ipleiria.worldcup.service.CampeonatoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;

public class SorteioPanel extends JPanel implements JogosPanel.Atualizavel {

    private final CampeonatoService service = new CampeonatoService();
    private final JPanel grelha = new JPanel(new GridLayout(0, 4, 10, 10));
    private final JButton btnSortear = new JButton("Realizar Sorteio");

    public SorteioPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Sorteio dos Grupos"), BorderLayout.NORTH);

        JTextArea restricoes = new JTextArea(
                "Restrições do sorteio:\n • 1 equipa por pote por grupo\n • Máx. 1 equipa por confederação por grupo");
        restricoes.setEditable(false);
        restricoes.setBackground(Ui.LIGHT);
        restricoes.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        btnSortear.addActionListener(e -> sortear());

        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        centro.add(restricoes, BorderLayout.NORTH);
        centro.add(new JScrollPane(grelha), BorderLayout.CENTER);
        JPanel sul = new JPanel();
        sul.add(btnSortear);
        centro.add(sul, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    private void sortear() {
        try {
            service.realizarSorteio();
            Ui.info(this, "Sorteio realizado com sucesso!");
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        grelha.removeAll();
        Campeonato c = DataStore.getInstance().getCampeonato();
        if (c != null) {
            for (Grupo g : c.getGrupos()) {
                JPanel p = new JPanel(new GridLayout(0, 1, 0, 2));
                p.setBackground(Color.WHITE);
                p.setBorder(BorderFactory.createTitledBorder("Grupo " + g.getNome()));
                for (Equipa e : g.getEquipas()) {
                    JLabel l = new JLabel("P" + e.getPote() + "  " + e.getPais()
                            + " (" + e.getConfederacao() + ")");
                    ImageIcon bandeira = Ui.imagem(e.getBandeira(), 28, 18);
                    if (bandeira != null) l.setIcon(bandeira);
                    l.setIconTextGap(8);
                    l.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                    p.add(l);
                }
                if (g.getEquipas().isEmpty()) p.add(new JLabel("  —"));
                grelha.add(p);
            }
            int total = DataStore.getInstance().getEquipas().size();
            btnSortear.setEnabled(!c.isSorteioRealizado());
            btnSortear.setText(c.isSorteioRealizado() ? "Sorteio já realizado"
                    : total == c.getTotalEquipas() ? "Realizar Sorteio"
                    : "Sorteio bloqueado — " + total + "/" + c.getTotalEquipas());
        } else {
            btnSortear.setEnabled(false);
            btnSortear.setText("Configure primeiro o campeonato");
        }
        grelha.revalidate();
        grelha.repaint();
    }
}
