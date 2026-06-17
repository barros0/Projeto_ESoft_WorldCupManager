package pt.ipleiria.worldcup;

import pt.ipleiria.worldcup.ui.LoginFrame;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;

/**
 * WorldCup Manager — Sistema de gestão do Campeonato do Mundo de Futebol.
 * Engenharia de Software 2025/26 — Politécnico de Leiria.
 */
public class App {
    public static void main(String[] args) {
        instalarLookAndFeel();
        // Guarda os dados automaticamente ao fechar a aplicação
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> pt.ipleiria.worldcup.data.DataStore.getInstance().guardar()));
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    /** FlatLaf (flat design moderno); fallback para Nimbus se indisponível. */
    private static void instalarLookAndFeel() {
        try {
            // Carregado por reflexão para o projeto compilar mesmo sem a dependência
            Class<?> flat = Class.forName("com.formdev.flatlaf.FlatLightLaf");
            UIManager.setLookAndFeel((LookAndFeel) flat.getDeclaredConstructor().newInstance());

            // Tema moderno: cantos arredondados, foco e seleção na cor de destaque
            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 14);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("ProgressBar.arc", 12);
            UIManager.put("Component.focusWidth", 1);
            UIManager.put("Component.innerFocusWidth", 1);
            UIManager.put("Component.accentColor", Ui.PRIMARY);
            UIManager.put("Component.focusColor", Ui.PRIMARY);
            UIManager.put("Button.default.background", Ui.PRIMARY);
            UIManager.put("Button.default.foreground", Color.WHITE);
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            UIManager.put("TabbedPane.underlineColor", Ui.PRIMARY);
            UIManager.put("TabbedPane.inactiveUnderlineColor", Ui.PRIMARY_SOFT);
            UIManager.put("Table.selectionBackground", Ui.PRIMARY_SOFT);
            UIManager.put("Table.selectionForeground", Ui.INK);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.gridColor", Ui.BORDER);
            UIManager.put("TableHeader.background", Ui.LIGHT);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.width", 12);
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception ignored) {}
        }
    }
}
