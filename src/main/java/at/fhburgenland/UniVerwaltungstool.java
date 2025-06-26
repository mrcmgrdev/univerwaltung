package at.fhburgenland;

import at.fhburgenland.dao.*;
import at.fhburgenland.model.*;
import jakarta.persistence.*;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

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
    private final PruefungstypDao pruefungstypDao;

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
        this.pruefungstypDao = new PruefungstypDao(emf);

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
                case "10" -> showKurseStatistiken();
                case "11" -> showOffeneNotenvergabe();
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
                case "1" -> addStudent();
                case "2" -> studentenAnzeigenMenu();
                case "3" -> updateStudent();
                case "4" -> deleteStudent();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private boolean invalidStudentMatrikelnummer(String matrikelnummer) {
        if (invalidEmpty(matrikelnummer, "Matrikelnummer") || invalidLength(matrikelnummer, "Matrikelnummer", 50)) {
            return true;
        }

        if (studentDao.findAll().stream().anyMatch(student -> matrikelnummer.equals(student.getMatrikelnummer()))) {
            System.out.println("Ein Student mit dieser Matrikelnummer existiert bereits.");
            return true;
        }

        return false;
    }

    private boolean invalidObjectEmail(String email, boolean emailAlreadyExists, String objectName) {
        if (invalidLength(email, "Email", 255)) {
            return true;
        }
        if (emailAlreadyExists) {
            System.out.println("Ein " + objectName + " mit dieser Email existiert bereits.");
            return true;
        }
        if (!email.matches("^(.+)@(\\S+)$")) {
            System.out.println("Ungültiges Email-Format.");
            return true;
        }
        return false;
    }

    private void addStudent() {
        System.out.print("Vorname: ");
        String vorname = scanner.nextLine();
        if (invalidEmpty(vorname, "Vorname") || invalidLength(vorname, "Vorname", 50)) {
            return;
        }

        System.out.print("Nachname: ");
        String nachname = scanner.nextLine();
        if (invalidEmpty(nachname, "Nachname") || invalidLength(nachname, "Nachname", 50)) {
            return;
        }

        System.out.print("Matrikelnummer: ");
        String matrikelnummer = scanner.nextLine();
        if (invalidStudentMatrikelnummer(matrikelnummer)) {
            return;
        }

        System.out.print("Email (optional): ");
        String email = scanner.nextLine();
        if (email.isEmpty()) {
            email = null;
        } else {
            String finalEmail = email;
            if (invalidObjectEmail(email, studentDao.findAll().stream().anyMatch(s -> finalEmail.equals(s.getEmail())), "Student")) {
                return;
            }
        }

        System.out.print("Geburtsdatum (YYYY-MM-DD, optional): ");
        String geburtsdatumInput = scanner.nextLine();
        LocalDate geburtsdatum = null;
        if (!geburtsdatumInput.isEmpty()) {
            try {
                geburtsdatum = LocalDate.parse(geburtsdatumInput);

                if (geburtsdatum.isAfter(LocalDate.now())) {
                    System.out.println("Geburtsdatum darf nicht in der Zukunft liegen.");
                    return;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Ungültiges Datum! Bitte im Format YYYY-MM-DD eingeben.");
                return;
            }
        }

        Student student = new Student().vorname(vorname).nachname(nachname).matrikelnummer(matrikelnummer).email(email).geburtsdatum(geburtsdatum);

        System.out.println("Verfügbare Studienprogramme:");
        List<Studienprogramm> studienprogramme = studienprogrammDao.findAll();
        if (studienprogramme.isEmpty()) {
            System.out.println("Keine Studienprogramme verfügbar. Student kann nicht erstellt werden.");
            return;
        } else {
            studienprogramme.forEach(System.out::println);

            boolean validInput = false;
            while (!validInput) {
                System.out.println("Geben Sie die Studienprogramm-IDs ein, die Sie hinzufügen möchten.");
                System.out.println("Format: ID1,ID2,ID3,...");
                String input = scanner.nextLine();

                if (input.isEmpty()) {
                    System.out.println("Der Student muss mindestens einem Studienprogramm zugeordnet werden.");
                    return;
                }

                String[] ids = input.split(",");
                boolean addedAtLeastOne = false;

                for (String idStr : ids) {
                    try {
                        int id = Integer.parseInt(idStr.trim());
                        Studienprogramm sp = studienprogrammDao.findById(id);
                        if (sp != null) {
                            student.addGewaehltesStudienprogramm(sp);
                            System.out.println("Studienprogramm " + sp.getName() + " hinzugefügt.");
                            addedAtLeastOne = true;
                        } else {
                            System.out.println("Studienprogramm mit ID " + id + " nicht gefunden.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Ungültige ID: " + idStr);
                    }
                }

                if (addedAtLeastOne) {
                    validInput = true;
                } else {
                    System.out.println("Der Student muss mindestens einem gültigen Studienprogramm zugeordnet werden.");
                    return;
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

        System.out.printf("Student: %s %s (ID: %d)%n", student.getVorname(), student.getNachname(), student.getStudentId());
        System.out.println("Eingeschriebene Studienprogramme:");

        if (student.getGewaehlteStudienprogramme().isEmpty()) {
            System.out.println("Der Student ist in keinem Studienprogramm eingeschrieben.");
        } else {
            student.getGewaehlteStudienprogramme().stream().sorted(Comparator.comparing(Studienprogramm::getStudienprogrammId)).forEach(sp -> System.out.printf("  - %d: %s%n", sp.getStudienprogrammId(), sp.getName()));
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

        System.out.printf("Student: %s %s (ID: %d)%n", student.getVorname(), student.getNachname(), student.getStudentId());
        System.out.println("Gewählte Kurse:");

        if (student.getBesuchteKurse().isEmpty()) {
            System.out.println("Der Student hat keine Kurse besucht.");
        } else {
            student.getBesuchteKurse().stream().sorted(Comparator.comparing(Besucht::getId)).forEach(besucht -> System.out.printf("  - %d: %s (Note: %s)%n", besucht.getKurs().getKursId(), besucht.getKurs().getBezeichnung(), besucht.getNote().getBezeichnung()));
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

        System.out.printf("Student: %s %s (ID: %d)%n", student.getVorname(), student.getNachname(), student.getStudentId());
        System.out.println("Absolvierte Prüfungen:");

        if (student.getAbsolviertePruefungen().isEmpty()) {
            System.out.println("Der Student hat keine Prüfungen absolviert.");
        } else {
            student.getAbsolviertePruefungen().stream().sorted(Comparator.comparing(Absolviert::getId)).forEach(absolviert -> System.out.printf("  - %d: %s (Note: %s)%n", absolviert.getPruefung().getPruefungsId(), absolviert.getPruefung().getBezeichnung(), (absolviert.getNote() != null ? absolviert.getNote().getBezeichnung() : "Keine Note vergeben")));
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
            if (invalidEmpty(vorname, "Vorname") || invalidLength(vorname, "Vorname", 50)) {
                return;
            }
            student.setVorname(vorname);
        }

        System.out.print("Neuer Nachname (aktuell: " + student.getNachname() + "): ");
        String nachname = scanner.nextLine();
        if (!nachname.isEmpty()) {
            if (invalidEmpty(nachname, "Nachname") || invalidLength(nachname, "Nachname", 50)) {
                return;
            }
            student.setNachname(nachname);
        }

        System.out.print("Neue Email (aktuell: " + student.getEmail() + ", optional): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) {
            if (invalidObjectEmail(email, studentDao.findAll().stream().anyMatch(s -> email.equals(s.getEmail())), "Student")) {
                return;
            }
            student.setEmail(email);
        }

        System.out.print("Neues Geburtsdatum (aktuell: " + student.getGeburtsdatum() + ", YYYY-MM-DD, optional): ");
        String geburtsdatumInput = scanner.nextLine();
        if (!geburtsdatumInput.isEmpty()) {
            try {
                LocalDate geburtsdatum = LocalDate.parse(geburtsdatumInput);
                if (geburtsdatum.isAfter(LocalDate.now())) {
                    System.out.println("Geburtsdatum darf nicht in der Zukunft liegen.");
                    return;
                }

                student.setGeburtsdatum(geburtsdatum);
            } catch (DateTimeParseException e) {
                System.out.println("Ungültiges Datum! Bitte im Format YYYY-MM-DD eingeben.");
                return;
            }
        }

        System.out.println("Aktuelle Studienprogramme des Studenten:");
        student.getGewaehlteStudienprogramme().stream().sorted(Comparator.comparing(Studienprogramm::getStudienprogrammId)).forEach(sp -> System.out.println(sp.getStudienprogrammId() + ": " + sp.getName()));

        System.out.print("Geben Sie die Änderungen im Format '+ID,-ID,+ID' ein (z.B. '+1,-5,+4' oder leer für keine Änderung):");
        String input = scanner.nextLine().trim();

        if (!input.isEmpty()) {
            String[] changes = input.split(",");
            Set<Studienprogramm> toAdd = new HashSet<>();
            Set<Studienprogramm> toRemove = new HashSet<>();

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
                            toAdd.add(sp);
                        }
                    } else if (operation == '-') {
                        if (foundSp.isPresent()) {
                            toRemove.add(foundSp.get());
                        } else {
                            System.out.println("Student ist nicht in Studienprogramm " + sp.getName() + " eingeschrieben.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige ID in: " + change);
                }
            }

            Set<Studienprogramm> currentProgramme = new HashSet<>(student.getGewaehlteStudienprogramme());
            currentProgramme.removeAll(toRemove);
            currentProgramme.addAll(toAdd);

            if (currentProgramme.isEmpty()) {
                System.out.println("Der Student muss in mindestens einem Studienprogramm eingeschrieben sein.");
                return;
            }

            for (Studienprogramm sp : toAdd) {
                student.addGewaehltesStudienprogramm(sp);
                studienprogrammDao.update(sp);
                System.out.println("Studienprogramm " + sp.getName() + " wurde hinzugefügt.");
            }

            for (Studienprogramm sp : toRemove) {
                student.removeGewaehltesStudienprogramm(sp);
                studienprogrammDao.update(sp);
                System.out.println("Studienprogramm " + sp.getName() + " wurde entfernt.");
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
        if (invalidEmpty(vorname, "Vorname") || invalidLength(vorname, "Vorname", 50)) {
            return;
        }

        System.out.print("Nachname: ");
        String nachname = scanner.nextLine();
        if (invalidEmpty(nachname, "Nachname") || invalidLength(nachname, "Nachname", 50)) {
            return;
        }

        System.out.print("Email (optional): ");
        String email = scanner.nextLine();
        if (email.isEmpty()) {
            email = null;
        } else {
            String finalEmail = email;
            if (invalidObjectEmail(email, professorDao.findAll().stream().anyMatch(professor -> finalEmail.equals(professor.getEmail())), "Professor")) {
                return;
            }
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

        System.out.printf("Professor: %s %s (ID: %d)%n", professor.getVorname(), professor.getNachname(), professor.getProfessorId());
        System.out.println("Zugeordnete Kurse:");

        if (professor.getUnterrichteteKurse().isEmpty()) {
            System.out.println("Der Professor hat keine Kurse zugeordnet.");
        } else {
            professor.getUnterrichteteKurse().stream().sorted(Comparator.comparing(Kurs::getKursId)).forEach(kurs -> System.out.printf("  - %d: %s%n", kurs.getKursId(), kurs.getBezeichnung()));
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
            if (invalidEmpty(vorname, "Vorname") || invalidLength(vorname, "Vorname", 50)) {
                return;
            }
            professor.setVorname(vorname);
        }

        System.out.print("Neuer Nachname (aktuell: " + professor.getNachname() + "): ");
        String nachname = scanner.nextLine();
        if (!nachname.isEmpty()) {
            if (invalidEmpty(nachname, "Nachname") || invalidLength(nachname, "Nachname", 50)) {
                return;
            }
            professor.setNachname(nachname);
        }

        System.out.print("Neue Email (aktuell: " + professor.getEmail() + ", optional): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) {
            if (invalidObjectEmail(email, professorDao.findAll().stream().anyMatch(prof -> email.equals(prof.getEmail())), "Professor")) {
                return;
            }
            professor.setEmail(email);
        }

        System.out.println("Aktuelle Fachabteilung: " + (professor.getFachabteilung().getName() + " (ID: " + professor.getFachabteilung().getAbteilungsId() + ")"));
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
                System.out.println("Keine Fachabteilung mit dieser ID gefunden.");
                return;
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
                case "1" -> addKurs();
                case "2" -> kursAnzeigenMenu();
                case "3" -> updateKurs();
                case "4" -> deleteKurs();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void addKurs() {
        System.out.print("Kursbezeichnung: ");
        String bezeichnung = scanner.nextLine();
        if (invalidEmpty(bezeichnung, "Kursbezeichnung") || invalidLength(bezeichnung, "Kursbezeichnung", 100)) {
            return;
        }

        System.out.print("Semester: ");
        int semester = readInt(scanner);
        if (semester < 1) {
            System.out.println("Semester muss größer als 0 sein.");
            return;
        }

        System.out.print("ECTS: ");
        int ects = readInt(scanner);
        if (ects < 0) {
            System.out.println("ECTS muss größer oder gleich 0 sein.");
            return;
        }

        System.out.println("Verfügbare Studienprogramme:");
        studienprogrammDao.findAll().forEach(System.out::println);
        System.out.print("Studienprogramm (Id): ");
        int studienprogrammId = readInt(scanner);
        Studienprogramm studienprogramm = studienprogrammDao.findById(studienprogrammId);
        if (studienprogramm == null) {
            System.out.println("Kein Studienprogramm mit dieser Id gefunden!");
            return;
        }

        Kurs kurs = new Kurs().bezeichnung(bezeichnung).semester(semester).ects(ects).studienprogramm(studienprogramm);

        System.out.println("Verfügbare Professoren:");
        List<Professor> professoren = professorDao.findAll();

        if (professoren.isEmpty()) {
            System.out.println("Keine Professoren verfügbar. Kurs kann nicht erstellt werden.");
            return;
        }

        professoren.forEach(System.out::println);

        System.out.println("Geben Sie die Professor-IDs ein, die Sie hinzufügen möchten (Format: ID1,ID2,ID3,...):");
        String input = scanner.nextLine();

        if (input.isEmpty()) {
            System.out.println("Der Kurs muss mindestens einem Professor zugeordnet werden.");
            return;
        }

        String[] ids = input.split(",");
        boolean addedAtLeastOne = false;

        for (String idStr : ids) {
            try {
                int profId = Integer.parseInt(idStr.trim());
                Professor professor = professorDao.findById(profId);

                if (professor == null) {
                    System.out.println("Professor mit ID " + profId + " nicht gefunden.");
                    continue;
                }

                kurs.addProfessor(professor);
                this.professorDao.update(professor);
                System.out.println("Professor " + professor.getVorname() + " " + professor.getNachname() + " hinzugefügt.");
                addedAtLeastOne = true;
            } catch (NumberFormatException e) {
                System.out.println("Ungültige ID: " + idStr);
            }
        }

        if (!addedAtLeastOne) {
            System.out.println("Der Kurs muss mindestens einem gültigen Professor zugeordnet werden.");
            return;
        }

        Kurs addedKurs = kursDao.save(kurs);
        if (addedKurs != null) {
            System.out.println("Kurs erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen des Kurses.");
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
                case "1" -> kursDao.findAll().forEach(System.out::println);
                case "2" -> showKursProfessoren();
                case "3" -> showKursStudenten();
                case "4" -> showKursPruefungen();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void showKursProfessoren() {
        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());
        System.out.println("Zugewiesene Professoren:");

        if (kurs.getProfessoren().isEmpty()) {
            System.out.println("Keine Professoren für diesen Kurs zugewiesen.");
        } else {
            kurs.getProfessoren().stream().sorted(Comparator.comparing(Professor::getProfessorId)).forEach(prof -> System.out.printf("  - %d: %s %s%n", prof.getProfessorId(), prof.getVorname(), prof.getNachname()));
        }
    }

    private void showKursStudenten() {
        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());
        System.out.println("Eingeschriebene Studenten:");

        if (kurs.getTeilnehmendeStudenten().isEmpty()) {
            System.out.println("Keine Studenten für diesen Kurs eingeschrieben.");
        } else {
            kurs.getTeilnehmendeStudenten().stream().sorted(Comparator.comparing(Besucht::getId)).forEach(besucht -> {
                Student student = besucht.getStudent();
                System.out.printf("  - %d: %s %s (Matrikelnr: %s)%n", student.getStudentId(), student.getVorname(), student.getNachname(), student.getMatrikelnummer());
            });
        }
    }

    private void showKursPruefungen() {
        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());
        System.out.println("Zugeordnete Prüfungen:");

        if (kurs.getZugehoerigePruefungen().isEmpty()) {
            System.out.println("Keine Prüfungen für diesen Kurs zugeordnet.");
        } else {
            kurs.getZugehoerigePruefungen().stream().sorted(Comparator.comparing(GehoertZuPruefung::getId)).forEach(zuordnung -> {
                Pruefung pruefung = zuordnung.getPruefung();
                System.out.printf("  - %d: %s%s%n", pruefung.getPruefungsId(), pruefung.getBezeichnung(), pruefung.getDatum() != null ? " (Datum: " + pruefung.getDatum() + ")" : "");
            });
        }
    }

    private void updateKurs() {
        System.out.print("Id des zu bearbeitenden Kurses: ");
        Kurs kurs = kursDao.findById(readInt(scanner));

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser Id gefunden.");
            return;
        }

        System.out.print("Neue Kursbezeichnung (aktuell: " + kurs.getBezeichnung() + "): ");
        String kursbezeichnung = scanner.nextLine();
        if (!kursbezeichnung.isEmpty()) {
            if (invalidEmpty(kursbezeichnung, "Kursbezeichnung") || invalidLength(kursbezeichnung, "Kursbezeichnung", 100)) {
                return;
            }
            kurs.setBezeichnung(kursbezeichnung);
        }

        System.out.print("Neues Semester (aktuell: " + kurs.getSemester() + "): ");
        String kursEingabe = scanner.nextLine();
        if (!kursEingabe.isEmpty()) {
            try {
                int kursSemester = Integer.parseInt(kursEingabe);
                if (kursSemester < 1) {
                    System.out.println("Semester muss größer als 0 sein.");
                    return;
                }

                kurs.setSemester(kursSemester);
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe. Semester wurde nicht geändert.");
            }
        }

        System.out.print("Neue ECTS (aktuell: " + kurs.getEcts() + "): ");
        String ectsEingabe = scanner.nextLine();
        if (!ectsEingabe.isEmpty()) {
            try {
                int kursEcts = Integer.parseInt(ectsEingabe);
                if (kursEcts < 0) {
                    System.out.println("ECTS muss größer oder gleich 0 sein.");
                    return;
                }

                kurs.setEcts(kursEcts);
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe. Ects wurde nicht geändert.");
            }
        }

        System.out.print("Möchten sie das zugehöriges Studienprogramm ändern? (aktuell: " + kurs.getStudienprogramm().getName() + "): j/n");
        if (scanner.nextLine().equalsIgnoreCase("j")) {
            int studienprogrammId = readInt(scanner);
            Studienprogramm studienprogramm = studienprogrammDao.findById(studienprogrammId);
            if (studienprogramm == null) {
                System.out.println("Kein Studienprogramm mit dieser Id gefunden!");
                return;
            }

            kurs.setStudienprogramm(studienprogramm);
        }

        System.out.println("Zugewiesene Professoren:");
        kurs.getProfessoren().forEach(prof -> System.out.println(prof.getProfessorId() + ": " + prof.getVorname() + " " + prof.getNachname()));

        System.out.print("Professoren ändern im Format '+ID,-ID,+ID' (z.B. '+1,-5,+4' oder leer für keine Änderung): ");
        String input = scanner.nextLine().trim();

        if (!input.isEmpty()) {
            String[] changes = input.split(",");
            Set<Professor> toRemove = new HashSet<>();
            Set<Professor> toAdd = new HashSet<>();

            for (String change : changes) {
                change = change.trim();
                if (change.length() < 2) continue;

                char operation = change.charAt(0);
                try {
                    int profId = Integer.parseInt(change.substring(1));
                    Professor prof = professorDao.findById(profId);

                    if (prof == null) {
                        System.out.println("Professor mit ID " + profId + " nicht gefunden.");
                        continue;
                    }

                    Optional<Professor> foundProf = kurs.getProfessoren().stream().filter(s -> s.getProfessorId() == profId).findFirst();

                    if (operation == '+') {
                        if (foundProf.isPresent()) {
                            System.out.println("Professor " + prof.getVorname() + " " + prof.getNachname() + " ist bereits zugewiesen.");
                        } else {
                            toAdd.add(prof);
                        }
                    } else if (operation == '-') {
                        if (foundProf.isPresent()) {
                            toRemove.add(foundProf.get());
                        } else {
                            System.out.println("Professor " + prof.getVorname() + " " + prof.getNachname() + " ist nicht zugewiesen.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige ID in: " + change);
                }
            }

            Set<Professor> resultingProfessors = new HashSet<>(kurs.getProfessoren());
            resultingProfessors.removeAll(toRemove);
            resultingProfessors.addAll(toAdd);

            if (resultingProfessors.isEmpty()) {
                System.out.println("Es muss mindestens ein Professor dem Kurs zugewiesen sein.");
                return;
            }

            for (Professor prof : toAdd) {
                kurs.addProfessor(prof);
                this.professorDao.update(prof);
                System.out.println("Professor " + prof.getVorname() + " " + prof.getNachname() + " wurde hinzugefügt.");
            }
            for (Professor prof : toRemove) {
                kurs.removeProfessor(prof);
                this.professorDao.update(prof);
                System.out.println("Professor " + prof.getVorname() + " " + prof.getNachname() + " wurde entfernt.");
            }
        }

        Kurs updatedKurs = kursDao.update(kurs);
        if (updatedKurs != null) {
            System.out.println("Kurs erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren des Kurses.");
        }
    }

    private void deleteKurs() {
        System.out.print("Id des zu löschenden Kurses: ");
        if (kursDao.deleteById(readInt(scanner))) {
            System.out.println("Kurs erfolgreich gelöscht.");
        } else {
            System.out.println("Kein Kurs mit dieser Id gefunden.");
        }
    }

    private void pruefungenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Prüfungen");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> addPruefung();
                case "2" -> pruefungAnzeigenMenu();
                case "3" -> updatePruefung();
                case "4" -> deletePruefung();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void addPruefung() {
        System.out.print("Bezeichnung der Prüfung: ");
        String bezeichnung = scanner.nextLine();
        if (invalidEmpty(bezeichnung, "Bezeichnung der Prüfung") || invalidLength(bezeichnung, "Bezeichnung der Prüfung", 100)) {
            return;
        }

        System.out.print("Datum der Prüfung (YYYY-MM-DD, optional): ");
        String datumInput = scanner.nextLine();
        LocalDate datum = null;
        if (!datumInput.isBlank()) {
            try {
                datum = LocalDate.parse(datumInput);
            } catch (DateTimeParseException e) {
                System.out.println("Ungültiges Datum!");
                return;
            }
        }

        System.out.println("Verfügbare Prüfungstypen:");
        pruefungstypDao.findAll().forEach(pt -> System.out.println("  " + pt.getTypId() + ": " + pt.getBezeichnung()));
        System.out.print("ID des Prüfungstyps: ");
        int typId = readInt(scanner);
        Pruefungstyp typ = pruefungstypDao.findById(typId);
        if (typ == null) {
            System.out.println("Kein gültiger Prüfungstyp gefunden.");
            return;
        }

        Pruefung pruefung = new Pruefung().bezeichnung(bezeichnung).datum(datum).pruefungstyp(typ);

        Pruefung created = pruefungDao.save(pruefung);
        if (created != null) {
            System.out.println("Prüfung erfolgreich erstellt.");
        } else {
            System.out.println("Fehler beim Erstellen der Prüfung.");
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
                case "1" -> pruefungDao.findAll().forEach(System.out::println);
                case "2" -> showPruefungKurse();
                case "3" -> showPruefungVersuche();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void showPruefungKurse() {
        System.out.print("ID der Prüfung: ");
        int pruefungId = readInt(scanner);
        Pruefung pruefung = pruefungDao.findById(pruefungId);

        if (pruefung == null) {
            System.out.println("Keine Prüfung mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Prüfung: %s (ID: %d)%n", pruefung.getBezeichnung(), pruefung.getPruefungsId());
        System.out.println("Zugeordnete Kurse:");

        if (pruefung.getZugehoerigeKurse().isEmpty()) {
            System.out.println("Keine Kurse für diese Prüfung zugeordnet.");
        } else {
            pruefung.getZugehoerigeKurse().stream().sorted(Comparator.comparing(GehoertZuPruefung::getId)).forEach(zuordnung -> {
                Kurs kurs = zuordnung.getKurs();
                System.out.printf("  - %d: %s%n", kurs.getKursId(), kurs.getBezeichnung());
            });
        }
    }

    private void showPruefungVersuche() {
        System.out.print("ID der Prüfung: ");
        int pruefungId = readInt(scanner);
        Pruefung pruefung = pruefungDao.findById(pruefungId);

        if (pruefung == null) {
            System.out.println("Keine Prüfung mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Prüfung: %s (ID: %d)%n", pruefung.getBezeichnung(), pruefung.getPruefungsId());
        System.out.println("Absolvierte Prüfungsversuche:");

        if (pruefung.getAbsolvierteVersuche().isEmpty()) {
            System.out.println("Keine Prüfungsversuche für diese Prüfung gefunden.");
        } else {
            pruefung.getAbsolvierteVersuche().stream().sorted(Comparator.comparing(Absolviert::getId)).forEach(absolviert -> {
                Student student = absolviert.getStudent();
                Note note = absolviert.getNote();
                int attempt = absolviert.getId().getVersuch();
                System.out.printf("  - Student: %-20s (ID: %3d), Versuch: %d, Note: %s%n", student.getVorname() + " " + student.getNachname(), student.getStudentId(), attempt, (note != null ? note.getBezeichnung() : "Keine Note vergeben"));
            });
        }
    }

    private void updatePruefung() {
        pruefungDao.findAll().forEach(System.out::println);
        System.out.print("ID der zu bearbeitenden Prüfung: ");
        int id = readInt(scanner);
        Pruefung pruefung = pruefungDao.findById(id);

        if (pruefung == null) {
            System.out.println("Keine Prüfung mit dieser ID gefunden.");
            return;
        }

        System.out.print("Neue Bezeichnung (aktuell: " + pruefung.getBezeichnung() + "): ");
        String bezeichnung = scanner.nextLine();
        if (!bezeichnung.isBlank()) {
            if (invalidEmpty(bezeichnung, "Bezeichnung der Prüfung") || invalidLength(bezeichnung, "Bezeichnung der Prüfung", 100)) {
                return;
            }
            pruefung.setBezeichnung(bezeichnung);
        }

        System.out.print("Neues Datum (aktuell: " + pruefung.getDatum() + ", YYYY-MM-DD, optional): ");
        String datumInput = scanner.nextLine();
        if (!datumInput.isBlank()) {
            try {
                pruefung.setDatum(LocalDate.parse(datumInput));
            } catch (Exception e) {
                System.out.println("Ungültiges Datum!");
                return;
            }
        }

        System.out.println("Aktueller Prüfungstyp: " + pruefung.getPruefungstyp().getBezeichnung());
        System.out.print("Neue Prüfungstyp-ID (leer = keine Änderung): ");
        String typInput = scanner.nextLine();
        if (!typInput.isBlank()) {
            try {
                int typId = Integer.parseInt(typInput);
                Pruefungstyp neuerTyp = pruefungstypDao.findById(typId);
                if (neuerTyp != null) {
                    pruefung.setPruefungstyp(neuerTyp);
                } else {
                    System.out.println("Kein gültiger Typ mit dieser ID gefunden.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungültige ID!");
                return;
            }
        }

        Pruefung updated = pruefungDao.update(pruefung);
        if (updated != null) {
            System.out.println("Prüfung erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren der Prüfung.");
        }
    }

    private void deletePruefung() {
        System.out.print("Id der zu löschenden Prüfung: ");
        if (pruefungDao.deleteById(readInt(scanner))) {
            System.out.println("Prüfung erfolgreich gelöscht.");
        } else {
            System.out.println("Keine Prüfung mit dieser Id gefunden.");
        }

    }

    private void notenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Noten");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> addNote();
                case "2" -> showNote();
                case "3" -> updateNote();
                case "4" -> deleteNote();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void addNote() {
        System.out.print("Note-ID (Integer): ");
        int id = readInt(scanner);

        if (noteDao.findById(id) != null) {
            System.out.println("Eine Note mit dieser ID existiert bereits.");
            return;
        }

        System.out.print("Bezeichnung der Note: ");
        String bezeichnung = scanner.nextLine();
        if (invalidEmpty(bezeichnung, "Bezeichnung der Note") || invalidLength(bezeichnung, "Bezeichnung der Note", 50)) {
            return;
        }

        Note created = noteDao.save(new Note().noteId(id).bezeichnung(bezeichnung));

        if (created != null) {
            System.out.println("Note erfolgreich erstellt.");
        } else {
            System.out.println("Fehler beim Erstellen der Note.");
        }
    }

    private void showNote() {
        List<Note> noten = noteDao.findAll();
        if (noten.isEmpty()) {
            System.out.println("Keine Noten vorhanden.");
        } else {
            noten.forEach(note -> System.out.println("ID: " + note.getNoteId() + " | Bezeichnung: " + note.getBezeichnung()));
        }
    }

    private void updateNote() {
        System.out.print("ID der zu bearbeitenden Note: ");
        int id = readInt(scanner);
        Note note = noteDao.findById(id);

        if (note == null) {
            System.out.println("Keine Note mit dieser ID gefunden.");
            return;
        }

        System.out.print("Neue Bezeichnung (aktuell: " + note.getBezeichnung() + "): ");
        String newBezeichnung = scanner.nextLine();
        if (invalidEmpty(newBezeichnung, "Bezeichnung der Note") || invalidLength(newBezeichnung, "Bezeichnung der Note", 50)) {
            return;
        }
        note.setBezeichnung(newBezeichnung);

        Note updated = noteDao.update(note);
        if (updated != null) {
            System.out.println("Note erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren der Note.");
        }
    }

    private void deleteNote() {
        System.out.print("ID der zu löschenden Note: ");

        if (noteDao.deleteById(readInt(scanner))) {
            System.out.println("Note erfolgreich gelöscht.");
        } else {
            System.out.println("Keine Note mit dieser ID gefunden oder sie wird noch verwendet.");
        }
    }

    private void fachabteilungenMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Fachabteilungen");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> addFachabteilung();
                case "2" -> fachabteilungAnzeigenMenu();
                case "3" -> updateFachabteilung();
                case "4" -> deleteFachabteilung();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void addFachabteilung() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        if (invalidEmpty(name, "Fachabteilungsname") || invalidLength(name, "Fachabteilungsname", 100)) {
            return;
        }

        System.out.print("Standort: ");
        String standort = scanner.nextLine();
        if (invalidEmpty(standort, "Fachabteilungsstandort") || invalidLength(standort, "Fachabteilungsstandort", 100)) {
            return;
        }

        Fachabteilung addedFachabteilung = fachabteilungDao.save(new Fachabteilung().name(name).standort(standort));
        if (addedFachabteilung != null) {
            System.out.println("Fachabteilung erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen der Fachabteilung.");
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
                case "1" -> fachabteilungDao.findAll().forEach(System.out::println);
                case "2" -> showFachabteilungProfessoren();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void showFachabteilungProfessoren() {
        System.out.print("ID der Fachabteilung: ");
        int abteilungsId = readInt(scanner);
        Fachabteilung fachabteilung = fachabteilungDao.findById(abteilungsId);

        if (fachabteilung == null) {
            System.out.println("Keine Fachabteilung mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Fachabteilung: %s (ID: %d)%n", fachabteilung.getName(), fachabteilung.getAbteilungsId());
        System.out.println("Zugehörige Professoren:");

        if (fachabteilung.getProfessoren().isEmpty()) {
            System.out.println("Keine Professoren in dieser Fachabteilung.");
        } else {
            fachabteilung.getProfessoren().stream().sorted(Comparator.comparing(Professor::getProfessorId)).forEach(prof -> System.out.printf("  - %d: %s %s%n", prof.getProfessorId(), prof.getVorname(), prof.getNachname()));
        }
    }

    private void updateFachabteilung() {
        System.out.print("Id der zu bearbeitenden Fachabteilung: ");
        Fachabteilung fachabteilung = fachabteilungDao.findById(readInt(scanner));
        if (fachabteilung == null) {
            System.out.println("Keine Fachabteilung mit dieser Id gefunden.");
            return;
        }

        System.out.print("Neuer Fachabteilungsname (aktuell: " + fachabteilung.getName() + "): ");
        String fachabteilungName = scanner.nextLine();
        if (!fachabteilungName.isEmpty()) {
            if (invalidEmpty(fachabteilungName, "Fachabteilungsname") || invalidLength(fachabteilungName, "Fachabteilungsname", 100)) {
                return;
            }

            fachabteilung.setName(fachabteilungName);
        }

        System.out.print("Neuer Fachabteilungsstandort (aktuell: " + fachabteilung.getStandort() + "): ");
        String fachabteilungStandort = scanner.nextLine();
        if (!fachabteilungStandort.isEmpty()) {
            if (invalidEmpty(fachabteilungStandort, "Fachabteilungsstandort") || invalidLength(fachabteilungStandort, "Fachabteilungsstandort", 100)) {
                return;
            }

            fachabteilung.setStandort(fachabteilungStandort);
        }

        Fachabteilung updatedFachabteilung = fachabteilungDao.update(fachabteilung);
        if (updatedFachabteilung != null) {
            System.out.println("Fachabteilung erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren der Fachabteilung.");
        }
    }

    private void deleteFachabteilung() {
        System.out.print("Id der zu löschenden Fachabteilung: ");
        if (fachabteilungDao.deleteById(readInt(scanner))) {
            System.out.println("Fachabteilung erfolgreich gelöscht.");
        } else {
            System.out.println("Keine Fachabteilung mit dieser Id gefunden.");
        }
    }

    private void studienprogrammMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Studienprogramme");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> addStudienprogramm();
                case "2" -> studienprogrammeAnzeigenMenu();
                case "3" -> updateStudienprogramm();
                case "4" -> deleteStudienprogramm();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void addStudienprogramm() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        if (invalidEmpty(name, "Name") || invalidLength(name, "Name", 100)) {
            return;
        }

        System.out.print("Abschluss: ");
        String abschluss = scanner.nextLine();
        if (invalidEmpty(abschluss, "Abschluss") || invalidLength(abschluss, "Abschluss", 10)) {
            return;
        }

        System.out.print("Regelstudienzeit in Semester: ");
        int semester = readInt(scanner);
        if (semester < 1) {
            System.out.println("Regelstudienzeit muss größer als 0 sein.");
            return;
        }

        System.out.println("Leiter für das Studienprogramm auswählen:");
        professorDao.findAll().stream().filter(professor -> professor.getStudienprogramm() == null).forEach(System.out::println);
        Professor studienprogrammLeiter = professorDao.findById(readInt(scanner));
        if (studienprogrammLeiter == null) {
            System.out.println("Kein Professor mit dieser Id gefunden.");
            return;
        }

        if (studienprogrammLeiter.getStudienprogramm() != null) {
            System.out.println("Dieser Professor ist bereits Leiter eines Studienprogramms.");
            return;
        }

        Studienprogramm studienprogramm = new Studienprogramm().name(name).abschluss(abschluss).regelstudienzeitInSemester(semester).programmleiter(studienprogrammLeiter);

        Studienprogramm addedStudienprogramm = studienprogrammDao.save(studienprogramm);
        if (addedStudienprogramm != null) {
            System.out.println("Studienprogramm erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen des Studienprogramms.");
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
                case "1" -> studienprogrammDao.findAll().forEach(System.out::println);
                case "2" -> showStudienprogrammKurse();
                case "3" -> showStudienprogrammStudenten();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void showStudienprogrammKurse() {
        System.out.print("ID des Studienprogramms: ");
        int studienprogrammId = readInt(scanner);
        Studienprogramm studienprogramm = studienprogrammDao.findById(studienprogrammId);

        if (studienprogramm == null) {
            System.out.println("Kein Studienprogramm mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Studienprogramm: %s (ID: %d)%n", studienprogramm.getName(), studienprogramm.getStudienprogrammId());
        System.out.println("Zugehörige Kurse:");

        if (studienprogramm.getKurse().isEmpty()) {
            System.out.println("Keine Kurse in diesem Studienprogramm.");
        } else {
            studienprogramm.getKurse().stream().sorted(Comparator.comparing(Kurs::getKursId)).forEach(kurs -> System.out.printf("  - %d: %s (Semester: %d, ECTS: %d)%n", kurs.getKursId(), kurs.getBezeichnung(), kurs.getSemester(), kurs.getEcts()));
        }
    }

    private void showStudienprogrammStudenten() {
        System.out.print("ID des Studienprogramms: ");
        int studienprogrammId = readInt(scanner);
        Studienprogramm studienprogramm = studienprogrammDao.findById(studienprogrammId);

        if (studienprogramm == null) {
            System.out.println("Kein Studienprogramm mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Studienprogramm: %s (ID: %d)%n", studienprogramm.getName(), studienprogramm.getStudienprogrammId());
        System.out.println("Eingeschriebene Studenten:");

        if (studienprogramm.getStudenten().isEmpty()) {
            System.out.println("Keine Studenten in diesem Studienprogramm eingeschrieben.");
        } else {
            studienprogramm.getStudenten().stream().sorted(Comparator.comparing(Student::getStudentId)).forEach(student -> System.out.printf("  - %d: %s %s (Matrikelnr: %s)%n", student.getStudentId(), student.getVorname(), student.getNachname(), student.getMatrikelnummer()));
        }
    }

    private void updateStudienprogramm() {
        System.out.print("Id des zu bearbeitenden Studienprogramms: ");
        Studienprogramm studienprogramm = studienprogrammDao.findById(readInt(scanner));
        if (studienprogramm == null) {
            System.out.println("Kein Studienprogramm mit dieser Id gefunden.");
            return;
        }

        System.out.print("Neuer Studienprogrammsname (aktuell: " + studienprogramm.getName() + "): ");
        String studienprogrammName = scanner.nextLine();
        if (!studienprogrammName.isEmpty()) {
            if (invalidEmpty(studienprogrammName, "Studienprogrammsname") || invalidLength(studienprogrammName, "Studienprogrammsname", 100)) {
                return;
            }

            studienprogramm.setName(studienprogrammName);
        }

        System.out.print("Neuer Abschluss des Studienprogramms (aktuell: " + studienprogramm.getAbschluss() + "): ");
        String studienprogrammAbschluss = scanner.nextLine();
        if (!studienprogrammAbschluss.isEmpty()) {
            if (invalidEmpty(studienprogrammAbschluss, "Abschluss des Studienprogramms") || invalidLength(studienprogrammAbschluss, "Abschluss des Studienprogramms", 10)) {
                return;
            }

            studienprogramm.setAbschluss(studienprogrammAbschluss);
        }

        System.out.print("Neue Regelstudienzeit (aktuell: " + studienprogramm.getRegelstudienzeitInSemester() + "): ");
        String regelstudienzeitEingabe = scanner.nextLine();
        if (!regelstudienzeitEingabe.isEmpty()) {
            try {
                int regelstudienzeit = Integer.parseInt(regelstudienzeitEingabe);
                if (regelstudienzeit < 1) {
                    System.out.println("Regelstudienzeit muss größer als 0 sein.");
                    return;
                }

                studienprogramm.setRegelstudienzeitInSemester(regelstudienzeit);
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe. Regelstudienzeit wurde nicht geändert.");
            }
        }

        System.out.print("Möchten sie den Studienprogrammleiter ändern? (aktuell: " + studienprogramm.getProgrammleiter() + "): j/n");
        if (scanner.nextLine().equalsIgnoreCase("j")) {
            professorDao.findAll().stream().filter(professor -> professor.getStudienprogramm() == null).forEach(System.out::println);
            int studienprogrammLeiterID = readInt(scanner);
            Professor studienprogrammLeiter = professorDao.findById(studienprogrammLeiterID);

            if (studienprogrammLeiter == null) {
                System.out.println("Kein Professor mit dieser Id gefunden.");
                return;
            }

            if (studienprogrammLeiter.getStudienprogramm() != null) {
                System.out.println("Dieser Professor ist bereits Leiter eines Studienprogramms.");
                return;
            }

            studienprogramm.setProgrammleiter(studienprogrammLeiter);
        }

        Studienprogramm updatedStudienprogramm = studienprogrammDao.update(studienprogramm);
        if (updatedStudienprogramm != null) {
            System.out.println("Studienprogramm erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren des Studienprogramms.");
        }
    }

    private void deleteStudienprogramm() {
        System.out.print("Id des zu löschenden Studienprogramms: ");
        if (studienprogrammDao.deleteById(readInt(scanner))) {
            System.out.println("Studienprogramm erfolgreich gelöscht.");
        } else {
            System.out.println("Kein Studienprogramm mit dieser Id gefunden.");
        }
    }

    private void pruefungstypMenu() {
        boolean isRunning = true;
        while (isRunning) {
            printSubMenu("Prüfungstypen");
            switch (scanner.nextLine()) {
                case "0" -> isRunning = false;
                case "1" -> addPruefungstyp();
                case "2" -> showPruefungstypen();
                case "3" -> updatePruefungstyp();
                case "4" -> deletePruefungstyp();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void addPruefungstyp() {
        System.out.print("Bezeichnung des neuen Prüfungstyps: ");
        String bezeichnung = scanner.nextLine();
        if (invalidEmpty(bezeichnung, "Bezeichnung des Prüfungstyps") || invalidLength(bezeichnung, "Bezeichnung des Prüfungstyps", 50)) {
            return;
        }

        Pruefungstyp created = pruefungstypDao.save(new Pruefungstyp().bezeichnung(bezeichnung));

        if (created != null) {
            System.out.println("Prüfungstyp erfolgreich erstellt.");
        } else {
            System.out.println("Fehler beim Erstellen des Prüfungstyps.");
        }
    }

    private void showPruefungstypen() {
        List<Pruefungstyp> typen = pruefungstypDao.findAll();
        if (typen.isEmpty()) {
            System.out.println("Keine Prüfungstypen vorhanden.");
        } else {
            typen.forEach(pt -> System.out.println("ID: " + pt.getTypId() + " | Bezeichnung: " + pt.getBezeichnung()));
        }
    }

    private void updatePruefungstyp() {
        System.out.print("ID des zu bearbeitenden Prüfungstyps: ");
        int id = readInt(scanner);
        Pruefungstyp typ = pruefungstypDao.findById(id);

        if (typ == null) {
            System.out.println("Kein Prüfungstyp mit dieser ID gefunden.");
            return;
        }

        System.out.print("Neue Bezeichnung (aktuell: " + typ.getBezeichnung() + "): ");
        String neueBezeichnung = scanner.nextLine();
        if (invalidEmpty(neueBezeichnung, "Bezeichnung des Prüfungstyps") || invalidLength(neueBezeichnung, "Bezeichnung des Prüfungstyps", 50)) {
            return;
        }
        typ.setBezeichnung(neueBezeichnung);

        Pruefungstyp updated = pruefungstypDao.update(typ);
        if (updated != null) {
            System.out.println("Prüfungstyp erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren des Prüfungstyps.");
        }
    }

    private void deletePruefungstyp() {
        System.out.print("ID des zu löschenden Prüfungstyps: ");

        if (pruefungstypDao.deleteById(readInt(scanner))) {
            System.out.println("Prüfungstyp erfolgreich gelöscht.");
        } else {
            System.out.println("Kein Prüfungstyp mit dieser ID gefunden oder bereits verwendet.");
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
                case "1" -> pruefungsNotenVerwaltung();
                case "2" -> kursNotenVerwaltung();
                case "3" -> kursStudentenVerwaltung();
                case "4" -> pruefungKursZuweisung();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void pruefungsNotenVerwaltung() {
        System.out.println("Verfügbare Prüfungen:");
        pruefungDao.findAll().stream().sorted(Comparator.comparing(Pruefung::getPruefungsId)).forEach(System.out::println);

        System.out.print("ID der Prüfung: ");
        int pruefungsId = readInt(scanner);
        Pruefung pruefung = pruefungDao.findById(pruefungsId);

        if (pruefung == null) {
            System.out.println("Keine Prüfung mit dieser ID gefunden.");
            return;
        }

        if (pruefung.getZugehoerigeKurse().isEmpty()) {
            System.out.println("Diese Prüfung ist keinem Kurs zugeordnet. Bitte zuerst einen Kurs zuweisen.");
            return;
        }

        System.out.printf("Ausgewählte Prüfung: %s (ID: %d)%n", pruefung.getBezeichnung(), pruefung.getPruefungsId());

        System.out.println("Verfügbare Studenten: ");
        List<Student> studentsInKurs = pruefung.getZugehoerigeKurse().stream().map(GehoertZuPruefung::getKurs).flatMap(kurs -> kurs.getTeilnehmendeStudenten().stream()).map(Besucht::getStudent).toList();
        if (studentsInKurs.isEmpty()) {
            System.out.println("Keine Studenten für den Kurs eingeschrieben zu der die Prüfung gehört.");
            return;
        }

        studentsInKurs.forEach(System.out::println);

        boolean addMoreStudents = true;
        while (addMoreStudents) {
            System.out.print("ID des Studenten (0 zum Beenden): ");
            int studentId = readInt(scanner);

            if (studentId == 0) {
                addMoreStudents = false;
                continue;
            }

            Student student = studentDao.findById(studentId);

            if (student == null) {
                System.out.println("Kein Student mit dieser ID gefunden.");
                continue;
            }

            System.out.printf("Student: %s %s (ID: %d)%n", student.getVorname(), student.getNachname(), student.getStudentId());

            boolean studentInKurs = studentsInKurs.stream().anyMatch(stu -> stu.getStudentId() == studentId);

            if (!studentInKurs) {
                System.out.println("Der Student ist nicht im Kurs eingeschrieben, dem diese Prüfung zugeordnet ist.");
                continue;
            }

            long attempt = student.getAbsolviertePruefungen().stream().filter(absolviert -> absolviert.getPruefung().getPruefungsId() == pruefungsId).count();
            int newAttempt;
            if (student.getAbsolviertePruefungen().stream().anyMatch(absolviert -> absolviert.getPruefung().getPruefungsId() == pruefungsId && absolviert.getNote() == null)) {
                System.out.println("Der Student hat bereits einen Versuch für diese Prüfung ohne Note.");
                newAttempt = (int) attempt;
            } else {
                newAttempt = (int) attempt + 1;
                if (newAttempt > 3) {
                    System.out.printf("Der Student hat bereits %d Versuche für diese Prüfung absolviert. Eine weitere Prüfung kann nicht hinzugefügt werden.%n", attempt);
                    continue;
                }
            }

            System.out.println("Leer lassen, wenn noch keine Note vergeben werden soll.");
            System.out.printf("Note für %d. Versuch (ID): ", newAttempt);
            String noteId = scanner.nextLine();
            Note note = null;
            if (!noteId.isEmpty()) {
                try {
                    note = noteDao.findById(Integer.parseInt(noteId));

                    if (note == null) {
                        System.out.println("Keine Note mit dieser ID gefunden. Keine Note wird zugewiesen.");
                    } else {
                        System.out.printf("Ausgewählte Note: %s%n", note.getBezeichnung());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige Note ID. Keine Note wird zugewiesen.");
                }
            }

            Absolviert addedAbsolviert = absolviertDao.update(new Absolviert(studentId, pruefungsId, newAttempt).student(student).pruefung(pruefung).note(note));
            if (addedAbsolviert != null) {
                System.out.printf("Prüfung für %s %s erfolgreich aktualisiert.%n", student.getVorname(), student.getNachname());
            } else {
                System.out.printf("Fehler beim Aktualisieren der Prüfung für %s %s.%n", student.getVorname(), student.getNachname());
            }
        }
    }

    private void kursNotenVerwaltung() {
        System.out.println("Verfügbare Kurse:");
        kursDao.findAll().stream().sorted(Comparator.comparing(Kurs::getKursId)).forEach(System.out::println);

        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Ausgewählter Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());
        System.out.println("Eingeschriebene Studenten:");

        if (kurs.getTeilnehmendeStudenten().isEmpty()) {
            System.out.println("Keine Studenten für diesen Kurs eingeschrieben.");
            return;
        }

        kurs.getTeilnehmendeStudenten().stream().sorted(Comparator.comparing(Besucht::getId)).forEach(besucht -> {
            Student student = besucht.getStudent();
            String noteInfo = besucht.getNote() != null ? ", Note: " + besucht.getNote().getBezeichnung() : ", Keine Note";
            System.out.printf("  - %-3d: %-15s %-15s (Matrikelnr: %s)%s%n", student.getStudentId(), student.getVorname(), student.getNachname(), student.getMatrikelnummer(), noteInfo);
        });

        System.out.print("ID des Studenten für Noteneintrag: ");
        int studentId = readInt(scanner);
        Student student = studentDao.findById(studentId);

        if (student == null) {
            System.out.println("Kein Student mit dieser ID gefunden.");
            return;
        }

        boolean attending = kurs.getTeilnehmendeStudenten().stream().anyMatch(besucht -> besucht.getStudent().getStudentId() == studentId);

        if (!attending) {
            System.out.println("Dieser Student ist nicht in diesem Kurs eingeschrieben.");
            return;
        }

        List<Absolviert> absolvierteKursPruefungen = student.getAbsolviertePruefungen().stream().filter(absolviert -> absolviert.getPruefung().getZugehoerigeKurse().stream().anyMatch(zuordnung -> zuordnung.getKurs().getKursId() == kursId)).toList();

        Map<Pruefung, Double> pruefungsGewichtungen = new HashMap<>();
        Note vorgeschlageneNote = null;

        if (!absolvierteKursPruefungen.isEmpty()) {
            System.out.println("Absolvierte Prüfungen in diesem Kurs:");

            double summeAnteile = 0.0;
            Map<Pruefung, Integer> anteile = new HashMap<>();

            for (Absolviert absolviert : absolvierteKursPruefungen) {
                Pruefung pruefung = absolviert.getPruefung();
                Note note = absolviert.getNote();

                Optional<GehoertZuPruefung> zuordnung = pruefung.getZugehoerigeKurse().stream().filter(z -> z.getKurs().getKursId() == kursId).findFirst();

                Integer anteil = zuordnung.map(GehoertZuPruefung::getAnteilGesamtnoteInProzent).orElse(null);

                System.out.printf("  - %s(%d. Versuch): %s (Anteil: %s%%)%n", pruefung.getBezeichnung(), absolviert.getId().getVersuch(), note != null ? note.getBezeichnung() : "Keine Note", anteil != null ? anteil : "nicht festgelegt");

                if (anteil != null) {
                    anteile.put(pruefung, anteil);
                    summeAnteile += anteil;
                }
            }

            if (anteile.isEmpty() || summeAnteile < 100.0) {
                double gleichmaessigerAnteil = 100.0 / absolvierteKursPruefungen.size();
                for (Absolviert absolviert : absolvierteKursPruefungen) {
                    pruefungsGewichtungen.put(absolviert.getPruefung(), gleichmaessigerAnteil);
                }

                if (!anteile.isEmpty()) {
                    System.out.println("Hinweis: Die Summe der Prüfungsanteile ergibt nicht 100%. Verwende gleichmäßige Verteilung.");
                }
            } else {
                anteile.forEach((pruefung, anteil) -> pruefungsGewichtungen.put(pruefung, (double) anteil));
            }

            double gewichteterDurchschnitt = 0.0;
            double gesamtGewichtung = 0.0;

            for (Absolviert absolviert : absolvierteKursPruefungen) {
                Note note = absolviert.getNote();
                if (note != null && note.getNoteId() <= 5) {
                    Double gewichtung = pruefungsGewichtungen.get(absolviert.getPruefung());
                    gewichteterDurchschnitt += note.getNoteId() * gewichtung;
                    gesamtGewichtung += gewichtung;
                }
            }
            if (gesamtGewichtung > 0) {
                double endnote = gewichteterDurchschnitt / gesamtGewichtung;
                int gerundeteNote = (int) Math.round(endnote);

                if (gerundeteNote <= 5) {
                    vorgeschlageneNote = noteDao.findById(gerundeteNote);
                }
            }
        } else {
            System.out.println("Keine absolvierten Prüfungen in diesem Kurs gefunden.");
        }

        System.out.println("Leer lassen, wenn noch keine Note vergeben werden soll.");
        if (vorgeschlageneNote != null) {
            System.out.printf("Vorgeschlagene Note basierend auf den absolvierten Prüfungen: %s%n", vorgeschlageneNote.getBezeichnung());
        }
        System.out.print("Note (ID): ");
        String noteId = scanner.nextLine();
        Note note = null;
        if (!noteId.isEmpty()) {
            try {
                note = noteDao.findById(Integer.parseInt(noteId));

                if (note == null) {
                    System.out.println("Keine Note mit dieser ID gefunden. Keine Note wird zugewiesen.");
                } else {
                    System.out.printf("Ausgewählte Note: %s%n", note.getBezeichnung());
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Note ID. Keine Note wird zugewiesen.");
            }
        }

        Besucht addedBesucht = besuchtDao.save(new Besucht(studentId, kursId).student(student).kurs(kurs).note(note));
        if (addedBesucht != null) {
            System.out.printf("Eintrag für %s %s im Kurs %s erfolgreich aktualisiert.%n", student.getVorname(), student.getNachname(), kurs.getBezeichnung());
        } else {
            System.out.printf("Fehler beim Eintragen für %s %s im Kurs %s.%n", student.getVorname(), student.getNachname(), kurs.getBezeichnung());
        }
    }

    private void kursStudentenVerwaltung() {
        System.out.println("Verfügbare Kurse:");
        kursDao.findAll().stream().sorted(Comparator.comparing(Kurs::getKursId)).forEach(System.out::println);

        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Ausgewählter Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());

        if (kurs.getTeilnehmendeStudenten().isEmpty()) {
            System.out.println("Keine Studenten für diesen Kurs eingeschrieben.");
        } else {
            System.out.println("Eingeschriebene Studenten:");
            kurs.getTeilnehmendeStudenten().stream().sorted(Comparator.comparing(besucht -> besucht.getStudent().getStudentId())).forEach(besucht -> {
                Student student = besucht.getStudent();
                Note note = besucht.getNote();
                String noteInfo = note != null ? ", Note: " + note.getBezeichnung() : ", Keine Note";
                System.out.printf("  - %-3d: %-15s %-15s (Matrikelnr: %s)%s%n", student.getStudentId(), student.getVorname(), student.getNachname(), student.getMatrikelnummer(), noteInfo);
            });
        }

        System.out.println("Verfügbare Studenten:");
        kurs.getStudienprogramm().getStudenten().stream().sorted(Comparator.comparing(Student::getStudentId)).forEach(System.out::println);

        System.out.print("Studenten ändern im Format '+ID,-ID,+ID' (z.B. '+1,-5,+4' oder leer für keine Änderung): ");
        String input = scanner.nextLine().trim();

        if (!input.isEmpty()) {
            String[] changes = input.split(",");
            for (String change : changes) {
                change = change.trim();
                if (change.length() < 2) continue;

                char operation = change.charAt(0);
                try {
                    int studentId = Integer.parseInt(change.substring(1));
                    Student student = studentDao.findById(studentId);

                    if (student == null) {
                        System.out.println("Student mit ID " + studentId + " nicht gefunden.");
                        continue;
                    }

                    Optional<Besucht> besuchtOptional = kurs.getTeilnehmendeStudenten().stream().filter(b -> b.getStudent().getStudentId() == studentId).findFirst();

                    if (operation == '+') {
                        if (besuchtOptional.isPresent()) {
                            System.out.printf("Student %s %s ist bereits eingeschrieben.%n", student.getVorname(), student.getNachname());
                        } else {
                            Besucht besucht = new Besucht(kursId, studentId).student(student).kurs(kurs);
                            if (besuchtDao.save(besucht) != null) {
                                System.out.printf("Student %s %s wurde erfolgreich zum Kurs hinzugefügt.%n", student.getVorname(), student.getNachname());
                            } else {
                                System.out.println("Fehler beim Hinzufügen des Studenten zum Kurs.");
                            }
                        }
                    } else if (operation == '-') {
                        if (besuchtOptional.isEmpty()) {
                            System.out.printf("Student %s %s ist nicht eingeschrieben.%n", student.getVorname(), student.getNachname());
                        } else {
                            Besucht besucht = besuchtOptional.get();
                            if (besucht.getNote() != null) {
                                System.out.printf("Student %s %s hat bereits eine Note. Entfernen nicht möglich.%n", student.getVorname(), student.getNachname());
                            } else if (besuchtDao.deleteById(besucht.getId())) {
                                System.out.printf("Student %s %s wurde erfolgreich vom Kurs entfernt.%n", student.getVorname(), student.getNachname());
                            } else {
                                System.out.println("Fehler beim Entfernen des Studenten vom Kurs.");
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige ID in: " + change);
                }
            }
        }
    }

    private void pruefungKursZuweisung() {
        System.out.println("Verfügbare Kurse:");
        List<Kurs> kurse = kursDao.findAll();
        kurse.forEach(System.out::println);

        if (kurse.isEmpty()) {
            System.out.println("Keine Kurse verfügbar.");
            return;
        }

        System.out.print("Wählen Sie einen Kurs (ID): ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.println("Aktuelle Prüfungszuordnungen für Kurs: " + kurs.getBezeichnung());
        List<GehoertZuPruefung> zuordnungen = gehoertZuPruefungDao.findAll().stream().filter(zuordnung -> zuordnung.getKurs().getKursId() == kursId).toList();
        if (zuordnungen.isEmpty()) {
            System.out.println("Keine Prüfungen zugeordnet.");
        } else {
            zuordnungen.stream().sorted(Comparator.comparing(GehoertZuPruefung::getId)).forEach(zuordnung -> {
                Pruefung pruefung = zuordnung.getPruefung();
                System.out.printf("  - %d: %s (Anteil an Gesamtnote: %d%%)%n", pruefung.getPruefungsId(), pruefung.getBezeichnung(), zuordnung.getAnteilGesamtnoteInProzent());
            });
        }

        System.out.println("Verfügbare Prüfungen:");
        List<Pruefung> pruefungen = pruefungDao.findAll();
        pruefungen.forEach(System.out::println);

        if (pruefungen.isEmpty()) {
            System.out.println("Keine Prüfungen verfügbar.");
            return;
        }

        System.out.print("Änderungen im Format '+ID:Anteil,-ID,=ID:Anteil' (z.B. '+1:50,-5,=4:30' oder leer für keine Änderung): ");
        String input = scanner.nextLine().trim();

        if (!input.isEmpty()) {
            String[] changes = input.split(",");
            for (String change : changes) {
                change = change.trim();
                if (change.length() < 2) continue;

                char operation = change.charAt(0);
                try {
                    String[] parts = change.substring(1).split(":");
                    int pruefungId = Integer.parseInt(parts[0]);
                    Pruefung pruefung = pruefungDao.findById(pruefungId);

                    if (pruefung == null) {
                        System.out.println("Prüfung mit ID " + pruefungId + " nicht gefunden.");
                        continue;
                    }

                    boolean existiert = zuordnungen.stream().anyMatch(z -> z.getPruefung().getPruefungsId() == pruefungId);

                    if (operation == '+') {
                        if (existiert) {
                            System.out.println("Prüfung " + pruefung.getBezeichnung() + " ist bereits zugeordnet.");
                        } else {

                            Integer anteil = null;
                            if (parts.length > 1) {
                                try {
                                    anteil = Integer.parseInt(parts[1]);
                                    System.out.println("Anteil an Gesamtnote auf " + anteil + "% gesetzt.");
                                } catch (NumberFormatException e) {
                                    System.out.println("Ungültiger Anteil: " + parts[1] + ".");
                                }
                            }

                            gehoertZuPruefungDao.save(new GehoertZuPruefung(kursId, pruefungId).kurs(kurs).pruefung(pruefung).anteilGesamtnoteInProzent(anteil));
                            System.out.println("Prüfung " + pruefung.getBezeichnung() + " wurde hinzugefügt.");
                        }
                    } else if (operation == '=') {
                        if (existiert) {
                            GehoertZuPruefung zuordnungToUpdate = zuordnungen.stream().filter(z -> z.getPruefung().getPruefungsId() == pruefungId).findFirst().orElse(null);

                            if (zuordnungToUpdate != null && parts.length > 1) {
                                try {
                                    int anteil = Integer.parseInt(parts[1]);
                                    zuordnungToUpdate.setAnteilGesamtnoteInProzent(anteil);
                                    gehoertZuPruefungDao.update(zuordnungToUpdate);
                                    System.out.println("Anteil für Prüfung " + pruefung.getBezeichnung() + " auf " + anteil + "% aktualisiert.");
                                } catch (NumberFormatException e) {
                                    System.out.println("Ungültiger Anteil: " + parts[1] + ". Keine Änderung vorgenommen.");
                                }
                            } else {
                                System.out.println("Für die Aktualisierung muss ein Anteil angegeben werden (=ID:Anteil).");
                            }
                        } else {
                            System.out.println("Prüfung " + pruefung.getBezeichnung() + " ist nicht zugeordnet und kann nicht aktualisiert werden.");
                        }
                    } else if (operation == '-') {
                        if (existiert) {
                            GehoertZuPruefung zuordnungToDelete = zuordnungen.stream().filter(z -> z.getPruefung().getPruefungsId() == pruefungId).findFirst().orElse(null);

                            if (zuordnungToDelete != null) {
                                gehoertZuPruefungDao.deleteById(zuordnungToDelete.getId());
                                System.out.println("Prüfung " + pruefung.getBezeichnung() + " wurde entfernt.");
                            }
                        } else {
                            System.out.println("Prüfung " + pruefung.getBezeichnung() + " ist nicht zugeordnet.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige Eingabe: " + change);
                }
            }
        }
    }

    private void showKurseStatistiken() {
        String sql = """
                SELECT k.Bezeichnung                                            AS Kursname,
                       p.Bezeichnung                                            AS Pruefung,
                       pt.Bezeichnung                                           AS Pruefungstyp,
                       MIN(a.Versuch)                                           AS Min_Versuch,
                       MAX(a.Versuch)                                           AS Max_Versuch,
                       ROUND(AVG(a.Versuch), 2)                                 AS Avg_Versuch,
                       ROUND(AVG(CASE WHEN a.Versuch = 1 THEN a.NoteID END), 2) AS Avg_Note_Versuch_1,
                       ROUND(AVG(CASE WHEN a.Versuch = 2 THEN a.NoteID END), 2) AS Avg_Note_Versuch_2,
                       ROUND(AVG(CASE WHEN a.Versuch = 3 THEN a.NoteID END), 2) AS Avg_Note_Versuch_3
                FROM Absolviert a
                         JOIN Pruefung p ON a.PruefungsID = p.PruefungsID
                         JOIN Pruefungstyp pt ON p.TypID = pt.TypID
                         JOIN GehoertZuPruefung gzp ON p.PruefungsID = gzp.PruefungsID
                         JOIN Kurs k ON gzp.KursID = k.KursID
                GROUP BY 1, 2, 3
                ORDER BY 1, 2;
                """;

        List<Tuple> results = absolviertDao.query(sql, null);

        if (results.isEmpty()) {
            System.out.println("Keine Daten für die Kursstatistiken gefunden.");
            return;
        }

        System.out.println("\n--- Kursstatistiken ---");
        System.out.printf("%-50s | %-50s | %-30s | %-5s | %-5s | %-10s | %-10s | %-10s | %-10s%n", "Kurs", "Prüfung", "Typ", "MinV", "MaxV", "AvgV", "AvgN V1", "AvgN V2", "AvgN V3");
        System.out.println(new String(new char[200]).replace('\0', '-'));

        for (Tuple row : results) {
            System.out.printf("%-50s | %-50s | %-30s | %-5s | %-5s | %-10s | %-10s | %-10s | %-10s%n", row.get("Kursname", String.class), row.get("Pruefung", String.class), row.get("Pruefungstyp", String.class), row.get("Min_Versuch") != null ? row.get("Min_Versuch", Number.class).intValue() : "N/A", row.get("Max_Versuch") != null ? row.get("Max_Versuch", Number.class).intValue() : "N/A", row.get("Avg_Versuch") != null ? row.get("Avg_Versuch", Number.class).doubleValue() : "N/A", row.get("Avg_Note_Versuch_1") != null ? row.get("Avg_Note_Versuch_1", Number.class).doubleValue() : "N/A", row.get("Avg_Note_Versuch_2") != null ? row.get("Avg_Note_Versuch_2", Number.class).doubleValue() : "N/A", row.get("Avg_Note_Versuch_3") != null ? row.get("Avg_Note_Versuch_3", Number.class).doubleValue() : "N/A");
        }
        System.out.println("-----------------------\n");
    }

    private void showOffeneNotenvergabe() {
        System.out.print("Id des Professors: ");
        Professor professor = professorDao.findById(readInt(scanner));

        if (professor == null) {
            System.out.println("Kein Professor mit dieser Id gefunden.");
            return;
        }

        String sql = """
                    SELECT S.Vorname       AS Student_Vorname,
                           S.Nachname      AS Student_Nachname,
                           S.Matrikelnummer,
                           SP_Student.Name AS Student_Studiengang,
                           K.Bezeichnung   AS Kurs_Bezeichnung
                    FROM Professor P
                             JOIN Unterrichtet U ON P.ProfessorID = U.ProfessorID
                             JOIN Kurs K ON U.KursID = K.KursID
                             JOIN Besucht B ON K.KursID = B.KursID
                             JOIN Student S ON B.StudentID = S.StudentID
                             JOIN Waehlt W ON S.StudentID = W.StudentID
                             JOIN Studienprogramm SP_Student ON W.StudienprogrammID = SP_Student.StudienprogrammID
                    WHERE P.ProfessorID = :profId
                      AND B.NoteID IS NULL
                    ORDER BY S.Vorname, S.Nachname, SP_Student.Name, K.Bezeichnung
                """;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("profId", professor.getProfessorId());

        List<Tuple> results = professorDao.query(sql, parameters);

        if (results.isEmpty()) {
            System.out.println("Keine Studenten gefunden, die von Professor " + professor.getVorname() + " " + professor.getNachname() + " benotet werden müssen.");
            return;
        }

        System.out.println("\n--- Studenten, die von Professor " + professor.getVorname() + " " + professor.getNachname() + " benotet werden müssen ---");
        System.out.printf("%-20s | %-20s | %-15s | %-30s | %-30s%n", "Vorname", "Nachname", "Matrikelnr.", "Studiengang", "Kurs");
        System.out.println(new String(new char[120]).replace('\0', '-'));

        for (Tuple row : results) {
            System.out.printf("%-20s | %-20s | %-15s | %-30s | %-30s%n", row.get("Student_Vorname", String.class), row.get("Student_Nachname", String.class), row.get("Matrikelnummer", String.class), row.get("Student_Studiengang", String.class), row.get("Kurs_Bezeichnung", String.class));
        }
        System.out.println("-------------------------------------------------------------------\n");
    }
}
