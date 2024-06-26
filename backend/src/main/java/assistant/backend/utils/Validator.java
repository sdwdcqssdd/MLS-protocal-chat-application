package assistant.backend.utils;

/**
 * class used to validate inputs
 */
public class Validator {

    public static boolean validateLength(String s, int minLength, int maxLength) {
        return s != null
                && s.length() >= minLength
                && s.length() <= maxLength;
    }

    public static boolean validateName(String s, int minLength, int maxLength) {
        return validateLength(s, minLength, maxLength)
                && s.matches("^[a-zA-Z0-9_]+$");
    }

    public static boolean validateEmail(String s) {
        return validateLength(s, 0, 100)
                && s.matches("^[a-zA-Z0-9._-]+@([a-z0-9-_]+\\.)+[a-z]{2,}$");
    }

    public static boolean validateToken(String s) {
        return validateName(s, 32, 32);
    }

    public static Long parseId(String s) {
        if (s == null) {
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean validateId(String s) {
        return parseId(s) != null;
    }

    public static boolean validateUsername(String s) {
        return validateName(s, 5, 18);
    }

    public static boolean validatePassword(String s) {
        return validateName(s, 8, 18);
    }
}
