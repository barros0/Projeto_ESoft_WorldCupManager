package pt.ipleiria.worldcup.ui.doping;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Enums.ResultadoTeste;
import pt.ipleiria.worldcup.model.TesteDoping;
import pt.ipleiria.worldcup.service.EstatisticaService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EstatisticasDopingPanel extends JPanel implements DopingPanel.Atualizavel {

    private final DataStore ds = DataStore.getInstance();
    private final EstatisticaService stats = new EstatisticaService();

    private final DefaultTableModel modelo = Ui.model("Estatística", "Valor");
    private final JComboBox<TesteDoping> teste = new JComboBox<>();
    private final JLabel suspensosNoTeste = new JLabel("-");

    public EstatisticasDopingPanel() {
        setLayout(new BorderLayout());
        setBackground(Ui.LIGHT);
        add(Ui.title("Estatísticas de Doping (acesso restrito)"), BorderLayout.NORTH);

        JPanel porTeste = new JPanel(new FlowLayout(FlowLayout.LEFT));
        porTeste.setOpaque(false);
        porTeste.setBorder(BorderFactory.createTitledBorder("Suspensos num determinado teste"));
        porTeste.add(new JLabel("Teste:"));
        porTeste.add(teste);
        porTeste.add(new JLabel("Jogadores suspensos nesse teste:"));
        suspensosNoTeste.setFont(suspensosNoTeste.getFont().deriveFont(Font.BOLD));
        porTeste.add(suspensosNoTeste);
        teste.addActionListener(e -> {
            TesteDoping t = (TesteDoping) teste.getSelectedItem();
            suspensosNoTeste.setText(t == null ? "-"
                    : ("-".equals(t.getCastigoAplicado()) ? "0" : "1"));
        });

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(porTeste, BorderLayout.NORTH);
        centro.add(Ui.table(new JTable(modelo)), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    @Override public void atualizar() {
        teste.setModel(new DefaultComboBoxModel<>(ds.getTestes().toArray(new TesteDoping[0])));
        teste.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof TesteDoping t)
                    setText(t.getJogador().getNome() + " — " + Ui.fmt(t.getData()) + " (" + t.getResultado() + ")");
                return this;
            }
        });

        modelo.setRowCount(0);
        modelo.addRow(new Object[]{"Jogadores suspensos por doping (todos os testes)", stats.suspensosPorDoping()});
        modelo.addRow(new Object[]{"Número total de testes", stats.totalTestes()});
        modelo.addRow(new Object[]{"Testes positivos", stats.testesPorResultado(ResultadoTeste.POSITIVO)});
        modelo.addRow(new Object[]{"Testes negativos", stats.testesPorResultado(ResultadoTeste.NEGATIVO)});
        modelo.addRow(new Object[]{"Testes duvidosos", stats.testesPorResultado(ResultadoTeste.DUVIDOSO)});
    }
}
