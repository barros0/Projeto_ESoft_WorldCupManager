package pt.ipleiria.worldcup.ui.ticket;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Jogo;
import pt.ipleiria.worldcup.model.Setor;
import pt.ipleiria.worldcup.service.EstatisticaService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Estatísticas de acesso restrito — Vendedor de Bilhetes e Admin. */
public class EstatisticasVendasPanel extends JPanel implements BilheteiraPanel.Atualizavel {

    private final EstatisticaService stats = new EstatisticaService();
    private final DefaultTableModel model = Ui.model("Estatística", "Valor");

    public EstatisticasVendasPanel() {
        setLayout(new BorderLayout());
        add(Ui.title("Estatísticas — Venda de Bilhetes"), BorderLayout.NORTH);
        add(Ui.table(new JTable(model)), BorderLayout.CENTER);
        JButton btn = new JButton("Atualizar");
        btn.addActionListener(e -> atualizar());
        JPanel sul = new JPanel();
        sul.add(btn);
        add(sul, BorderLayout.SOUTH);
        atualizar();
    }

    @Override public void atualizar() {
        model.setRowCount(0);
        model.addRow(new Object[]{ "Bilhetes vendidos (ativos)", stats.bilhetesVendidos() });
        stats.bilhetesPorJogo().forEach((j, n) ->
                model.addRow(new Object[]{ "Bilhetes — " + j.descricaoCurta(), n }));
        stats.valorPorJogo().forEach((j, v) ->
                model.addRow(new Object[]{ "Valor arrecadado — " + j.descricaoCurta(), String.format("%.2f €", v) }));
        model.addRow(new Object[]{ "Valor total arrecadado", String.format("%.2f €", stats.valorTotalArrecadado()) });
        stats.bilhetesPorSetor().forEach((s, n) ->
                model.addRow(new Object[]{ "Bilhetes vendidos — " + s, n }));
        for (Jogo j : DataStore.getInstance().getJogos()) {
            if (j.getEstadio() == null) continue;
            for (Setor s : j.getEstadio().getSetores())
                model.addRow(new Object[]{ "Lugares disponíveis — " + j.descricaoCurta() + " — " + s,
                        j.lugaresDisponiveis(s) });
        }
        model.addRow(new Object[]{ "Bilhetes cancelados", stats.bilhetesCancelados() });
        model.addRow(new Object[]{ "Valor total reembolsado", String.format("%.2f €", stats.valorReembolsado()) });
    }
}
