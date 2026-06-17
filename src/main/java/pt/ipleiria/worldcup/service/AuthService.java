package pt.ipleiria.worldcup.service;

import pt.ipleiria.worldcup.data.DataStore;
import pt.ipleiria.worldcup.model.Utilizador;

public class AuthService {

    /** @return o utilizador autenticado ou null se credenciais inválidas. */
    public Utilizador login(String username, String password) {
        return DataStore.getInstance().getUtilizadores().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username) && u.checkPassword(password))
                .findFirst().orElse(null);
    }
}