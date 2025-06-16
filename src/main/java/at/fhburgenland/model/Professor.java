// File: at/fhburgenland/model/Professor.java
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

    @ManyToOne
    @JoinColumn(name = "AbteilungsID", nullable = false)
    private Fachabteilung fachabteilung;

    @OneToMany(mappedBy = "programmleiter")
    private List<Studienprogramm> geleiteteStudienprogramme = new LinkedList<>();

    @ManyToMany
    @JoinTable(name = "Unterrichtet", joinColumns = @JoinColumn(name = "ProfessorID"), inverseJoinColumns = @JoinColumn(name = "KursID"))
    private Set<Kurs> unterrichteteKurse = new HashSet<>();

    public Professor() {
    }

    public void addGeleitetesStudienprogramm(Studienprogramm sp) {
        this.geleiteteStudienprogramme.add(sp);
        sp.setProgrammleiter(this);
    }

    public void removeGeleitetesStudienprogramm(Studienprogramm sp) {
        this.geleiteteStudienprogramme.remove(sp);
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

    public void setStudienprogramme(List<Studienprogramm> studienprogramme) {
        this.geleiteteStudienprogramme = studienprogramme;
    }

    public List<Studienprogramm> getStudienprogramme() {
        return geleiteteStudienprogramme;
    }

    public Professor studienprogramme(List<Studienprogramm> studienprogramme) {
        this.geleiteteStudienprogramme = studienprogramme;
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
        return "Professor{" + "professorId=" + professorId + ", vorname='" + vorname + '\'' + ", nachname='" + nachname + '\'' + ", email='" + email + '\'' + ", fachabteilung=" + fachabteilung.toString() + '}';
    }
}