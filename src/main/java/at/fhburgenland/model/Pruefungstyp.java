package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Pruefungstyp")
public class Pruefungstyp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TypID")
    private Integer typId;

    @Column(name = "Bezeichnung", nullable = false, length = 50)
    private String bezeichnung;

    @OneToMany(mappedBy = "pruefungstyp")
    private Set<Pruefung> pruefungen = new HashSet<>();

    public Pruefungstyp() {
    }

    public void addPruefung(Pruefung pruefung) {
        this.pruefungen.add(pruefung);
        pruefung.setPruefungstyp(this);
    }

    public void removePruefung(Pruefung pruefung) {
        this.pruefungen.remove(pruefung);
        pruefung.setPruefungstyp(null);
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

    public void setPruefungen(Set<Pruefung> pruefungen) {
        this.pruefungen = pruefungen;
    }

    public Set<Pruefung> getPruefungen() {
        return pruefungen;
    }

    public Pruefungstyp pruefungen(Set<Pruefung> pruefungen) {
        this.pruefungen = pruefungen;
        return this;
    }

    @Override
    public String toString() {
        return "Pruefungstyp{" + "typId=" + typId + ", bezeichnung='" + bezeichnung + '\'' + ", pruefungen=" + pruefungen + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pruefungstyp that)) return false;
        return Objects.equals(typId, that.typId) && Objects.equals(bezeichnung, that.bezeichnung) && Objects.equals(pruefungen, that.pruefungen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typId, bezeichnung, pruefungen);
    }
}