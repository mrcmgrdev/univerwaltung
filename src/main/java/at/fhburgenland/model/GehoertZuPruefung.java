package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "GehoertZuPruefung")
public class GehoertZuPruefung {

    @EmbeddedId
    private GehoertZuPruefungPK id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("kursId")
    @JoinColumn(name = "KursID")
    private Kurs kurs;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("pruefungsId")
    @JoinColumn(name = "PruefungsID")
    private Pruefung pruefung;

    @Column(name = "AnteilGesamtnote_in_Prozent")
    private Integer anteilGesamtnoteInProzent;

    public GehoertZuPruefung() {
    }

    public GehoertZuPruefungPK getId() {
        return id;
    }

    public Kurs getKurs() {
        return kurs;
    }

    public void setKurs(Kurs kurs) {
        this.kurs = kurs;
    }

    public GehoertZuPruefung kurs(Kurs kurs) {
        this.kurs = kurs;
        return this;
    }

    public Pruefung getPruefung() {
        return pruefung;
    }

    public void setPruefung(Pruefung pruefung) {
        this.pruefung = pruefung;
    }

    public GehoertZuPruefung pruefung(Pruefung pruefung) {
        this.pruefung = pruefung;
        return this;
    }

    public Integer getAnteilGesamtnoteInProzent() {
        return anteilGesamtnoteInProzent;
    }

    public void setAnteilGesamtnoteInProzent(Integer anteilGesamtnoteInProzent) {
        this.anteilGesamtnoteInProzent = anteilGesamtnoteInProzent;
    }

    public GehoertZuPruefung anteilGesamtnoteInProzent(Integer anteilGesamtnoteInProzent) {
        this.anteilGesamtnoteInProzent = anteilGesamtnoteInProzent;
        return this;
    }

    @Override
    public String toString() {
        return "GehoertZuPruefung{" + "id=" + id + ", kurs=" + kurs + ", pruefung=" + pruefung + ", anteilGesamtnoteInProzent=" + anteilGesamtnoteInProzent + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof GehoertZuPruefung that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(kurs, that.kurs) && Objects.equals(pruefung, that.pruefung) && Objects.equals(anteilGesamtnoteInProzent, that.anteilGesamtnoteInProzent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, kurs, pruefung, anteilGesamtnoteInProzent);
    }
}