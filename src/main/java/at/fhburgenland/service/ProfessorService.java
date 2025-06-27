package at.fhburgenland.service;

import at.fhburgenland.dao.FachabteilungDao;
import at.fhburgenland.dao.ProfessorDao;
import at.fhburgenland.model.Fachabteilung;
import at.fhburgenland.model.Kurs;
import at.fhburgenland.model.Professor;

import java.util.Comparator;
import java.util.Scanner;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.invalidEmpty;
import static at.fhburgenland.helper.Validator.invalidLength;

public class ProfessorService {
    private final Scanner scanner;

    private final ProfessorDao professorDao;
    private final FachabteilungDao fachabteilungDao;

    public ProfessorService(Scanner scanner, ProfessorDao professorDao, FachabteilungDao fachabteilungDao) {
        this.scanner = scanner;
        this.professorDao = professorDao;
        this.fachabteilungDao = fachabteilungDao;
    }

    public void showProfessorList() {
        professorDao.findAll().forEach(System.out::println);
    }

    public void addProfessor() {
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
            if (invalidProfessorEmail(email)) {
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

    public void showProfessorKurse() {
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

    public void updateProfessor() {
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
            if (invalidProfessorEmail(email)) {
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

    public void deleteProfessor() {
        System.out.print("Id des zu löschenden Professors: ");
        if (professorDao.deleteById(readInt(scanner))) {
            System.out.println("Professor erfolgreich gelöscht.");
        } else {
            System.out.println("Fehler beim Löschen des Professors. Möglicherweise existiert der Professor nicht, ist noch Kursen zugeordnet oder ist Progammleiter eines Studienprogramms.");
        }
    }

    private boolean invalidProfessorEmail(String email) {
        if (invalidLength(email, "Email", 255)) {
            return true;
        }
        if (professorDao.findAll().stream().anyMatch(s -> email.equals(s.getEmail()))) {
            System.out.println("Ein Professor mit dieser Email existiert bereits.");
            return true;
        }
        if (!email.matches("^(.+)@(\\S+)$")) {
            System.out.println("Ungültiges Email-Format.");
            return true;
        }
        return false;
    }
}
