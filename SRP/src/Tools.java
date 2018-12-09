import java.math.BigInteger;
import java.util.Random;

public class Tools {
    private static byte[] trim(byte[] data) {
        if (data[0] == 0) {
            byte[] oldData = data;
            data = new byte[oldData.length - 1];
            for (int i = 0; i < data.length; i++)
                data[i] = oldData[i + 1];
        }
        return data;
        // Обрезание большого числа - если его последний разряд нулевой (т.е. незначащий) - то сдвигаем все байты на одну позицию влево.
        // Под последним разрядом имеется наибольшая позиция в числе (1000000: 1 это последний разряд)
    }

    private static char toHexChar(int i) {
        if ((0 <= i) && (i <= 9 ))
            return (char)('0' + i);
        else
            return (char)('a' + (i-10));
        //Функция перевода целого числа в его HEX-представление (шестнадцетиричная система счисления).
        //здесь мы вычисляем HEX-символ в зависимости от поданного на вход числа.
    }

    private static String byteToHex(byte data) {
        StringBuffer buf = new StringBuffer();
        buf.append(toHexChar((data>>>4)&0x0F));
        buf.append(toHexChar(data&0x0F));
        return buf.toString();
        // Представление байта в HEX-формате.
        // Пример: 255 - FF. Разбиваем байт на две части, каждую часть переводим в HEX - на выходе получаются два HEX-значения.
    }

    public static String bytesToHex(byte[] data) {
        if (data == null) return "";

        StringBuffer buf = new StringBuffer();
        data = trim(data);
        for (int i = 0; i < data.length; i++ ) {
            buf.append(byteToHex(data[i]));
        }

        String output = buf.toString();
        return output;
        //То же самое, что и выше, но переводится уже множество байт
    }

    public static BigInteger getRandomBigInteger() {
        int countBits = 128;
        return new BigInteger(countBits, new Random());
        //Взятие псевдослучайного большого числа размерностью 128 байт
    }
}
