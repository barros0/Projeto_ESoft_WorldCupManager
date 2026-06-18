# WorldCup Manager

Sistema de gestão do Campeonato do Mundo de Futebol, desenvolvido em **Java (Swing)** com **Maven** e tema visual moderno **FlatLaf**, segundo a proposta do cliente (Engenharia de Software 2025/26 — Politécnico de Leiria).

## Como abrir no IntelliJ

1. `File → Open…` e selecionar a pasta do projeto (onde está o `pom.xml`).
2. O IntelliJ deteta automaticamente o projeto Maven (JDK 17 ou superior).
3. Executar a classe `pt.ipleiria.worldcup.App` (botão direito → *Run 'App.main()'*).

Em alternativa, na linha de comandos:

```bash
mvn clean package
java -jar target/worldcup-manager-1.0.0.jar
```

## Credenciais de teste (pré-carregadas)

| Utilizador | Password | Perfil               |
|-----------|----------|----------------------|
| admin     | admin    | Administrador (tudo) |
| gjogos    | 1234     | Gestor de Jogos      |
| gequipa   | 1234     | Gestor de Equipa     |
| gdoping   | 1234     | Gestor de Doping     |
| vendedor  | 1234     | Vendedor de Bilhetes |

O acesso **Guest** (Área Pública) não requer autenticação — botão "Entrar como Guest" no ecrã de login.

## Funcionalidades por módulo

### ⚽ Jogos (Gestor de Jogos)
- Configurar campeonato com **4 ou 8 grupos** (campos calculados readonly: total de equipas, apurados e fases eliminatórias geradas automaticamente — 8 apurados → Quartos/Meias/Final; 16 → Oitavos em diante).
- Registo de equipas (país, bandeira, sigla, confederação, pote).
- **Sorteio automático** respeitando: 1 equipa de cada pote por grupo e no máximo 1 equipa por confederação por grupo (com *backtracking*).
- **Calendário automático** da fase de grupos (máx. 1 jogo por estádio por dia) com possibilidade de ajustar data/hora/estádio (validação de conflitos).
- Criação manual de jogos das eliminatórias; o vencedor **avança automaticamente** para a fase seguinte.
- Registo de resultados com prolongamento e penáltis (apenas nas eliminatórias e em caso de empate).
- Eventos do jogo (golo, assistência, cartões, substituição) com **suspensão automática** (2 amarelos no mesmo jogo ou vermelho direto).
- Estatísticas do jogo (faltas, cantos, remates, passes, posse — tem de somar 100% —, foras de jogo).
- Gestão de árbitros e **equipa de arbitragem de 5 elementos** (sem repetições e sem a nacionalidade das equipas em jogo) + rating após o resultado.
- Gestão de estádios com **4 setores obrigatórios**.

### 👥 Equipas (Gestor de Equipa)
- Jogadores com **nº de camisola único por equipa**; estados físicos (disponível, lesionado, suspenso); não é possível remover jogadores com eventos.
- Staff (adicionar, editar, apagar).
- Alojamento em hotéis pré-carregados, com **pedido de confirmação** se o hotel já tiver outra equipa.

### 🧪 Doping (Gestor de Doping)
- Testes com resultado Positivo / Negativo / Duvidoso.
- Em caso positivo, o **castigo é atribuído automaticamente** consoante a substância (listagem pré-carregada).
- **2 resultados duvidosos** do mesmo jogador ⇒ castigo automático.
- Listagem com filtros (equipa, jogador, resultado, data, castigo) e estatísticas restritas.

### 🎟 Bilheteira (Vendedor de Bilhetes)
- Venda com lugares disponíveis e total **calculados automaticamente (readonly)**; gera código `BLH-n` e **ficheiro de bilhete** em `bilhetes/`.
- Pesquisa por código; cancelamento liberta o assento e regista reembolso (se aplicável).
- Estatísticas de vendas (acesso restrito).

### 🌍 Área Pública (Guest — sem login)
- Torneio (grupos e fases), equipas e jogadores, jogos futuros e passados (resumo, eventos, estatísticas), estádios, árbitros.
- Estatísticas públicas: jogos por fase, prolongamentos/penáltis, médias (golos, faltas, cantos, remates, passes, foras de jogo), posse por equipa, melhores marcadores e assistentes, cartões, estádio com mais jogos, top 5 de árbitros (jogos e rating), totais.

## Arquitetura

```
pt.ipleiria.worldcup
├── App.java              → ponto de entrada (Nimbus LAF)
├── data/DataStore.java   → padrão SINGLETON: repositório central em memória
├── model/                → entidades de domínio
├── service/              → regras de negócio (sorteio, calendário, suspensões, castigos…)
└── ui/                   → Swing
    ├── common/Ui.java    → utilitários de UI partilhados
    ├── ticket/  games/  teams/  doping/  guest/
```

- **FlatLaf** (flat design, cantos arredondados, paleta indigo/slate) carregado por reflexão, com *fallback* automático para Nimbus se a dependência não estiver disponível.
- **Singleton** (requisito não-funcional): `DataStore.getInstance()` centraliza todos os dados.
- Separação UI ↔ serviços ↔ modelo; validação de formulários com mensagens de erro claras; campos calculados são readonly.
- Classificação dos grupos: vitória 3 pts, empate 1, derrota 0; desempate por golos marcados.
