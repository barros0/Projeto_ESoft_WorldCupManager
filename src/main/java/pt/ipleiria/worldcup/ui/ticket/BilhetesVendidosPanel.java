package pt.ipleiria.worldcup.ui.ticket;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Bilhete;
import pt.ipleiria.worldcup.model.Jogo;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class BilhetesVendidosPanel extends JPanel implements BilheteiraPanel.Atualizavel {

    private final JTextField txtPesquisa = new JTextField(14);
    private final JComboBox<Object> cmbJogo = new JComboBox<>();
    private final DefaultTableModel model = Ui.model("Código", "Comprador", "Jogo", "Bancada", "Estado");

    public BilhetesVendidosPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Bilhetes Vendidos"), BorderLayout.NORTH);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Pesquisar código:"));
        filtros.add(txtPesquisa);
        filtros.add(new JLabel("Filtrar por jogo:"));
        filtros.add(cmbJogo);

        // Tabela atualizada dinamicamente conforme os filtros
        txtPesquisa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { refrescar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { refrescar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { refrescar(); }
        });
        cmbJogo.addActionListener(e -> refrescar());

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(filtros, BorderLayout.NORTH);
        centro.add(Ui.table(new JTable(model)), BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    @Override public void atualizar() {
        cmbJogo.removeAllItems();
        cmbJogo.addItem("— Todos os jogos —");
        for (Jogo j : DataStore.getInstance().getJogos()) cmbJogo.addItem(j);
        refrescar();
    }

    private void refrescar() {
        model.setRowCount(0);
        String filtroCodigo = txtPesquisa.getText().trim().toLowerCase();
        Object filtroJogo = cmbJogo.getSelectedItem();
        for (Bilhete b : DataStore.getInstance().getBilhetes()) {
            if (!filtroCodigo.isEmpty() && !b.getCodigo().toLowerCase().contains(filtroCodigo)) continue;
            if (filtroJogo instanceof Jogo j && b.getJogo() != j) continue;
            model.addRow(new Object[]{ b.getCodigo(), b.getComprador(),
                    b.getJogo().descricaoCurta(), b.getSetor(), b.getEstado() });
        }
    }
}
