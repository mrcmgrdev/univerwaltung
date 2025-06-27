package at.fhburgenland.service;

import at.fhburgenland.dao.StudentDao;
import at.fhburgenland.dao.StudienprogrammDao;
import at.fhburgenland.model.Absolviert;
import at.fhburgenland.model.Besucht;
import at.fhburgenland.model.Student;
import at.fhburgenland.model.Studienprogramm;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.invalidEmpty;
import static at.fhburgenland.helper.Validator.invalidLength;

public class StudentService {
    private final Scanner scanner;

    private final StudentDao studentDao;
    private final StudienprogrammDao studienprogrammDao;

    public StudentService(Scanner scanner, StudentDao studentDao, StudienprogrammDao studienprogrammDao) {
        this.scanner = scanner;
        this.studentDao = studentDao;
        this.studienprogrammDao = studienprogrammDao;
    }

    public void showStudentList() {
        studentDao.findAll().forEach(System.out::println);
    }

    public void addStudent() {
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
            if (invalidStudentEmail(email)) {
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

    public void showStudentProgramme() {
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

    public void showStudentKurse() {
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

    public void showStudentPruefungen() {
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

    public void updateStudent() {
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
            if (invalidStudentEmail(email)) {
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

    public void deleteStudent() {
        System.out.print("Id des zu löschenden Studenten: ");
        if (studentDao.deleteById(readInt(scanner))) {
            System.out.println("Student erfolgreich gelöscht.");
        } else {
            System.out.println("Fehler beim Löschen des Studenten. Möglicherweise existiert kein Student mit dieser ID oder der Student hat noch zugeordnete Kurse, Prüfungen oder Studienprogramme.");
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

    private boolean invalidStudentEmail(String email) {
        if (invalidLength(email, "Email", 255)) {
            return true;
        }
        if (studentDao.findAll().stream().anyMatch(s -> email.equals(s.getEmail()))) {
            System.out.println("Ein Student mit dieser Email existiert bereits.");
            return true;
        }
        if (!email.matches("^(.+)@(\\S+)$")) {
            System.out.println("Ungültiges Email-Format.");
            return true;
        }
        return false;
    }
}
