package pt.ipleiria.worldcup.ui.teams;

import javax.swing.*;

/** Módulo de gestão de equipas: jogadores, staff e alojamento. */
public class EquipasPanel extends JTabbedPane {
    public interface Atualizavel { void atualizar(); }

    public EquipasPanel() {
        addTab("Jogadores", new JogadoresPanel());
        addTab("Staff", new StaffPanel());
        addTab("Alojamento", new AlojamentoPanel());
        addChangeListener(e -> {
            var c = getSelectedComponent();
            if (c instanceof Atualizavel a) a.atualizar();
        });
    }
}
