package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Arbitro;
import pt.ipleiria.worldcup.model.Jogo;
import pt.ipleiria.worldcup.model.Enums.PapelArbitro;
import pt.ipleiria.worldcup.service.JogoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class EquipaArbitragemPanel extends JPanel implements JogosPanel.Atualizavel {

    private final JogoService service = new JogoService();
    private final JComboBox<Jogo> cmbJogo = new JComboBox<>();
    private final Map<PapelArbitro, JComboBox<Arbitro>> combos = new EnumMap<>(PapelArbitro.class);

    public EquipaArbitragemPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Equipa de Arbitragem (5 árbitros por jogo)"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Jogo:"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(cmbJogo, c);

        String[] labels = { "Árbitro Principal", "Assistente 1", "Assistente 2", "Quarto Árbitro", "VAR" };
        int y = 1;
        for (PapelArbitro p : PapelArbitro.values()) {
            JComboBox<Arbitro> cmb = new JComboBox<>();
            combos.put(p, cmb);
            c.gridx = 0; c.gridy = y; c.weightx = 0;
            form.add(new JLabel(labels[y - 1] + ":"), c);
            c.gridx = 1; c.weightx = 1;
            form.add(cmb, c);
            y++;
        }

        JButton btn = new JButton("Guardar equipa de arbitragem");
        btn.addActionListener(e -> guardar());
        JPanel sul = new JPanel();
        sul.add(btn);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        centro.add(sul, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    private void guardar() {
        try {
            Jogo j = (Jogo) cmbJogo.getSelectedItem();
            if (j == null) throw new IllegalArgumentException("Selecione um jogo.");
            Map<PapelArbitro, Arbitro> equipa = new EnumMap<>(PapelArbitro.class);
            for (var e : combos.entrySet()) equipa.put(e.getKey(), (Arbitro) e.getValue().getSelectedItem());
            service.atribuirEquipaArbitragem(j, equipa);
            Ui.info(this, "Equipa de arbitragem atribuída.");
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        cmbJogo.removeAllItems();
        for (Jogo j : DataStore.getInstance().getJogos())
            if (!j.isRealizado() && j.getEquipa1() != null && j.getEquipa2() != null) cmbJogo.addItem(j);
        for (var cmb : combos.values()) {
            cmb.removeAllItems();
            for (Arbitro a : DataStore.getInstance().getArbitros()) cmb.addItem(a);
        }
    }
}
