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

    // campos exclusivos da substituição
    private final JLabel lblJogador = new JLabel("Jogador:");
    private final JLabel lblEntra = new JLabel("Jogador que entra:");
    private final JComboBox<Jogador> cmbJogadorEntra = new JComboBox<>();

    private final DefaultTableModel model =
            Ui.model("Min.", "Tipo", "Jogador", "Entra (substituição)", "Equipa");

    public EventosPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Registar Eventos do Jogo"), BorderLayout.NORTH);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        form.add(new JLabel("Jogo:"));    form.add(cmbJogo);
        form.add(new JLabel("Tipo:"));    form.add(cmbTipo);
        form.add(new JLabel("Equipa:")); form.add(cmbEquipa);
        form.add(lblJogador); form.add(cmbJogador);
        form.add(lblEntra); form.add(cmbJogadorEntra);
        form.add(new JLabel("Minuto:")); form.add(spnMinuto);
        JButton btnAdd = new JButton("Adicionar evento");
        btnAdd.addActionListener(e -> adicionar());
        form.add(btnAdd);

        cmbJogo.addActionListener(e -> jogoMudou());
        cmbEquipa.addActionListener(e -> equipaMudou());
        cmbTipo.addActionListener(e -> tipoMudou());

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(Ui.formScroll(form), BorderLayout.NORTH);
        centro.add(Ui.table(new JTable(model)), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    /** Mostra/oculta o combo "Jogador que entra" conforme o tipo selecionado. */
    private void tipoMudou() {
        TipoEvento tipo = (TipoEvento) cmbTipo.getSelectedItem();
        boolean isSub = tipo == TipoEvento.SUBSTITUICAO;
        lblEntra.setVisible(isSub);
        cmbJogadorEntra.setVisible(isSub);

        // o label do jogador principal muda de acordo com o tipo de evento
        lblJogador.setText(switch (tipo) {
            case GOLO -> "Jogador que marca:";
            case ASSISTENCIA -> "Jogador que assiste:";
            case CARTAO_AMARELO, CARTAO_VERMELHO -> "Jogador advertido:";
            case SUBSTITUICAO -> "Jogador que sai:";
        });

        // repreencher com jogadores da mesma equipa
        if (isSub) equipaMudou();
    }

    private void adicionar() {
        try {
            Jogo j = (Jogo) cmbJogo.getSelectedItem();
            if (j == null) throw new IllegalArgumentException("Selecione um jogo.");
            TipoEvento tipo = (TipoEvento) cmbTipo.getSelectedItem();
            Jogador jogadorEntra = tipo == TipoEvento.SUBSTITUICAO
                    ? (Jogador) cmbJogadorEntra.getSelectedItem() : null;
            service.registarEvento(j, tipo,
                    (Equipa) cmbEquipa.getSelectedItem(),
                    (Jogador) cmbJogador.getSelectedItem(),
                    jogadorEntra,
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
        Equipa e = (Equipa) cmbEquipa.getSelectedItem();
        cmbJogador.removeAllItems();
        cmbJogadorEntra.removeAllItems();
        if (e != null) {
            for (Jogador jg : e.getJogadores()) {
                cmbJogador.addItem(jg);
                cmbJogadorEntra.addItem(jg);
            }
        }
    }

    private void refrescarTabela() {
        model.setRowCount(0);
        Jogo j = (Jogo) cmbJogo.getSelectedItem();
        if (j == null) return;
        for (EventoJogo e : j.getEventos()) {
            String nomeJogador = e.getJogador() == null ? "—" : e.getJogador().getNome();
            // o significado do jogador principal depende do tipo de evento
            String papel = switch (e.getTipo()) {
                case GOLO -> "marca: ";
                case ASSISTENCIA -> "assiste: ";
                case CARTAO_AMARELO, CARTAO_VERMELHO -> "advertido: ";
                case SUBSTITUICAO -> "sai: ";
            };
            model.addRow(new Object[]{
                    e.getMinuto() + "'",
                    e.getTipo(),
                    papel + nomeJogador,
                    e.getJogadorEntra() == null ? "—" : "entra: " + e.getJogadorEntra().getNome(),
                    e.getEquipa() == null ? "—" : e.getEquipa().getPais()});
        }
    }

    @Override public void atualizar() {
        cmbJogo.removeAllItems();
        for (Jogo j : DataStore.getInstance().getJogos())
            if (j.getEquipa1() != null && j.getEquipa2() != null) cmbJogo.addItem(j);
        jogoMudou();
        tipoMudou();
    }
}
