package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Professor")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "professor_seq_generator")
    @SequenceGenerator(name = "professor_seq_generator", sequenceName = "professor_professorid_seq", allocationSize = 1)
    @Column(name = "ProfessorID", updatable = false, nullable = false)
    private Integer professorId;

    @Column(name = "Vorname", nullable = false, length = 50)
    private String vorname;

    @Column(name = "Nachname", nullable = false, length = 50)
    private String nachname;

    @Column(name = "Email", unique = true)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AbteilungsID", nullable = false)
    private Fachabteilung fachabteilung;

    @OneToOne(mappedBy = "programmleiter", fetch = FetchType.EAGER)
    private Studienprogramm geleiteteStudienprogramm;

    @ManyToMany(mappedBy = "professoren", fetch = FetchType.EAGER)
    private List<Kurs> unterrichteteKurse = new ArrayList<>();

    public Professor() {
    }

    public Integer getProfessorId() {
        return professorId;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getVorname() {
        return vorname;
    }

    public Professor vorname(String vorname) {
        this.vorname = vorname;
        return this;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getNachname() {
        return nachname;
    }

    public Professor nachname(String nachname) {
        this.nachname = nachname;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Professor email(String email) {
        this.email = email;
        return this;
    }

    public void setFachabteilung(Fachabteilung fachabteilung) {
        this.fachabteilung = fachabteilung;
    }

    public Fachabteilung getFachabteilung() {
        return fachabteilung;
    }

    public Professor fachabteilung(Fachabteilung fachabteilung) {
        this.fachabteilung = fachabteilung;
        return this;
    }

    public void setStudienprogramm(Studienprogramm studienprogramm) {
        this.geleiteteStudienprogramm = studienprogramm;
    }

    public Studienprogramm getStudienprogramm() {
        return geleiteteStudienprogramm;
    }

    public Professor studienprogramm(Studienprogramm studienprogramm) {
        this.geleiteteStudienprogramm = studienprogramm;
        return this;
    }

    public void setUnterrichteteKurse(List<Kurs> unterrichteteKurse) {
        this.unterrichteteKurse = unterrichteteKurse;
    }

    public List<Kurs> getUnterrichteteKurse() {
        return unterrichteteKurse;
    }

    public Professor unterrichteteKurse(List<Kurs> unterrichteteKurse) {
        this.unterrichteteKurse = unterrichteteKurse;
        return this;
    }

    @Override
    public String toString() {
        return String.format("Professor ID: %s, Name: %-20s, Email: %-30s, Fachabteilung: %s, Programmleiter: %s", professorId.toString(), vorname + " " + nachname, email != null ? email : "n/a", fachabteilung != null ? fachabteilung.getName() : "n/a", geleiteteStudienprogramm != null ? geleiteteStudienprogramm.getName() : "n/a");
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Professor professor)) return false;
        return Objects.equals(professorId, professor.professorId) && Objects.equals(vorname, professor.vorname) && Objects.equals(nachname, professor.nachname) && Objects.equals(email, professor.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(professorId, vorname, nachname, email);
    }
}