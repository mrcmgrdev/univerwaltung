package at.fhburgenland.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

@Embeddable
public class AbsolviertPK implements Serializable, Comparable<AbsolviertPK> {
    @Column(name = "StudentID")
    private Integer studentId;

    @Column(name = "PruefungsID")
    private Integer pruefungsId;

    @Column(name = "Versuch", nullable = false)
    private Integer versuch;

    public AbsolviertPK() {
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public AbsolviertPK studentId(Integer studentId) {
        this.studentId = studentId;
        return this;
    }

    public Integer getPruefungsId() {
        return pruefungsId;
    }

    public void setPruefungsId(Integer pruefungsId) {
        this.pruefungsId = pruefungsId;
    }

    public AbsolviertPK pruefungsId(Integer pruefungsId) {
        this.pruefungsId = pruefungsId;
        return this;
    }

    public Integer getVersuch() {
        return versuch;
    }

    public void setVersuch(Integer versuch) {
        this.versuch = versuch;
    }

    public AbsolviertPK versuch(Integer versuch) {
        this.versuch = versuch;
        return this;
    }

    @Override
    public String toString() {
        return String.format("AbsolviertPK StudentID: %s, PruefungsID: %s, Versuch: %s", studentId.toString(), pruefungsId.toString(), versuch.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AbsolviertPK that)) return false;
        return Objects.equals(studentId, that.studentId) && Objects.equals(pruefungsId, that.pruefungsId) && Objects.equals(versuch, that.versuch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, pruefungsId, versuch);
    }

    @Override
    public int compareTo(AbsolviertPK o) {
        return Comparator.comparing(AbsolviertPK::getStudentId).thenComparing(AbsolviertPK::getPruefungsId).thenComparing(AbsolviertPK::getVersuch).compare(this, o);
    }
}