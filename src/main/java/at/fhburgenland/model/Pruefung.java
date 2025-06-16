package at.fhburgenland.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "Pruefung")
public class Pruefung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PruefungsID")
    private Integer pruefungsId;

    @Column(name = "Bezeichnung", nullable = false, length = 100)
    private String bezeichnung;

    @Column(name = "Datum")
    private LocalDate datum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TypID", nullable = false)
    private Pruefungstyp pruefungstyp;

    @OneToMany(mappedBy = "pruefung")
    private Set<GehoertZuPruefung> zugehoerigeKurse = new HashSet<>();

    @OneToMany(mappedBy = "pruefung")
    private Set<Absolviert> absolvierteVersuche = new HashSet<>();

    public Pruefung() {
    }

    public Integer getPruefungsId() {
        return pruefungsId;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public Pruefung bezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
        return this;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public Pruefung datum(LocalDate datum) {
        this.datum = datum;
        return this;
    }

    public void setPruefungstyp(Pruefungstyp pruefungstyp) {
        this.pruefungstyp = pruefungstyp;
    }

    public Pruefungstyp getPruefungstyp() {
        return pruefungstyp;
    }

    public Pruefung pruefungstyp(Pruefungstyp pruefungstyp) {
        this.pruefungstyp = pruefungstyp;
        return this;
    }

    public void setZugehoerigeKurse(Set<GehoertZuPruefung> zugehoerigeKurse) {
        this.zugehoerigeKurse = zugehoerigeKurse;
    }

    public Set<GehoertZuPruefung> getZugehoerigeKurse() {
        return zugehoerigeKurse;
    }

    public Pruefung zugehoerigeKurse(Set<GehoertZuPruefung> zugehoerigeKurse) {
        this.zugehoerigeKurse = zugehoerigeKurse;
        return this;
    }

    public void setAbsolvierteVersuche(Set<Absolviert> absolvierteVersuche) {
        this.absolvierteVersuche = absolvierteVersuche;
    }

    public Set<Absolviert> getAbsolvierteVersuche() {
        return absolvierteVersuche;
    }

    public Pruefung absolvierteVersuche(Set<Absolviert> absolvierteVersuche) {
        this.absolvierteVersuche = absolvierteVersuche;
        return this;
    }

    @Override
    public String toString() {
        return "Pruefung{" + "pruefungsId=" + pruefungsId + ", bezeichnung='" + bezeichnung + '\'' + ", datum=" + datum + ", pruefungstyp=" + pruefungstyp + ", zugehoerigeKurse=" + zugehoerigeKurse + ", absolvierteVersuche=" + absolvierteVersuche + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pruefung pruefung)) return false;
        return Objects.equals(pruefungsId, pruefung.pruefungsId) && Objects.equals(bezeichnung, pruefung.bezeichnung) && Objects.equals(datum, pruefung.datum) && Objects.equals(pruefungstyp, pruefung.pruefungstyp) && Objects.equals(zugehoerigeKurse, pruefung.zugehoerigeKurse) && Objects.equals(absolvierteVersuche, pruefung.absolvierteVersuche);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pruefungsId, bezeichnung, datum, pruefungstyp, zugehoerigeKurse, absolvierteVersuche);
    }
}