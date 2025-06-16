package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Kurs")
public class Kurs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KursID")
    private Integer kursId;

    @Column(name = "Bezeichnung", nullable = false, length = 100)
    private String bezeichnung;

    @Column(name = "Semester")
    private Integer semester;

    @Column(name = "ECTS")
    private Integer ects;

    @ManyToOne()
    @JoinColumn(name = "StudienprogrammID", nullable = false)
    private Studienprogramm studienprogramm;

    @ManyToMany(mappedBy = "unterrichteteKurse")
    private Set<Professor> professoren = new HashSet<>();

    @OneToMany(mappedBy = "kurs")
    private Set<Besucht> teilnehmendeStudenten = new HashSet<>();

    @OneToMany(mappedBy = "kurs")
    private Set<GehoertZuPruefung> zugehoerigePruefungen = new HashSet<>();

    public Kurs() {
    }

    public void addProfessor(Professor professor) {
        this.professoren.add(professor);
        professor.getUnterrichteteKurse().add(this);
    }
    public void removeProfessor(Professor professor) {
        this.professoren.remove(professor);
        professor.getUnterrichteteKurse().remove(this);
    }

    public Integer getKursId() {
        return kursId;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public Kurs bezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
        return this;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getSemester() {
        return semester;
    }

    public Kurs semester(Integer semester) {
        this.semester = semester;
        return this;
    }

    public void setEcts(Integer ects) {
        this.ects = ects;
    }

    public Integer getEcts() {
        return ects;
    }

    public Kurs ects(Integer ects) {
        this.ects = ects;
        return this;
    }

    public void setStudienprogramm(Studienprogramm studienprogramm) {
        this.studienprogramm = studienprogramm;
    }

    public Studienprogramm getStudienprogramm() {
        return studienprogramm;
    }

    public Kurs studienprogramm(Studienprogramm studienprogramm) {
        this.studienprogramm = studienprogramm;
        return this;
    }

    public void setProfessoren(Set<Professor> professoren) {
        this.professoren = professoren;
    }

    public Set<Professor> getProfessoren() {
        return professoren;
    }

    public Kurs professoren(Set<Professor> professoren) {
        this.professoren = professoren;
        return this;
    }

    public void setTeilnehmendeStudenten(Set<Besucht> teilnehmendeStudenten) {
        this.teilnehmendeStudenten = teilnehmendeStudenten;
    }

    public Set<Besucht> getTeilnehmendeStudenten() {
        return teilnehmendeStudenten;
    }

    public Kurs teilnehmendeStudenten(Set<Besucht> teilnehmendeStudenten) {
        this.teilnehmendeStudenten = teilnehmendeStudenten;
        return this;
    }

    public void setZugehoerigePruefungen(Set<GehoertZuPruefung> zugehoerigePruefungen) {
        this.zugehoerigePruefungen = zugehoerigePruefungen;
    }

    public Set<GehoertZuPruefung> getZugehoerigePruefungen() {
        return zugehoerigePruefungen;
    }

    public Kurs zugehoerigePruefungen(Set<GehoertZuPruefung> zugehoerigePruefungen) {
        this.zugehoerigePruefungen = zugehoerigePruefungen;
        return this;
    }

    @Override
    public String toString() {
        return "Kurs{" + "kursId=" + kursId + ", bezeichnung='" + bezeichnung + '\'' + ", semester=" + semester + ", ects=" + ects + ", studienprogramm=" + studienprogramm + ", professoren=" + professoren + ", teilnehmendeStudenten=" + teilnehmendeStudenten + ", zugehoerigePruefungen=" + zugehoerigePruefungen + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Kurs kurs)) return false;
        return Objects.equals(kursId, kurs.kursId) && Objects.equals(bezeichnung, kurs.bezeichnung) && Objects.equals(semester, kurs.semester) && Objects.equals(ects, kurs.ects) && Objects.equals(studienprogramm, kurs.studienprogramm) && Objects.equals(professoren, kurs.professoren) && Objects.equals(teilnehmendeStudenten, kurs.teilnehmendeStudenten) && Objects.equals(zugehoerigePruefungen, kurs.zugehoerigePruefungen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kursId, bezeichnung, semester, ects, studienprogramm, professoren, teilnehmendeStudenten, zugehoerigePruefungen);
    }
}