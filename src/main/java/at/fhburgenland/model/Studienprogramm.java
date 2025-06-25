package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Studienprogramm")
public class Studienprogramm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StudienprogrammID")
    private Integer studienprogrammId;

    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "Abschluss", length = 10)
    private String abschluss;

    @Column(name = "Regelstudienzeit_in_Semester")
    private Integer regelstudienzeitInSemester;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ProfessorID", nullable = false)
    private Professor programmleiter;

    @OneToMany(mappedBy = "studienprogramm", fetch = FetchType.EAGER)
    private List<Kurs> kurse = new ArrayList<>();

    @ManyToMany(mappedBy = "gewaehlteStudienprogramme", fetch = FetchType.EAGER)
    private Set<Student> studenten = new HashSet<>();

    public Studienprogramm() {
    }

    public void addKurs(Kurs kurs) {
        this.kurse.add(kurs);
        kurs.setStudienprogramm(this);
    }

    public void removeKurs(Kurs kurs) {
        this.kurse.remove(kurs);
        kurs.setStudienprogramm(null);
    }

    public void addStudent(Student student) {
        this.studenten.add(student);
        student.getGewaehlteStudienprogramme().add(this);
    }

    public void removeStudent(Student student) {
        this.studenten.remove(student);
        student.getGewaehlteStudienprogramme().remove(this);
    }

    public Integer getStudienprogrammId() {
        return studienprogrammId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Studienprogramm name(String name) {
        this.name = name;
        return this;
    }

    public void setAbschluss(String abschluss) {
        this.abschluss = abschluss;
    }

    public String getAbschluss() {
        return abschluss;
    }

    public Studienprogramm abschluss(String abschluss) {
        this.abschluss = abschluss;
        return this;
    }

    public void setRegelstudienzeitInSemester(Integer regelstudienzeitInSemester) {
        this.regelstudienzeitInSemester = regelstudienzeitInSemester;
    }

    public Integer getRegelstudienzeitInSemester() {
        return regelstudienzeitInSemester;
    }

    public Studienprogramm regelstudienzeitInSemester(Integer regelstudienzeitInSemester) {
        this.regelstudienzeitInSemester = regelstudienzeitInSemester;
        return this;
    }

    public void setProgrammleiter(Professor programmleiter) {
        this.programmleiter = programmleiter;
    }

    public Professor getProgrammleiter() {
        return programmleiter;
    }

    public Studienprogramm programmleiter(Professor programmleiter) {
        this.programmleiter = programmleiter;
        return this;
    }

    public void setKurse(List<Kurs> kurse) {
        this.kurse = kurse;
    }

    public List<Kurs> getKurse() {
        return kurse;
    }

    public Studienprogramm kurse(List<Kurs> kurse) {
        this.kurse = kurse;
        return this;
    }

    public void setStudenten(Set<Student> studenten) {
        this.studenten = studenten;
    }

    public Set<Student> getStudenten() {
        return studenten;
    }

    public Studienprogramm studenten(Set<Student> studenten) {
        this.studenten = studenten;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Studienprogramm ID: %s, Name: %-30s, Abschluss: %-10s, Regelstudienzeit: %s Semester, Programmleiter: %s", studienprogrammId != null ? studienprogrammId.toString() : "n/a", name != null ? name : "n/a", abschluss != null ? abschluss : "n/a", regelstudienzeitInSemester != null ? regelstudienzeitInSemester.toString() : "n/a", programmleiter != null ? programmleiter.getVorname() + " " + programmleiter.getNachname() : "n/a");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Studienprogramm that)) return false;
        return Objects.equals(studienprogrammId, that.studienprogrammId) && Objects.equals(name, that.name) && Objects.equals(abschluss, that.abschluss) && Objects.equals(regelstudienzeitInSemester, that.regelstudienzeitInSemester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studienprogrammId, name, abschluss, regelstudienzeitInSemester);
    }
}