package pt.ipleiria.worldcup.data;

import pt.ipleiria.worldcup.model.*;
import pt.ipleiria.worldcup.model.Enums.Perfil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório central de dados da aplicação.
 * Implementa o padrão SINGLETON (requisito não funcional obrigatório).
 */
public final class DataStore implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Ficheiro de dados — fica dentro da pasta do projeto e viaja com ele. */
    private static final File FICHEIRO = new File("dados/worldcup.dat");

    private static DataStore instance;

    public static synchronized DataStore getInstance() {
        if (instance == null) instance = carregar();
        return instance;
    }

    /** Carrega os dados do ficheiro; se não existir, começa com os dados pré-carregados. */
    private static DataStore carregar() {
        if (FICHEIRO.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FICHEIRO))) {
                DataStore ds = (DataStore) in.readObject();
                // repõe o contador dos códigos de bilhete (BLH-n) após carregar
                int max = 0;
                for (Bilhete b : ds.bilhetes) {
                    String n = b.getCodigo().replace("BLH-", "");
                    try { max = Math.max(max, Integer.parseInt(n)); } catch (NumberFormatException ignored) {}
                }
                Bilhete.sincronizarContador(max + 1);
                return ds;
            } catch (Exception e) {
                System.err.println("Aviso: não foi possível ler " + FICHEIRO + " (" + e.getMessage()
                        + "). A iniciar com dados novos.");
            }
        }
        return new DataStore();
    }

    /** Guarda todos os dados no ficheiro (chamado automaticamente ao fechar a aplicação). */
    public synchronized void guardar() {
        try {
            File pasta = FICHEIRO.getParentFile();
            if (pasta != null) pasta.mkdirs();
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FICHEIRO))) {
                out.writeObject(this);
            }
        } catch (IOException e) {
            System.err.println("Erro ao guardar dados: " + e.getMessage());
        }
    }

    private final List<Utilizador> utilizadores = new ArrayList<>();
    private final List<Equipa> equipas = new ArrayList<>();
    private final List<Estadio> estadios = new ArrayList<>();
    private final List<Arbitro> arbitros = new ArrayList<>();
    private final List<Jogo> jogos = new ArrayList<>();
    private final List<Bilhete> bilhetes = new ArrayList<>();
    private final List<TesteDoping> testes = new ArrayList<>();
    private final List<Substancia> substancias = new ArrayList<>();
    private final List<Hotel> hoteis = new ArrayList<>();
    private final List<Alojamento> alojamentos = new ArrayList<>();
    private Campeonato campeonato;

    private DataStore() {
        // Utilizadores pré-definidos (autenticação por login, exceto Guest)
        utilizadores.add(new Utilizador("admin", "admin", Perfil.ADMIN));
        utilizadores.add(new Utilizador("gjogos", "1234", Perfil.GESTOR_JOGOS));
        utilizadores.add(new Utilizador("gequipa", "1234", Perfil.GESTOR_EQUIPA));
        utilizadores.add(new Utilizador("gdoping", "1234", Perfil.GESTOR_DOPING));
        utilizadores.add(new Utilizador("vendedor", "1234", Perfil.VENDEDOR_BILHETES));

        // Dados pré-carregados: substâncias proibidas e respetivos castigos
        substancias.add(new Substancia("Esteroides anabolizantes", "Suspensão 4 anos"));
        substancias.add(new Substancia("EPO", "Suspensão 2 anos"));
        substancias.add(new Substancia("Estimulantes", "Suspensão 1 ano"));
        substancias.add(new Substancia("Diuréticos (mascarantes)", "Suspensão 2 anos"));
        substancias.add(new Substancia("Hormona de crescimento", "Suspensão 4 anos"));
        substancias.add(new Substancia("Canabinoides", "Suspensão 3 meses"));

        // Dados pré-carregados: lista de hotéis
        hoteis.add(new Hotel("Hotel Lisboa Plaza"));
        hoteis.add(new Hotel("Porto Palace Hotel"));
        hoteis.add(new Hotel("Grande Hotel de Leiria"));
        hoteis.add(new Hotel("Atlântico Resort"));
        hoteis.add(new Hotel("Hotel Mundial"));
    }

    public List<Utilizador> getUtilizadores() { return utilizadores; }
    public List<Equipa> getEquipas() { return equipas; }
    public List<Estadio> getEstadios() { return estadios; }
    public List<Arbitro> getArbitros() { return arbitros; }
    public List<Jogo> getJogos() { return jogos; }
    public List<Bilhete> getBilhetes() { return bilhetes; }
    public List<TesteDoping> getTestes() { return testes; }
    public List<Substancia> getSubstancias() { return substancias; }
    public List<Hotel> getHoteis() { return hoteis; }
    public List<Alojamento> getAlojamentos() { return alojamentos; }
    public Campeonato getCampeonato() { return campeonato; }
    public void setCampeonato(Campeonato c) { this.campeonato = c; }
}
