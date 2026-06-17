package pt.ipleiria.worldcup.ui.ticket;

import javax.swing.*;

public class BilheteiraPanel extends JTabbedPane {
    public BilheteiraPanel() {
        addTab("Vender Bilhete", new VenderBilhetePanel());
        addTab("Bilhetes Vendidos", new BilhetesVendidosPanel());
        addTab("Cancelar Bilhete", new CancelarBilhetePanel());
        addTab("Estatísticas de Vendas", new EstatisticasVendasPanel());
        addChangeListener(e -> {
            var c = getSelectedComponent();
            if (c instanceof Atualizavel a) a.atualizar();
        });
    }

    public interface Atualizavel { void atualizar(); }
}
