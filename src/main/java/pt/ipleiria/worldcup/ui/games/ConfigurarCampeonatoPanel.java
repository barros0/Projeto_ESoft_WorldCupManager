package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Campeonato;
import pt.ipleiria.worldcup.service.CampeonatoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;

public class ConfigurarCampeonatoPanel extends JPanel implements JogosPanel.Atualizavel {

    private final CampeonatoService service = new CampeonatoService();
    private final JTextField txtNome = new JTextField(18);
    private final JComboBox<Integer> cmbGrupos = new JComboBox<>(new Integer[]{4, 8});
    private final JTextField txtEquipasPorGrupo = ro();
    private final JTextField txtTotalEquipas = ro();
    private final JTextField txtJogosFaseGrupos = ro();
    private final JTextField txtApuradas = ro();
    private final JTextField txtFases = ro();

    private static JTextField ro() { JTextField t = new JTextField(16); t.setEditable(false); return t; }

    public ConfigurarCampeonatoPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Configurar Campeonato"), BorderLayout.NORTH);

        JPanel form = Ui.form(
                new JLabel("Nome do campeonato:"), txtNome,
                new JLabel("Número de grupos:"), cmbGrupos,
                new JLabel("Equipas por grupo:"), txtEquipasPorGrupo,
                new JLabel("Total de equipas:"), txtTotalEquipas,
                new JLabel("Jogos na fase de grupos:"), txtJogosFaseGrupos,
                new JLabel("Equipas apuradas (2/grupo):"), txtApuradas,
                new JLabel("Fases eliminatórias geradas:"), txtFases);

        JButton btnGuardar = new JButton("Guardar Campeonato");
        btnGuardar.addActionListener(e -> guardar());
        cmbGrupos.addActionListener(e -> recalc());

        JPanel botoes = new JPanel();
        botoes.add(btnGuardar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        centro.add(botoes, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        recalc();
        atualizar();
    }

    private void recalc() {
        int n = (Integer) cmbGrupos.getSelectedItem();
        int apuradas = n * Campeonato.APURADOS_POR_GRUPO;
        txtEquipasPorGrupo.setText("4");
        txtTotalEquipas.setText(String.valueOf(n * 4));
        txtJogosFaseGrupos.setText(String.valueOf(n * 6));
        txtApuradas.setText(String.valueOf(apuradas));
        txtFases.setText(apuradas == 16 ? "Oitavos, Quartos, Meias, Final" : "Quartos, Meias, Final");
    }

    private void guardar() {
        try {
            if (txtNome.getText().isBlank()) throw new IllegalArgumentException("Indique o nome do campeonato.");
            if (DataStore.getInstance().getCampeonato() != null
                    && !Ui.confirmar(this, "Já existe um campeonato configurado. Substituir?")) return;
            service.criarCampeonato(txtNome.getText().trim(), (Integer) cmbGrupos.getSelectedItem());
            Ui.info(this, "Campeonato guardado com sucesso.");
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        Campeonato c = DataStore.getInstance().getCampeonato();
        if (c != null) {
            txtNome.setText(c.getNome());
            cmbGrupos.setSelectedItem(c.getNumGrupos());
        }
        recalc();
    }
}
