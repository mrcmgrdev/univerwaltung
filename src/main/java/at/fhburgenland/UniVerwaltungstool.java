package at.fhburgenland;

import at.fhburgenland.dao.*;
import at.fhburgenland.model.*;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
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
                |8. Prüfungstypen          |
                |9. Statistiken Kurse      |
                |10. Offene Notenvergabe   |
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

        System.out.print("Semester: ");
        int semester = Integer.parseInt(scanner.nextLine());

        System.out.print("ECTS: ");
        int ects = Integer.parseInt(scanner.nextLine());

        System.out.println("Zugehöriges Studienprogramm:");
        studienprogrammDao.findAll().forEach(System.out::println);
        System.out.print("Studienprogramm (Id): ");
        Studienprogramm studienprogramm;
        int studienprogrammId = readInt(scanner);
       studienprogramm = studienprogrammDao.findById(studienprogrammId);
        if (studienprogramm == null) {
            System.out.println("Kein Studienprogramm mit dieser Id gefunden!");
            return;
        }

        Kurs kurs = new Kurs().bezeichnung(bezeichnung).semester(semester).ects(ects).studienprogramm(studienprogramm);

        Kurs addedKurs = kursDao.save(kurs);
        if (addedKurs != null) {
            System.out.println("Kurs erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen des Kurses.");
        }
    }

    private void kursAnzeigenMenu(){
        kursDao.findAll().forEach(System.out::println);
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
            kurs.setBezeichnung(kursbezeichnung);
        }

        System.out.print("Neues Semester (aktuell: " + kurs.getSemester() + "): ");
        String kursEingabe = scanner.nextLine();
        if (!kursEingabe.isEmpty()) {
            try {
                int kursSemester = Integer.parseInt(kursEingabe);
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
                    kurs.setEcts(kursEcts);
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige Eingabe. Ects wurde nicht geändert.");
                }
            }

                System.out.println("Möchten sie das zugehöriges Studienprogramm ändern? (aktuell: " + kurs.getStudienprogramm() + "): j/n");
        if (scanner.nextLine().equalsIgnoreCase("j")) {
                int studienprogrammId = readInt(scanner);
                try{
                Studienprogramm studienprogramm = studienprogrammDao.findById(studienprogrammId);
                    kurs.setStudienprogramm(studienprogramm);}
                catch(EntityNotFoundException e){
                    System.out.println("Kein Studienprogramm mit dieser Id gefunden: " + studienprogrammId);
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

        System.out.print("Datum der Prüfung (YYYY-MM-DD, optional): ");
        String datumInput = scanner.nextLine();
        LocalDate datum = null;
        if (!datumInput.isBlank()) {
            try {
                datum = LocalDate.parse(datumInput);
            } catch (Exception e) {
                System.out.println("Ungültiges Datum! Wird ignoriert.");
            }
        }

        System.out.println("Verfügbare Prüfungstypen:");
        List<Pruefungstyp> typen = pruefungstypDao.findAll();
        typen.forEach(pt -> System.out.println("  " + pt.getTypId() + ": " + pt.getBezeichnung()));
        System.out.print("ID des Prüfungstyps: ");
        int typId = readInt(scanner);
        Pruefungstyp typ = pruefungstypDao.findById(typId);
        if (typ == null) {
            System.out.println("Kein gültiger Prüfungstyp gefunden.");
            return;
        }

        Pruefung pruefung = new Pruefung().bezeichnung(bezeichnung).datum(datum).pruefungstyp(typ);

        Pruefung created = pruefungDao.save(pruefung);
        if (created != null) {System.out.println("Prüfung erfolgreich erstellt.");}
        else {
            System.out.println("Fehler beim Erstellen der Prüfung.");
        }
    }

    private void pruefungAnzeigenMenu(){
        pruefungDao.findAll().forEach(System.out::println);
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
            pruefung.setBezeichnung(bezeichnung);
        }

        System.out.print("Neues Datum (aktuell: " + pruefung.getDatum() + ", YYYY-MM-DD, optional): ");
        String datumInput = scanner.nextLine();
        if (!datumInput.isBlank()) {
            try {
                pruefung.setDatum(LocalDate.parse(datumInput));
            } catch (Exception e) {
                System.out.println("Ungültiges Datum! Keine Änderung.");
            }
        }

        System.out.println("Aktueller Prüfungstyp: " + pruefung.getPruefungstyp().getBezeichnung());
        System.out.print("Neuer Prüfungstyp-ID (leer = keine Änderung): ");
        String typInput = scanner.nextLine();
        if (!typInput.isBlank()) {
            try {
                int typId = Integer.parseInt(typInput);
                Pruefungstyp neuerTyp = pruefungstypDao.findById(typId);
                if (neuerTyp != null) {
                    pruefung.setPruefungstyp(neuerTyp);
                } else {
                    System.out.println("Kein gültiger Typ mit dieser ID gefunden.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungültige ID!");
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
                case "2" -> noteAnzeigenMenu();
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

        if (bezeichnung.isBlank()) {
            System.out.println("Bezeichnung darf nicht leer sein.");
            return;
        }

        Note note = new Note().noteId(id).bezeichnung(bezeichnung);
        Note created = noteDao.save(note);

        if (created != null) {
            System.out.println("Note erfolgreich erstellt.");
        } else {
            System.out.println("Fehler beim Erstellen der Note.");
        }
    }

    private void noteAnzeigenMenu() {
        List<Note> noten = noteDao.findAll();
        if (noten.isEmpty()) {
            System.out.println("Keine Noten vorhanden.");
        } else {
            noten.forEach(note -> System.out.println("ID: " + note.getNoteId() + " | Bezeichnung: " + note.getBezeichnung()));
        }
    }

    private void updateNote() {
        noteAnzeigenMenu();
        System.out.print("ID der zu bearbeitenden Note: ");
        int id = readInt(scanner);
        Note note = noteDao.findById(id);

        if (note == null) {
            System.out.println("Keine Note mit dieser ID gefunden.");
            return;
        }

        System.out.print("Neue Bezeichnung (aktuell: " + note.getBezeichnung() + "): ");
        String neueBezeichnung = scanner.nextLine();

        if (!neueBezeichnung.isBlank()) {
            note.setBezeichnung(neueBezeichnung);
            Note updated = noteDao.update(note);
            if (updated != null) {
                System.out.println("Note erfolgreich aktualisiert.");
            } else {
                System.out.println("Fehler beim Aktualisieren der Note.");
            }
        } else {
            System.out.println("Keine Änderung vorgenommen.");
        }
    }

    private void deleteNote() {
        noteAnzeigenMenu();
        System.out.print("ID der zu löschenden Note: ");
        int id = readInt(scanner);

        if (noteDao.deleteById(id)) {
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

        System.out.print("Standort: ");
        String standort = scanner.nextLine();

        Fachabteilung fachabteilung = new Fachabteilung().name(name).standort(standort);

        Fachabteilung addedFachabteilung =fachabteilungDao.save(fachabteilung);
        if (addedFachabteilung != null) {
            System.out.println("Fachabteilung erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen der Fachabteilung.");
        }
    }

    private void fachabteilungAnzeigenMenu(){
        fachabteilungDao.findAll().forEach(System.out::println);
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
            fachabteilung.setName(fachabteilungName);
        }

        System.out.print("Neuer Fachabteilungsstandort (aktuell: " + fachabteilung.getStandort() + "): ");
        String fachabteilungStandort = scanner.nextLine();
        if (!fachabteilungStandort.isEmpty()) {
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

        System.out.print("Abschluss: ");
        String abschluss = scanner.nextLine();

        System.out.print("Regelstudienzeit in Semester: ");
        int semester = Integer.parseInt(scanner.nextLine());

        System.out.println("Leiter für das Studienprogramm auswählen:");
        professorDao.findAll().forEach(System.out::println);
        Professor studienprogrammLeiter = professorDao.findById(readInt(scanner));
        if (studienprogrammLeiter == null) {
            System.out.println("Kein Professor mit dieser Id gefunden.");
            return;
        }

        Studienprogramm studienprogramm = new Studienprogramm().name(name).abschluss(abschluss).regelstudienzeitInSemester(semester).programmleiter(studienprogrammLeiter);

        Studienprogramm addedStudienprogramm =studienprogrammDao.save(studienprogramm);
        if (addedStudienprogramm != null) {
            System.out.println("Studienprogramm erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen des Studienprogramms.");
        }
    }

    private void studienprogrammeAnzeigenMenu(){
        studienprogrammDao.findAll().forEach(System.out::println);
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
            studienprogramm.setName(studienprogrammName);
        }

        System.out.print("Neuer Abschluss des Studienprogramms (aktuell: " + studienprogramm.getAbschluss() + "): ");
        String studienprogrammAbschluss = scanner.nextLine();
        if (!studienprogrammAbschluss.isEmpty()) {
            studienprogramm.setAbschluss(studienprogrammAbschluss);
        }

        System.out.print("Neue Regelstudienzeit (aktuell: " + studienprogramm.getRegelstudienzeitInSemester() + "): ");
        String regelstudienzeitEingabe = scanner.nextLine();
        if (!regelstudienzeitEingabe.isEmpty()) {
            try {
                int regelstudienzeit = Integer.parseInt(regelstudienzeitEingabe);
                studienprogramm.setRegelstudienzeitInSemester(regelstudienzeit);
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe. Regelstudienzeit wurde nicht geändert.");
            }
        }

        System.out.println("Möchten sie den Studienprogrammleiter ändern? (aktuell: " + studienprogramm.getProgrammleiter() + "): j/n");
        if (scanner.nextLine().equalsIgnoreCase("j")) {
            professorDao.findAll().forEach(System.out::println);
            int studienprogrammLeiterID = readInt(scanner);
            try{
                Professor studienprogrammLeiter = professorDao.findById(studienprogrammLeiterID);
                studienprogramm.setProgrammleiter(studienprogrammLeiter);}
            catch(EntityNotFoundException e){
                System.out.println("Kein Professor mit dieser Id gefunden: "+ studienprogrammLeiterID);
            }
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
                case "2" -> pruefungstypenAnzeigenMenu();
                case "3" -> updatePruefungstyp();
                case "4" -> deletePruefungstyp();
                default -> System.out.println("Ungültige Eingabe!");
            }
        }
    }

    private void addPruefungstyp() {
        System.out.print("Bezeichnung des neuen Prüfungstyps: ");
        String bezeichnung = scanner.nextLine();

        if (bezeichnung.isBlank()) {
            System.out.println("Bezeichnung darf nicht leer sein.");
            return;
        }

        Pruefungstyp typ = new Pruefungstyp().bezeichnung(bezeichnung);
        Pruefungstyp created = pruefungstypDao.save(typ);

        if (created != null) {
            System.out.println("Prüfungstyp erfolgreich erstellt.");
        } else {
            System.out.println("Fehler beim Erstellen des Prüfungstyps.");
        }
    }

    private void pruefungstypenAnzeigenMenu() {
        List<Pruefungstyp> typen = pruefungstypDao.findAll();
        if (typen.isEmpty()) {
            System.out.println("Keine Prüfungstypen vorhanden.");
        } else {
            typen.forEach(pt -> System.out.println("ID: " + pt.getTypId() + " | Bezeichnung: " + pt.getBezeichnung()));
        }
    }

    private void updatePruefungstyp() {
        pruefungstypenAnzeigenMenu();
        System.out.print("ID des zu bearbeitenden Prüfungstyps: ");
        int id = readInt(scanner);
        Pruefungstyp typ = pruefungstypDao.findById(id);

        if (typ == null) {
            System.out.println("Kein Prüfungstyp mit dieser ID gefunden.");
            return;
        }

        System.out.print("Neue Bezeichnung (aktuell: " + typ.getBezeichnung() + "): ");
        String neueBezeichnung = scanner.nextLine();

        if (!neueBezeichnung.isBlank()) {
            typ.setBezeichnung(neueBezeichnung);
        }

        Pruefungstyp updated = pruefungstypDao.update(typ);
        if (updated != null) {
            System.out.println("Prüfungstyp erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren des Prüfungstyps.");
        }
    }

    private void deletePruefungstyp() {
        pruefungstypenAnzeigenMenu();
        System.out.print("ID des zu löschenden Prüfungstyps: ");
        int id = readInt(scanner);

        if (pruefungstypDao.deleteById(id)) {
            System.out.println("Prüfungstyp erfolgreich gelöscht.");
        } else {
            System.out.println("Kein Prüfungstyp mit dieser ID gefunden oder bereits verwendet.");
        }
    }

}
