package pt.ipleiria.worldcup.ui.doping;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Enums.ResultadoTeste;
import pt.ipleiria.worldcup.model.Equipa;
import pt.ipleiria.worldcup.model.Jogador;
import pt.ipleiria.worldcup.model.Substancia;
import pt.ipleiria.worldcup.model.TesteDoping;
import pt.ipleiria.worldcup.service.DopingService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FazerTestePanel extends JPanel implements DopingPanel.Atualizavel {

    private final DataStore ds = DataStore.getInstance();
    private final DopingService service = new DopingService();

    private final JComboBox<Jogador> jogador = new JComboBox<>();
    private final JTextField data = new JTextField(10);
    private final JComboBox<ResultadoTeste> resultado = new JComboBox<>(ResultadoTeste.values());
    private final JComboBox<Substancia> substancia = new JComboBox<>(); // listagem pré-carregada

    public FazerTestePanel() {
        setLayout(new BorderLayout());
        setBackground(Ui.LIGHT);

        add(Ui.title("Fazer Teste de Doping"), BorderLayout.NORTH);
        data.setToolTipText("DD-MM-AAAA");

        // A substância só fica ativa quando o resultado é Positivo
        resultado.addActionListener(e ->
                substancia.setEnabled(resultado.getSelectedItem() == ResultadoTeste.POSITIVO));
        substancia.setEnabled(false);

        JButton registar = new JButton("Registar Teste");
        registar.addActionListener(e -> registar());

        JPanel form = Ui.form(
                new JLabel("Jogador"), jogador,
                new JLabel("Data do teste (DD-MM-AAAA)"), data,
                new JLabel("Resultado"), resultado,
                new JLabel("Substância (se positivo)"), substancia,
                new JLabel(), registar);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(form, BorderLayout.NORTH);
        add(wrap, BorderLayout.CENTER);
        atualizar();
    }

    private void registar() {
        try {
            Jogador j = (Jogador) jogador.getSelectedItem();
            if (j == null) throw new IllegalArgumentException("Selecione o jogador testado.");
            ResultadoTeste r = (ResultadoTeste) resultado.getSelectedItem();
            Substancia s = r == ResultadoTeste.POSITIVO ? (Substancia) substancia.getSelectedItem() : null;
            TesteDoping t = service.registarTeste(j, Ui.parseDate(data.getText(), "Data do teste"), r, s);
            data.setText("");
            String msg = "Teste registado com sucesso.";
            if (!"-".equals(t.getCastigoAplicado()))
                msg += "\nCastigo aplicado automaticamente: " + t.getCastigoAplicado();
            Ui.info(this, msg);
        } catch (RuntimeException ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        List<Jogador> js = new ArrayList<>();
        for (Equipa e : ds.getEquipas()) js.addAll(e.getJogadores());
        Jogador sel = (Jogador) jogador.getSelectedItem();
        jogador.setModel(new DefaultComboBoxModel<>(js.toArray(new Jogador[0])));
        if (sel != null && js.contains(sel)) jogador.setSelectedItem(sel);
        substancia.setModel(new DefaultComboBoxModel<>(ds.getSubstancias().toArray(new Substancia[0])));
    }
}
