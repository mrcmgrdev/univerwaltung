package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Note")
public class Note {

    @Id
    @Column(name = "NoteID")
    private Integer noteId;

    @Column(name = "Bezeichnung", nullable = false, length = 50)
    private String bezeichnung;

    @OneToMany(mappedBy = "note")
    private List<Besucht> besuchteKurseNoten = new ArrayList<>();

    @OneToMany(mappedBy = "note")
    private List<Absolviert> absolviertePruefungenNoten = new ArrayList<>();

    public Note() {
    }

    public void ListNoteId(Integer noteId) {
        this.noteId = noteId;
    }

    public Integer getNoteId() {
        return noteId;
    }

    public Note noteId(Integer noteId) {
        this.noteId = noteId;
        return this;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public Note bezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
        return this;
    }

    public void setBesuchteKurseNoten(List<Besucht> besuchteKurseNoten) {
        this.besuchteKurseNoten = besuchteKurseNoten;
    }

    public List<Besucht> getBesuchteKurseNoten() {
        return besuchteKurseNoten;
    }

    public Note besuche(List<Besucht> besuche) {
        this.besuchteKurseNoten = besuche;
        return this;
    }

    public void setAbsolviertePruefungenNoten(List<Absolviert> absolviertePruefungenNoten) {
        this.absolviertePruefungenNoten = absolviertePruefungenNoten;
    }

    public List<Absolviert> getAbsolviertePruefungenNoten() {
        return absolviertePruefungenNoten;
    }

    public Note absolvierungen(List<Absolviert> absolvierungen) {
        this.absolviertePruefungenNoten = absolvierungen;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Note ID: %s, Bezeichnung: %-20s",
                noteId != null ? noteId.toString() : "n/a",
                bezeichnung != null ? bezeichnung : "n/a");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Note note)) return false;
        return Objects.equals(noteId, note.noteId) && Objects.equals(bezeichnung, note.bezeichnung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId, bezeichnung);
    }
}