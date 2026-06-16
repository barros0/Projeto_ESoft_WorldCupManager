package pt.ipleiria.worldcup.model;

import pt.ipleiria.worldcup.model.Enums.Perfil;

public class Utilizador implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final Perfil perfil;

    public Utilizador(String username, String password, Perfil perfil) {
        this.username = username;
        this.password = password;
        this.perfil = perfil;
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String pw) { return password.equals(pw); }
    public Perfil getPerfil() { return perfil; }
}
