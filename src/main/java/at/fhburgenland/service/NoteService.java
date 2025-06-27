package at.fhburgenland.service;

import at.fhburgenland.dao.Crud;
import at.fhburgenland.dao.NoteDao;
import at.fhburgenland.model.Note;

import java.util.List;
import java.util.Scanner;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.invalidEmpty;
import static at.fhburgenland.helper.Validator.invalidLength;

public class NoteService {
    private final Scanner scanner;

    private final NoteDao noteDao;

    public NoteService(Scanner scanner, NoteDao noteDao) {
        this.scanner = scanner;
        this.noteDao = noteDao;
    }

    public void addNote() {
        System.out.print("Note (ID): ");
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

    public void showNote() {
        List<Note> noten = noteDao.findAll();
        if (noten.isEmpty()) {
            System.out.println("Keine Noten vorhanden.");
        } else {
            noten.forEach(note -> System.out.println("ID: " + note.getNoteId() + " | Bezeichnung: " + note.getBezeichnung()));
        }
    }

    public void updateNote() {
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

    public void deleteNote() {
        System.out.print("ID der zu löschenden Note: ");

        if (noteDao.deleteById(readInt(scanner))) {
            System.out.println("Note erfolgreich gelöscht.");
        } else {
            System.out.println("Keine Note mit dieser ID gefunden oder sie wird noch für Kurse oder Prüfungen verwendet.");
        }
    }
}
