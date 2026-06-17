package pt.ipleiria.worldcup.ui.doping;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Enums.ResultadoTeste;
import pt.ipleiria.worldcup.model.Equipa;
import pt.ipleiria.worldcup.model.Jogador;
import pt.ipleiria.worldcup.model.TesteDoping;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VerTestesPanel extends JPanel implements DopingPanel.Atualizavel {

    private final DataStore ds = DataStore.getInstance();

    private final JComboBox<Object> fEquipa = new JComboBox<>();
    private final JComboBox<Object> fJogador = new JComboBox<>();
    private final JComboBox<Object> fResultado = new JComboBox<>();
    private final JTextField fData = new JTextField(8);
    private final JComboBox<String> fCastigo =
            new JComboBox<>(new String[]{"Todos", "Com castigo", "Sem castigo"});

    private final DefaultTableModel modelo =
            Ui.model("Equipa", "Jogador", "Data", "Resultado", "Substância", "Castigo");
    private final JTable tabela = new JTable(modelo);

    public VerTestesPanel() {
        setLayout(new BorderLayout());
        setBackground(Ui.LIGHT);

        add(Ui.title("Ver Testes de Doping"), BorderLayout.NORTH);

        fData.setToolTipText("DD-MM-AAAA (vazio = todas)");
        JButton aplicar = new JButton("Aplicar filtros");
        aplicar.addActionListener(e -> refrescarTabela());
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.setOpaque(false);
        filtros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        filtros.add(new JLabel("Equipa:"));   filtros.add(fEquipa);
        filtros.add(new JLabel("Jogador:"));  filtros.add(fJogador);
        filtros.add(new JLabel("Resultado:"));filtros.add(fResultado);
        filtros.add(new JLabel("Data:"));     filtros.add(fData);
        filtros.add(new JLabel("Castigo:"));  filtros.add(fCastigo);
        filtros.add(aplicar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(filtros, BorderLayout.NORTH);
        centro.add(Ui.table(tabela), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    @Override public void atualizar() {
        DefaultComboBoxModel<Object> me = new DefaultComboBoxModel<>();
        me.addElement("Todas");
        ds.getEquipas().forEach(me::addElement);
        fEquipa.setModel(me);

        DefaultComboBoxModel<Object> mj = new DefaultComboBoxModel<>();
        mj.addElement("Todos");
        List<Jogador> js = new ArrayList<>();
        for (Equipa e : ds.getEquipas()) js.addAll(e.getJogadores());
        js.forEach(mj::addElement);
        fJogador.setModel(mj);

        DefaultComboBoxModel<Object> mr = new DefaultComboBoxModel<>();
        mr.addElement("Todos");
        for (ResultadoTeste r : ResultadoTeste.values()) mr.addElement(r);
        fResultado.setModel(mr);

        refrescarTabela();
    }

    private void refrescarTabela() {
        modelo.setRowCount(0);
        Object fe = fEquipa.getSelectedItem();
        Object fj = fJogador.getSelectedItem();
        Object fr = fResultado.getSelectedItem();
        String fd = fData.getText().trim();
        String fc = (String) fCastigo.getSelectedItem();

        for (TesteDoping t : ds.getTestes()) {
            if (fe instanceof Equipa e && t.getJogador().getEquipa() != e) continue;
            if (fj instanceof Jogador j && t.getJogador() != j) continue;
            if (fr instanceof ResultadoTeste r && t.getResultado() != r) continue;
            if (!fd.isEmpty() && !Ui.fmt(t.getData()).equals(fd.replace('/', '-'))) continue;
            boolean comCastigo = !"-".equals(t.getCastigoAplicado());
            if ("Com castigo".equals(fc) && !comCastigo) continue;
            if ("Sem castigo".equals(fc) && comCastigo) continue;
            modelo.addRow(new Object[]{
                    t.getJogador().getEquipa().getSigla(),
                    t.getJogador().getNome(),
                    Ui.fmt(t.getData()),
                    t.getResultado(),
                    t.getSubstancia() == null ? "-" : t.getSubstancia().getNome(),
                    t.getCastigoAplicado()});
        }
    }
}
