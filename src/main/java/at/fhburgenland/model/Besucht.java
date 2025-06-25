package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "Besucht")
public class Besucht {

    @EmbeddedId
    private BesuchtPK id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("studentId")
    @JoinColumn(name = "StudentID")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("kursId")
    @JoinColumn(name = "KursID")
    private Kurs kurs;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "NoteID")
    private Note note;

    public Besucht() {
    }

    public BesuchtPK getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Besucht student(Student student) {
        this.student = student;
        return this;
    }

    public Kurs getKurs() {
        return kurs;
    }

    public void setKurs(Kurs kurs) {
        this.kurs = kurs;
    }

    public Besucht kurs(Kurs kurs) {
        this.kurs = kurs;
        return this;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Besucht note(Note note) {
        this.note = note;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Besucht ID: %s, Student: %s, Kurs: %s, Note: %s",
                id != null ? id.toString() : "n/a",
                student != null ? student.getVorname() + " " + student.getNachname() : "n/a",
                kurs != null ? kurs.getBezeichnung() : "n/a",
                note != null ? note.getBezeichnung() : "n/a");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Besucht besucht)) return false;
        return Objects.equals(student, besucht.student) && Objects.equals(kurs, besucht.kurs) && Objects.equals(note, besucht.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, kurs, note);
    }
}