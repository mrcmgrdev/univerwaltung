package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Absolviert")
public class Absolviert {

    @EmbeddedId
    private AbsolviertPK id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("pruefungsId")
    @JoinColumn(name = "PruefungsID")
    private Pruefung pruefung;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "NoteID")
    private Note note;

    public Absolviert() {
    }

    public Absolviert(int studentId, int pruefungsId, int versuch) {
        this.id = new AbsolviertPK().studentId(studentId).pruefungsId(pruefungsId).versuch(versuch);
    }

    public AbsolviertPK getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Absolviert student(Student student) {
        this.student = student;
        return this;
    }

    public Pruefung getPruefung() {
        return pruefung;
    }

    public void setPruefung(Pruefung pruefung) {
        this.pruefung = pruefung;
    }

    public Absolviert pruefung(Pruefung pruefung) {
        this.pruefung = pruefung;
        return this;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Absolviert note(Note note) {
        this.note = note;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Absolviert ID: %s, Student: %s, Pruefung: %s, Note: %s", id.toString(), student.getVorname() + " " + student.getNachname(), pruefung.getBezeichnung(), note != null ? note.getBezeichnung() : "n/a");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Absolviert that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(student, that.student) && Objects.equals(pruefung, that.pruefung) && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, student, pruefung, note);
    }
}