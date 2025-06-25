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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "StudienprogrammID", nullable = false)
    private Studienprogramm studienprogramm;

    @ManyToMany(mappedBy = "unterrichteteKurse", fetch = FetchType.EAGER)
    private List<Professor> professoren = new ArrayList<>();

    @OneToMany(mappedBy = "kurs", fetch = FetchType.EAGER)
    private List<Besucht> teilnehmendeStudenten = new ArrayList<>();

    @OneToMany(mappedBy = "kurs", fetch = FetchType.EAGER)
    private List<GehoertZuPruefung> zugehoerigePruefungen = new ArrayList<>();

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
        return String.format("Kurs ID: %s, Bezeichnung: %-30s, Semester: %s, ECTS: %s, Studienprogramm: %s",
                kursId != null ? kursId.toString() : "n/a",
                bezeichnung != null ? bezeichnung : "n/a",
                semester != null ? semester.toString() : "n/a",
                ects != null ? ects.toString() : "n/a",
                studienprogramm != null ? studienprogramm.getName() : "n/a");
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