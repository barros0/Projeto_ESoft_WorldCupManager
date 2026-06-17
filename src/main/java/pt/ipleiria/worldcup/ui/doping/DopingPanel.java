package pt.ipleiria.worldcup.ui.doping;

import pt.ipleiria.worldcup.model.Enums.Perfil;

import javax.swing.*;

public class DopingPanel extends JTabbedPane {
    public interface Atualizavel { void atualizar(); }

    public DopingPanel(Perfil perfil) {
        addTab("Fazer Teste", new FazerTestePanel());
        addTab("Ver Testes", new VerTestesPanel());
        // Estatísticas de acesso restrito (Gestor de Doping e Admin)
        if (perfil == Perfil.ADMIN || perfil == Perfil.GESTOR_DOPING)
            addTab("Estatísticas", new EstatisticasDopingPanel());
        addChangeListener(e -> {
            var c = getSelectedComponent();
            if (c instanceof Atualizavel a) a.atualizar();
        });
    }
}