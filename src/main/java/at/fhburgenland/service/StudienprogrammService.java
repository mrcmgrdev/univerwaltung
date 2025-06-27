package at.fhburgenland.service;

import at.fhburgenland.dao.Crud;
import at.fhburgenland.dao.GenericDao;
import at.fhburgenland.dao.ProfessorDao;
import at.fhburgenland.dao.StudienprogrammDao;
import at.fhburgenland.model.Kurs;
import at.fhburgenland.model.Professor;
import at.fhburgenland.model.Student;
import at.fhburgenland.model.Studienprogramm;

import java.util.Comparator;
import java.util.Scanner;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.invalidEmpty;
import static at.fhburgenland.helper.Validator.invalidLength;

public class StudienprogrammService {
    private final Scanner scanner;

    private final StudienprogrammDao studienprogrammDao;
    private final ProfessorDao professorDao;

    public StudienprogrammService(Scanner scanner, StudienprogrammDao studienprogrammDao, ProfessorDao professorDao) {
        this.scanner = scanner;
        this.studienprogrammDao = studienprogrammDao;
        this.professorDao = professorDao;
    }

    public void showStudienprogrammList() {
        studienprogrammDao.findAll().forEach(System.out::println);
    }

    public void addStudienprogramm() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        if (invalidEmpty(name, "Name") || invalidLength(name, "Name", 100)) {
            return;
        }

        System.out.print("Abschluss: ");
        String abschluss = scanner.nextLine();
        if (invalidEmpty(abschluss, "Abschluss") || invalidLength(abschluss, "Abschluss", 10)) {
            return;
        }

        System.out.print("Regelstudienzeit in Semester: ");
        int semester = readInt(scanner);
        if (semester < 1) {
            System.out.println("Regelstudienzeit muss größer als 0 sein.");
            return;
        }

        System.out.println("Leiter für das Studienprogramm auswählen:");
        professorDao.findAll().stream().filter(professor -> professor.getStudienprogramm() == null).forEach(System.out::println);
        Professor studienprogrammLeiter = professorDao.findById(readInt(scanner));
        if (studienprogrammLeiter == null) {
            System.out.println("Kein Professor mit dieser Id gefunden.");
            return;
        }

        if (studienprogrammLeiter.getStudienprogramm() != null) {
            System.out.println("Dieser Professor ist bereits Leiter eines Studienprogramms.");
            return;
        }

        Studienprogramm studienprogramm = new Studienprogramm().name(name).abschluss(abschluss).regelstudienzeitInSemester(semester).programmleiter(studienprogrammLeiter);

