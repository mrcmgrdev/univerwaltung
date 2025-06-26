package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Pruefungstyp")
public class Pruefungstyp {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pruefungstyp_seq_generator")
    @SequenceGenerator(name = "pruefungstyp_seq_generator", sequenceName = "pruefungstyp_typid_seq", allocationSize = 1)
    @Column(name = "TypID", nullable = false, updatable = false)
    private Integer typId;

    @Column(name = "Bezeichnung", nullable = false, length = 50)
    private String bezeichnung;

    @OneToMany(mappedBy = "pruefungstyp")
    private List<Pruefung> pruefungen = new ArrayList<>();

    public Pruefungstyp() {
    }

    public Integer getTypId() {
        return typId;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public Pruefungstyp bezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
        return this;
    }

    public void setPruefungen(List<Pruefung> pruefungen) {
        this.pruefungen = pruefungen;
    }

    public List<Pruefung> getPruefungen() {
        return pruefungen;
    }

    public Pruefungstyp pruefungen(List<Pruefung> pruefungen) {
        this.pruefungen = pruefungen;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Pruefungstyp ID: %s, Bezeichnung: %-30s", typId.toString(), bezeichnung);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pruefungstyp that)) return false;
        return Objects.equals(typId, that.typId) && Objects.equals(bezeichnung, that.bezeichnung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typId, bezeichnung);
    }
}