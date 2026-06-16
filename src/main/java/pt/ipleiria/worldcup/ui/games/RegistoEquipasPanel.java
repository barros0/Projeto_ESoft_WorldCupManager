package pt.ipleiria.worldcup.ui.games;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Enums.Confederacao;
import pt.ipleiria.worldcup.model.Equipa;
import pt.ipleiria.worldcup.service.CampeonatoService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class RegistoEquipasPanel extends JPanel implements JogosPanel.Atualizavel {

    private final CampeonatoService service = new CampeonatoService();
    private final JTextField txtPais = new JTextField(12);
    private final JTextField txtSigla = new JTextField(5);
    private final JTextField txtBandeira = new JTextField(12);
    private final JLabel lblPreview = new JLabel();   // pré-visualização da bandeira escolhida
    private final JComboBox<Confederacao> cmbConf = new JComboBox<>(Confederacao.values());
    private final JComboBox<Integer> cmbPote = new JComboBox<>(new Integer[]{1, 2, 3, 4});

    /** Modelo com a 1.ª coluna do tipo Icon para a miniatura da bandeira. */
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Bandeira", "País", "Sigla", "Confederação", "Pote", "Grupo"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
        @Override public Class<?> getColumnClass(int c) {
            return c == 0 ? Icon.class : Object.class;
        }
    };
    private final JTable tabela = new JTable(model);
    private final JLabel lblContagem = new JLabel();

    public RegistoEquipasPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Equipas Registadas"), BorderLayout.NORTH);

        lblPreview.setPreferredSize(new Dimension(36, 24));
        lblPreview.setBorder(BorderFactory.createLineBorder(Ui.BORDER));
        lblPreview.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnFoto = new JButton("Adicionar foto...");
        btnFoto.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagens (png, jpg, gif)", "png", "jpg", "jpeg", "gif"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtBandeira.setText(fc.getSelectedFile().getAbsolutePath());
                lblPreview.setIcon(Ui.imagem(txtBandeira.getText(), 32, 20));
            }
        });

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("País:")); form.add(txtPais);
        form.add(new JLabel("Sigla:")); form.add(txtSigla);
        form.add(new JLabel("Bandeira:")); form.add(txtBandeira);
        form.add(btnFoto); form.add(lblPreview);
        form.add(new JLabel("Confederação:")); form.add(cmbConf);
        form.add(new JLabel("Pote:")); form.add(cmbPote);

        JButton btnAdd = new JButton("Adicionar Equipa");
        JButton btnEditar = new JButton("Editar Selecionada");
        JButton btnRemover = new JButton("Remover Selecionada");
        btnAdd.addActionListener(e -> adicionar());
        btnEditar.addActionListener(e -> editar());
        btnRemover.addActionListener(e -> remover());
        form.add(btnAdd);

        JPanel sul = new JPanel(new FlowLayout(FlowLayout.LEFT));
        sul.add(btnEditar);
        sul.add(btnRemover);
        sul.add(lblContagem);

        JPanel centro = new JPanel(new BorderLayout());
        centro.add(form, BorderLayout.NORTH);
        JScrollPane sp = Ui.table(tabela);
        tabela.setRowHeight(30); // espaço para a miniatura
        tabela.getColumnModel().getColumn(0).setMaxWidth(70);
        centro.add(sp, BorderLayout.CENTER);
        centro.add(sul, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);
        atualizar();
    }


    private void adicionar() {
        try {
            if (txtPais.getText().isBlank() || txtSigla.getText().isBlank())
                throw new IllegalArgumentException("Preencha o país e a sigla.");
            service.adicionarEquipa(new Equipa(txtPais.getText().trim(), txtBandeira.getText().trim(),
                    txtSigla.getText().trim().toUpperCase(), (Confederacao) cmbConf.getSelectedItem(),
                    (Integer) cmbPote.getSelectedItem()));
            txtPais.setText(""); txtSigla.setText(""); txtBandeira.setText("");
            lblPreview.setIcon(null);
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void editar() {
        int row = tabela.getSelectedRow();
        if (row < 0) { Ui.erro(this, "Selecione uma equipa na tabela."); return; }
        Equipa eq = DataStore.getInstance().getEquipas().get(row);

        JTextField fPais = new JTextField(eq.getPais(), 12);
        JTextField fSigla = new JTextField(eq.getSigla(), 5);
        JTextField fBandeira = new JTextField(eq.getBandeira(), 12);
        JLabel fPreview = new JLabel(Ui.imagem(eq.getBandeira(), 32, 20));
        JButton fFoto = new JButton("Escolher...");
        fFoto.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Imagens (png, jpg, gif)", "png", "jpg", "jpeg", "gif"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                fBandeira.setText(fc.getSelectedFile().getAbsolutePath());
                fPreview.setIcon(Ui.imagem(fBandeira.getText(), 32, 20));
            }
        });
        JComboBox<Confederacao> fConf = new JComboBox<>(Confederacao.values());
        fConf.setSelectedItem(eq.getConfederacao());
        JComboBox<Integer> fPote = new JComboBox<>(new Integer[]{1, 2, 3, 4});
        fPote.setSelectedItem(eq.getPote());

        JPanel bandeiraLinha = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bandeiraLinha.add(fBandeira); bandeiraLinha.add(fFoto); bandeiraLinha.add(fPreview);
        JPanel p = Ui.form(
                new JLabel("País:"), fPais,
                new JLabel("Sigla:"), fSigla,
                new JLabel("Bandeira:"), bandeiraLinha,
                new JLabel("Confederação:"), fConf,
                new JLabel("Pote:"), fPote);

        if (JOptionPane.showConfirmDialog(this, p, "Editar Equipa — " + eq.getPais(),
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        try {
            service.editarEquipa(eq, fPais.getText(), fBandeira.getText(), fSigla.getText(),
                    (Confederacao) fConf.getSelectedItem(), (Integer) fPote.getSelectedItem());
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    private void remover() {
        int row = tabela.getSelectedRow();
        if (row < 0) { Ui.erro(this, "Selecione uma equipa na tabela."); return; }
        try {
            service.removerEquipa(DataStore.getInstance().getEquipas().get(row));
            atualizar();
        } catch (Exception ex) { Ui.erro(this, ex.getMessage()); }
    }

    @Override public void atualizar() {
        model.setRowCount(0);
        List<Equipa> eqs = DataStore.getInstance().getEquipas();
        for (Equipa e : eqs)
            model.addRow(new Object[]{ Ui.imagem(e.getBandeira(), 32, 20),
                    e.getPais(), e.getSigla(), e.getConfederacao(),
                    "P" + e.getPote(), e.getGrupo() == null ? "—" : e.getGrupo().getNome() });
        var c = DataStore.getInstance().getCampeonato();
        lblContagem.setText("   Registadas: " + eqs.size() + (c != null ? " / " + c.getTotalEquipas() : ""));
    }
}
