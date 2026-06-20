package pt.ipleiria.worldcup.service;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.model.Enums.Confederacao;
import pt.ipleiria.worldcup.model.Enums.Fase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class CampeonatoService {

    private final DataStore ds = DataStore.getInstance();

    public Campeonato criarCampeonato(String nome, int numGrupos) {
        Campeonato c = new Campeonato(nome, numGrupos);
        ds.setCampeonato(c);
        return c;
    }

    public void adicionarEquipa(Equipa e) {
        boolean existe = ds.getEquipas().stream().anyMatch(x -> x.getPais().equalsIgnoreCase(e.getPais()));
        if (existe) throw new IllegalStateException("A equipa já existe no sistema.");
        ds.getEquipas().add(e);
    }

    public void removerEquipa(Equipa e) {
        Campeonato c = ds.getCampeonato();
        if (c != null && c.isSorteioRealizado())
            throw new IllegalStateException("Não é possível remover equipas após o sorteio.");
        ds.getEquipas().remove(e);
    }

    /** Edita os dados de uma equipa. Só é permitido ANTES do sorteio. */
    public void editarEquipa(Equipa e, String pais, String bandeira, String sigla,
                             Enums.Confederacao confederacao, int pote) {
        Campeonato c = ds.getCampeonato();
        if (c != null && c.isSorteioRealizado())
            throw new IllegalStateException("Não é possível editar equipas após o sorteio.");
        if (pais == null || pais.isBlank() || sigla == null || sigla.isBlank())
            throw new IllegalArgumentException("Preencha o país e a sigla.");
        boolean duplicado = ds.getEquipas().stream()
                .anyMatch(x -> x != e && x.getPais().equalsIgnoreCase(pais.trim()));
        if (duplicado) throw new IllegalStateException("Já existe outra equipa com esse país.");
        e.setPais(pais.trim());
        e.setBandeira(bandeira == null ? "" : bandeira.trim());
        e.setSigla(sigla.trim().toUpperCase());
        e.setConfederacao(confederacao);
        e.setPote(pote);
    }

    /**
     * Sorteio dos grupos com restrições:
     * 1 equipa por pote por grupo; máx. 1 equipa por confederação por grupo.
     */
    public void realizarSorteio() {
        Campeonato c = ds.getCampeonato();
        if (c == null) throw new IllegalStateException("Configure primeiro o campeonato.");
        if (c.isSorteioRealizado()) throw new IllegalStateException("O sorteio já foi realizado.");
        List<Equipa> equipas = ds.getEquipas();
        if (equipas.size() != c.getTotalEquipas())
            throw new IllegalStateException("São necessárias " + c.getTotalEquipas()
                    + " equipas (registadas: " + equipas.size() + ").");

        int n = c.getNumGrupos();
        for (int pote = 1; pote <= 4; pote++) {
            final int p = pote;
            long count = equipas.stream().filter(e -> e.getPote() == p).count();
            if (count != n) throw new IllegalStateException("O pote " + p + " deve ter " + n + " equipas (tem " + count + ").");
        }

        // Backtracking com baralhamento aleatório para respeitar as restrições
        Random rnd = new Random();
        for (int tentativa = 0; tentativa < 500; tentativa++) {
            List<List<Equipa>> grupos = new ArrayList<>();
            for (int i = 0; i < n; i++) grupos.add(new ArrayList<>());
            boolean ok = true;
            for (int pote = 1; pote <= 4 && ok; pote++) {
                final int p = pote;
                List<Equipa> doPote = new ArrayList<>(equipas.stream().filter(e -> e.getPote() == p).toList());
                Collections.shuffle(doPote, rnd);
                List<Integer> idx = new ArrayList<>();
                for (int i = 0; i < n; i++) idx.add(i);
                if (!atribuir(doPote, 0, idx, grupos)) ok = false;
            }
            if (ok) {
                for (int i = 0; i < n; i++) {
                    Grupo g = c.getGrupos().get(i);
                    g.getEquipas().clear();
                    for (Equipa e : grupos.get(i)) { g.getEquipas().add(e); e.setGrupo(g); }
                }
                c.setSorteioRealizado(true);
                return;
            }
        }
        throw new IllegalStateException("Não foi possível realizar o sorteio respeitando as restrições de confederação.");
    }

    private boolean atribuir(List<Equipa> doPote, int k, List<Integer> gruposLivres, List<List<Equipa>> grupos) {
        if (k == doPote.size()) return true;
        Equipa e = doPote.get(k);
        List<Integer> shuffled = new ArrayList<>(gruposLivres);
        Collections.shuffle(shuffled);
        for (int g : shuffled) {
            if (compativel(grupos.get(g), e)) {
                grupos.get(g).add(e);
                List<Integer> resto = new ArrayList<>(gruposLivres);
                resto.remove(Integer.valueOf(g));
                if (atribuir(doPote, k + 1, resto, grupos)) return true;
                grupos.get(g).remove(e);
            }
        }
        return false;
    }

    private boolean compativel(List<Equipa> grupo, Equipa e) {
        for (Equipa x : grupo) {
            if (x.getPote() == e.getPote()) return false;
            if (x.getConfederacao() == e.getConfederacao()) return false; // máx. 1 por confederação
        }
        return true;
    }

    /**
     * Gera o calendário da fase de grupos: distribui os jogos pelos dias,
     * garantindo que nenhum estádio tem mais de um jogo por dia.
     */
    public void gerarCalendario(LocalDate dataInicio, int jogosPorDia, double precoBase) {
        Campeonato c = ds.getCampeonato();
        if (c == null || !c.isSorteioRealizado())
            throw new IllegalStateException("Realize primeiro o sorteio dos grupos.");
        if (c.isCalendarioGerado()) throw new IllegalStateException("O calendário já foi gerado.");
        List<Estadio> estadios = ds.getEstadios();
        if (estadios.isEmpty()) throw new IllegalStateException("Registe estádios antes de gerar o calendário.");
        if (jogosPorDia < 1) throw new IllegalArgumentException("Número de jogos por dia inválido.");
        if (jogosPorDia > estadios.size())
            throw new IllegalStateException("Jogos por dia (" + jogosPorDia + ") excede o número de estádios (" + estadios.size() + ").");

        // gerar confrontos (round-robin dentro de cada grupo: 6 jogos por grupo)
        List<Jogo> novos = new ArrayList<>();
        for (Grupo g : c.getGrupos()) {
            List<Equipa> eq = g.getEquipas();
            for (int i = 0; i < eq.size(); i++)
                for (int j = i + 1; j < eq.size(); j++) {
                    Jogo jogo = new Jogo(eq.get(i), eq.get(j), null, null, null, Fase.GRUPOS, precoBase);
                    jogo.setGrupo(g);
                    g.getJogos().add(jogo);
                    novos.add(jogo);
                }
        }

        LocalDate dia = dataInicio;
        int noDia = 0, estadioIdx = 0;
        LocalTime[] horas = { LocalTime.of(14, 0), LocalTime.of(17, 0), LocalTime.of(20, 0) };
        for (Jogo j : novos) {
            if (noDia == jogosPorDia) { dia = dia.plusDays(1); noDia = 0; estadioIdx = 0; }
            j.setData(dia);
            j.setHora(horas[noDia % horas.length]);
            j.setEstadio(estadios.get(estadioIdx)); // 1 estádio = 1 jogo por dia
            estadioIdx++;
            noDia++;
        }
        ds.getJogos().addAll(novos);
        c.setCalendarioGerado(true);
        criarPlaceholdersEliminatorias(c);
    }

    /**
     * Cria os jogos vazios (sem equipas atribuídas) de todas as fases eliminatórias,
     * na quantidade correta consoante o número de apurados. À medida que os resultados
     * da fase de grupos / anterior são registados, os vencedores preenchem estas vagas
     * automaticamente (ver JogoService.avancarVencedor).
     */
    private void criarPlaceholdersEliminatorias(Campeonato c) {
        for (Fase fase : c.getFasesEliminatorias()) {
            int nJogos = switch (fase) {
                case OITAVOS -> 8;
                case QUARTOS -> c.getEquipasApuradas() == 16 ? 4 : 4; // 4 grupos: quartos é a 1ª fase eliminatória
                case MEIAS -> 2;
                case FINAL -> 1;
                default -> 0;
            };
            for (int i = 0; i < nJogos; i++)
                ds.getJogos().add(new Jogo(null, null, null, null, null, fase, 0));
        }
    }

    /** Valida ajuste manual de data/estádio: bloqueia conflitos de estádio no mesmo dia. */
    public void ajustarJogo(Jogo j, LocalDate novaData, LocalTime novaHora, Estadio novoEstadio) {
        boolean conflito = ds.getJogos().stream().anyMatch(x -> x != j
                && x.getEstadio() == novoEstadio
                && novaData != null && novaData.equals(x.getData()));
        if (conflito)
            throw new IllegalStateException("Conflito: o estádio " + novoEstadio + " já tem um jogo nesse dia.");
        j.setData(novaData);
        j.setHora(novaHora);
        j.setEstadio(novoEstadio);
    }

    public void criarJogoEliminatoria(Equipa e1, Equipa e2, LocalDate data, LocalTime hora,
                                      Estadio estadio, Fase fase, double preco) {
        if (fase == Fase.GRUPOS) throw new IllegalArgumentException("Use o calendário para jogos da fase de grupos.");
        Campeonato c = ds.getCampeonato();
        if (c == null || !c.getFasesEliminatorias().contains(fase))
            throw new IllegalArgumentException("A fase " + fase
                    + " não existe neste campeonato (" + (c == null ? "campeonato não configurado"
                    : c.getNumGrupos() + " grupos") + ").");
        if (e1 == e2) throw new IllegalArgumentException("As equipas têm de ser diferentes.");
        boolean conflito = ds.getJogos().stream().anyMatch(x -> x.getEstadio() == estadio && data.equals(x.getData()));
        if (conflito) throw new IllegalStateException("Conflito: o estádio já tem um jogo nesse dia.");

        // preferir preencher um placeholder vazio já existente dessa fase (mantém a posição correta no bracket)
        Jogo placeholder = ds.getJogos().stream()
                .filter(j -> j.getFase() == fase && j.getEquipa1() == null && j.getEquipa2() == null)
                .findFirst().orElse(null);
        if (placeholder != null) {
            placeholder.setEquipa1(e1);
            placeholder.setEquipa2(e2);
            placeholder.setData(data);
            placeholder.setHora(hora);
            placeholder.setEstadio(estadio);
            placeholder.setPrecoBase(preco);
        } else {
            ds.getJogos().add(new Jogo(e1, e2, data, hora, estadio, fase, preco));
        }
    }
}
