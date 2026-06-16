package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Jogo;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;

public class EstatisticasJogoPanel extends JPanel implements JogosPanel.Atualizavel {

    private final JComboBox<Jogo> cmbJogo = new JComboBox<>();
    private final JSpinner[][] campos = new JSpinner[6][2];
    private static final String[] NOMES = { "Faltas", "Cantos", "Remates", "Passes", "Posse de bola (%)", "Foras de jogo" };

    public EstatisticasJogoPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Estatísticas do Jogo"), BorderLayout.NORTH);

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Jogo:"));
        topo.add(cmbJogo);

        JPanel grelha = new JPanel(new GridLayout(NOMES.length, 3, 10, 6));
        grelha.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        for (int i = 0; i < NOMES.length; i++) {
            campos[i][0] = new JSpinner(new SpinnerNumberModel(0, 0, 1500, 1));
            campos[i][1] = new JSpinner(new SpinnerNumberModel(0, 0, 1500, 1));
            grelha.add(campos[i][0]);
            grelha.add(new JLabel(NOMES[i], SwingConstants.CENTER));
            grelha.add(campos[i][1]);
        }

        JButton btn = new JButton("Guardar estatísticas");
        btn.addActionListener(e -> guardar());
        JPanel sul = new JPanel();
        sul.add(btn);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(topo, BorderLayout.NORTH);
        centro.add(grelha, BorderLayout.CENTER);
        centro.add(sul, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    private void guardar() {
        try {
            Jogo j = (Jogo) cmbJogo.getSelectedItem();
            if (j == null) throw new IllegalArgumentException("Selecione um jogo.");
            int posse1 = (int) campos[4][0].getValue(), posse2 = (int) campos[4][1].getValue();
            if (posse1 + posse2 != 100 && posse1 + posse2 != 0)
                throw new IllegalArgumentException("A posse de bola das duas equipas deve somar 100%.");
            for (int lado = 0; lado < 2; lado++)
                j.getEstatisticas().registar(lado,
                        (int) campos[0][lado].getValue(), (int) campos[1][lado].getValue(),
                        (int) campos[2][lado].getValue(), (int) campos[3][lado].getValue(),
                        (int) campos[4][lado].getValue(), (int) campos[5][lado].getValue());
            Ui.info(this, "Estatísticas registadas.");
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        cmbJogo.removeAllItems();
        for (Jogo j : DataStore.getInstance().getJogos())
            if (j.getEquipa1() != null && j.getEquipa2() != null) cmbJogo.addItem(j);
    }
}
