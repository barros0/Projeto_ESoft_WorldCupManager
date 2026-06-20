package pt.ipleiria.worldcup.ui.doping;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Enums.ResultadoTeste;
import pt.ipleiria.worldcup.model.Equipa;
import pt.ipleiria.worldcup.model.Jogo;
import pt.ipleiria.worldcup.model.Jogador;
import pt.ipleiria.worldcup.model.Substancia;
import pt.ipleiria.worldcup.model.TesteDoping;
import pt.ipleiria.worldcup.service.DopingService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Fazer teste de Doping — máx. 2 por jogo, data preenchida automaticamente pelo jogo. */
public class FazerTestePanel extends JPanel implements DopingPanel.Atualizavel {

    private final DataStore ds = DataStore.getInstance();
    private final DopingService service = new DopingService();

    private final JComboBox<Object> jogo = new JComboBox<>();   // selecionar o jogo
    private final JTextField data = new JTextField(10);         // preenchido automaticamente
    private final JComboBox<Jogador> jogador = new JComboBox<>();
    private final JComboBox<ResultadoTeste> resultado = new JComboBox<>(ResultadoTeste.values());
    private final JComboBox<Substancia> substancia = new JComboBox<>();
    private final JLabel lblTestesFeitos = new JLabel("Testes neste jogo: 0 / 2");

    public FazerTestePanel() {
        setLayout(new BorderLayout());
        setBackground(Ui.LIGHT);
        add(Ui.title("Fazer Teste de Doping"), BorderLayout.NORTH);
        data.setEditable(false);
        data.setBackground(new Color(240, 242, 245));
        data.setToolTipText("Preenchido automaticamente com a data do jogo");

        // ao selecionar jogo → preenche data e filtra jogadores das 2 equipas
        jogo.addActionListener(e -> aoSelecionarJogo());
        // substância só ativa se positivo
        resultado.addActionListener(e ->
                substancia.setEnabled(resultado.getSelectedItem() == ResultadoTeste.POSITIVO));
        substancia.setEnabled(false);

        JButton btnRegistar = new JButton("Registar Teste");
        btnRegistar.addActionListener(e -> registar());

        JPanel form = Ui.form(
                new JLabel("Jogo:"), jogo,
                new JLabel("Data do teste:"), data,
                new JLabel(), lblTestesFeitos,
                new JLabel("Jogador:"), jogador,
                new JLabel("Resultado:"), resultado,
                new JLabel("Substância (se positivo):"), substancia,
                new JLabel(), btnRegistar);
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(form, BorderLayout.NORTH);
        add(wrap, BorderLayout.CENTER);
        atualizar();
    }

    private void aoSelecionarJogo() {
        Object sel = jogo.getSelectedItem();
        if (!(sel instanceof Jogo j)) {
            data.setText("");
            lblTestesFeitos.setText("Testes neste jogo: — / 2");
            jogador.setModel(new DefaultComboBoxModel<>());
            return;
        }
        // data automática
        data.setText(j.getData() != null ? Ui.fmt(j.getData()) : "");

        // contar testes já feitos para ESTE JOGO (não para a data — pode haver +1 jogo no mesmo dia)
        long feitos = ds.getTestes().stream().filter(t -> t.getJogo() == j).count();
        lblTestesFeitos.setText("Testes neste jogo: " + feitos + " / 2"
                + (feitos >= 2 ? "  ⚠ limite atingido" : ""));
        lblTestesFeitos.setForeground(feitos >= 2 ? Color.RED : Ui.MUTED);

        // filtrar jogadores das duas equipas do jogo
        List<Jogador> js = new ArrayList<>();
        if (j.getEquipa1() != null) js.addAll(j.getEquipa1().getJogadores());
        if (j.getEquipa2() != null) js.addAll(j.getEquipa2().getJogadores());
        jogador.setModel(new DefaultComboBoxModel<>(js.toArray(new Jogador[0])));
    }

    private void registar() {
        try {
            Object sel = jogo.getSelectedItem();
            if (!(sel instanceof Jogo j)) throw new IllegalArgumentException("Selecione o jogo.");
            Jogador jog = (Jogador) jogador.getSelectedItem();
            if (jog == null) throw new IllegalArgumentException("Selecione o jogador testado.");
            ResultadoTeste r = (ResultadoTeste) resultado.getSelectedItem();
            Substancia s = r == ResultadoTeste.POSITIVO ? (Substancia) substancia.getSelectedItem() : null;
            TesteDoping t = service.registarTeste(jog, j, r, s);
            aoSelecionarJogo(); // atualiza contador
            String msg = "Teste registado com sucesso.";
            if (!"-".equals(t.getCastigoAplicado()))
                msg += "\nCastigo aplicado automaticamente: " + t.getCastigoAplicado();
            Ui.info(this, msg);
        } catch (RuntimeException ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        // combo de jogos: opção vazia + todos os jogos realizados
        DefaultComboBoxModel<Object> mj = new DefaultComboBoxModel<>();
        mj.addElement("— Selecione o jogo —");
        ds.getJogos().stream().filter(Jogo::isRealizado).forEach(mj::addElement);
        jogo.setModel(mj);
        substancia.setModel(new DefaultComboBoxModel<>(ds.getSubstancias().toArray(new Substancia[0])));
        data.setText("");
        lblTestesFeitos.setText("Testes neste jogo: — / 2");
    }
}
