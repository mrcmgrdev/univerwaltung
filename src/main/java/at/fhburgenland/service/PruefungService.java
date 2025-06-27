package at.fhburgenland.service;

import at.fhburgenland.dao.GehoertZuPruefungDao;
import at.fhburgenland.dao.KursDao;
import at.fhburgenland.dao.PruefungDao;
import at.fhburgenland.dao.PruefungstypDao;
import at.fhburgenland.model.*;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.invalidEmpty;
import static at.fhburgenland.helper.Validator.invalidLength;

public class PruefungService {
    private final Scanner scanner;

    private final PruefungstypDao pruefungstypDao;
    private final KursDao kursDao;
    private final PruefungDao pruefungDao;
    private final GehoertZuPruefungDao gehoertZuPruefungDao;

    public PruefungService(Scanner scanner, PruefungstypDao pruefungstypDao, KursDao kursDao, PruefungDao pruefungDao, GehoertZuPruefungDao gehoertZuPruefungDao) {
        this.scanner = scanner;
        this.pruefungstypDao = pruefungstypDao;
        this.kursDao = kursDao;
        this.pruefungDao = pruefungDao;
        this.gehoertZuPruefungDao = gehoertZuPruefungDao;
    }

    public void showPruefungList() {
        pruefungDao.findAll().forEach(System.out::println);
    }

    public void addPruefung() {
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

        System.out.println("Verfügbare Kurse:");
        kursDao.findAll().forEach(k -> System.out.println("  " + k.getKursId() + ": " + k.getBezeichnung()));
        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);
        if (kurs == null) {
            System.out.println("Kein gültiger Kurs gefunden.");
            return;
        }

        Pruefung createdPruefung = pruefungDao.save(new Pruefung().bezeichnung(bezeichnung).datum(datum).pruefungstyp(typ));
        if (createdPruefung == null) {
            System.out.println("Fehler beim Erstellen der Prüfung.");
            return;
        }

        GehoertZuPruefung createdZuweisung = gehoertZuPruefungDao.save(new GehoertZuPruefung(kurs.getKursId(), createdPruefung.getPruefungsId()).kurs(kurs).pruefung(createdPruefung));
        if (createdZuweisung == null) {
            pruefungDao.deleteById(createdPruefung.getPruefungsId());
            System.out.println("Fehler beim Zuordnen des Kurses zur Prüfung. Prüfung wurde gelöscht.");
        } else {
            System.out.println("Prüfung erfolgreich erstellt und Kurs zugeordnet.");
        }
    }

    public void showPruefungKurse() {
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

    public void showPruefungVersuche() {
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

    public void updatePruefung() {
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

    public void deletePruefung() {
        System.out.print("Id der zu löschenden Prüfung: ");
        if (pruefungDao.deleteById(readInt(scanner))) {
            System.out.println("Prüfung erfolgreich gelöscht.");
        } else {
            System.out.println("Fehler beim Löschen der Prüfung. Möglicherweise existiert die Prüfung nicht oder es gibt noch zugeordnete Kurse oder Prüfungsversuche.");
        }
    }
}
