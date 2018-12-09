import javafx.util.Pair;

import java.math.BigInteger;

public class Main {
    public  static void test() {
        ClientIO client = new ClientIO();

        client.registration("TEST", "test");

        Pair<String, BigInteger> infoFromClient = client.authorizationLogin("TEST");

        Pair<BigInteger, Pair<BigInteger, BigInteger>> infoFromServer =
                Server.authorizationLoginUser(infoFromClient);

        BigInteger KFromUser = client.authorizationPassword("test",
                infoFromServer.getValue().getKey(), infoFromServer.getValue().getValue());

        BigInteger KFromServer = infoFromServer.getKey();

        System.out.println(KFromUser);
        System.out.println(KFromServer);
    }

    public static void main(String args[]) {
        ClientIO client = new ClientIO();
        BigInteger KFromUser = null, KFromServer = null;

        System.out.println("Регистрация:");
        client.registration(null, null);
        System.out.println("===================================");

        System.out.println("Авторизация:");
        while (true){
            Pair<String, BigInteger> infoFromClient = client.authorizationLogin(null);
            Pair<BigInteger, Pair<BigInteger, BigInteger>> infoFromServer = null;

            try {
                infoFromServer =
                        Server.authorizationLoginUser(infoFromClient);
            } catch (NullPointerException ex) {
                System.out.println(ex.getMessage());
                continue;
            }

            KFromUser = client.authorizationPassword(null,
                    infoFromServer.getValue().getKey(), infoFromServer.getValue().getValue());

            KFromServer = infoFromServer.getKey();

            if (KFromServer.equals(KFromUser)) {
                System.out.println("Авторизация успешно пройдена!");
                break;
            } else {
                System.out.println("Введенный пароль не соответствует данному пользователю.");
            }
            System.out.println("===================================");
        }
    }
}
