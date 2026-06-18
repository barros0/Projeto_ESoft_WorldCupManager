package pt.ipleiria.worldcup.ui.teams;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Equipa;
import pt.ipleiria.worldcup.model.Staff;
import pt.ipleiria.worldcup.service.EquipaService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Adicionar / editar / remover Staff (Figuras 20 e 21). */
public class StaffPanel extends JPanel implements EquipasPanel.Atualizavel {

    private final DataStore ds = DataStore.getInstance();
    private final EquipaService service = new EquipaService();

    private final JTextField nome = new JTextField(15);
    private final JComboBox<String> cargo = new JComboBox<>(new String[]{
            "Treinador Principal", "Treinador Adjunto", "Preparador Físico",
            "Médico", "Fisioterapeuta", "Diretor Desportivo", "Outro"});
    private final JComboBox<Equipa> equipa = new JComboBox<>();

    private final DefaultTableModel modelo = Ui.model("Nome", "Cargo", "Equipa");
    private final JTable tabela = new JTable(modelo);
    private final List<Staff> linhas = new ArrayList<>();

    public StaffPanel() {
        setLayout(new BorderLayout());
        setBackground(Ui.LIGHT);

        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(Ui.title("Adicionar Staff"), BorderLayout.NORTH);
        JButton adicionar = new JButton("Adicionar");
        adicionar.addActionListener(e -> adicionar());
        topo.add(Ui.form(
                new JLabel("Nome"), nome,
                new JLabel("Cargo"), cargo,
                new JLabel("Equipa"), equipa,
                new JLabel(), adicionar), BorderLayout.CENTER);

        JPanel meio = new JPanel(new BorderLayout());
        meio.setOpaque(false);
        meio.add(Ui.title("Lista de Staff"), BorderLayout.NORTH);
        meio.add(Ui.table(tabela), BorderLayout.CENTER);

        JPanel acoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acoes.setOpaque(false);
        JButton editar = new JButton("Editar");
        editar.addActionListener(e -> editar());
        JButton apagar = new JButton("Apagar");
        apagar.addActionListener(e -> apagar());
        acoes.add(editar);
        acoes.add(apagar);
        meio.add(acoes, BorderLayout.SOUTH);

        add(Ui.formScroll(topo), BorderLayout.NORTH);
        add(meio, BorderLayout.CENTER);
        atualizar();
    }

    private void adicionar() {
        try {
            Equipa eq = (Equipa) equipa.getSelectedItem();
            if (eq == null) throw new IllegalArgumentException("Selecione a equipa.");
            if (nome.getText().isBlank()) throw new IllegalArgumentException("Indique o nome.");
            service.adicionarStaff(eq, nome.getText().trim(), (String) cargo.getSelectedItem());
            nome.setText("");
            refrescarTabela();
        } catch (RuntimeException ex) { Ui.erro(this, ex.getMessage()); }
    }

    private Staff selecionado() {
        int r = tabela.getSelectedRow();
        if (r < 0) { Ui.erro(this, "Selecione um elemento do staff na tabela."); return null; }
        return linhas.get(r);
    }

    private void editar() {
        Staff s = selecionado();
        if (s == null) return;
        JTextField n = new JTextField(s.getNome(), 15);
        JComboBox<String> c = new JComboBox<>(new String[]{
                "Treinador Principal", "Treinador Adjunto", "Preparador Físico",
                "Médico", "Fisioterapeuta", "Diretor Desportivo", "Outro"});
        c.setSelectedItem(s.getCargo());
        JPanel p = Ui.form(new JLabel("Nome"), n, new JLabel("Cargo"), c);
        if (JOptionPane.showConfirmDialog(this, p, "Editar Staff",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (n.getText().isBlank()) { Ui.erro(this, "Indique o nome."); return; }
        s.setNome(n.getText().trim());
        s.setCargo((String) c.getSelectedItem());
        refrescarTabela();
    }

    private void apagar() {
        Staff s = selecionado();
        if (s == null) return;
        if (!Ui.confirmar(this, "Apagar " + s.getNome() + " do staff?")) return;
        service.removerStaff(s);
        refrescarTabela();
    }

    @Override public void atualizar() {
        equipa.setModel(new DefaultComboBoxModel<>(ds.getEquipas().toArray(new Equipa[0])));
        refrescarTabela();
    }

    private void refrescarTabela() {
        modelo.setRowCount(0);
        linhas.clear();
        for (Equipa e : ds.getEquipas())
            for (Staff s : e.getStaff()) {
                linhas.add(s);
                modelo.addRow(new Object[]{s.getNome(), s.getCargo(), e.getPais()});
            }
    }
}
