package pt.ipleiria.worldcup.ui;

import pt.ipleiria.worldcup.model.Enums.Perfil;
import pt.ipleiria.worldcup.model.Utilizador;
import pt.ipleiria.worldcup.service.AuthService;
import pt.ipleiria.worldcup.ui.common.Ui;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthService auth = new AuthService();
    private final JTextField txtUser = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);

    public LoginFrame() {
        super("WorldCup Manager — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 380);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Ui.LIGHT);

        JPanel hero = new JPanel(new BorderLayout());
        hero.setBackground(Ui.DARK);
        hero.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));
        JLabel header = new JLabel("⚽ WorldCup Manager", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 22f));
        header.setForeground(Color.WHITE);
        JLabel tagline = new JLabel("Gestão do Campeonato do Mundo", SwingConstants.CENTER);
        tagline.setForeground(new Color(148, 163, 184));
        hero.add(header, BorderLayout.CENTER);
        hero.add(tagline, BorderLayout.SOUTH);
        root.add(hero, BorderLayout.NORTH);

        JPanel form = Ui.form(
                new JLabel("Utilizador:"), txtUser,
                new JLabel("Password:"), txtPass);
        form.setBorder(BorderFactory.createEmptyBorder(14, 30, 0, 30));
        root.add(form, BorderLayout.CENTER);

        JButton btnLogin = Ui.primaryButton("Entrar");
        JButton btnGuest = new JButton("Entrar como Guest");
        btnGuest.setFocusPainted(false);
        btnLogin.addActionListener(e -> doLogin());
        btnGuest.addActionListener(e -> abrir(null));
        getRootPane().setDefaultButton(btnLogin);

        JPanel south = new JPanel(new GridLayout(2, 1, 5, 8));
        south.setOpaque(false);
        south.setBorder(BorderFactory.createEmptyBorder(10, 30, 22, 30));
        south.add(btnLogin);
        south.add(btnGuest);
        root.add(south, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void doLogin() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) { Ui.erro(this, "Preencha o utilizador e a password."); return; }
        Utilizador user = auth.login(u, p);
        if (user == null) { Ui.erro(this, "Credenciais inválidas."); return; }
        abrir(user);
    }

    private void abrir(Utilizador user) {
        Perfil perfil = user == null ? Perfil.GUEST : user.getPerfil();
        new MainFrame(perfil, user == null ? "Guest" : user.getUsername()).setVisible(true);
        dispose();
    }
}
