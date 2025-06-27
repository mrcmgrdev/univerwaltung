package at.fhburgenland.service;

import at.fhburgenland.dao.GenericDao;
import at.fhburgenland.dao.PruefungstypDao;
import at.fhburgenland.model.Pruefungstyp;

import java.util.List;
import java.util.Scanner;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.invalidEmpty;
import static at.fhburgenland.helper.Validator.invalidLength;

public class PruefungstypService {
    private final Scanner scanner;

    private final PruefungstypDao pruefungstypDao;

    public PruefungstypService(Scanner scanner, PruefungstypDao pruefungstypDao) {
        this.scanner = scanner;
        this.pruefungstypDao = pruefungstypDao;
    }

    public void addPruefungstyp() {
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

    public void showPruefungstypen() {
        List<Pruefungstyp> typen = pruefungstypDao.findAll();
        if (typen.isEmpty()) {
            System.out.println("Keine Prüfungstypen vorhanden.");
        } else {
            typen.forEach(pt -> System.out.println("ID: " + pt.getTypId() + " | Bezeichnung: " + pt.getBezeichnung()));
        }
    }

    public void updatePruefungstyp() {
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

    public void deletePruefungstyp() {
        System.out.print("ID des zu löschenden Prüfungstyps: ");

        if (pruefungstypDao.deleteById(readInt(scanner))) {
            System.out.println("Prüfungstyp erfolgreich gelöscht.");
        } else {
            System.out.println("Fehler beim Löschen des Prüfungstyps. Möglicherweise existieren noch Prüfungen, die diesen Typ verwenden.");
        }
    }
}
