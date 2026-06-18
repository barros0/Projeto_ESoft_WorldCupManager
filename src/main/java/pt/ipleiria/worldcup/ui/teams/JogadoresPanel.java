package pt.ipleiria.worldcup.ui.teams;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Enums.EstadoJogador;
import pt.ipleiria.worldcup.model.Equipa;
import pt.ipleiria.worldcup.model.Jogador;
import pt.ipleiria.worldcup.service.EquipaService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Adicionar Jogador + Lista de Jogadores (Figuras 18 e 19). */
public class JogadoresPanel extends JPanel implements EquipasPanel.Atualizavel {

    private final DataStore ds = DataStore.getInstance();
    private final EquipaService service = new EquipaService();

    private final JTextField nome = new JTextField(15);
    private final JTextField nascimento = new JTextField(10);
    private final JComboBox<String> posicao =
            new JComboBox<>(new String[]{"Guarda-Redes", "Defesa", "Médio", "Avançado"});
    private final JComboBox<Equipa> equipa = new JComboBox<>();
    private final JTextField camisola = new JTextField(4);

    private final JComboBox<Object> filtroEquipa = new JComboBox<>();
    private final DefaultTableModel modelo =
            Ui.model("Nr.", "Nome", "Equipa", "Posição", "Estado");
    private final JTable tabela = new JTable(modelo);
    private final List<Jogador> linhas = new ArrayList<>();

    public JogadoresPanel() {
        setLayout(new BorderLayout());
        setBackground(Ui.LIGHT);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(Ui.title("Adicionar Jogador"), BorderLayout.NORTH);
        nascimento.setToolTipText("DD-MM-AAAA");
        JButton adicionar = new JButton("Adicionar");
        adicionar.addActionListener(e -> adicionar());
        topo.add(Ui.form(
                new JLabel("Nome"), nome,
                new JLabel("Data de nascimento (DD-MM-AAAA)"), nascimento,
                new JLabel("Posição"), posicao,
                new JLabel("Seleção que representa"), equipa,
                new JLabel("Nr. camisola"), camisola,
                new JLabel(), adicionar), BorderLayout.CENTER);

        JPanel meio = new JPanel(new BorderLayout());
        meio.setOpaque(false);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setOpaque(false);
        filtros.add(Ui.title("Lista de Jogadores"));
        filtros.add(new JLabel("Equipa:"));
        filtros.add(filtroEquipa);
        filtroEquipa.addActionListener(e -> refrescarTabela());
        meio.add(filtros, BorderLayout.NORTH);
        meio.add(Ui.table(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoes.setOpaque(false);
        JButton remover = new JButton("Remover jogador");
        remover.addActionListener(e -> remover());
        JComboBox<EstadoJogador> estados = new JComboBox<>(EstadoJogador.values());
        JButton alterarEstado = new JButton("Alterar estado físico");
        alterarEstado.addActionListener(e -> alterarEstado((EstadoJogador) estados.getSelectedItem()));
        acoes.add(remover);
        acoes.add(new JLabel("   Estado físico:"));
        acoes.add(estados);
        acoes.add(alterarEstado);
        meio.add(acoes, BorderLayout.SOUTH);

        add(topo, BorderLayout.NORTH);
        add(meio, BorderLayout.CENTER);
        atualizar();
    }

    private void adicionar() {
        try {
            Equipa eq = (Equipa) equipa.getSelectedItem();
            if (eq == null) throw new IllegalArgumentException("Selecione a equipa.");
            if (nome.getText().isBlank()) throw new IllegalArgumentException("Indique o nome do jogador.");
            service.adicionarJogador(eq, nome.getText().trim(),
                    Ui.parseDate(nascimento.getText(), "Data de nascimento"),
                    (String) posicao.getSelectedItem(),
                    Ui.parseInt(camisola.getText(), "Nr. camisola"));
            nome.setText(""); nascimento.setText(""); camisola.setText("");
            refrescarTabela();
            Ui.info(this, "Jogador adicionado com sucesso.");
        } catch (RuntimeException ex) { Ui.erro(this, ex.getMessage()); }
    }

    private Jogador selecionado() {
        int r = tabela.getSelectedRow();
        if (r < 0) { Ui.erro(this, "Selecione um jogador na tabela."); return null; }
        return linhas.get(r);
    }

    private void remover() {
        Jogador j = selecionado();
        if (j == null) return;
        try {
            if (!Ui.confirmar(this, "Remover o jogador " + j.getNome() + "?")) return;
            service.removerJogador(j);
            refrescarTabela();
        } catch (RuntimeException ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void alterarEstado(EstadoJogador estado) {
        Jogador j = selecionado();
        if (j == null) return;
        j.setEstado(estado);
        refrescarTabela();
    }

    @Override public void atualizar() {
        equipa.setModel(new DefaultComboBoxModel<>(ds.getEquipas().toArray(new Equipa[0])));
        Object sel = filtroEquipa.getSelectedItem();
        DefaultComboBoxModel<Object> m = new DefaultComboBoxModel<>();
        m.addElement("Todas as equipas");
        ds.getEquipas().forEach(m::addElement);
        filtroEquipa.setModel(m);
        if (sel != null) filtroEquipa.setSelectedItem(sel);
        refrescarTabela();
    }

    private void refrescarTabela() {
        modelo.setRowCount(0);
        linhas.clear();
        Object f = filtroEquipa.getSelectedItem();
        for (Equipa e : ds.getEquipas()) {
            if (f instanceof Equipa eq && eq != e) continue;
            for (Jogador j : e.getJogadores()) {
                linhas.add(j);
                modelo.addRow(new Object[]{j.getNumeroCamisola(), j.getNome(),
                        e.getPais(), j.getPosicao(), j.getEstado()});
            }
        }
    }
}
