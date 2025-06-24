package at.fhburgenland;

import at.fhburgenland.dao.*;
import at.fhburgenland.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import static at.fhburgenland.helper.ScannerHelper.readInt;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UniVerwaltungstool {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("uni");
    private final Scanner scanner;

    private final AbsolviertDao absolviertDao;
    private final BesuchtDao besuchtDao;
    private final FachabteilungDao fachabteilungDao;
    private final GehoertZuPruefungDao gehoertZuPruefungDao;
    private final KursDao kursDao;
    private final NoteDao noteDao;
    private final ProfessorDao professorDao;
    private final PruefungDao pruefungDao;
    private final StudentDao studentDao;
    private final StudienprogrammDao studienprogrammDao;

    public UniVerwaltungstool() {
        scanner = new Scanner(System.in);
        this.absolviertDao = new AbsolviertDao(emf);
        this.besuchtDao = new BesuchtDao(emf);
        this.fachabteilungDao = new FachabteilungDao(emf);
        this.gehoertZuPruefungDao = new GehoertZuPruefungDao(emf);
        this.kursDao = new KursDao(emf);
        this.noteDao = new NoteDao(emf);
        this.professorDao = new ProfessorDao(emf);
        this.pruefungDao = new PruefungDao(emf);
        this.studentDao = new StudentDao(emf);
        this.studienprogrammDao = new StudienprogrammDao(emf);

        System.out.println("Willkommen beim UniVerwaltungstool!");
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
                case "4" -> prufungenMenu();
                case "5" -> notenMenu();
                case "6" -> fachabteilungenMenu();
                case "7" -> studienprogrammMenu();
                default -> System.out.println("Ungültige Eingabe! Bitte versuchen Sie es erneut.");
            }
        }

        emf.close();
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
                |8. Statistiken Kurse      |
                |9. Offene Notenvergabe    |
                --------------------------
                Eingabe:""");
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
                case "1" -> addStudent();
                case "2" -> studentenAnzeigenMenu();
                case "3" -> updateStudent();
                case "4" -> deleteStudent();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void addStudent() {
        System.out.print("Vorname: ");
        String vorname = scanner.nextLine();

        System.out.print("Nachname: ");
        String nachname = scanner.nextLine();

        System.out.print("Matrikelnummer: ");
        String matrikelnummer = scanner.nextLine();

        System.out.print("Email (optional): ");
        String email = scanner.nextLine();
        if (email.isEmpty()) {
            email = null;
        }

        System.out.print("Geburtsdatum (YYYY-MM-DD, optional): ");
        String geburtsdatumInput = scanner.nextLine();
        LocalDate geburtsdatum = null;
        if (!geburtsdatumInput.isEmpty()) {
            try {
                geburtsdatum = LocalDate.parse(geburtsdatumInput);
            } catch (Exception e) {
                System.out.println("Ungültiges Datum! Bitte im Format YYYY-MM-DD eingeben.");
            }
        }

        Student student = new Student().vorname(vorname).nachname(nachname).matrikelnummer(matrikelnummer).email(email).geburtsdatum(geburtsdatum);

        System.out.print("Möchten Sie Studienprogramme hinzufügen? (j/n)");
        if (scanner.nextLine().equalsIgnoreCase("j")) {
            System.out.println("Verfügbare Studienprogramme:");
            List<Studienprogramm> studienprogramme = studienprogrammDao.findAll();

            if (studienprogramme.isEmpty()) {
                System.out.println("Keine Studienprogramme verfügbar.");
            } else {
                studienprogramme.forEach(System.out::println);

                System.out.println("Geben Sie die Studienprogramm-IDs ein, die Sie hinzufügen möchten.");
                System.out.println("Format: ID1,ID2,ID3,...");
                String input = scanner.nextLine();

                if (!input.isEmpty()) {
                    String[] ids = input.split(",");
                    for (String idStr : ids) {
                        try {
                            int id = Integer.parseInt(idStr.trim());
                            Studienprogramm sp = studienprogrammDao.findById(id);
                            if (sp != null) {
                                student.addGewaehltesStudienprogramm(sp);
                                System.out.println("Studienprogramm " + sp.getName() + " hinzugefügt.");
                            } else {
                                System.out.println("Studienprogramm mit ID " + id + " nicht gefunden.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Ungültige ID: " + idStr);
                        }
                    }
                }
            }
        }

        Student addedStudent = studentDao.save(student);
        if (addedStudent != null) {
            System.out.println("Student erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen des Studenten.");
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
                case "1" -> studentDao.findAll().forEach(System.out::println);
                case "2" -> showStudentProgramme();
                case "3" -> showStudentKurse();
                case "4" -> showStudentPruefungen();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void showStudentProgramme() {
        System.out.print("ID des Studenten: ");
        int studentId = readInt(scanner);
        Student student = studentDao.findById(studentId);

        if (student == null) {
            System.out.println("Kein Student mit dieser ID gefunden.");
            return;
        }

        System.out.println("Student: " + student.getVorname() + " " + student.getNachname() + " (ID: " + student.getStudentId() + ")");
        System.out.println("Eingeschriebene Studienprogramme:");

        if (student.getGewaehlteStudienprogramme().isEmpty()) {
            System.out.println("Der Student ist in keinem Studienprogramm eingeschrieben.");
        } else {
            student.getGewaehlteStudienprogramme().stream().sorted(Comparator.comparing(Studienprogramm::getStudienprogrammId)).forEach(sp -> System.out.println("  - " + sp.getStudienprogrammId() + ": " + sp.getName()));
        }
    }

    private void showStudentKurse() {
        System.out.print("ID des Studenten: ");
        int studentId = readInt(scanner);
        Student student = studentDao.findById(studentId);

        if (student == null) {
            System.out.println("Kein Student mit dieser ID gefunden.");
            return;
        }

        System.out.println("Student: " + student.getVorname() + " " + student.getNachname() + " (ID: " + student.getStudentId() + ")");
        System.out.println("Gewählte Kurse:");

        if (student.getBesuchteKurse().isEmpty()) {
            System.out.println("Der Student hat keine Kurse besucht.");
        } else {
            student.getBesuchteKurse().stream().sorted(Comparator.comparing(Besucht::getId)).forEach(besucht -> System.out.println("  - " + besucht.getKurs().getKursId() + ": " + besucht.getKurs().getBezeichnung()));
        }
    }

    private void showStudentPruefungen() {
        System.out.print("ID des Studenten: ");
        int studentId = readInt(scanner);
        Student student = studentDao.findById(studentId);

        if (student == null) {
            System.out.println("Kein Student mit dieser ID gefunden.");
            return;
        }

        System.out.println("Student: " + student.getVorname() + " " + student.getNachname() + " (ID: " + student.getStudentId() + ")");
        System.out.println("Absolvierte Prüfungen:");

        if (student.getAbsolviertePruefungen().isEmpty()) {
            System.out.println("Der Student hat keine Prüfungen absolviert.");
        } else {
            student.getAbsolviertePruefungen().stream().sorted(Comparator.comparing(Absolviert::getId)).forEach(absolviert -> System.out.println("  - " + absolviert.getPruefung().getPruefungsId() + ": " + absolviert.getPruefung().getBezeichnung() + " (Note: " + absolviert.getNote().getBezeichnung() + ")"));
        }
    }

    private void updateStudent() {
        System.out.print("Id des zu bearbeitenden Studenten: ");
        int id = readInt(scanner);
        Student student = studentDao.findById(id);
        if (student == null) {
            System.out.println("Kein Student mit dieser Id gefunden.");
            return;
        }

        System.out.print("Neuer Vorname (aktuell: " + student.getVorname() + "): ");
        String vorname = scanner.nextLine();
        if (!vorname.isEmpty()) {
            student.setVorname(vorname);
        }

        System.out.print("Neuer Nachname (aktuell: " + student.getNachname() + "): ");
        String nachname = scanner.nextLine();
        if (!nachname.isEmpty()) {
            student.setNachname(nachname);
        }

        System.out.print("Neue Email (aktuell: " + student.getEmail() + ", optional): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) {
            student.setEmail(email);
        }

        System.out.print("Neues Geburtsdatum (aktuell: " + student.getGeburtsdatum() + ", YYYY-MM-DD, optional): ");
        String geburtsdatumInput = scanner.nextLine();
        if (!geburtsdatumInput.isEmpty()) {
            try {
                LocalDate geburtsdatum = LocalDate.parse(geburtsdatumInput);
                student.setGeburtsdatum(geburtsdatum);
            } catch (Exception e) {
                System.out.println("Ungültiges Datum! Bitte im Format YYYY-MM-DD eingeben.");
            }
        }

        System.out.println("Aktuelle Studienprogramme des Studenten:");
        if (student.getGewaehlteStudienprogramme().isEmpty()) {
            System.out.println("Keine Studienprogramme zugeordnet.");
        } else {
            student.getGewaehlteStudienprogramme().stream().sorted(Comparator.comparing(Studienprogramm::getStudienprogrammId)).forEach(sp -> System.out.println(sp.getStudienprogrammId() + ": " + sp.getName()));
        }

        System.out.print("Geben Sie die Änderungen im Format '+ID,-ID,+ID' ein (z.B. '+1,-5,+4' oder leer für keine Änderung):");
        String input = scanner.nextLine().trim();

        if (!input.isEmpty()) {
            String[] changes = input.split(",");
            for (String change : changes) {
                change = change.trim();
                if (change.length() < 2) continue;

                char operation = change.charAt(0);
                try {
                    int spId = Integer.parseInt(change.substring(1));
                    Studienprogramm sp = studienprogrammDao.findById(spId);

                    if (sp == null) {
                        System.out.println("Studienprogramm mit ID " + spId + " nicht gefunden.");
                        continue;
                    }

                    Optional<Studienprogramm> foundSp = student.getGewaehlteStudienprogramme().stream().filter(s -> s.getStudienprogrammId() == spId).findFirst();

                    if (operation == '+') {
                        if (foundSp.isPresent()) {
                            System.out.println("Student ist bereits in Studienprogramm " + sp.getName() + " eingeschrieben.");
                        } else {
                            student.addGewaehltesStudienprogramm(sp);
                            System.out.println("Studienprogramm " + sp.getName() + " wurde hinzugefügt.");
                        }
                    } else if (operation == '-') {
                        if (foundSp.isPresent()) {
                            student.removeGewaehltesStudienprogramm(foundSp.get());
                            System.out.println("Studienprogramm " + sp.getName() + " wurde entfernt.");
                        } else {
                            System.out.println("Student ist nicht in Studienprogramm " + sp.getName() + " eingeschrieben.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige ID in: " + change);
                }
            }
        }

        Student updatedStudent = studentDao.update(student);
        if (updatedStudent != null) {
            System.out.println("Student erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren des Studenten.");
        }
    }

    private void deleteStudent() {
        System.out.print("Id des zu löschenden Studenten: ");
        if (studentDao.deleteById(readInt(scanner))) {
            System.out.println("Student erfolgreich gelöscht.");
        } else {
            System.out.println("Kein Student mit dieser Id gefunden.");
        }
    }

    private void professorenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Professoren");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> addProfessor();
                case "2" -> professorAnzeigenMenu();
                case "3" -> updateProfessor();
                case "4" -> deleteProfessor();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void addProfessor() {
        System.out.print("Vorname: ");
        String vorname = scanner.nextLine();

        System.out.print("Nachname: ");
        String nachname = scanner.nextLine();

        System.out.print("Email (optional): ");
        String email = scanner.nextLine();
        if (email.isEmpty()) {
            email = null;
        }

        System.out.println("Verfügbare Fachabteilungen:");
        fachabteilungDao.findAll().forEach(System.out::println);
        System.out.print("Fachabteilung (Id): ");
        Fachabteilung fachabteilung;
        int abteilungsId = readInt(scanner);
        fachabteilung = fachabteilungDao.findById(abteilungsId);
        if (fachabteilung == null) {
            System.out.println("Keine Fachabteilung mit dieser Id gefunden!");
            return;
        }

        Professor professor = new Professor().vorname(vorname).nachname(nachname).email(email).fachabteilung(fachabteilung);

        Professor addedProfessor = professorDao.save(professor);
        if (addedProfessor != null) {
            System.out.println("Professor erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen des Professors.");
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
                case "1" -> professorDao.findAll().forEach(System.out::println);
                case "2" -> showProfessorKurse();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void showProfessorKurse() {
        System.out.print("ID des Professors: ");
        int professorId = readInt(scanner);
        Professor professor = professorDao.findById(professorId);

        if (professor == null) {
            System.out.println("Kein Professor mit dieser ID gefunden.");
            return;
        }

        System.out.println("Professor: " + professor.getVorname() + " " + professor.getNachname() + " (ID: " + professor.getProfessorId() + ")");
        System.out.println("Zugeordnete Kurse:");

        if (professor.getUnterrichteteKurse().isEmpty()) {
            System.out.println("Der Professor hat keine Kurse zugeordnet.");
        } else {
            professor.getUnterrichteteKurse().stream().sorted(Comparator.comparing(Kurs::getKursId)).forEach(kurs -> System.out.println("  - " + kurs.getKursId() + ": " + kurs.getBezeichnung()));
        }
    }

    private void updateProfessor() {
        System.out.print("Id des zu bearbeitenden Professors: ");
        Professor professor = professorDao.findById(readInt(scanner));

        if (professor == null) {
            System.out.println("Kein Professor mit dieser Id gefunden.");
            return;
        }

        System.out.print("Neuer Vorname (aktuell: " + professor.getVorname() + "): ");
        String vorname = scanner.nextLine();
        if (!vorname.isEmpty()) {
            professor.setVorname(vorname);
        }

        System.out.print("Neuer Nachname (aktuell: " + professor.getNachname() + "): ");
        String nachname = scanner.nextLine();
        if (!nachname.isEmpty()) {
            professor.setNachname(nachname);
        }

        System.out.print("Neue Email (aktuell: " + professor.getEmail() + ", optional): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) {
            professor.setEmail(email);
        }

        System.out.println("Aktuelle Fachabteilung: " + (professor.getFachabteilung() != null ? professor.getFachabteilung().getName() + " (ID: " + professor.getFachabteilung().getAbteilungsId() + ")" : "Keine"));
        System.out.print("Möchten Sie die Fachabteilung ändern? (j/n)");
        if (scanner.nextLine().equalsIgnoreCase("j")) {
            System.out.println("Verfügbare Fachabteilungen:");
            fachabteilungDao.findAll().forEach(System.out::println);
            System.out.print("Neue Fachabteilung (ID): ");
            int abteilungsId = readInt(scanner);
            Fachabteilung fachabteilung = fachabteilungDao.findById(abteilungsId);
            if (fachabteilung != null) {
                professor.setFachabteilung(fachabteilung);
            } else {
                System.out.println("Keine Fachabteilung mit dieser ID gefunden. Fachabteilung bleibt unverändert.");
            }
        }

        Professor updatedProfessor = professorDao.update(professor);
        if (updatedProfessor != null) {
            System.out.println("Professor erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren des Professors.");
        }
    }

    private void deleteProfessor() {
        System.out.print("Id des zu löschenden Professors: ");
        if (professorDao.deleteById(readInt(scanner))) {
            System.out.println("Professor erfolgreich gelöscht.");
        } else {
            System.out.println("Kein Professor mit dieser Id gefunden.");
        }

    }

    private void kurseMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Kurse");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
//                case "1" -> addKurs();
//                case "2" -> kursAnzeigenMenu();
//                case "3" -> updateKurs();
//                case "4" -> deleteKurs();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void prufungenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Prüfungen");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
//                case "1" -> addPrufung();
//                case "2" -> prufungAnzeigenMenu();
//                case "3" -> updatePrufung();
//                case "4" -> deletePrufung();
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
//                case "1" -> addNote();
//                case "2" -> noteAnzeigenMenu();
//                case "3" -> updateNote();
//                case "4" -> deleteNote();
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
//                case "1" -> addFachabteilung();
//                case "2" -> fachabteilungAnzeigenMenu();
//                case "3" -> updateFachabteilung();
//                case "4" -> deleteFachabteilung();
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
//                case "1" -> addStudienprogramm();
//                case "2" -> studienprogrammeAnzeigenMenu();
//                case "3" -> updateStudienprogramm();
//                case "4" -> deleteStudienprogramm();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }
}
