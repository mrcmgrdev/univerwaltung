package at.fhburgenland.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

@Embeddable
public class BesuchtPK implements Serializable, Comparable<BesuchtPK> {
    @Column(name = "StudentID")
    private Integer studentId;

    @Column(name = "KursID")
    private Integer kursId;

    public BesuchtPK() {
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public BesuchtPK studentId(Integer studentId) {
        this.studentId = studentId;
        return this;
    }

    public Integer getKursId() {
        return kursId;
    }

    public void setKursId(Integer kursId) {
        this.kursId = kursId;
    }

    public BesuchtPK kursId(Integer kursId) {
        this.kursId = kursId;
        return this;
    }

    @Override
    public String toString() {
        return "BesuchtPK{" + "studentId=" + studentId + ", kursId=" + kursId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BesuchtPK besuchtPK)) return false;
        return Objects.equals(studentId, besuchtPK.studentId) && Objects.equals(kursId, besuchtPK.kursId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, kursId);
    }


    @Override
    public int compareTo(BesuchtPK o) {
        return Comparator.comparing(BesuchtPK::getStudentId).thenComparing(BesuchtPK::getKursId).compare(this, o);

    }
}
