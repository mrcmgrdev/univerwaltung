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
    private Set<Besucht> besuchteKurseNoten = new HashSet<>();

    @OneToMany(mappedBy = "note")
    private Set<Absolviert> absolviertePruefungenNoten = new HashSet<>();

    public Note() {
    }

    public void setNoteId(Integer noteId) {
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

    public void setBesuchteKurseNoten(Set<Besucht> besuchteKurseNoten) {
        this.besuchteKurseNoten = besuchteKurseNoten;
    }

    public Set<Besucht> getBesuchteKurseNoten() {
        return besuchteKurseNoten;
    }

    public Note besuche(Set<Besucht> besuche) {
        this.besuchteKurseNoten = besuche;
        return this;
    }

    public void setAbsolviertePruefungenNoten(Set<Absolviert> absolviertePruefungenNoten) {
        this.absolviertePruefungenNoten = absolviertePruefungenNoten;
    }

    public Set<Absolviert> getAbsolviertePruefungenNoten() {
        return absolviertePruefungenNoten;
    }

    public Note absolvierungen(Set<Absolviert> absolvierungen) {
        this.absolviertePruefungenNoten = absolvierungen;
        return this;
    }

    @Override
    public String toString() {
        return "Note{" + "noteId=" + noteId + ", bezeichnung='" + bezeichnung + '\'' + ", besuchteKurseNoten=" + besuchteKurseNoten + ", absolviertePruefungenNoten=" + absolviertePruefungenNoten + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Note note)) return false;
        return Objects.equals(noteId, note.noteId) && Objects.equals(bezeichnung, note.bezeichnung) && Objects.equals(besuchteKurseNoten, note.besuchteKurseNoten) && Objects.equals(absolviertePruefungenNoten, note.absolviertePruefungenNoten);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId, bezeichnung, besuchteKurseNoten, absolviertePruefungenNoten);
    }
}