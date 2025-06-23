package at.fhburgenland.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

@Embeddable
public class GehoertZuPruefungPK implements Serializable, Comparable<GehoertZuPruefungPK> {
    @Column(name = "KursID")
    private Integer kursId;

    @Column(name = "PruefungsID")
    private Integer pruefungsId;

    public GehoertZuPruefungPK() {
    }

    public Integer getKursId() {
        return kursId;
    }

    public void setKursId(Integer kursId) {
        this.kursId = kursId;
    }

    public GehoertZuPruefungPK kursId(Integer kursId) {
        this.kursId = kursId;
        return this;
    }

    public Integer getPruefungsId() {
        return pruefungsId;
    }

    public void setPruefungsId(Integer pruefungsId) {
        this.pruefungsId = pruefungsId;
    }

    public GehoertZuPruefungPK pruefungsId(Integer pruefungsId) {
        this.pruefungsId = pruefungsId;
        return this;
    }

    @Override
    public String toString() {
        return "GehoertZuPruefungPK{" + "kursId=" + kursId + ", pruefungsId=" + pruefungsId + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GehoertZuPruefungPK that)) return false;
        return Objects.equals(kursId, that.kursId) && Objects.equals(pruefungsId, that.pruefungsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kursId, pruefungsId);
    }

    @Override
    public int compareTo(GehoertZuPruefungPK o) {
        return Comparator
                .comparing(GehoertZuPruefungPK::getKursId)
                .thenComparing(GehoertZuPruefungPK::getPruefungsId)
                .compare(this, o);
    }
}