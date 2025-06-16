package at.fhburgenland.helper;

import java.util.Scanner;

public class ScannerHelper {
    public static int readInt(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Bitte eine gültige Zahl eingeben: ");
            }
        }
    }
}
