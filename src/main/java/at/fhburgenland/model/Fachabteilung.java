package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Fachabteilung")
public class Fachabteilung {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fachabteilung_seq_generator")
    @SequenceGenerator(name = "fachabteilung_seq_generator", sequenceName = "fachabteilung_abteilungsid_seq", allocationSize = 1)
    @Column(name = "AbteilungsID", updatable = false, nullable = false)
    private Integer abteilungsId;

    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "Standort", nullable = false, length = 100)
    private String standort;

    @OneToMany(mappedBy = "fachabteilung", fetch = FetchType.EAGER)
    private List<Professor> professoren = new ArrayList<>();

    public Fachabteilung() {
    }

    public Integer getAbteilungsId() {
        return abteilungsId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Fachabteilung name(String name) {
        this.name = name;
        return this;
    }

    public void setStandort(String standort) {
        this.standort = standort;
    }

    public String getStandort() {
        return standort;
    }

    public Fachabteilung standort(String standort) {
        this.standort = standort;
        return this;
    }

    public void setProfessoren(List<Professor> professoren) {
        this.professoren = professoren;
    }

    public List<Professor> getProfessoren() {
        return professoren;
    }

    public Fachabteilung professoren(List<Professor> professoren) {
        this.professoren = professoren;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Fachabteilung ID: %s, Name: %s, Standort: %s", abteilungsId.toString(), name, standort);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Fachabteilung that)) return false;
        return Objects.equals(abteilungsId, that.abteilungsId) && Objects.equals(name, that.name) && Objects.equals(standort, that.standort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abteilungsId, name, standort);
    }
}