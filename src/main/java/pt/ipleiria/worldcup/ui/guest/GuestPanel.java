package pt.ipleiria.worldcup.ui.guest;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.model.Enums.Fase;
import pt.ipleiria.worldcup.model.Enums.TipoEvento;
import pt.ipleiria.worldcup.service.EstatisticaService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Área pública (Guest) — acesso sem autenticação (Figuras 25 a 33):
 * Torneio, Equipas, Jogos, Estádios, Árbitros e Estatísticas públicas.
 */
public class GuestPanel extends JTabbedPane {
    public interface Atualizavel { void atualizar(); }

    public GuestPanel() {
        addTab("Torneio", new TorneioView());
        addTab("Equipas", new EquipasView());
        addTab("Jogos", new JogosView());
        addTab("Estádios", new EstadiosView());
        addTab("Árbitros", new ArbitrosView());
        addTab("Estatísticas", new EstatisticasView());
        addChangeListener(e -> {
            var c = getSelectedComponent();
            if (c instanceof Atualizavel a) a.atualizar();
        });
    }

    // ------------------------------------------------------------------
    /** Ver Torneio: estrutura (grupos + fases eliminatórias) e equipas por grupo. */
    static class TorneioView extends JPanel implements Atualizavel {
        private final DataStore ds = DataStore.getInstance();
        private final JPanel grupos = new JPanel();
        private final JLabel fases = new JLabel();

        TorneioView() {
            setLayout(new BorderLayout());
            setBackground(Ui.LIGHT);
            add(Ui.title("Torneio"), BorderLayout.NORTH);
            grupos.setOpaque(false);
            JPanel centro = new JPanel(new BorderLayout());
            centro.setOpaque(false);
            fases.setBorder(BorderFactory.createEmptyBorder(5, 12, 10, 12));
            centro.add(fases, BorderLayout.NORTH);
            centro.add(new JScrollPane(grupos), BorderLayout.CENTER);
            add(centro, BorderLayout.CENTER);
            atualizar();
        }

        @Override public void atualizar() {
            grupos.removeAll();
            Campeonato c = ds.getCampeonato();
            if (c == null) {
                fases.setText("Ainda não foi configurado nenhum campeonato.");
            } else {
                StringBuilder sb = new StringBuilder("<html><b>" + c.getNome() + "</b> — "
                        + c.getNumGrupos() + " grupos · Fases eliminatórias: ");
                List<Fase> fs = c.getFasesEliminatorias();
                for (int i = 0; i < fs.size(); i++)
                    sb.append(i > 0 ? " → " : "").append(fs.get(i));
                fases.setText(sb.append("</html>").toString());

                grupos.setLayout(new GridLayout(0, Math.min(4, Math.max(1, c.getGrupos().size())), 10, 10));
                for (Grupo g : c.getGrupos()) {
                    JPanel card = new JPanel(new BorderLayout());
                    card.setBackground(Color.WHITE);
                    card.setBorder(BorderFactory.createTitledBorder("Grupo " + g.getNome()));
                    DefaultTableModel m = Ui.modelComIcone("", "Equipa", "Sigla", "Pote");
                    for (Equipa e : g.getEquipas())
                        m.addRow(new Object[]{Ui.imagem(e.getBandeira(), 28, 18),
                                e.getPais(), e.getSigla(), "P" + e.getPote()});
                    JTable tg = new JTable(m);
                    tg.setRowHeight(26);
                    JScrollPane spg = Ui.table(tg);
                    tg.getColumnModel().getColumn(0).setMaxWidth(44);
                    card.add(spg, BorderLayout.CENTER);
                    grupos.add(card);
                }
            }
            grupos.revalidate();
            grupos.repaint();
        }
    }

    // ------------------------------------------------------------------
    /** Ver Equipas: lista, detalhes e jogadores. */
    static class EquipasView extends JPanel implements Atualizavel {
        private final DataStore ds = DataStore.getInstance();
        private final JTextField pesquisa = new JTextField(15);
        private final DefaultTableModel modelo =
                Ui.modelComIcone("Bandeira", "País", "Sigla", "Confederação", "Grupo");
        private final JTable tabela = new JTable(modelo);
        private final DefaultTableModel jogadores = Ui.model("Nr.", "Nome", "Posição");
        private java.util.List<Equipa> linhas = java.util.List.of();

