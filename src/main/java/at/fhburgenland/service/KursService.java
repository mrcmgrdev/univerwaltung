package at.fhburgenland.service;

import at.fhburgenland.dao.*;
import at.fhburgenland.model.*;
import jakarta.persistence.*;

import java.util.*;

import static at.fhburgenland.helper.ScannerHelper.readInt;
import static at.fhburgenland.helper.Validator.invalidEmpty;
import static at.fhburgenland.helper.Validator.invalidLength;

public class KursService {
    private final Scanner scanner;

    private final StudienprogrammDao studienprogrammDao;
    private final ProfessorDao professorDao;
    private final KursDao kursDao;

    public KursService(Scanner scanner, StudienprogrammDao studienprogrammDao, ProfessorDao professorDao, KursDao kursDao) {
        this.scanner = scanner;
        this.studienprogrammDao = studienprogrammDao;
        this.professorDao = professorDao;
        this.kursDao = kursDao;
    }

    public void showKursList() {
        kursDao.findAll().forEach(System.out::println);
    }

    public void addKurs() {
        System.out.print("Kursbezeichnung: ");
        String bezeichnung = scanner.nextLine();
        if (invalidEmpty(bezeichnung, "Kursbezeichnung") || invalidLength(bezeichnung, "Kursbezeichnung", 100)) {
            return;
        }

        System.out.print("Semester: ");
        int semester = readInt(scanner);
        if (semester < 1) {
            System.out.println("Semester muss größer als 0 sein.");
            return;
        }

        System.out.print("ECTS: ");
        int ects = readInt(scanner);
        if (ects < 0) {
            System.out.println("ECTS muss größer oder gleich 0 sein.");
            return;
        }

        System.out.println("Verfügbare Studienprogramme:");
        studienprogrammDao.findAll().forEach(System.out::println);
        System.out.print("Studienprogramm (Id): ");
        int studienprogrammId = readInt(scanner);
        Studienprogramm studienprogramm = studienprogrammDao.findById(studienprogrammId);
        if (studienprogramm == null) {
            System.out.println("Kein Studienprogramm mit dieser Id gefunden!");
            return;
        }

        Kurs kurs = new Kurs().bezeichnung(bezeichnung).semester(semester).ects(ects).studienprogramm(studienprogramm);

        System.out.println("Verfügbare Professoren:");
        List<Professor> professoren = professorDao.findAll();

        if (professoren.isEmpty()) {
            System.out.println("Keine Professoren verfügbar. Kurs kann nicht erstellt werden.");
            return;
        }

        professoren.forEach(System.out::println);

        System.out.println("Geben Sie die Professor-IDs ein, die Sie hinzufügen möchten (Format: ID1,ID2,ID3,...):");
        String input = scanner.nextLine();

        if (input.isEmpty()) {
            System.out.println("Der Kurs muss mindestens einem Professor zugeordnet werden.");
            return;
        }

        String[] ids = input.split(",");
        boolean addedAtLeastOne = false;

        for (String idStr : ids) {
            try {
                int profId = Integer.parseInt(idStr.trim());
                Professor professor = professorDao.findById(profId);

                if (professor == null) {
                    System.out.println("Professor mit ID " + profId + " nicht gefunden.");
                    continue;
                }

                kurs.addProfessor(professor);
                this.professorDao.update(professor);
                System.out.println("Professor " + professor.getVorname() + " " + professor.getNachname() + " hinzugefügt.");
                addedAtLeastOne = true;
            } catch (NumberFormatException e) {
                System.out.println("Ungültige ID: " + idStr);
            }
        }

        if (!addedAtLeastOne) {
            System.out.println("Der Kurs muss mindestens einem gültigen Professor zugeordnet werden.");
            return;
        }

        Kurs addedKurs = kursDao.save(kurs);
        if (addedKurs != null) {
            System.out.println("Kurs erfolgreich hinzugefügt.");
        } else {
            System.out.println("Fehler beim Hinzufügen des Kurses.");
        }
    }

    public void showKursProfessoren() {
        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());
        System.out.println("Zugewiesene Professoren:");

