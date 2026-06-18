package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Arbitro;
import pt.ipleiria.worldcup.service.JogoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ArbitrosPanel extends JPanel implements JogosPanel.Atualizavel {

    private final JogoService service = new JogoService();
    private final JTextField txtNome = new JTextField(14);
    private final JTextField txtNac = new JTextField(10);
    private final DefaultTableModel model = Ui.model("Nome", "Nacionalidade");
    private final JTable tabela = new JTable(model);

    public ArbitrosPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Árbitros"), BorderLayout.NORTH);

        JButton btnAdd = new JButton("Adicionar");
        JButton btnRem = new JButton("Remover selecionado");
        btnAdd.addActionListener(e -> adicionar());
        btnRem.addActionListener(e -> remover());

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Nome:")); form.add(txtNome);
        form.add(new JLabel("Nacionalidade:")); form.add(txtNac);
        form.add(btnAdd);

        JPanel sul = new JPanel();
        sul.add(btnRem);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(Ui.formScroll(form), BorderLayout.NORTH);
        centro.add(Ui.table(tabela), BorderLayout.CENTER);
        centro.add(sul, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    private void adicionar() {
        try {
            if (txtNome.getText().isBlank() || txtNac.getText().isBlank())
                throw new IllegalArgumentException("Preencha o nome e a nacionalidade.");
            service.adicionarArbitro(txtNome.getText().trim(), txtNac.getText().trim());
            txtNome.setText(""); txtNac.setText("");
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void remover() {
        int row = tabela.getSelectedRow();
        if (row < 0) { Ui.erro(this, "Selecione um árbitro na tabela."); return; }
        try {
            service.removerArbitro(DataStore.getInstance().getArbitros().get(row));
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        model.setRowCount(0);
        for (Arbitro a : DataStore.getInstance().getArbitros())
            model.addRow(new Object[]{ a.getNome(), a.getNacionalidade() });
    }
}
