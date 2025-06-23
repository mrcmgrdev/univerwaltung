package at.fhburgenland.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "Student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StudentID")
    private Integer studentId;

    @Column(name = "Vorname", nullable = false, length = 50)
    private String vorname;

    @Column(name = "Nachname", nullable = false, length = 50)
    private String nachname;

    @Column(name = "Matrikelnummer", unique = true, nullable = false, length = 20)
    private String matrikelnummer;

    @Column(name = "Email", unique = true)
    private String email;

    @Column(name = "Geburtsdatum")
    private LocalDate geburtsdatum;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Waehlt", 
        joinColumns = @JoinColumn(name = "StudentID"), 
        inverseJoinColumns = @JoinColumn(name = "StudienprogrammID"))
    private Set<Studienprogramm> gewaehlteStudienprogramme = new HashSet<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    private List<Besucht> besuchteKurse = new ArrayList<>();

    @OneToMany(mappedBy = "student", fetch = FetchType.EAGER)
    private List<Absolviert> absolviertePruefungen = new ArrayList<>();

    public Student() {
    }

    public void addGewaehltesStudienprogramm(Studienprogramm sp) {
        this.gewaehlteStudienprogramme.add(sp);
        sp.getStudenten().add(this);
    }

    public void removeGewaehltesStudienprogramm(Studienprogramm sp) {
        this.gewaehlteStudienprogramme.remove(sp);
        sp.getStudenten().remove(this);
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getVorname() {
        return vorname;
    }

    public Student vorname(String vorname) {
        this.vorname = vorname;
        return this;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getNachname() {
        return nachname;
    }

    public Student nachname(String nachname) {
        this.nachname = nachname;
        return this;
    }

    public void setMatrikelnummer(String matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
    }

    public String getMatrikelnummer() {
        return matrikelnummer;
    }

    public Student matrikelnummer(String matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Student email(String email) {
        this.email = email;
        return this;
    }

    public void setGeburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public LocalDate getGeburtsdatum() {
        return geburtsdatum;
    }

    public Student geburtsdatum(LocalDate geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
        return this;
    }

    public void setGewaehlteStudienprogramme(Set<Studienprogramm> gewaehlteStudienprogramme) {
        this.gewaehlteStudienprogramme = gewaehlteStudienprogramme;
    }

    public Set<Studienprogramm> getGewaehlteStudienprogramme() {
        return gewaehlteStudienprogramme;
    }

    public Student gewaehlteStudienprogramme(Set<Studienprogramm> gewaehlteStudienprogramme) {
        this.gewaehlteStudienprogramme = gewaehlteStudienprogramme;
        return this;
    }

    public void setBesuchteKurse(List<Besucht> besuchteKurse) {
        this.besuchteKurse = besuchteKurse;
    }

    public List<Besucht> getBesuchteKurse() {
        return besuchteKurse;
    }

    public Student besuchteKurse(List<Besucht> besuchteKurse) {
        this.besuchteKurse = besuchteKurse;
        return this;
    }

    public void setAbsolviertePruefungen(List<Absolviert> absolviertePruefungen) {
        this.absolviertePruefungen = absolviertePruefungen;
    }

    public List<Absolviert> getAbsolviertePruefungen() {
        return absolviertePruefungen;
    }

    public Student absolviertePruefungen(List<Absolviert> absolviertePruefungen) {
        this.absolviertePruefungen = absolviertePruefungen;
        return this;
    }

    @Override
    public String toString() {
        return "Student{" + "studentId=" + studentId + ", vorname='" + vorname + '\'' + ", nachname='" + nachname + '\'' + ", matrikelnummer='" + matrikelnummer + '\'' + ", email='" + email + '\'' + ", geburtsdatum=" + geburtsdatum + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Student student)) return false;
        return Objects.equals(studentId, student.studentId) && Objects.equals(vorname, student.vorname) && Objects.equals(nachname, student.nachname) && Objects.equals(matrikelnummer, student.matrikelnummer) && Objects.equals(email, student.email) && Objects.equals(geburtsdatum, student.geburtsdatum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, vorname, nachname, matrikelnummer, email, geburtsdatum);
    }
}