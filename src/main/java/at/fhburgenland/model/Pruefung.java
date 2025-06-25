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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TypID", nullable = false)
    private Pruefungstyp pruefungstyp;

    @OneToMany(mappedBy = "pruefung", fetch = FetchType.EAGER)
    private List<GehoertZuPruefung> zugehoerigeKurse = new ArrayList<>();

    @OneToMany(mappedBy = "pruefung", fetch = FetchType.EAGER)
    private List<Absolviert> absolvierteVersuche = new ArrayList<>();

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

    public void setZugehoerigeKurse(List<GehoertZuPruefung> zugehoerigeKurse) {
        this.zugehoerigeKurse = zugehoerigeKurse;
    }

    public List<GehoertZuPruefung> getZugehoerigeKurse() {
        return zugehoerigeKurse;
    }

    public Pruefung zugehoerigeKurse(List<GehoertZuPruefung> zugehoerigeKurse) {
        this.zugehoerigeKurse = zugehoerigeKurse;
        return this;
    }

    public void setAbsolvierteVersuche(List<Absolviert> absolvierteVersuche) {
        this.absolvierteVersuche = absolvierteVersuche;
    }

    public List<Absolviert> getAbsolvierteVersuche() {
        return absolvierteVersuche;
    }

    public Pruefung absolvierteVersuche(List<Absolviert> absolvierteVersuche) {
        this.absolvierteVersuche = absolvierteVersuche;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Pruefung ID: %s, Bezeichnung: %-30s, Datum: %s, Typ: %s",
                pruefungsId != null ? pruefungsId.toString() : "n/a",
                bezeichnung != null ? bezeichnung : "n/a",
                datum != null ? datum.toString() : "n/a",
                pruefungstyp != null ? pruefungstyp.getBezeichnung() : "n/a");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pruefung pruefung)) return false;
        return Objects.equals(pruefungsId, pruefung.pruefungsId) && Objects.equals(bezeichnung, pruefung.bezeichnung) && Objects.equals(datum, pruefung.datum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pruefungsId, bezeichnung, datum);
    }
}