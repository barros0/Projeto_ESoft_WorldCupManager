package pt.ipleiria.worldcup.ui.teams;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Alojamento;
import pt.ipleiria.worldcup.model.Equipa;
import pt.ipleiria.worldcup.model.Hotel;
import pt.ipleiria.worldcup.model.Jogo;
import pt.ipleiria.worldcup.service.EquipaService;
import pt.ipleiria.worldcup.ui.common.Ui;

import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Atribuir alojamento + Alojamentos atribuídos (Figuras 22 e 23). */
public class AlojamentoPanel extends JPanel implements EquipasPanel.Atualizavel {

    private final DataStore ds = DataStore.getInstance();
    private final EquipaService service = new EquipaService();

    private final JComboBox<Equipa> equipa = new JComboBox<>();
    private final JComboBox<Jogo> jogo = new JComboBox<>();
    private final JComboBox<Hotel> hotel = new JComboBox<>(); // lista pré-carregada

    private final DefaultTableModel modelo = Ui.model("Equipa", "Jogo", "Hotel");
    private final JTable tabela = new JTable(modelo);
    private final java.util.List<Alojamento> linhas = new java.util.ArrayList<>();

    public AlojamentoPanel() {
        setLayout(new BorderLayout());
        setBackground(Ui.LIGHT);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(Ui.title("Atribuir Alojamento"), BorderLayout.NORTH);
        JButton confirmar = new JButton("Confirmar alojamento");
        confirmar.addActionListener(e -> confirmar());
        topo.add(Ui.form(
                new JLabel("Equipa"), equipa,
                new JLabel("Jogo"), jogo,
                new JLabel("Hotel"), hotel,
                new JLabel(), confirmar), BorderLayout.CENTER);

        JPanel meio = new JPanel(new BorderLayout());
        meio.setOpaque(false);
        meio.add(Ui.title("Alojamentos Atribuídos"), BorderLayout.NORTH);
        meio.add(Ui.table(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoes.setOpaque(false);
        JButton btnEditar = new JButton("Editar selecionado");
        JButton btnApagar = new JButton("Apagar selecionado");
        btnEditar.addActionListener(e -> editar());
        btnApagar.addActionListener(e -> apagar());
        acoes.add(btnEditar);
        acoes.add(btnApagar);
        meio.add(acoes, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(meio, BorderLayout.CENTER);
        atualizar();
    }

    private void filtrarJogosPorEquipa() {
        Equipa eq = (Equipa) equipa.getSelectedItem();
        Jogo sel = (Jogo) jogo.getSelectedItem();
        List<Jogo> filtrados = eq == null ? ds.getJogos()
                : ds.getJogos().stream()
                    .filter(j -> j.getEquipa1() == eq || j.getEquipa2() == eq)
                    .toList();
        jogo.setModel(new DefaultComboBoxModel<>(filtrados.toArray(new Jogo[0])));
        if (sel != null && filtrados.contains(sel)) jogo.setSelectedItem(sel);
    }

    private void confirmar() {
        try {
            Equipa eq = (Equipa) equipa.getSelectedItem();
            Jogo j = (Jogo) jogo.getSelectedItem();
            Hotel h = (Hotel) hotel.getSelectedItem();
            if (eq == null) throw new IllegalArgumentException("Selecione a equipa.");
            if (j == null) throw new IllegalArgumentException("Selecione o jogo.");
            if (h == null) throw new IllegalArgumentException("Selecione o hotel.");

            // O sistema pede confirmação caso já exista outra equipa nesse hotel
            if (service.hotelOcupado(h, eq)
                    && !Ui.confirmar(this, "Já existe outra equipa alojada no hotel \""
                    + h.getNome() + "\".\nDeseja continuar?")) return;

            service.atribuirAlojamento(eq, j, h);
            refrescarTabela();
            Ui.info(this, "Alojamento atribuído com sucesso.");
        } catch (RuntimeException ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        equipa.setModel(new DefaultComboBoxModel<>(ds.getEquipas().toArray(new Equipa[0])));
        equipa.addActionListener(e -> filtrarJogosPorEquipa());
        jogo.setModel(new DefaultComboBoxModel<>(ds.getJogos().toArray(new Jogo[0])));
        hotel.setModel(new DefaultComboBoxModel<>(ds.getHoteis().toArray(new Hotel[0])));
        refrescarTabela();
    }

    private Alojamento selecionado() {
        int r = tabela.getSelectedRow();
        if (r < 0) { Ui.erro(this, "Selecione um alojamento na tabela."); return null; }
        return linhas.get(r);
    }

    private void editar() {
        Alojamento a = selecionado();
        if (a == null) return;
        JComboBox<Jogo> fJogo = new JComboBox<>(ds.getJogos().toArray(new Jogo[0]));
        fJogo.setSelectedItem(a.getJogo());
        JComboBox<Hotel> fHotel = new JComboBox<>(ds.getHoteis().toArray(new Hotel[0]));
        fHotel.setSelectedItem(a.getHotel());
        JPanel p = Ui.form(new JLabel("Jogo"), fJogo, new JLabel("Hotel"), fHotel);
        if (JOptionPane.showConfirmDialog(this, p,
                "Editar Alojamento — " + a.getEquipa().getPais(),
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            Hotel novo = (Hotel) fHotel.getSelectedItem();
            // mantém a regra: confirmar se o hotel novo já tem outra equipa
            if (novo != a.getHotel() && service.hotelOcupado(novo, a.getEquipa())
                    && !Ui.confirmar(this, "Já existe outra equipa alojada no hotel \""
                    + novo.getNome() + "\".\nDeseja continuar?")) return;
            service.editarAlojamento(a, (Jogo) fJogo.getSelectedItem(), novo);
            refrescarTabela();
        } catch (RuntimeException ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void apagar() {
        Alojamento a = selecionado();
        if (a == null) return;
        if (!Ui.confirmar(this, "Apagar o alojamento de " + a.getEquipa().getPais()
                + " no hotel \"" + a.getHotel().getNome() + "\"?")) return;
        service.removerAlojamento(a);
        refrescarTabela();
    }

    private void refrescarTabela() {
        modelo.setRowCount(0);
        linhas.clear();
        for (Alojamento a : ds.getAlojamentos()) {
            linhas.add(a);
            modelo.addRow(new Object[]{a.getEquipa().getPais(),
                    a.getJogo().descricaoCurta(), a.getHotel().getNome()});
        }
    }
}
