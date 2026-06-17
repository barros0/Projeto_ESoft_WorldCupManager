package pt.ipleiria.worldcup.ui.ticket;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.service.BilheteService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;

public class VenderBilhetePanel extends JPanel implements BilheteiraPanel.Atualizavel {

    private final BilheteService service = new BilheteService();
    private final JComboBox<Jogo> cmbJogo = new JComboBox<>();
    private final JComboBox<Setor> cmbSetor = new JComboBox<>();
    private final JTextField txtComprador = new JTextField(18);
    private final JSpinner spnQtd = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));
    private final JTextField txtDisponiveis = new JTextField(10);
    private final JTextField txtTotal = new JTextField(10);

    public VenderBilhetePanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Vender Bilhete"), BorderLayout.NORTH);

        // Campos calculados automaticamente são readonly
        txtDisponiveis.setEditable(false);
        txtTotal.setEditable(false);

        JPanel form = Ui.form(
                new JLabel("Jogo:"), cmbJogo,
                new JLabel("Bancada/Setor:"), cmbSetor,
                new JLabel("Nome do comprador:"), txtComprador,
                new JLabel("Quantidade:"), spnQtd,
                new JLabel("Lugares disponíveis:"), txtDisponiveis,
                new JLabel("Valor total (€):"), txtTotal);

        JButton btnVender = new JButton("Confirmar Venda");
        JButton btnLimpar = new JButton("Limpar");
        btnVender.addActionListener(e -> vender());
        btnLimpar.addActionListener(e -> limpar());

        cmbJogo.addActionListener(e -> jogoMudou());
        cmbSetor.addActionListener(e -> recalcular());
        spnQtd.addChangeListener(e -> recalcular());

        JPanel botoes = new JPanel();
        botoes.add(btnVender);
        botoes.add(btnLimpar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        centro.add(botoes, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    @Override public void atualizar() {
        Jogo sel = (Jogo) cmbJogo.getSelectedItem();
        cmbJogo.removeAllItems();
        for (Jogo j : DataStore.getInstance().getJogos())
            if (!j.isRealizado() && j.getEstadio() != null) cmbJogo.addItem(j);
        if (sel != null) cmbJogo.setSelectedItem(sel);
        jogoMudou();
    }

    private void jogoMudou() {
        cmbSetor.removeAllItems();
        Jogo j = (Jogo) cmbJogo.getSelectedItem();
        if (j != null && j.getEstadio() != null)
            for (Setor s : j.getEstadio().getSetores()) cmbSetor.addItem(s);
        recalcular();
    }

    private void recalcular() {
        Jogo j = (Jogo) cmbJogo.getSelectedItem();
        Setor s = (Setor) cmbSetor.getSelectedItem();
        if (j == null || s == null) { txtDisponiveis.setText(""); txtTotal.setText(""); return; }
        txtDisponiveis.setText(String.valueOf(j.lugaresDisponiveis(s)));
        int qtd = (int) spnQtd.getValue();
        txtTotal.setText(String.format("%.2f", qtd * j.getPrecoBase()));
    }

    private void vender() {
        try {
            Jogo j = (Jogo) cmbJogo.getSelectedItem();
            Setor s = (Setor) cmbSetor.getSelectedItem();
            if (j == null || s == null) throw new IllegalArgumentException("Selecione o jogo e a bancada/setor.");
            Bilhete b = service.venderBilhete(txtComprador.getText(), j, s, (int) spnQtd.getValue());
            Ui.info(this, "Venda registada!\nCódigo: " + b.getCodigo()
                    + "\nFicheiro gerado em bilhetes/" + b.getCodigo() + ".txt");
            limpar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void limpar() {
        txtComprador.setText("");
        spnQtd.setValue(1);
        recalcular();
    }
}
