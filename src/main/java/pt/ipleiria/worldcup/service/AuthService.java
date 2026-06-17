package service;

public class AuthService {
    public enum Perfil { ADMIN, GESTOR_TORNEIO, GESTOR_EQUIPAS, GESTOR_DOPING, VENDEDOR_BILHETES, GUEST }

    private static AuthService instance;
    private Perfil perfilAtual = Perfil.GUEST; // Por defeito inicia como Guest
    private String utilizadorAutenticado = null;

    private AuthService() {}

    public static synchronized AuthService getInstance() {
        if (instance == null) {
            instance = new AuthService();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        // Validação simples/Mock para o ambiente de testes (Simular a base de dados da equipa)
        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
            perfilAtual = Perfil.ADMIN;
            utilizadorAutenticado = "Administrador";
            return true;
        } else if ("vendedor".equalsIgnoreCase(username) && "bilhete123".equals(password)) {
            perfilAtual = Perfil.VENDEDOR_BILHETES;
            utilizadorAutenticado = "Vendedor Oficial";
            return true;
        }
        // Adicionar outros mocks conforme necessário para os testes dos colegas
        return false;
    }

    public void logout() {
        perfilAtual = Perfil.GUEST;
        utilizadorAutenticado = null;
    }

    public Perfil getPerfilAtual() { return perfilAtual; }
    public String getUtilizadorAutenticado() { return utilizadorAutenticado; }
    public boolean isAuthenticated() { return perfilAtual != Perfil.GUEST; }

    // Segurança: Método auxiliar para verificar se o perfil atual tem autorização
    public void verificarPermissao(Perfil... perfisPermitidos) throws SecurityException {
        if (perfilAtual == Perfil.ADMIN) return; // Admin tem acesso total [cite: 1, 15, 16]
        for (Perfil p : perfisPermitidos) {
            if (perfilAtual == p) return;
        }
        throw new SecurityException("Acesso Negado: O seu perfil não tem permissões para esta operação.");
    }
}