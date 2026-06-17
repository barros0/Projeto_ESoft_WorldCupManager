package pt.ipleiria.worldcup.ui;

import pt.ipleiria.worldcup.model.Enums.Perfil;
import pt.ipleiria.worldcup.ui.common.Ui;
import pt.ipleiria.worldcup.ui.doping.DopingPanel;
import pt.ipleiria.worldcup.ui.games.JogosPanel;
import pt.ipleiria.worldcup.ui.guest.GuestPanel;
import pt.ipleiria.worldcup.ui.teams.EquipasPanel;
import pt.ipleiria.worldcup.ui.ticket.BilheteiraPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame(Perfil perfil, String username) {
        super("WorldCup Manager — " + username + " (" + perfil + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 760);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JTabbedPane tabs = new JTabbedPane();

        // Cada perfil só tem acesso às suas funcionalidades
        boolean admin = perfil == Perfil.ADMIN;
        if (admin || perfil == Perfil.VENDEDOR_BILHETES)
            tabs.addTab("🎟 Bilheteira", new BilheteiraPanel());
        if (admin || perfil == Perfil.GESTOR_JOGOS)
            tabs.addTab("⚽ Jogos", new JogosPanel());
        if (admin || perfil == Perfil.GESTOR_EQUIPA)
            tabs.addTab("👥 Equipas", new EquipasPanel());
        if (admin || perfil == Perfil.GESTOR_DOPING)
            tabs.addTab("Doping", iconeDoping(), new DopingPanel(perfil));

        tabs.addTab("🌍 Área Pública", new GuestPanel()); // acessível a todos

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Ui.DARK);
        top.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 12));
        JLabel lbl = new JLabel("⚽ WorldCup Manager");
        lbl.setForeground(Color.WHITE);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 17f));
        JLabel sub = new JLabel("  " + username + " · " + perfil);
        sub.setForeground(new Color(148, 163, 184));
        JPanel esq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        esq.setOpaque(false);
        esq.add(lbl);
        esq.add(sub);
        JButton sair = new JButton("Terminar sessão");
        sair.setBackground(Ui.PRIMARY);
        sair.setForeground(Color.WHITE);
        sair.setFocusPainted(false);
        sair.addActionListener(e -> {
            pt.ipleiria.worldcup.data.DataStore.getInstance().guardar();
            new LoginFrame().setVisible(true);
            dispose();
        });
        top.add(esq, BorderLayout.WEST);
        top.add(sair, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    /** Ícone do módulo Doping desenhado em código (frasco de teste), para não depender de emojis da fonte. */
    private static Icon iconeDoping() {
        int w = 16, h = 16;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h,
                java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // corpo do tubo de ensaio (inclinado)
        g.rotate(Math.toRadians(20), w / 2.0, h / 2.0);
        g.setColor(new Color(148, 163, 184));
        g.fillRoundRect(6, 1, 4, 13, 4, 4);
        // líquido
        g.setColor(Ui.ACCENT);
        g.fillRoundRect(6, 8, 4, 6, 4, 4);
        // boca do tubo
        g.setColor(new Color(100, 116, 139));
        g.fillRect(5, 1, 6, 2);
        g.dispose();
        return new ImageIcon(img);
    }
}
