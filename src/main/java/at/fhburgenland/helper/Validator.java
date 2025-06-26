package at.fhburgenland.helper;

public class Validator {
    public static boolean invalidEmpty(String value, String fieldName) {
        if (value.isEmpty()) {
            System.out.println(fieldName + " darf nicht leer sein.");
            return true;
        }
        return false;
    }

    public static boolean invalidLength(String value, String fieldName, int maxLength) {
        if (value.length() > maxLength) {
            System.out.println(fieldName + " darf nicht länger als " + maxLength + " Zeichen sein.");
            return true;
        }
        return false;
    }
}
