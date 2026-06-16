package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.model.Enums.Fase;
import pt.ipleiria.worldcup.service.CampeonatoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;

/** Criação manual de jogos para as fases eliminatórias. */
public class CriarJogoPanel extends JPanel implements JogosPanel.Atualizavel {

    private final CampeonatoService service = new CampeonatoService();
    private final JComboBox<Equipa> cmbE1 = new JComboBox<>();
    private final JComboBox<Equipa> cmbE2 = new JComboBox<>();
    private final JTextField txtData = new JTextField("01-07-2026", 10);
    private final JTextField txtHora = new JTextField("20:00", 6);
    private final JComboBox<Estadio> cmbEstadio = new JComboBox<>();
    private final JComboBox<Fase> cmbFase = new JComboBox<>(
            new Fase[]{ Fase.OITAVOS, Fase.QUARTOS, Fase.MEIAS, Fase.FINAL });
    private final JTextField txtPreco = new JTextField("75", 6);

    public CriarJogoPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Criar Jogo — Eliminatórias"), BorderLayout.NORTH);

        JPanel form = Ui.form(
                new JLabel("Equipa 1:"), cmbE1,
                new JLabel("Equipa 2:"), cmbE2,
                new JLabel("Data (DD-MM-AAAA):"), txtData,
                new JLabel("Hora (HH:MM):"), txtHora,
                new JLabel("Estádio:"), cmbEstadio,
                new JLabel("Fase:"), cmbFase,
                new JLabel("Preço base (€):"), txtPreco);

        JButton btnCriar = new JButton("Criar Jogo");
        JButton btnLimpar = new JButton("Limpar");
        btnCriar.addActionListener(e -> criar());
        btnLimpar.addActionListener(e -> atualizar());

        JPanel botoes = new JPanel();
        botoes.add(btnCriar); botoes.add(btnLimpar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        centro.add(botoes, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    private void criar() {
        try {
            service.criarJogoEliminatoria((Equipa) cmbE1.getSelectedItem(), (Equipa) cmbE2.getSelectedItem(),
                    Ui.parseDate(txtData.getText(), "Data"), Ui.parseTime(txtHora.getText(), "Hora"),
                    (Estadio) cmbEstadio.getSelectedItem(), (Fase) cmbFase.getSelectedItem(),
                    Ui.parseDouble(txtPreco.getText(), "Preço base"));
            Ui.info(this, "Jogo criado com sucesso.");
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        cmbE1.removeAllItems(); cmbE2.removeAllItems(); cmbEstadio.removeAllItems();
        for (Equipa e : DataStore.getInstance().getEquipas()) { cmbE1.addItem(e); cmbE2.addItem(e); }
        for (Estadio s : DataStore.getInstance().getEstadios()) cmbEstadio.addItem(s);
    }
}