        if (kurs.getProfessoren().isEmpty()) {
            System.out.println("Keine Professoren für diesen Kurs zugewiesen.");
        } else {
            kurs.getProfessoren().stream().sorted(Comparator.comparing(Professor::getProfessorId)).forEach(prof -> System.out.printf("  - %d: %s %s%n", prof.getProfessorId(), prof.getVorname(), prof.getNachname()));
        }
    }

    public void showKursStudenten() {
        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());
        System.out.println("Eingeschriebene Studenten:");

        if (kurs.getTeilnehmendeStudenten().isEmpty()) {
            System.out.println("Keine Studenten für diesen Kurs eingeschrieben.");
        } else {
            kurs.getTeilnehmendeStudenten().stream().sorted(Comparator.comparing(Besucht::getId)).forEach(besucht -> {
                Student student = besucht.getStudent();
                System.out.printf("  - %d: %s %s (Matrikelnr: %s)%n", student.getStudentId(), student.getVorname(), student.getNachname(), student.getMatrikelnummer());
            });
        }
    }

    public void showKursPruefungen() {
        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());
        System.out.println("Zugeordnete Prüfungen:");

        if (kurs.getZugehoerigePruefungen().isEmpty()) {
            System.out.println("Keine Prüfungen für diesen Kurs zugeordnet.");
        } else {
            kurs.getZugehoerigePruefungen().stream().sorted(Comparator.comparing(GehoertZuPruefung::getId)).forEach(zuordnung -> {
                Pruefung pruefung = zuordnung.getPruefung();
                System.out.printf("  - %d: %s%s%n", pruefung.getPruefungsId(), pruefung.getBezeichnung(), pruefung.getDatum() != null ? " (Datum: " + pruefung.getDatum() + ")" : "");
            });
        }
    }

    public void updateKurs() {
        System.out.print("Id des zu bearbeitenden Kurses: ");
        Kurs kurs = kursDao.findById(readInt(scanner));

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser Id gefunden.");
            return;
        }

        System.out.print("Neue Kursbezeichnung (aktuell: " + kurs.getBezeichnung() + "): ");
        String kursbezeichnung = scanner.nextLine();
        if (!kursbezeichnung.isEmpty()) {
            if (invalidEmpty(kursbezeichnung, "Kursbezeichnung") || invalidLength(kursbezeichnung, "Kursbezeichnung", 100)) {
                return;
            }
            kurs.setBezeichnung(kursbezeichnung);
        }

        System.out.print("Neues Semester (aktuell: " + kurs.getSemester() + "): ");
        String kursEingabe = scanner.nextLine();
        if (!kursEingabe.isEmpty()) {
            try {
                int kursSemester = Integer.parseInt(kursEingabe);
                if (kursSemester < 1) {
                    System.out.println("Semester muss größer als 0 sein.");
                    return;
                }

                kurs.setSemester(kursSemester);
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe. Semester wurde nicht geändert.");
            }
        }

        System.out.print("Neue ECTS (aktuell: " + kurs.getEcts() + "): ");
        String ectsEingabe = scanner.nextLine();
        if (!ectsEingabe.isEmpty()) {
            try {
                int kursEcts = Integer.parseInt(ectsEingabe);
                if (kursEcts < 0) {
                    System.out.println("ECTS muss größer oder gleich 0 sein.");
                    return;
                }

                kurs.setEcts(kursEcts);
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Eingabe. Ects wurde nicht geändert.");
            }
        }

        System.out.print("Möchten sie das zugehöriges Studienprogramm ändern? (aktuell: " + kurs.getStudienprogramm().getName() + "): j/n");
        if (scanner.nextLine().equalsIgnoreCase("j")) {
            int studienprogrammId = readInt(scanner);
            Studienprogramm studienprogramm = studienprogrammDao.findById(studienprogrammId);
            if (studienprogramm == null) {
                System.out.println("Kein Studienprogramm mit dieser Id gefunden!");
                return;
            }

            kurs.setStudienprogramm(studienprogramm);
        }

        System.out.println("Zugewiesene Professoren:");
        kurs.getProfessoren().forEach(prof -> System.out.println(prof.getProfessorId() + ": " + prof.getVorname() + " " + prof.getNachname()));

        System.out.print("Professoren ändern im Format '+ID,-ID,+ID' (z.B. '+1,-5,+4' oder leer für keine Änderung): ");
        String input = scanner.nextLine().trim();

        if (!input.isEmpty()) {
            String[] changes = input.split(",");
            Set<Professor> toRemove = new HashSet<>();
            Set<Professor> toAdd = new HashSet<>();

            for (String change : changes) {
                change = change.trim();
                if (change.length() < 2) continue;

                char operation = change.charAt(0);
                try {
                    int profId = Integer.parseInt(change.substring(1));
                    Professor prof = professorDao.findById(profId);

                    if (prof == null) {
                        System.out.println("Professor mit ID " + profId + " nicht gefunden.");
                        continue;
                    }

                    Optional<Professor> foundProf = kurs.getProfessoren().stream().filter(s -> s.getProfessorId() == profId).findFirst();

                    if (operation == '+') {
                        if (foundProf.isPresent()) {
                            System.out.println("Professor " + prof.getVorname() + " " + prof.getNachname() + " ist bereits zugewiesen.");
                        } else {
                            toAdd.add(prof);
                        }
                    } else if (operation == '-') {
                        if (foundProf.isPresent()) {
                            toRemove.add(foundProf.get());
                        } else {
                            System.out.println("Professor " + prof.getVorname() + " " + prof.getNachname() + " ist nicht zugewiesen.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige ID in: " + change);
                }
            }

            Set<Professor> resultingProfessors = new HashSet<>(kurs.getProfessoren());
            resultingProfessors.removeAll(toRemove);
            resultingProfessors.addAll(toAdd);

            if (resultingProfessors.isEmpty()) {
                System.out.println("Es muss mindestens ein Professor dem Kurs zugewiesen sein.");
                return;
            }

            for (Professor prof : toAdd) {
                kurs.addProfessor(prof);
                this.professorDao.update(prof);
                System.out.println("Professor " + prof.getVorname() + " " + prof.getNachname() + " wurde hinzugefügt.");
            }
            for (Professor prof : toRemove) {
                kurs.removeProfessor(prof);
                this.professorDao.update(prof);
                System.out.println("Professor " + prof.getVorname() + " " + prof.getNachname() + " wurde entfernt.");
            }
        }

        Kurs updatedKurs = kursDao.update(kurs);
        if (updatedKurs != null) {
            System.out.println("Kurs erfolgreich aktualisiert.");
        } else {
            System.out.println("Fehler beim Aktualisieren des Kurses.");
        }
    }

    public void deleteKurs() {
        System.out.print("Id des zu löschenden Kurses: ");

        if (kursDao.deleteById(readInt(scanner))) {
            System.out.println("Kurs erfolgreich gelöscht.");
        } else {
            System.out.println("Fehler beim Löschen des Kurses. Möglicherweise existiert der Kurs nicht, hat Professoren zugewiesen, hat eingeschriebene Studenten oder zugeordnete Prüfungen.");
        }
    }
}
