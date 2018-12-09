import javafx.util.Pair;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class ClientIO {
    private Scanner in;

    private BigInteger privateKey, publicKey; // приватный (a) и публичный (А) ключ

    private String login; // логин пользователя

    public ClientIO() {
        in = new Scanner(System.in);
    }

    // Регистрация пользователя.
    public void registration(String inputLogin, String inputPass) {
        // Считываем логин и пароль. Просим ввести их, если они не были заданы извне //
        String login = inputLogin;
        String password = inputPass;

        if (login == null) {
            System.out.print("Введите ваш логин: ");
            login = in.next();
        }
        if (password == null) {
            System.out.print("Введите ваш пароль: ");
            password = in.next();
        }
        // =================================================================== //

        // Вычисляем необходимые при регистрации параметры на стороне клиента //
        BigInteger salt = getSalt(); // соль генерируется на клиенте

        // H(s, p) - взятие хеша от соли и логина с паролем
        BigInteger x = new BigInteger( Server.function_Hash.getHash(
                salt.toByteArray(),
                Server.function_Hash.getHash(new String(login + ":" + password).getBytes())
               ));

        // v = g ^ x - верификатор
        BigInteger verifier = Server.generator_g.modPow(x, Server.modulus_N);

        // =================================================================== //

        // отправляем данные на сервер
        // отправляется тройка:
        // I - логин пользователя
        // s - сгенерированная соль
        // v - верификатор пользователя
        Server.registrationUser(login, salt, verifier);
    }

    // Первичная авторизация пользователя (необходим только логин).
    public Pair<String, BigInteger> authorizationLogin(String inputLogin) {
        // Считываем логин. Просим ввести его, если он не был задан извне //
        login = inputLogin;

        if (login == null) {
            System.out.print("Введите ваш логин: ");
            login = in.next();
        }
        // =================================================================== //

        // Генерируем приватный ключ a (случайное число) и публичный ключ А //
        privateKey = Tools.getRandomBigInteger();

        // A = (g ^ a) % N
        publicKey = Server.generator_g.modPow(privateKey, Server.modulus_N);

        // =================================================================== //

        // возвращается пара вида: логин и открытый ключ пользователя
        return new Pair<String, BigInteger>(login, publicKey);
    }

    // Вторичная авторизация пользователя (необходим первичный этап).
    public BigInteger authorizationPassword(String inputPass, BigInteger salt, BigInteger B) {
        // Считываем пароль. Просим ввести его, если он не был задан извне //
        String password = inputPass;

        if (password == null) {
            System.out.print("Введите ваш пароль: ");
            password = in.next();
        }
        // =================================================================== //

        // Вычисляем сессионный ключ //
        BigInteger scrambler = Server.scrambler;

        // H(s, p) - взятие хеша от соли и логина с паролем
        BigInteger x = new BigInteger( Server.function_Hash.getHash(
                salt.toByteArray(),
                Server.function_Hash.getHash(new String(login + ":" + password).getBytes())
        ));

        // a + u * x
        BigInteger aux = privateKey.add(scrambler.multiply(x));

        // (B - ( (g^x % N) *k)) ^ (a + u*x) (% N)
        BigInteger S = B.subtract((Server.generator_g.modPow(x, Server.modulus_N)).
                multiply(Server.multiplier_k)).modPow(aux, Server.modulus_N);

        // K = H(S)
        BigInteger K = new BigInteger(Server.function_Hash.getHash(S.toByteArray()));
        // =================================================================== //
        return K;
    }

    private static BigInteger getSalt() {
        int saltBits = 128;
        return new BigInteger(saltBits, new Random());
    }

    public static String getStringSalt() {
        return Tools.bytesToHex(getSalt().toByteArray());
    }
}
