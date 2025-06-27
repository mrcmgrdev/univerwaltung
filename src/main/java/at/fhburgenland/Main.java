package at.fhburgenland;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    static {
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);
    }

    public static void main(String[] args) {
        new UniVerwaltungstool().runProgram();
    }
}