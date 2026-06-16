package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Estadio;
import pt.ipleiria.worldcup.model.Setor;
import pt.ipleiria.worldcup.service.JogoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EstadiosPanel extends JPanel implements JogosPanel.Atualizavel {

    private final JogoService service = new JogoService();
    private final JTextField txtNome = new JTextField(12);
    private final JTextField txtEnd = new JTextField(14);
    private final JTextField txtFoto = new JTextField(10);
    private final JSpinner[] caps = new JSpinner[4];
    private final DefaultTableModel model = Ui.modelComIcone("Foto", "Nome", "Endereço", "Setores A/B/C/D", "Capacidade total");
    private final JTable tabela = new JTable(model);

    public EstadiosPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Estádios (cada estádio tem obrigatoriamente 4 setores)"), BorderLayout.NORTH);

        JButton btnFoto = new JButton("Fotografia...");
        btnFoto.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                txtFoto.setText(fc.getSelectedFile().getAbsolutePath());
        });

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Nome:")); form.add(txtNome);
        form.add(new JLabel("Endereço:")); form.add(txtEnd);
        form.add(btnFoto); form.add(txtFoto);
        String[] nomes = { "A", "B", "C", "D" };
        for (int i = 0; i < 4; i++) {
            caps[i] = new JSpinner(new SpinnerNumberModel(1000, 1, 100000, 100));
            form.add(new JLabel("Setor " + nomes[i] + ":"));
            form.add(caps[i]);
        }
        JButton btnAdd = new JButton("Adicionar estádio");
        btnAdd.addActionListener(e -> adicionar());
        form.add(btnAdd);

        JButton btnRem = new JButton("Remover selecionado");
        JButton btnEditarFoto = new JButton("Editar foto do selecionado...");
        btnRem.addActionListener(e -> remover());
        btnEditarFoto.addActionListener(e -> editarFoto());
        JPanel sul = new JPanel();
        sul.add(btnRem);
        sul.add(btnEditarFoto);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        JScrollPane sp = Ui.table(tabela);
        tabela.setRowHeight(46);
        tabela.getColumnModel().getColumn(0).setMaxWidth(90);
        // duplo clique numa linha amplia a fotografia
        tabela.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) ampliarFoto();
            }
        });
        centro.add(sp, BorderLayout.CENTER);
        centro.add(sul, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }

    private void adicionar() {
        try {
            if (txtNome.getText().isBlank() || txtEnd.getText().isBlank())
                throw new IllegalArgumentException("Preencha o nome e o endereço.");
            List<Setor> setores = List.of(
                    new Setor("A", (int) caps[0].getValue()), new Setor("B", (int) caps[1].getValue()),
                    new Setor("C", (int) caps[2].getValue()), new Setor("D", (int) caps[3].getValue()));
            service.adicionarEstadio(new Estadio(txtNome.getText().trim(), txtEnd.getText().trim(),
                    txtFoto.getText().trim(), setores));
            txtNome.setText(""); txtEnd.setText(""); txtFoto.setText("");
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void remover() {
        int row = tabela.getSelectedRow();
        if (row < 0) { Ui.erro(this, "Selecione um estádio na tabela."); return; }
        try {
            service.removerEstadio(DataStore.getInstance().getEstadios().get(row));
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void editarFoto() {
        int row = tabela.getSelectedRow();
        if (row < 0) { Ui.erro(this, "Selecione um estádio na tabela."); return; }
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            DataStore.getInstance().getEstadios().get(row).setFoto(fc.getSelectedFile().getAbsolutePath());
            atualizar();
            Ui.info(this, "Foto do estádio atualizada.");
        }
    }

    @Override public void atualizar() {
        model.setRowCount(0);
        for (Estadio e : DataStore.getInstance().getEstadios()) {
            StringBuilder s = new StringBuilder();
            for (Setor st : e.getSetores()) s.append(st.getCapacidade()).append("/");
            s.setLength(s.length() - 1);
            model.addRow(new Object[]{ Ui.imagem(e.getFoto(), 70, 40), e.getNome(), e.getEndereco(), s, e.getCapacidadeTotal() });
        }
    }

    /** Mostra a fotografia do estádio selecionado em tamanho grande. */
    private void ampliarFoto() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        Estadio e = DataStore.getInstance().getEstadios().get(row);
        ImageIcon grande = Ui.imagem(e.getFoto(), 640, 400);
        if (grande == null) { Ui.info(this, "Este estádio não tem fotografia válida."); return; }
        JOptionPane.showMessageDialog(this, new JLabel(grande), e.getNome(), JOptionPane.PLAIN_MESSAGE);
    }
}
