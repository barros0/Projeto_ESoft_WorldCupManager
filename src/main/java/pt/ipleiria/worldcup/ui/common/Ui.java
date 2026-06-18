package pt.ipleiria.worldcup.ui.common;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Utilitários partilhados de UI — paleta moderna (indigo / slate). */
public final class Ui {
    private Ui() {}

    /** Cor principal — indigo moderno. */
    public static final Color PRIMARY = new Color(79, 70, 229);      // #4F46E5
    /** Versão suave da cor principal (seleções, sublinhados inativos). */
    public static final Color PRIMARY_SOFT = new Color(224, 231, 255); // #E0E7FF
    /** Cor de destaque secundária — esmeralda (sucesso/positivo). */
    public static final Color ACCENT = new Color(16, 185, 129);      // #10B981
    /** Fundo claro dos painéis. */
    public static final Color LIGHT = new Color(248, 250, 252);      // #F8FAFC
    /** Fundo escuro (cabeçalhos / barras). */
    public static final Color DARK = new Color(15, 23, 42);          // #0F172A
    /** Texto principal. */
    public static final Color INK = new Color(30, 41, 59);           // #1E293B
    /** Texto secundário. */
    public static final Color MUTED = new Color(100, 116, 139);      // #64748B
    /** Linhas e contornos subtis. */
    public static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0

    public static JLabel title(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 20f));
        l.setForeground(INK);
        l.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return l;
    }

    /** Botão de ação principal (cheio, cor primária). */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Cartão branco com contorno subtil e cantos suaves. */
    public static JPanel card(String titulo, Component conteudo) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)));
        if (titulo != null) {
            JLabel t = new JLabel(titulo);
            t.setFont(t.getFont().deriveFont(Font.BOLD, 13f));
            t.setForeground(MUTED);
            t.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
            p.add(t, BorderLayout.NORTH);
        }
        p.add(conteudo, BorderLayout.CENTER);
        return p;
    }

    /**
     * Envolve um painel num JScrollPane horizontal (nunca faz scroll vertical)
     * com altura mínima garantida — evita que campos/botões fiquem fora do ecrã.
     */
    public static JScrollPane formScroll(JPanel formPanel) {
        formPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, formPanel.getPreferredSize().height + 20));
        JScrollPane sp = new JScrollPane(formPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.setMinimumSize(new Dimension(0, formPanel.getPreferredSize().height + 24));
        sp.setPreferredSize(new Dimension(Integer.MAX_VALUE, formPanel.getPreferredSize().height + 24));
        return sp;
    }

    public static JPanel form(Component... pairs) {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 8, 5, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < pairs.length; i++) {
            c.gridx = i % 2;
            c.gridy = i / 2;
            c.weightx = (i % 2 == 0) ? 0 : 1;
            if (pairs[i] instanceof JLabel l && i % 2 == 0) l.setForeground(MUTED);
            p.add(pairs[i], c);
        }
        return p;
    }

    /** Carrega e redimensiona uma imagem do disco; devolve null se o caminho for inválido. */
    public static ImageIcon imagem(String caminho, int w, int h) {
        if (caminho == null || caminho.isBlank()) return null;
        java.io.File f = new java.io.File(caminho);
        if (!f.exists()) return null;
        ImageIcon ic = new ImageIcon(caminho);
        if (ic.getIconWidth() <= 0) return null;
        return new ImageIcon(ic.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

    /** Modelo de tabela cuja PRIMEIRA coluna mostra ícones (miniaturas de imagens). */
    public static DefaultTableModel modelComIcone(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Icon.class : Object.class; }
        };
    }

    public static DefaultTableModel model(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    public static JScrollPane table(JTable t) {
        t.setRowHeight(28);
        t.setFillsViewportHeight(true);
        t.setShowVerticalLines(false);
        t.setGridColor(BORDER);
        t.setSelectionBackground(PRIMARY_SOFT);
        t.setSelectionForeground(INK);
        t.setIntercellSpacing(new Dimension(0, 1));
        if (t.getTableHeader() != null) {
            t.getTableHeader().setBackground(LIGHT);
            t.getTableHeader().setForeground(MUTED);
            t.getTableHeader().setFont(t.getTableHeader().getFont().deriveFont(Font.BOLD, 12f));
        }
        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    public static void erro(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component parent, String msg) {
        return JOptionPane.showConfirmDialog(parent, msg, "Confirmação",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    public static int parseInt(String s, String campo) {
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Campo \"" + campo + "\" inválido: indique um número inteiro."); }
    }

    public static double parseDouble(String s, String campo) {
        try { return Double.parseDouble(s.trim().replace(",", ".")); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Campo \"" + campo + "\" inválido: indique um número."); }
    }

    /** Formato europeu de datas usado em toda a aplicação. */
    public static final java.time.format.DateTimeFormatter DATA_EU =
            java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /** Formata uma data no formato europeu (DD-MM-AAAA); devolve "-" se nula. */
    public static String fmt(java.time.LocalDate d) {
        return d == null ? "-" : d.format(DATA_EU);
    }

    public static java.time.LocalDate parseDate(String s, String campo) {
        try {
            java.time.LocalDate d = java.time.LocalDate.parse(s.trim().replace('/', '-'), DATA_EU);
            if (d.getYear() < 1900 || d.getYear() > 2100)
                throw new IllegalArgumentException("Campo \"" + campo + "\" inválido: ano fora do intervalo (1900-2100).");
            return d;
        } catch (IllegalArgumentException ex) { throw ex; }
        catch (Exception e) { throw new IllegalArgumentException("Campo \"" + campo + "\" inválido: use o formato DD-MM-AAAA."); }
    }

    public static java.time.LocalTime parseTime(String s, String campo) {
        try { return java.time.LocalTime.parse(s.trim()); }
        catch (Exception e) { throw new IllegalArgumentException("Campo \"" + campo + "\" inválido: use o formato HH:MM."); }
    }
}
