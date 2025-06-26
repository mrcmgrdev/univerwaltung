package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Kurs")
public class Kurs {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kurs_seq_generator")
    @SequenceGenerator(name = "kurs_seq_generator", sequenceName = "kurs_kursid_seq", allocationSize = 1)
    @Column(name = "KursID", nullable = false, updatable = false)
    private Integer kursId;

    @Column(name = "Bezeichnung", nullable = false, length = 100)
    private String bezeichnung;

    @Column(name = "Semester", nullable = false)
    private Integer semester;

    @Column(name = "ECTS", nullable = false)
    private Integer ects;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "StudienprogrammID", nullable = false)
    private Studienprogramm studienprogramm;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Unterrichtet", joinColumns = @JoinColumn(name = "KursID"), inverseJoinColumns = @JoinColumn(name = "ProfessorID"))
    private List<Professor> professoren = new ArrayList<>();

    @OneToMany(mappedBy = "kurs", fetch = FetchType.EAGER)
    private List<Besucht> teilnehmendeStudenten = new ArrayList<>();

    @OneToMany(mappedBy = "kurs", fetch = FetchType.EAGER)
    private List<GehoertZuPruefung> zugehoerigePruefungen = new ArrayList<>();

    public Kurs() {
    }

    public void addProfessor(Professor professor) {
        professor.getUnterrichteteKurse().add(this);
        this.professoren.add(professor);
    }

    public void removeProfessor(Professor professor) {
        professor.getUnterrichteteKurse().remove(this);
        this.professoren.remove(professor);
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

    public void setProfessoren(List<Professor> professoren) {
        this.professoren = professoren;
    }

    public List<Professor> getProfessoren() {
        return professoren;
    }

    public Kurs professoren(List<Professor> professoren) {
        this.professoren = professoren;
        return this;
    }

    public void setTeilnehmendeStudenten(List<Besucht> teilnehmendeStudenten) {
        this.teilnehmendeStudenten = teilnehmendeStudenten;
    }

    public List<Besucht> getTeilnehmendeStudenten() {
        return teilnehmendeStudenten;
    }

    public Kurs teilnehmendeStudenten(List<Besucht> teilnehmendeStudenten) {
        this.teilnehmendeStudenten = teilnehmendeStudenten;
        return this;
    }

    public void setZugehoerigePruefungen(List<GehoertZuPruefung> zugehoerigePruefungen) {
        this.zugehoerigePruefungen = zugehoerigePruefungen;
    }

    public List<GehoertZuPruefung> getZugehoerigePruefungen() {
        return zugehoerigePruefungen;
    }

    public Kurs zugehoerigePruefungen(List<GehoertZuPruefung> zugehoerigePruefungen) {
        this.zugehoerigePruefungen = zugehoerigePruefungen;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Kurs ID: %s, Bezeichnung: %-30s, Semester: %s, ECTS: %s, Studienprogramm: %s", kursId.toString(), bezeichnung, semester != null ? semester.toString() : "n/a", ects != null ? ects.toString() : "n/a", studienprogramm.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Kurs kurs)) return false;
        return Objects.equals(kursId, kurs.kursId) && Objects.equals(bezeichnung, kurs.bezeichnung) && Objects.equals(semester, kurs.semester) && Objects.equals(ects, kurs.ects);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kursId, bezeichnung, semester, ects);
    }
}