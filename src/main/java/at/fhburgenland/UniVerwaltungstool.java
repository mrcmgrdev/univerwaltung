package at.fhburgenland;

import at.fhburgenland.dao.*;
import at.fhburgenland.model.*;
import at.fhburgenland.service.*;
import jakarta.persistence.*;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class UniVerwaltungstool {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("uni");

    private final Scanner scanner = new Scanner(System.in);

    private final StudentService studentService;
    private final ProfessorService professorService;
    private final KursService kursService;
    private final PruefungService pruefungService;
    private final NoteService noteService;
    private final FachabteilungService fachabteilungService;
    private final StudienprogrammService studienprogrammService;
    private final PruefungstypService pruefungstypService;
    private final PruefungKursVerwaltungService pruefungKursVerwaltungService;
    private final SpecialQueryService specialQueryService;

    public UniVerwaltungstool() {
        AbsolviertDao absolviertDao = new AbsolviertDao(emf);
        BesuchtDao besuchtDao = new BesuchtDao(emf);
        FachabteilungDao fachabteilungDao = new FachabteilungDao(emf);
        GehoertZuPruefungDao gehoertZuPruefungDao = new GehoertZuPruefungDao(emf);
        KursDao kursDao = new KursDao(emf);
        NoteDao noteDao = new NoteDao(emf);
        ProfessorDao professorDao = new ProfessorDao(emf);
        PruefungDao pruefungDao = new PruefungDao(emf);
        StudentDao studentDao = new StudentDao(emf);
        StudienprogrammDao studienprogrammDao = new StudienprogrammDao(emf);
        PruefungstypDao pruefungstypDao = new PruefungstypDao(emf);

        studentService = new StudentService(scanner, studentDao, studienprogrammDao);
        professorService = new ProfessorService(scanner, professorDao, fachabteilungDao);
        kursService = new KursService(scanner, studienprogrammDao, professorDao, kursDao);
        pruefungService = new PruefungService(scanner, pruefungstypDao, kursDao, pruefungDao, gehoertZuPruefungDao);
        noteService = new NoteService(scanner, noteDao);
        fachabteilungService = new FachabteilungService(scanner, fachabteilungDao);
        studienprogrammService = new StudienprogrammService(scanner, studienprogrammDao, professorDao);
        pruefungstypService = new PruefungstypService(scanner, pruefungstypDao);
        pruefungKursVerwaltungService = new PruefungKursVerwaltungService(scanner, pruefungDao, studentDao, absolviertDao, kursDao, noteDao, besuchtDao, gehoertZuPruefungDao);
        specialQueryService = new SpecialQueryService(scanner, absolviertDao, professorDao);

        System.out.println("Willkommen beim UniVerwaltungstool!");
    }

    private void printMenu() {
        System.out.print("""
                ------------Menü-----------
                |0. Exit                   |
                |1. Studenten              |
                |2. Professoren            |
                |3. Kurse                  |
                |4. Prüfungen              |
                |5. Noten                  |
                |6. Fachabteilungen        |
                |7. Studienprogramme       |
                |8. Prüfungstypen          |
                |9. Prüfung/Kurs Verwaltung|
                |10. Kurse Statistiken     |
                |11. Offene Notenvergabe   |
                --------------------------
                Eingabe:""");
    }

    public void runProgram() {
        boolean isRunning = true;
        while (isRunning) {
            printMenu();
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> studentenMenu();
                case "2" -> professorenMenu();
                case "3" -> kurseMenu();
                case "4" -> pruefungenMenu();
                case "5" -> notenMenu();
                case "6" -> fachabteilungenMenu();
                case "7" -> studienprogrammMenu();
                case "8" -> pruefungstypMenu();
                case "9" -> pruefungKursVerwaltung();
                case "10" -> specialQueryService.showKurseStatistiken();
                case "11" -> specialQueryService.showOffeneNotenvergabe();
                default -> System.out.println("Ungültige Eingabe! Bitte versuchen Sie es erneut.");
            }
        }

        emf.close();
    }

    private void printSubMenu(String name) {
        System.out.printf("""
                ------%s------
                0. Zurück
                1. Erstellen
                2. Anzeigen
                3. Bearbeiten
                4. Löschen
                -------------------
                Eingabe:""", name);
    }

    private void studentenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Studenten");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> studentService.addStudent();
                case "2" -> studentenAnzeigenMenu();
                case "3" -> studentService.updateStudent();
                case "4" -> studentService.deleteStudent();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void studentenAnzeigenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.print("""
                    ------Studenten Anzeigen------
                    0. Zurück
                    1. Alle Studenten anzeigen
                    2. Studienprogramme eines Studenten anzeigen
                    3. Kurse eines Studenten anzeigen
                    4. Prüfungen eines Studenten anzeigen
                    -------------------
                    Eingabe:""");

            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> studentService.showStudentList();
                case "2" -> studentService.showStudentProgramme();
                case "3" -> studentService.showStudentKurse();
                case "4" -> studentService.showStudentPruefungen();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void professorenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Professoren");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> professorService.addProfessor();
                case "2" -> professorAnzeigenMenu();
                case "3" -> professorService.updateProfessor();
                case "4" -> professorService.deleteProfessor();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void professorAnzeigenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.print("""
                    ------Professoren Anzeigen------
                    0. Zurück
                    1. Alle Professoren anzeigen
                    2. Kurse eines Professors anzeigen
                    -------------------
                    Eingabe:""");

            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> professorService.showProfessorList();
                case "2" -> professorService.showProfessorKurse();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void kurseMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Kurse");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> kursService.addKurs();
                case "2" -> kursAnzeigenMenu();
                case "3" -> kursService.updateKurs();
                case "4" -> kursService.deleteKurs();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void kursAnzeigenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.print("""
                    ------Kurse Anzeigen------
                    0. Zurück
                    1. Alle Kurse anzeigen
                    2. Professoren eines Kurses anzeigen
                    3. Studenten eines Kurses anzeigen
                    4. Prüfungen eines Kurses anzeigen
                    -------------------
                    Eingabe:""");

            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> kursService.showKursList();
                case "2" -> kursService.showKursProfessoren();
                case "3" -> kursService.showKursStudenten();
                case "4" -> kursService.showKursPruefungen();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void pruefungenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Prüfungen");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> pruefungService.addPruefung();
                case "2" -> pruefungAnzeigenMenu();
                case "3" -> pruefungService.updatePruefung();
                case "4" -> pruefungService.deletePruefung();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void pruefungAnzeigenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.print("""
                    ------Prüfungen Anzeigen------
                    0. Zurück
                    1. Alle Prüfungen anzeigen
                    2. Kurse einer Prüfung anzeigen
                    3. Absolvierte Prüfung pro Kurs anzeigen
                    -------------------
                    Eingabe:""");

            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> pruefungService.showPruefungList();
                case "2" -> pruefungService.showPruefungKurse();
                case "3" -> pruefungService.showPruefungVersuche();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void notenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Noten");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> noteService.addNote();
                case "2" -> noteService.showNote();
                case "3" -> noteService.updateNote();
                case "4" -> noteService.deleteNote();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void fachabteilungenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Fachabteilungen");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> fachabteilungService.addFachabteilung();
                case "2" -> fachabteilungAnzeigenMenu();
                case "3" -> fachabteilungService.updateFachabteilung();
                case "4" -> fachabteilungService.deleteFachabteilung();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void fachabteilungAnzeigenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.print("""
                    ------Fachabteilungen Anzeigen------
                    0. Zurück
                    1. Alle Fachabteilungen anzeigen
                    2. Professoren einer Fachabteilung anzeigen
                    -------------------
                    Eingabe:""");

            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> fachabteilungService.showFachabteilungList();
                case "2" -> fachabteilungService.showFachabteilungProfessoren();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void studienprogrammMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Studienprogramme");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> studienprogrammService.addStudienprogramm();
                case "2" -> studienprogrammeAnzeigenMenu();
                case "3" -> studienprogrammService.updateStudienprogramm();
                case "4" -> studienprogrammService.deleteStudienprogramm();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void studienprogrammeAnzeigenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.print("""
                    ------Studienprogramme Anzeigen------
                    0. Zurück
                    1. Alle Studienprogramme anzeigen
                    2. Kurse eines Studienprogramms anzeigen
                    3. Studenten eines Studienprogramms anzeigen
                    -------------------
                    Eingabe:""");

            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> studienprogrammService.showStudienprogrammList();
                case "2" -> studienprogrammService.showStudienprogrammKurse();
                case "3" -> studienprogrammService.showStudienprogrammStudenten();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void pruefungstypMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Prüfungstypen");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> pruefungstypService.addPruefungstyp();
                case "2" -> pruefungstypService.showPruefungstypen();
                case "3" -> pruefungstypService.updatePruefungstyp();
                case "4" -> pruefungstypService.deletePruefungstyp();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void pruefungKursVerwaltung() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.print("""
                    ------Prüfung/Kurs Verwaltung------
                    0. Zurück
                    1. Prüfungen zuordnen (+Noten vergeben)
                    2. Kurse Notenverwaltung
                    3. Kurse Zuordnung Student
                    4. Prüfung Kurs Zuweisung
                    ----------------------------
                    Eingabe:""");

            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> pruefungKursVerwaltungService.pruefungsNotenVerwaltung();
                case "2" -> pruefungKursVerwaltungService.kursNotenVerwaltung();
                case "3" -> pruefungKursVerwaltungService.kursStudentenVerwaltung();
                case "4" -> pruefungKursVerwaltungService.pruefungKursZuweisung();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }
}