        Studienprogramm addedStudienprogramm = studienprogrammDao.save(studienprogramm);
        if (addedStudienprogramm != null) {
            System.out.println("Studienprogramm erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen des Studienprogramms.");
        }
    }


    public void showStudienprogrammKurse() {
        System.out.print("ID des Studienprogramms: ");
        int studienprogrammId = readInt(scanner);
        Studienprogramm studienprogramm = studienprogrammDao.findById(studienprogrammId);

        if (studienprogramm == null) {
            System.out.println("Kein Studienprogramm mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Studienprogramm: %s (ID: %d)%n", studienprogramm.getName(), studienprogramm.getStudienprogrammId());
        System.out.println("Zugehörige Kurse:");

        if (studienprogramm.getKurse().isEmpty()) {
            System.out.println("Keine Kurse in diesem Studienprogramm.");
        } else {
            studienprogramm.getKurse().stream().sorted(Comparator.comparing(Kurs::getKursId)).forEach(kurs -> System.out.printf("  - %d: %s (Semester: %d, ECTS: %d)%n", kurs.getKursId(), kurs.getBezeichnung(), kurs.getSemester(), kurs.getEcts()));
        }
    }

    public void showStudienprogrammStudenten() {
        System.out.print("ID des Studienprogramms: ");
        int studienprogrammId = readInt(scanner);
        Studienprogramm studienprogramm = studienprogrammDao.findById(studienprogrammId);

        if (studienprogramm == null) {
            System.out.println("Kein Studienprogramm mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Studienprogramm: %s (ID: %d)%n", studienprogramm.getName(), studienprogramm.getStudienprogrammId());
        System.out.println("Eingeschriebene Studenten:");

        if (studienprogramm.getStudenten().isEmpty()) {
            System.out.println("Keine Studenten in diesem Studienprogramm eingeschrieben.");
        } else {
            studienprogramm.getStudenten().stream().sorted(Comparator.comparing(Student::getStudentId)).forEach(student -> System.out.printf("  - %d: %s %s (Matrikelnr: %s)%n", student.getStudentId(), student.getVorname(), student.getNachname(), student.getMatrikelnummer()));
        }
    }

    public void updateStudienprogramm() {
        System.out.print("Id des zu bearbeitenden Studienprogramms: ");
        Studienprogramm studienprogramm = studienprogrammDao.findById(readInt(scanner));
        if (studienprogramm == null) {
            System.out.println("Kein Studienprogramm mit dieser Id gefunden.");
            return;
        }

        System.out.print("Neuer Studienprogrammsname (aktuell: " + studienprogramm.getName() + "): ");
        String studienprogrammName = scanner.nextLine();
        if (!studienprogrammName.isEmpty()) {
            if (invalidEmpty(studienprogrammName, "Studienprogrammsname") || invalidLength(studienprogrammName, "Studienprogrammsname", 100)) {
                return;
            }

            studienprogramm.setName(studienprogrammName);
        }

        System.out.print("Neuer Abschluss des Studienprogramms (aktuell: " + studienprogramm.getAbschluss() + "): ");
        String studienprogrammAbschluss = scanner.nextLine();
        if (!studienprogrammAbschluss.isEmpty()) {
            if (invalidEmpty(studienprogrammAbschluss, "Abschluss des Studienprogramms") || invalidLength(studienprogrammAbschluss, "Abschluss des Studienprogramms", 10)) {
                return;
            }

            studienprogramm.setAbschluss(studienprogrammAbschluss);
        }

        System.out.print("Neue Regelstudienzeit (aktuell: " + studienprogramm.getRegelstudienzeitInSemester() + "): ");
        String regelstudienzeitEingabe = scanner.nextLine();
        if (!regelstudienzeitEingabe.isEmpty()) {
            try {
                int regelstudienzeit = Integer.parseInt(regelstudienzeitEingabe);
                if (regelstudienzeit < 1) {
                    System.out.println("Regelstudienzeit muss größer als 0 sein.");
                    return;
                }

                studienprogramm.setRegelstudienzeitInSemester(regelstudienzeit);
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe. Regelstudienzeit wurde nicht geändert.");
            }
        }

        System.out.print("Möchten sie den Studienprogrammleiter ändern? (aktuell: " + studienprogramm.getProgrammleiter() + "): j/n");
        if (scanner.nextLine().equalsIgnoreCase("j")) {
            professorDao.findAll().stream().filter(professor -> professor.getStudienprogramm() == null).forEach(System.out::println);
            int studienprogrammLeiterID = readInt(scanner);
            Professor studienprogrammLeiter = professorDao.findById(studienprogrammLeiterID);

            if (studienprogrammLeiter == null) {
                System.out.println("Kein Professor mit dieser Id gefunden.");
                return;
            }

            if (studienprogrammLeiter.getStudienprogramm() != null) {
                System.out.println("Dieser Professor ist bereits Leiter eines Studienprogramms.");
                return;
            }

            studienprogramm.setProgrammleiter(studienprogrammLeiter);
        }

        Studienprogramm updatedStudienprogramm = studienprogrammDao.update(studienprogramm);
        if (updatedStudienprogramm != null) {
            System.out.println("Studienprogramm erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren des Studienprogramms.");
        }
    }

    public void deleteStudienprogramm() {
        System.out.print("Id des zu löschenden Studienprogramms: ");
        if (studienprogrammDao.deleteById(readInt(scanner))) {
            System.out.println("Studienprogramm erfolgreich gelöscht.");
        } else {
            System.out.println("Fehler beim Löschen des Studienprogramms. Möglicherweise existieren noch Kurse oder Studenten in diesem Programm.");
        }
    }
}
