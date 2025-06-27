package at.fhburgenland.service;

import at.fhburgenland.dao.Crud;
import at.fhburgenland.dao.FachabteilungDao;
import at.fhburgenland.model.Fachabteilung;
import at.fhburgenland.model.Professor;

import java.util.Comparator;
import java.util.Scanner;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.invalidEmpty;
import static at.fhburgenland.helper.Validator.invalidLength;

public class FachabteilungService {
    private final Scanner scanner;

    private final FachabteilungDao fachabteilungDao;

    public FachabteilungService(Scanner scanner, FachabteilungDao fachabteilungDao) {
        this.scanner = scanner;
        this.fachabteilungDao = fachabteilungDao;
    }


    public void showFachabteilungList() {
        fachabteilungDao.findAll().forEach(System.out::println);
    }

    public void addFachabteilung() {
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


    public void showFachabteilungProfessoren() {
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

    public void updateFachabteilung() {
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

    public void deleteFachabteilung() {
        System.out.print("Id der zu löschenden Fachabteilung: ");
        if (fachabteilungDao.deleteById(readInt(scanner))) {
            System.out.println("Fachabteilung erfolgreich gelöscht.");
        } else {
            System.out.println("Fehler beim Löschen der Fachabteilung. Möglicherweise existiert die Fachabteilung nicht oder es gibt noch Professoren in dieser Fachabteilung.");
        }
    }
}
