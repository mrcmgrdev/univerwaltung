package at.fhburgenland.model;

import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "Professor")
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProfessorID")
    private Integer professorId;

    @Column(name = "Vorname", nullable = false, length = 50)
    private String vorname;

    @Column(name = "Nachname", nullable = false, length = 50)
    private String nachname;

    @Column(name = "Email", unique = true, length = 255)
    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "AbteilungsID", nullable = false)
    private Fachabteilung fachabteilung;

    @OneToOne(mappedBy = "programmleiter", fetch = FetchType.EAGER)
    private Studienprogramm geleiteteStudienprogramm;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Unterrichtet",
            joinColumns = @JoinColumn(name = "ProfessorID"),
            inverseJoinColumns = @JoinColumn(name = "KursID"))
    private Set<Kurs> unterrichteteKurse = new HashSet<>();

    public Professor() {
    }

    public void addGeleitetesStudienprogramm(Studienprogramm sp) {
        this.geleiteteStudienprogramm = sp;
        sp.setProgrammleiter(this);
    }

    public void removeGeleitetesStudienprogramm(Studienprogramm sp) {
        this.geleiteteStudienprogramm = null;
        sp.setProgrammleiter(null);
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

    public void setUnterrichteteKurse(Set<Kurs> unterrichteteKurse) {
        this.unterrichteteKurse = unterrichteteKurse;
    }

    public Set<Kurs> getUnterrichteteKurse() {
        return unterrichteteKurse;
    }

    public Professor unterrichteteKurse(Set<Kurs> unterrichteteKurse) {
        this.unterrichteteKurse = unterrichteteKurse;
        return this;
    }

    @Override
    public String toString() {
        return "Professor{" +
                "professorId=" + professorId +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                ", email='" + email + '\'' +
                ", fachabteilung=" + (fachabteilung != null ? fachabteilung.getName() : "") +
                ", geleiteteStudienprogramm=" + (geleiteteStudienprogramm != null ? geleiteteStudienprogramm.getName() : "") +
                '}';
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