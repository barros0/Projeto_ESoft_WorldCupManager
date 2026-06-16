package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Jogo;
import pt.ipleiria.worldcup.model.Enums.Fase;
import pt.ipleiria.worldcup.service.JogoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;

public class RegistarResultadoPanel extends JPanel implements JogosPanel.Atualizavel {

    private final JogoService service = new JogoService();
    private final JComboBox<Jogo> cmbJogo = new JComboBox<>();
    private final JSpinner g1 = spinner(), g2 = spinner();
    private final JSpinner p1 = spinner(), p2 = spinner();
    private final JSpinner pen1 = spinner(), pen2 = spinner();
    private final JCheckBox chkProl = new JCheckBox("Prolongamento");
    private final JCheckBox chkPen = new JCheckBox("Penáltis");
    private final JSpinner rating = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));

    private static JSpinner spinner() { return new JSpinner(new SpinnerNumberModel(0, 0, 30, 1)); }

    public RegistarResultadoPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Registar Resultado"), BorderLayout.NORTH);

        // Formulário alinhado à ESQUERDA (sem esticar os campos)
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.NONE;

        cmbJogo.setPreferredSize(new Dimension(420, cmbJogo.getPreferredSize().height));
        for (JSpinner s : new JSpinner[]{g1, g2, p1, p2, pen1, pen2})
            s.setPreferredSize(new Dimension(70, s.getPreferredSize().height));

        int linha = 0;
        Component[][] linhas = {
                {new JLabel("Jogo:"), cmbJogo},
                {new JLabel("Golos — Equipa 1:"), g1},
                {new JLabel("Golos — Equipa 2:"), g2},
                {chkProl, new JLabel("(apenas eliminatórias, em caso de empate)")},
                {new JLabel("Golos prolongamento — Eq. 1:"), p1},
                {new JLabel("Golos prolongamento — Eq. 2:"), p2},
                {chkPen, null},
                {new JLabel("Penáltis — Eq. 1:"), pen1},
                {new JLabel("Penáltis — Eq. 2:"), pen2}};
        for (Component[] l : linhas) {
            c.gridy = linha++;
            c.gridx = 0;
            c.weightx = 0;
            form.add(l[0], c);
            if (l[1] != null) {
                c.gridx = 1;
                form.add(l[1], c);
            }
            // coluna fantasma que absorve o espaço restante e mantém tudo à esquerda
            c.gridx = 2;
            c.weightx = 1;
            form.add(Box.createHorizontalGlue(), c);
        }

        JButton btnGuardar = new JButton("Guardar Resultado");
        btnGuardar.addActionListener(e -> guardar());
        JButton btnRating = new JButton("Atribuir rating à arbitragem");
        btnRating.addActionListener(e -> atribuirRating());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.setOpaque(false);
        botoes.add(btnGuardar);
        botoes.add(new JLabel("   Rating (1-5):"));
        botoes.add(rating);
        botoes.add(btnRating);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        centro.add(botoes, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        // Prolongamento só nas eliminatórias e em caso de empate; penáltis só se o empate persistir
        cmbJogo.addActionListener(e -> atualizarControlos());
        javax.swing.event.ChangeListener cl = e -> atualizarControlos();
        g1.addChangeListener(cl);
        g2.addChangeListener(cl);
        p1.addChangeListener(cl);
        p2.addChangeListener(cl);
        chkProl.addActionListener(e -> atualizarControlos());

        atualizar();
        atualizarControlos();
    }

    /** Ativa/desativa prolongamento e penáltis conforme as regras. */
    private void atualizarControlos() {
        Jogo j = (Jogo) cmbJogo.getSelectedItem();
        boolean eliminatoria = j != null && j.getFase() != Fase.GRUPOS;
        boolean empate90 = (int) g1.getValue() == (int) g2.getValue();

        boolean podeProl = eliminatoria && empate90;
        chkProl.setEnabled(podeProl);
        if (!podeProl) chkProl.setSelected(false);

        boolean prolAtivo = chkProl.isSelected();
        p1.setEnabled(prolAtivo);
        p2.setEnabled(prolAtivo);

        // penáltis: empate ao fim do prolongamento (ou empate sem prolongamento numa eliminatória)
        int t1 = (int) g1.getValue() + (prolAtivo ? (int) p1.getValue() : 0);
        int t2 = (int) g2.getValue() + (prolAtivo ? (int) p2.getValue() : 0);
        boolean podePen = eliminatoria && t1 == t2;
        chkPen.setEnabled(podePen);
        chkPen.setSelected(podePen); // obrigatórios quando o empate persiste
        pen1.setEnabled(podePen);
        pen2.setEnabled(podePen);
    }

    private void guardar() {
        try {
            Jogo j = (Jogo) cmbJogo.getSelectedItem();
            if (j == null) throw new IllegalArgumentException("Selecione um jogo.");
            boolean prol = chkProl.isSelected() && j.getFase() != Fase.GRUPOS;
            boolean pen = chkPen.isSelected() && j.getFase() != Fase.GRUPOS;
            service.registarResultado(j, (int) g1.getValue(), (int) g2.getValue(),
                    prol ? (Integer) p1.getValue() : null, prol ? (Integer) p2.getValue() : null,
                    pen ? (Integer) pen1.getValue() : null, pen ? (Integer) pen2.getValue() : null);
            Ui.info(this, "Resultado registado." + (j.getFase() != Fase.GRUPOS && j.getVencedor() != null
                    ? "\nVencedor: " + j.getVencedor().getPais() + " (avança automaticamente)" : ""));
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void atribuirRating() {
        try {
            // permite escolher entre jogos já realizados
            Jogo[] realizados = DataStore.getInstance().getJogos().stream()
                    .filter(Jogo::isRealizado).toArray(Jogo[]::new);
            if (realizados.length == 0) throw new IllegalStateException("Não há jogos com resultado registado.");
            Jogo j = (Jogo) JOptionPane.showInputDialog(this, "Jogo:", "Rating da arbitragem",
                    JOptionPane.QUESTION_MESSAGE, null, realizados, realizados[0]);
            if (j == null) return;
            new JogoService().atribuirRating(j, (int) rating.getValue());
            Ui.info(this, "Rating atribuído à equipa de arbitragem.");
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        cmbJogo.removeAllItems();
        for (Jogo j : DataStore.getInstance().getJogos())
            if (!j.isRealizado() && j.getEquipa1() != null && j.getEquipa2() != null) cmbJogo.addItem(j);
    }
}
