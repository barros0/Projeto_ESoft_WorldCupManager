package pt.ipleiria.worldcup.ui.ticket;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Bilhete;
import pt.ipleiria.worldcup.service.BilheteService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CancelarBilhetePanel extends JPanel implements BilheteiraPanel.Atualizavel {

    private final BilheteService service = new BilheteService();
    private final JTextField txtCodigo = new JTextField(14);
    private final DefaultTableModel model =
            Ui.model("Código", "Comprador", "Jogo", "Bancada", "Valor", "Estado");
    private final JTable tabela = new JTable(model);
    private final List<Bilhete> linhas = new ArrayList<>();

    public CancelarBilhetePanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Cancelar Bilhete"), BorderLayout.NORTH);

        JButton btnPesquisar = new JButton("Pesquisar");
        JButton btnLimpar = new JButton("Mostrar todos");
        JButton btnConfirmar = new JButton("Cancelar bilhete selecionado");

        btnPesquisar.addActionListener(e -> pesquisar());
        btnLimpar.addActionListener(e -> { txtCodigo.setText(""); refrescar(); });
        btnConfirmar.addActionListener(e -> confirmar());
        txtCodigo.addActionListener(e -> pesquisar()); // Enter pesquisa

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Pesquisar código:"));
        topo.add(txtCodigo);
        topo.add(btnPesquisar);
        topo.add(btnLimpar);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botoes.add(btnConfirmar);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(Ui.formScroll(topo), BorderLayout.NORTH);
        centro.add(Ui.table(tabela), BorderLayout.CENTER);
        centro.add(botoes, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        refrescar();
    }

    /** Por omissão mostra TODOS os bilhetes vendidos. */
    @Override public void atualizar() {
        txtCodigo.setText("");
        refrescar();
    }

    private void refrescar() {
        model.setRowCount(0);
        linhas.clear();
        for (Bilhete b : DataStore.getInstance().getBilhetes()) {
            linhas.add(b);
            model.addRow(linha(b));
        }
    }

    private Object[] linha(Bilhete b) {
        return new Object[]{ b.getCodigo(), b.getComprador(), b.getJogo().descricaoCurta(),
                b.getSetor(), String.format("%.2f €", b.getValorTotal()), b.getEstado() };
    }

    private void pesquisar() {
        if (txtCodigo.getText().isBlank()) { refrescar(); return; }
        Bilhete b = service.pesquisarPorCodigo(txtCodigo.getText());
        if (b == null) { Ui.erro(this, "Bilhete não encontrado."); return; }
        model.setRowCount(0);
        linhas.clear();
        linhas.add(b);
        model.addRow(linha(b));
        tabela.setRowSelectionInterval(0, 0);
    }

    private void confirmar() {
        int r = tabela.getSelectedRow();
        if (r < 0) { Ui.erro(this, "Selecione um bilhete na tabela."); return; }
        Bilhete b = linhas.get(r);
        try {
            if (!Ui.confirmar(this, "Cancelar o bilhete " + b.getCodigo()
                    + "?\nO assento será libertado e o valor de "
                    + String.format("%.2f €", b.getValorTotal()) + " reembolsado.")) return;
            service.cancelarBilhete(b);
            Ui.info(this, "Bilhete cancelado e reembolso registado.");
            refrescar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }
}
