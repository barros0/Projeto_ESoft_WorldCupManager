package pt.ipleiria.worldcup.service;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.model.Enums.EstadoBilhete;

import java.io.IOException;
import java.nio.file.*;

public class BilheteService {

    private final DataStore ds = DataStore.getInstance();

    /** Regista a venda: valida lugares, gera código único e gera o ficheiro do bilhete. */
    public Bilhete venderBilhete(String comprador, Jogo jogo, Setor setor, int quantidade) {
        if (comprador == null || comprador.isBlank())
            throw new IllegalArgumentException("Indique o nome do comprador.");
        if (quantidade < 1) throw new IllegalArgumentException("Quantidade inválida.");
        int disponiveis = jogo.lugaresDisponiveis(setor);
        if (quantidade > disponiveis)
            throw new IllegalStateException("Apenas " + disponiveis + " lugares disponíveis no " + setor + ".");

        double total = quantidade * jogo.getPrecoBase();
        Bilhete b = new Bilhete(comprador, jogo, setor, quantidade, total);
        jogo.vender(setor, quantidade);
        ds.getBilhetes().add(b);
        gerarFicheiro(b);
        return b;
    }

    private void gerarFicheiro(Bilhete b) {
        try {
            Path dir = Paths.get("bilhetes");
            Files.createDirectories(dir);
            String conteudo = """
                    ============ BILHETE ============
                    Código:    %s
                    Comprador: %s
                    Jogo:      %s
                    Data:      %s  %s
                    Estádio:   %s
                    Setor:     %s
                    Qtd:       %d
                    Total:     %.2f €
                    =================================
                    """.formatted(b.getCodigo(), b.getComprador(), b.getJogo().descricaoCurta(),
                    b.getJogo().getData(), b.getJogo().getHora(),
                    b.getJogo().getEstadio(), b.getSetor().getNome(),
                    b.getQuantidade(), b.getValorTotal());
            Files.writeString(dir.resolve(b.getCodigo() + ".txt"), conteudo);
        } catch (IOException e) {
            System.err.println("Aviso: não foi possível gerar o ficheiro do bilhete: " + e.getMessage());
        }
    }

    public Bilhete pesquisarPorCodigo(String codigo) {
        return ds.getBilhetes().stream()
                .filter(b -> b.getCodigo().equalsIgnoreCase(codigo.trim()))
                .findFirst().orElse(null);
    }

    /** Cancela: muda estado, liberta o assento no setor e regista o reembolso. */
    public void cancelarBilhete(Bilhete b) {
        if (b.getEstado() == EstadoBilhete.CANCELADO)
            throw new IllegalStateException("O bilhete já está cancelado.");
        b.cancelar();
        b.getJogo().libertar(b.getSetor(), b.getQuantidade());
    }
}
