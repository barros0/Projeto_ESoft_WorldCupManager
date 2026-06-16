package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.model.Enums.TipoEvento;
import pt.ipleiria.worldcup.service.JogoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EventosPanel extends JPanel implements JogosPanel.Atualizavel {

    private final JogoService service = new JogoService();
    private final JComboBox<Jogo> cmbJogo = new JComboBox<>();
    private final JComboBox<TipoEvento> cmbTipo = new JComboBox<>(TipoEvento.values());
    private final JComboBox<Equipa> cmbEquipa = new JComboBox<>();
    private final JComboBox<Jogador> cmbJogador = new JComboBox<>();
    private final JSpinner spnMinuto = new JSpinner(new SpinnerNumberModel(1, 1, 130, 1));
    private final DefaultTableModel model = Ui.model("Min.", "Tipo", "Jogador", "Equipa");

    public EventosPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Registar Eventos do Jogo"), BorderLayout.NORTH);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Jogo:")); form.add(cmbJogo);
        form.add(new JLabel("Tipo:")); form.add(cmbTipo);
        form.add(new JLabel("Equipa:")); form.add(cmbEquipa);
        form.add(new JLabel("Jogador:")); form.add(cmbJogador);
        form.add(new JLabel("Minuto:")); form.add(spnMinuto);
        JButton btnAdd = new JButton("Adicionar evento");
        btnAdd.addActionListener(e -> adicionar());
        form.add(btnAdd);

        cmbJogo.addActionListener(e -> jogoMudou());
        cmbEquipa.addActionListener(e -> equipaMudou());

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        centro.add(Ui.table(new JTable(model)), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    private void adicionar() {
        try {
            Jogo j = (Jogo) cmbJogo.getSelectedItem();
            if (j == null) throw new IllegalArgumentException("Selecione um jogo.");
            service.registarEvento(j, (TipoEvento) cmbTipo.getSelectedItem(),
                    (Equipa) cmbEquipa.getSelectedItem(), (Jogador) cmbJogador.getSelectedItem(),
                    (int) spnMinuto.getValue());
            refrescarTabela();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void jogoMudou() {
        cmbEquipa.removeAllItems();
        Jogo j = (Jogo) cmbJogo.getSelectedItem();
        if (j != null) {
            if (j.getEquipa1() != null) cmbEquipa.addItem(j.getEquipa1());
            if (j.getEquipa2() != null) cmbEquipa.addItem(j.getEquipa2());
        }
        equipaMudou();
        refrescarTabela();
    }

    private void equipaMudou() {
        cmbJogador.removeAllItems();
        Equipa e = (Equipa) cmbEquipa.getSelectedItem();
        if (e != null) for (Jogador jg : e.getJogadores()) cmbJogador.addItem(jg);
    }

    private void refrescarTabela() {
        model.setRowCount(0);
        Jogo j = (Jogo) cmbJogo.getSelectedItem();
        if (j == null) return;
        for (EventoJogo e : j.getEventos())
            model.addRow(new Object[]{ e.getMinuto() + "'", e.getTipo(),
                    e.getJogador() == null ? "—" : e.getJogador().getNome(),
                    e.getEquipa() == null ? "—" : e.getEquipa().getPais() });
    }

    @Override public void atualizar() {
        cmbJogo.removeAllItems();
        for (Jogo j : DataStore.getInstance().getJogos())
            if (j.getEquipa1() != null && j.getEquipa2() != null) cmbJogo.addItem(j);
        jogoMudou();
    }
}
