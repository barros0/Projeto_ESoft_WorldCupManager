package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Estadio;
import pt.ipleiria.worldcup.model.Jogo;
import pt.ipleiria.worldcup.service.CampeonatoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CalendarioPanel extends JPanel implements JogosPanel.Atualizavel {

    private final CampeonatoService service = new CampeonatoService();
    private final JTextField txtInicio = new JTextField(Ui.fmt(java.time.LocalDate.now()), 10);
    private final JSpinner spnJogosDia = new JSpinner(new SpinnerNumberModel(2, 1, 10, 1));
    private final JTextField txtPreco = new JTextField("45", 6);
    private final DefaultTableModel model = Ui.model("Grupo/Fase", "Jogo", "Data", "Hora", "Estádio", "Preço (€)");
    private final JTable tabela = new JTable(model);

    public CalendarioPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Configurar Calendário — Fase de Grupos"), BorderLayout.NORTH);

        JButton btnGerar = new JButton("Gerar calendário");
        JButton btnAjustar = new JButton("Ajustar jogo selecionado...");
        btnGerar.addActionListener(e -> gerar());
        btnAjustar.addActionListener(e -> ajustar());

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Data de início (DD-MM-AAAA):")); topo.add(txtInicio);
        topo.add(new JLabel("Jogos por dia:")); topo.add(spnJogosDia);
        topo.add(new JLabel("Preço base (€):")); topo.add(txtPreco);
        topo.add(btnGerar);

        JPanel sul = new JPanel();
        sul.add(btnAjustar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(Ui.formScroll(topo), BorderLayout.NORTH);
        centro.add(Ui.table(tabela), BorderLayout.CENTER);
        centro.add(sul, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    private void gerar() {
        try {
            service.gerarCalendario(Ui.parseDate(txtInicio.getText(), "Data de início"),
                    (int) spnJogosDia.getValue(), Ui.parseDouble(txtPreco.getText(), "Preço base"));
            Ui.info(this, "Calendário gerado: os jogos foram distribuídos automaticamente,\nsem mais de um jogo por estádio por dia.");
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void ajustar() {
        int row = tabela.getSelectedRow();
        if (row < 0) { Ui.erro(this, "Selecione um jogo na tabela."); return; }
        Jogo j = DataStore.getInstance().getJogos().get(row);

        JTextField data = new JTextField(Ui.fmt(j.getData()));
        JTextField hora = new JTextField(String.valueOf(j.getHora()));
        JComboBox<Estadio> est = new JComboBox<>(DataStore.getInstance().getEstadios().toArray(new Estadio[0]));
        est.setSelectedItem(j.getEstadio());
        JTextField preco = new JTextField(String.valueOf(j.getPrecoBase()));

        JPanel p = Ui.form(new JLabel("Data (DD-MM-AAAA):"), data, new JLabel("Hora (HH:MM):"), hora,
                new JLabel("Estádio:"), est, new JLabel("Preço base (€):"), preco);
        if (JOptionPane.showConfirmDialog(this, p, "Ajustar jogo",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            service.ajustarJogo(j, Ui.parseDate(data.getText(), "Data"),
                    Ui.parseTime(hora.getText(), "Hora"), (Estadio) est.getSelectedItem());
            j.setPrecoBase(Ui.parseDouble(preco.getText(), "Preço base"));
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        model.setRowCount(0);
        List<Jogo> jogos = DataStore.getInstance().getJogos();
        for (Jogo j : jogos)
            model.addRow(new Object[]{
                    j.getGrupo() != null ? "Grupo " + j.getGrupo().getNome() : j.getFase().toString(),
                    (j.getEquipa1() == null ? "?" : j.getEquipa1().getSigla()) + " vs "
                            + (j.getEquipa2() == null ? "?" : j.getEquipa2().getSigla()),
                    Ui.fmt(j.getData()), j.getHora(), j.getEstadio(), j.getPrecoBase() });
    }
}
