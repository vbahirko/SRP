import java.math.BigInteger;

public class ClientForServer {
    private String login_s; // логин
    private BigInteger salt_s; // сгенерированная соль при регистрации
    private BigInteger verifier_v; // верификатор пароля

    public ClientForServer(String login, BigInteger salt, BigInteger verifier) {
        this.login_s = login;
        this.salt_s = salt;
        this.verifier_v = verifier;
    }

    public String getLogin() {
        return login_s;
    }

    public BigInteger getSalt() {
        return salt_s;
    }

    public BigInteger getPassVerifier() {
        return verifier_v;
    }
}
