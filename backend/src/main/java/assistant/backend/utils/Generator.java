package assistant.backend.utils;

import java.util.Random;

/**
 * generate string of fixed length
 */
public class Generator {

    static Random random = new Random();

    static String charset = "1234567890ABCDEF";

    public static String generateString(int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= len; ++i) {
            builder.append(charset.charAt(random.nextInt(charset.length())));
        }
        return builder.toString();
    }

    public static String generateToken() {
        return generateString(32);
    }
}
