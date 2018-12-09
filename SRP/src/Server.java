import javafx.util.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Server {
    // N - модуль.
    public final static BigInteger modulus_N =
            new BigInteger("115b8b692e0e045692cf280b436735c77a5a9e8a9e7ed56c965f87db5b2a2ece3", 16);

    // g  - генератор по модулю N => для любого 0 < X < N существует и единственный x такой, что g^x % N = X.
    public final static BigInteger generator_g =
            new BigInteger("2", 10);

    // k - параметр-множитель. В 6-ой версии он равен 3.
    public final static BigInteger multiplier_k =
            new BigInteger("3", 10);

    // u - произвольный параметр для кодирования.
    public final static BigInteger scrambler =
            new BigInteger(128, new Random());

    // H - используемая хеш-функция. Используется SHA-1.
    public final static HashFunction function_Hash = new HashFunction();

    // клиенты, зрегистрированные на сервере.
    private static List<ClientForServer> users = new ArrayList<ClientForServer>();

    public Server() {

    }

    // Регистрация пользователя на сервере.
    // Клиент присылает серверу три поля при регистрации:
    // Логин, соль, верификатор
    public static void registrationUser(String login, BigInteger s, BigInteger v) {
        // После этого сервер хранит пару (I, s, v) в своей базе данных
        users.add(new ClientForServer(login, s, v));
    }

    // Первичная авторизация пользователя на сервере.
    // Клиент присылает пару из: логин и публичный ключ
    public static Pair<BigInteger, Pair<BigInteger, BigInteger>> authorizationLoginUser(Pair<String, BigInteger> clientInfo) {
        String login = clientInfo.getKey();
        BigInteger A = clientInfo.getValue();

        // Ищем пользователя среди зарегистрированных. Если не находим - выбрасываем исключение //
        ClientForServer curUser = null;
        for (ClientForServer user : users) {
            if (user.getLogin().equals(login)) {
                curUser = user;
                break;
            }
        }

        if (curUser == null) throw new NullPointerException("Пользователь не найден!");
        // =================================================================== //

        // Генерируем приватный ключ b (случайное число) и публичный ключ B //
        BigInteger privateKey = Tools.getRandomBigInteger();

        // k * v
        BigInteger firstPart = multiplier_k.multiply(curUser.getPassVerifier());

        // g ^ b % N
        BigInteger secondPart = generator_g.modPow(privateKey, modulus_N);

        // B = k * v + g ^ b % N
        BigInteger publicKey = firstPart.add(secondPart).mod(modulus_N);

        // =================================================================== //

        // Вычисляем сессионный ключ //
        // A * (v^u % N)
        firstPart = A.multiply(curUser.getPassVerifier().modPow(scrambler, modulus_N));

        // S = ((A*(v^u % N)) ^ b) % N
        BigInteger S = firstPart.modPow(privateKey, modulus_N);


        // K = H(S)
        BigInteger K = new BigInteger(function_Hash.getHash(S.toByteArray()));
        // =================================================================== //

        // возвращается тройка вида: сессионный ключ, соль пользователя при регистрации, публичный ключ сервера
        return new Pair<BigInteger, Pair<BigInteger, BigInteger>>(K, new Pair<BigInteger, BigInteger>(
                curUser.getSalt(), publicKey));
    }
}