        EquipasView() {
            setLayout(new BorderLayout());
            setBackground(Ui.LIGHT);
            JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
            topo.setOpaque(false);
            topo.add(Ui.title("Equipas"));
            topo.add(new JLabel("Pesquisar:"));
            topo.add(pesquisa);
            pesquisa.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) { refrescar(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { refrescar(); }
                public void changedUpdate(javax.swing.event.DocumentEvent e) { refrescar(); }
            });
            add(topo, BorderLayout.NORTH);

            JPanel dir = new JPanel(new BorderLayout());
            dir.setOpaque(false);
            dir.add(new JLabel("  Jogadores da equipa selecionada"), BorderLayout.NORTH);
            dir.add(Ui.table(new JTable(jogadores)), BorderLayout.CENTER);

            tabela.setRowHeight(28);
            JScrollPane spT = Ui.table(tabela);
            tabela.getColumnModel().getColumn(0).setMaxWidth(70);
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spT, dir);
            split.setResizeWeight(0.6);
            add(split, BorderLayout.CENTER);

            tabela.getSelectionModel().addListSelectionListener(e -> mostrarJogadores());
            atualizar();
        }

        @Override public void atualizar() { refrescar(); }

        private void refrescar() {
            String q = pesquisa.getText().trim().toLowerCase();
            modelo.setRowCount(0);
            linhas = ds.getEquipas().stream()
                    .filter(e -> q.isEmpty() || e.getPais().toLowerCase().contains(q)
                            || e.getSigla().toLowerCase().contains(q)).toList();
            for (Equipa e : linhas)
                modelo.addRow(new Object[]{Ui.imagem(e.getBandeira(), 32, 20), e.getPais(), e.getSigla(),
                        e.getConfederacao(), e.getGrupo() == null ? "-" : e.getGrupo().getNome()});
            jogadores.setRowCount(0);
        }

        private void mostrarJogadores() {
            jogadores.setRowCount(0);
            int r = tabela.getSelectedRow();
            if (r < 0 || r >= linhas.size()) return;
            for (Jogador j : linhas.get(r).getJogadores())
                jogadores.addRow(new Object[]{j.getNumeroCamisola(), j.getNome(), j.getPosicao()});
        }
    }

    // ------------------------------------------------------------------
    /** Ver Jogos: futuros e passados (resumo, eventos e estatísticas). */
    static class JogosView extends JPanel implements Atualizavel {
        private final DataStore ds = DataStore.getInstance();
        private final DefaultTableModel modelo =
                Ui.model("Jogo", "Fase", "Data", "Hora", "Estádio", "Estado");
        private final JTable tabela = new JTable(modelo);
        private final JTextArea detalhes = new JTextArea();
        private java.util.List<Jogo> linhas = java.util.List.of();

        JogosView() {
            setLayout(new BorderLayout());
            setBackground(Ui.LIGHT);
            add(Ui.title("Jogos"), BorderLayout.NORTH);
            detalhes.setEditable(false);
            detalhes.setMargin(new Insets(8, 8, 8, 8));
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    Ui.table(tabela), new JScrollPane(detalhes));
            split.setResizeWeight(0.55);
            add(split, BorderLayout.CENTER);
            tabela.getSelectionModel().addListSelectionListener(e -> mostrarDetalhes());
            atualizar();
        }

        @Override public void atualizar() {
            modelo.setRowCount(0);
            linhas = List.copyOf(ds.getJogos());
            for (Jogo j : linhas)
                modelo.addRow(new Object[]{
                        j.getEquipa1() + " vs " + j.getEquipa2(), j.getFase(),
                        Ui.fmt(j.getData()), j.getHora(), j.getEstadio(),
                        j.isRealizado() ? j.getGolos1() + " - " + j.getGolos2() : "Por jogar"});
            detalhes.setText("Selecione um jogo para ver os detalhes.");
        }

        private void mostrarDetalhes() {
            int r = tabela.getSelectedRow();
            if (r < 0 || r >= linhas.size()) return;
            Jogo j = linhas.get(r);
            StringBuilder sb = new StringBuilder();
            sb.append(j.getEquipa1()).append("  vs  ").append(j.getEquipa2()).append('\n');
            sb.append("Fase: ").append(j.getFase()).append('\n');
            sb.append("Data: ").append(Ui.fmt(j.getData())).append("  Hora: ").append(j.getHora()).append('\n');
            sb.append("Estádio: ").append(j.getEstadio()).append("\n\n");
            if (!j.isRealizado()) {
                sb.append("Jogo ainda por realizar.");
            } else {
                sb.append("Resultado: ").append(j.getGolos1()).append(" - ").append(j.getGolos2()).append('\n');
                if (j.isProlongamento())
                    sb.append("Prolongamento: ").append(j.getGolosProl1()).append(" - ").append(j.getGolosProl2()).append('\n');
                if (j.isPenaltis())
                    sb.append("Penáltis: ").append(j.getPenaltis1()).append(" - ").append(j.getPenaltis2()).append('\n');
                sb.append("\nEventos:\n");
                if (j.getEventos().isEmpty()) sb.append("  (sem eventos registados)\n");
                for (EventoJogo ev : j.getEventos())
                    sb.append("  ").append(ev.getMinuto()).append("'  ").append(ev.getTipo())
                      .append("  ").append(ev.getJogador() == null ? "" : ev.getJogador().getNome())
                      .append(" (").append(ev.getEquipa() == null ? "-" : ev.getEquipa().getSigla()).append(")\n");
                EstatisticasJogo s = j.getEstatisticas();
                if (s.isRegistadas()) {
                    sb.append("\nEstatísticas (").append(j.getEquipa1().getSigla())
                      .append(" / ").append(j.getEquipa2().getSigla()).append("):\n");
                    sb.append("  Faltas: ").append(s.getFaltas(0)).append(" / ").append(s.getFaltas(1)).append('\n');
                    sb.append("  Cantos: ").append(s.getCantos(0)).append(" / ").append(s.getCantos(1)).append('\n');
                    sb.append("  Remates: ").append(s.getRemates(0)).append(" / ").append(s.getRemates(1)).append('\n');
                    sb.append("  Passes: ").append(s.getPasses(0)).append(" / ").append(s.getPasses(1)).append('\n');
                    sb.append("  Posse de bola: ").append(s.getPosseBola(0)).append("% / ").append(s.getPosseBola(1)).append("%\n");
                    sb.append("  Foras de jogo: ").append(s.getForasDeJogo(0)).append(" / ").append(s.getForasDeJogo(1)).append('\n');
                }
            }
            detalhes.setText(sb.toString());
            detalhes.setCaretPosition(0);
        }
    }

    // ------------------------------------------------------------------
    /** Ver Estádios: nome, endereço, foto, setores e capacidade. */
    static class EstadiosView extends JPanel implements Atualizavel {
        private final DataStore ds = DataStore.getInstance();
        private final DefaultTableModel modelo =
                Ui.modelComIcone("Foto", "Nome", "Endereço", "Setores A/B/C/D", "Capacidade total");

        EstadiosView() {
            setLayout(new BorderLayout());
            setBackground(Ui.LIGHT);
            add(Ui.title("Estádios"), BorderLayout.NORTH);
            JTable t = new JTable(modelo);
            t.setRowHeight(46);
            JScrollPane sp = Ui.table(t);
            t.getColumnModel().getColumn(0).setMaxWidth(90);
            add(sp, BorderLayout.CENTER);
            atualizar();
        }

        @Override public void atualizar() {
            modelo.setRowCount(0);
            for (Estadio e : ds.getEstadios()) {
                StringBuilder caps = new StringBuilder();
                for (Setor s : e.getSetores())
                    caps.append(caps.length() > 0 ? "/" : "").append(s.getCapacidade());
                modelo.addRow(new Object[]{Ui.imagem(e.getFoto(), 70, 40), e.getNome(),
                        e.getEndereco(), caps.toString(), e.getCapacidadeTotal()});
            }
        }
    }

    // ------------------------------------------------------------------
    /** Ver Árbitros: nome e nacionalidade. */
    static class ArbitrosView extends JPanel implements Atualizavel {
        private final DataStore ds = DataStore.getInstance();
        private final DefaultTableModel modelo = Ui.model("Nome", "Nacionalidade");

        ArbitrosView() {
            setLayout(new BorderLayout());
            setBackground(Ui.LIGHT);
            add(Ui.title("Árbitros"), BorderLayout.NORTH);
            add(Ui.table(new JTable(modelo)), BorderLayout.CENTER);
            atualizar();
        }

        @Override public void atualizar() {
            modelo.setRowCount(0);
            for (Arbitro a : ds.getArbitros())
                modelo.addRow(new Object[]{a.getNome(), a.getNacionalidade()});
        }
    }

    // ------------------------------------------------------------------
    /** Estatísticas de acesso a Guest (jogos, jogadores, equipas, estádios, árbitros). */
    static class EstatisticasView extends JPanel implements Atualizavel {
        private final EstatisticaService stats = new EstatisticaService();
        private final DefaultTableModel modelo = Ui.model("Estatística", "Valor");

        EstatisticasView() {
            setLayout(new BorderLayout());
            setBackground(Ui.LIGHT);
            add(Ui.title("Estatísticas Públicas"), BorderLayout.NORTH);
            add(Ui.table(new JTable(modelo)), BorderLayout.CENTER);
            atualizar();
        }

        private String fmt(double v) { return String.format("%.2f", v); }

        private String topJogadores(Map<Jogador, Long> m) {
            if (m.isEmpty()) return "-";
            StringBuilder sb = new StringBuilder();
            m.forEach((j, n) -> sb.append(sb.length() > 0 ? ", " : "")
                    .append(j.getNome()).append(" (").append(n).append(")"));
            return sb.toString();
        }

        @Override public void atualizar() {
            modelo.setRowCount(0);
            // Jogos
            modelo.addRow(new Object[]{"— JOGOS —", ""});
            modelo.addRow(new Object[]{"Jogos com resultado registado", stats.jogosComResultado()});
            stats.jogosPorFase().forEach((f, n) ->
                    modelo.addRow(new Object[]{"Jogos na fase " + f, n}));
            modelo.addRow(new Object[]{"Jogos com prolongamento", stats.jogosComProlongamento()});
            modelo.addRow(new Object[]{"Jogos decididos por penáltis", stats.jogosComPenaltis()});
            modelo.addRow(new Object[]{"Média de golos por jogo", fmt(stats.mediaGolosPorJogo())});
            modelo.addRow(new Object[]{"Média de faltas por jogo", fmt(stats.mediaFaltas())});
            modelo.addRow(new Object[]{"Média de cantos por jogo", fmt(stats.mediaCantos())});
            modelo.addRow(new Object[]{"Média de remates por jogo", fmt(stats.mediaRemates())});
            modelo.addRow(new Object[]{"Média de passes por jogo", fmt(stats.mediaPasses())});
            modelo.addRow(new Object[]{"Média de foras de jogo por jogo", fmt(stats.mediaForasDeJogo())});
            stats.mediaPossePorEquipa().forEach((e, v) ->
                    modelo.addRow(new Object[]{"Média de posse de bola — " + e.getPais(), fmt(v) + "%"}));

            // Jogadores
            modelo.addRow(new Object[]{"— JOGADORES —", ""});
            modelo.addRow(new Object[]{"Total de jogadores registados", stats.totalJogadores()});
            modelo.addRow(new Object[]{"Melhores marcadores", topJogadores(stats.melhoresMarcadores(5))});
            modelo.addRow(new Object[]{"Jogadores com mais assistências",
                    topJogadores(stats.melhoresAssistentes(5))});
            stats.cartoesPorJogador(TipoEvento.CARTAO_AMARELO).forEach((j, n) ->
                    modelo.addRow(new Object[]{"Cartões amarelos — " + j.getNome(), n}));
            stats.cartoesPorJogador(TipoEvento.CARTAO_VERMELHO).forEach((j, n) ->
                    modelo.addRow(new Object[]{"Cartões vermelhos — " + j.getNome(), n}));

            // Equipas
            modelo.addRow(new Object[]{"— EQUIPAS —", ""});
            stats.golosPorEquipa().forEach((e, n) ->
                    modelo.addRow(new Object[]{"Golos — " + e.getPais(), n}));
            stats.cartoesPorEquipa(TipoEvento.CARTAO_AMARELO).forEach((e, n) ->
                    modelo.addRow(new Object[]{"Cartões amarelos — " + e.getPais(), n}));
            stats.cartoesPorEquipa(TipoEvento.CARTAO_VERMELHO).forEach((e, n) ->
                    modelo.addRow(new Object[]{"Cartões vermelhos — " + e.getPais(), n}));

            // Estádios
            modelo.addRow(new Object[]{"— ESTÁDIOS —", ""});
            Estadio top = stats.estadioComMaisJogos();
            modelo.addRow(new Object[]{"Estádio com mais jogos", top == null ? "-" : top.getNome()});
            modelo.addRow(new Object[]{"Total de estádios registados", stats.totalEstadios()});

            // Árbitros
            modelo.addRow(new Object[]{"— ÁRBITROS —", ""});
            stats.topArbitrosMaisJogos(5).forEach((a, n) ->
                    modelo.addRow(new Object[]{"Top jogos — " + a.getNome(), n}));
            stats.topArbitrosMelhorRating(5).forEach((a, v) ->
                    modelo.addRow(new Object[]{"Top rating — " + a.getNome(), fmt(v)}));
            modelo.addRow(new Object[]{"Total de árbitros registados", stats.totalArbitros()});
        }
    }
}
