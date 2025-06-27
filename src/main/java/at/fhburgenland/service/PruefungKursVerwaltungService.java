package at.fhburgenland.service;

import at.fhburgenland.dao.*;
import at.fhburgenland.model.*;

import java.util.*;

import static at.fhburgenland.helper.ScannerHelper.readInt;

public class PruefungKursVerwaltungService {
    private final Scanner scanner;

    private final PruefungDao pruefungDao;
    private final StudentDao studentDao;
    private final AbsolviertDao absolviertDao;
    private final KursDao kursDao;
    private final NoteDao noteDao;
    private final BesuchtDao besuchtDao;
    private final GehoertZuPruefungDao gehoertZuPruefungDao;

    public PruefungKursVerwaltungService(Scanner scanner, PruefungDao pruefungDao, StudentDao studentDao, AbsolviertDao absolviertDao, KursDao kursDao, NoteDao noteDao, BesuchtDao besuchtDao, GehoertZuPruefungDao gehoertZuPruefungDao) {
        this.scanner = scanner;
        this.pruefungDao = pruefungDao;
        this.studentDao = studentDao;
        this.absolviertDao = absolviertDao;
        this.kursDao = kursDao;
        this.noteDao = noteDao;
        this.besuchtDao = besuchtDao;
        this.gehoertZuPruefungDao = gehoertZuPruefungDao;
    }

    public void pruefungsNotenVerwaltung() {
        System.out.println("Verfügbare Prüfungen:");
        pruefungDao.findAll().stream().sorted(Comparator.comparing(Pruefung::getPruefungsId)).forEach(System.out::println);

        System.out.print("ID der Prüfung: ");
        int pruefungsId = readInt(scanner);
        Pruefung pruefung = pruefungDao.findById(pruefungsId);

        if (pruefung == null) {
            System.out.println("Keine Prüfung mit dieser ID gefunden.");
            return;
        }

        if (pruefung.getZugehoerigeKurse().isEmpty()) {
            System.out.println("Diese Prüfung ist keinem Kurs zugeordnet. Bitte zuerst einen Kurs zuweisen.");
            return;
        }

        System.out.printf("Ausgewählte Prüfung: %s (ID: %d)%n", pruefung.getBezeichnung(), pruefung.getPruefungsId());

        System.out.println("Verfügbare Studenten: ");
        List<Student> studentsInKurs = pruefung.getZugehoerigeKurse().stream().map(GehoertZuPruefung::getKurs).flatMap(kurs -> kurs.getTeilnehmendeStudenten().stream()).map(Besucht::getStudent).toList();
        if (studentsInKurs.isEmpty()) {
            System.out.println("Keine Studenten für den Kurs eingeschrieben zu der die Prüfung gehört.");
            return;
        }

        studentsInKurs.forEach(System.out::println);

        boolean addMoreStudents = true;
        while (addMoreStudents) {
            System.out.print("ID des Studenten (0 zum Beenden): ");
            int studentId = readInt(scanner);

            if (studentId == 0) {
                addMoreStudents = false;
                continue;
            }

            Student student = studentDao.findById(studentId);

            if (student == null) {
                System.out.println("Kein Student mit dieser ID gefunden.");
                continue;
            }

            System.out.printf("Student: %s %s (ID: %d)%n", student.getVorname(), student.getNachname(), student.getStudentId());

            boolean studentInKurs = studentsInKurs.stream().anyMatch(stu -> stu.getStudentId() == studentId);

            if (!studentInKurs) {
                System.out.println("Der Student ist nicht im Kurs eingeschrieben, dem diese Prüfung zugeordnet ist.");
                continue;
            }

            long attempt = student.getAbsolviertePruefungen().stream().filter(absolviert -> absolviert.getPruefung().getPruefungsId() == pruefungsId).count();
            int newAttempt;
            if (student.getAbsolviertePruefungen().stream().anyMatch(absolviert -> absolviert.getPruefung().getPruefungsId() == pruefungsId && absolviert.getNote() == null)) {
                System.out.println("Der Student hat bereits einen Versuch für diese Prüfung ohne Note.");
                newAttempt = (int) attempt;
            } else {
                newAttempt = (int) attempt + 1;
                if (newAttempt > 3) {
                    System.out.printf("Der Student hat bereits %d Versuche für diese Prüfung absolviert. Eine weitere Prüfung kann nicht hinzugefügt werden.%n", attempt);
                    continue;
                }
            }

            System.out.println("Leer lassen, wenn noch keine Note vergeben werden soll.");
            System.out.printf("Note für %d. Versuch (ID): ", newAttempt);
            String noteId = scanner.nextLine();
            Note note = null;
            if (!noteId.isEmpty()) {
                try {
                    note = noteDao.findById(Integer.parseInt(noteId));

                    if (note == null) {
                        System.out.println("Keine Note mit dieser ID gefunden. Keine Note wird zugewiesen.");
                    } else {
                        System.out.printf("Ausgewählte Note: %s%n", note.getBezeichnung());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige Note ID. Keine Note wird zugewiesen.");
                }
            }

            Absolviert addedAbsolviert = absolviertDao.update(new Absolviert(studentId, pruefungsId, newAttempt).student(student).pruefung(pruefung).note(note));
            if (addedAbsolviert != null) {
                System.out.printf("Prüfung für %s %s erfolgreich aktualisiert.%n", student.getVorname(), student.getNachname());
            } else {
                System.out.printf("Fehler beim Aktualisieren der Prüfung für %s %s.%n", student.getVorname(), student.getNachname());
            }
        }
    }

    public void kursNotenVerwaltung() {
        System.out.println("Verfügbare Kurse:");
        kursDao.findAll().stream().sorted(Comparator.comparing(Kurs::getKursId)).forEach(System.out::println);

        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Ausgewählter Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());
        System.out.println("Eingeschriebene Studenten:");

        if (kurs.getTeilnehmendeStudenten().isEmpty()) {
            System.out.println("Keine Studenten für diesen Kurs eingeschrieben.");
            return;
        }

        kurs.getTeilnehmendeStudenten().stream().sorted(Comparator.comparing(Besucht::getId)).forEach(besucht -> {
            Student student = besucht.getStudent();
            String noteInfo = besucht.getNote() != null ? ", Note: " + besucht.getNote().getBezeichnung() : ", Keine Note";
            System.out.printf("  - %-3d: %-15s %-15s (Matrikelnr: %s)%s%n", student.getStudentId(), student.getVorname(), student.getNachname(), student.getMatrikelnummer(), noteInfo);
        });

        System.out.print("ID des Studenten für Noteneintrag: ");
        int studentId = readInt(scanner);
        Student student = studentDao.findById(studentId);

        if (student == null) {
            System.out.println("Kein Student mit dieser ID gefunden.");
            return;
        }

        boolean attending = kurs.getTeilnehmendeStudenten().stream().anyMatch(besucht -> besucht.getStudent().getStudentId() == studentId);

        if (!attending) {
            System.out.println("Dieser Student ist nicht in diesem Kurs eingeschrieben.");
            return;
        }

        List<Absolviert> absolvierteKursPruefungen = student.getAbsolviertePruefungen().stream().filter(absolviert -> absolviert.getPruefung().getZugehoerigeKurse().stream().anyMatch(zuordnung -> zuordnung.getKurs().getKursId() == kursId)).toList();

        Map<Pruefung, Double> pruefungsGewichtungen = new HashMap<>();
        Note vorgeschlageneNote = null;

        if (!absolvierteKursPruefungen.isEmpty()) {
            System.out.println("Absolvierte Prüfungen in diesem Kurs:");

            double summeAnteile = 0.0;
            Map<Pruefung, Integer> anteile = new HashMap<>();

            for (Absolviert absolviert : absolvierteKursPruefungen) {
                Pruefung pruefung = absolviert.getPruefung();
                Note note = absolviert.getNote();

                Optional<GehoertZuPruefung> zuordnung = pruefung.getZugehoerigeKurse().stream().filter(z -> z.getKurs().getKursId() == kursId).findFirst();

                Integer anteil = zuordnung.map(GehoertZuPruefung::getAnteilGesamtnoteInProzent).orElse(null);

                System.out.printf("  - %s(%d. Versuch): %s (Anteil: %s%%)%n", pruefung.getBezeichnung(), absolviert.getId().getVersuch(), note != null ? note.getBezeichnung() : "Keine Note", anteil != null ? anteil : "nicht festgelegt");

                if (anteil != null) {
                    anteile.put(pruefung, anteil);
                    summeAnteile += anteil;
                }
            }

            if (anteile.isEmpty() || summeAnteile < 100.0) {
                double gleichmaessigerAnteil = 100.0 / absolvierteKursPruefungen.size();
                for (Absolviert absolviert : absolvierteKursPruefungen) {
                    pruefungsGewichtungen.put(absolviert.getPruefung(), gleichmaessigerAnteil);
                }

                if (!anteile.isEmpty()) {
                    System.out.println("Hinweis: Die Summe der Prüfungsanteile ergibt nicht 100%. Verwende gleichmäßige Verteilung.");
                }
            } else {
                anteile.forEach((pruefung, anteil) -> pruefungsGewichtungen.put(pruefung, (double) anteil));
            }

            double gewichteterDurchschnitt = 0.0;
            double gesamtGewichtung = 0.0;

            for (Absolviert absolviert : absolvierteKursPruefungen) {
                Note note = absolviert.getNote();
                if (note != null && note.getNoteId() <= 5) {
                    Double gewichtung = pruefungsGewichtungen.get(absolviert.getPruefung());
                    gewichteterDurchschnitt += note.getNoteId() * gewichtung;
                    gesamtGewichtung += gewichtung;
                }
            }
            if (gesamtGewichtung > 0) {
                double endnote = gewichteterDurchschnitt / gesamtGewichtung;
                int gerundeteNote = (int) Math.round(endnote);

                if (gerundeteNote <= 5) {
                    vorgeschlageneNote = noteDao.findById(gerundeteNote);
                }
            }
        } else {
            System.out.println("Keine absolvierten Prüfungen in diesem Kurs gefunden.");
        }

        System.out.println("Leer lassen, wenn noch keine Note vergeben werden soll.");
        if (vorgeschlageneNote != null) {
            System.out.printf("Vorgeschlagene Note basierend auf den absolvierten Prüfungen: %s%n", vorgeschlageneNote.getBezeichnung());
        }
        System.out.print("Note (ID): ");
        String noteId = scanner.nextLine();
        Note note = null;
        if (!noteId.isEmpty()) {
            try {
                note = noteDao.findById(Integer.parseInt(noteId));

                if (note == null) {
                    System.out.println("Keine Note mit dieser ID gefunden. Keine Note wird zugewiesen.");
                } else {
                    System.out.printf("Ausgewählte Note: %s%n", note.getBezeichnung());
                }
            } catch (NumberFormatException e) {
                System.out.println("Ungültige Note ID. Keine Note wird zugewiesen.");
            }
        }

        Besucht addedBesucht = besuchtDao.save(new Besucht(studentId, kursId).student(student).kurs(kurs).note(note));
        if (addedBesucht != null) {
            System.out.printf("Eintrag für %s %s im Kurs %s erfolgreich aktualisiert.%n", student.getVorname(), student.getNachname(), kurs.getBezeichnung());
        } else {
            System.out.printf("Fehler beim Eintragen für %s %s im Kurs %s.%n", student.getVorname(), student.getNachname(), kurs.getBezeichnung());
        }
    }

    public void kursStudentenVerwaltung() {
        System.out.println("Verfügbare Kurse:");
        kursDao.findAll().stream().sorted(Comparator.comparing(Kurs::getKursId)).forEach(System.out::println);

        System.out.print("ID des Kurses: ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.printf("Ausgewählter Kurs: %s (ID: %d)%n", kurs.getBezeichnung(), kurs.getKursId());

        if (kurs.getTeilnehmendeStudenten().isEmpty()) {
            System.out.println("Keine Studenten für diesen Kurs eingeschrieben.");
        } else {
            System.out.println("Eingeschriebene Studenten:");
            kurs.getTeilnehmendeStudenten().stream().sorted(Comparator.comparing(besucht -> besucht.getStudent().getStudentId())).forEach(besucht -> {
                Student student = besucht.getStudent();
                Note note = besucht.getNote();
                String noteInfo = note != null ? ", Note: " + note.getBezeichnung() : ", Keine Note";
                System.out.printf("  - %-3d: %-15s %-15s (Matrikelnr: %s)%s%n", student.getStudentId(), student.getVorname(), student.getNachname(), student.getMatrikelnummer(), noteInfo);
            });
        }

        System.out.println("Verfügbare Studenten:");
        kurs.getStudienprogramm().getStudenten().stream().sorted(Comparator.comparing(Student::getStudentId)).forEach(System.out::println);

        System.out.print("Studenten ändern im Format '+ID,-ID,+ID' (z.B. '+1,-5,+4' oder leer für keine Änderung): ");
        String input = scanner.nextLine().trim();

        if (!input.isEmpty()) {
            String[] changes = input.split(",");
            for (String change : changes) {
                change = change.trim();
                if (change.length() < 2) continue;

                char operation = change.charAt(0);
                try {
                    int studentId = Integer.parseInt(change.substring(1));
                    Student student = studentDao.findById(studentId);

                    if (student == null) {
                        System.out.println("Student mit ID " + studentId + " nicht gefunden.");
                        continue;
                    }

                    Optional<Besucht> besuchtOptional = kurs.getTeilnehmendeStudenten().stream().filter(b -> b.getStudent().getStudentId() == studentId).findFirst();

                    if (operation == '+') {
                        if (besuchtOptional.isPresent()) {
                            System.out.printf("Student %s %s ist bereits eingeschrieben.%n", student.getVorname(), student.getNachname());
                        } else {
                            Besucht besucht = new Besucht(kursId, studentId).student(student).kurs(kurs);
                            if (besuchtDao.save(besucht) != null) {
                                System.out.printf("Student %s %s wurde erfolgreich zum Kurs hinzugefügt.%n", student.getVorname(), student.getNachname());
                            } else {
                                System.out.println("Fehler beim Hinzufügen des Studenten zum Kurs.");
                            }
                        }
                    } else if (operation == '-') {
                        if (besuchtOptional.isEmpty()) {
                            System.out.printf("Student %s %s ist nicht eingeschrieben.%n", student.getVorname(), student.getNachname());
                        } else {
                            Besucht besucht = besuchtOptional.get();
                            if (besucht.getNote() != null) {
                                System.out.printf("Student %s %s hat bereits eine Note. Entfernen nicht möglich.%n", student.getVorname(), student.getNachname());
                            } else if (besuchtDao.deleteById(besucht.getId())) {
                                System.out.printf("Student %s %s wurde erfolgreich vom Kurs entfernt.%n", student.getVorname(), student.getNachname());
                            } else {
                                System.out.println("Fehler beim Entfernen des Studenten vom Kurs.");
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige ID in: " + change);
                }
            }
        }
    }

    public void pruefungKursZuweisung() {
        System.out.println("Verfügbare Kurse:");
        List<Kurs> kurse = kursDao.findAll();
        kurse.forEach(System.out::println);

        if (kurse.isEmpty()) {
            System.out.println("Keine Kurse verfügbar.");
            return;
        }

        System.out.print("Wählen Sie einen Kurs (ID): ");
        int kursId = readInt(scanner);
        Kurs kurs = kursDao.findById(kursId);

        if (kurs == null) {
            System.out.println("Kein Kurs mit dieser ID gefunden.");
            return;
        }

        System.out.println("Aktuelle Prüfungszuordnungen für Kurs: " + kurs.getBezeichnung());
        List<GehoertZuPruefung> zuordnungen = gehoertZuPruefungDao.findAll().stream().filter(zuordnung -> zuordnung.getKurs().getKursId() == kursId).toList();
        if (zuordnungen.isEmpty()) {
            System.out.println("Keine Prüfungen zugeordnet.");
        } else {
            zuordnungen.stream().sorted(Comparator.comparing(GehoertZuPruefung::getId)).forEach(zuordnung -> {
                Pruefung pruefung = zuordnung.getPruefung();
                System.out.printf("  - %d: %s (Anteil an Gesamtnote: %d%%)%n", pruefung.getPruefungsId(), pruefung.getBezeichnung(), zuordnung.getAnteilGesamtnoteInProzent());
            });
        }

        System.out.println("Verfügbare Prüfungen:");
        List<Pruefung> pruefungen = pruefungDao.findAll();
        pruefungen.forEach(System.out::println);

        if (pruefungen.isEmpty()) {
            System.out.println("Keine Prüfungen verfügbar.");
            return;
        }

        System.out.print("Änderungen im Format '+ID:Anteil,-ID,=ID:Anteil' (z.B. '+1:50,-5,=4:30' oder leer für keine Änderung): ");
        String input = scanner.nextLine().trim();

        if (!input.isEmpty()) {
            String[] changes = input.split(",");
            for (String change : changes) {
                change = change.trim();
                if (change.length() < 2) continue;

                char operation = change.charAt(0);
                try {
                    String[] parts = change.substring(1).split(":");
                    int pruefungId = Integer.parseInt(parts[0]);
                    Pruefung pruefung = pruefungDao.findById(pruefungId);

                    if (pruefung == null) {
                        System.out.println("Prüfung mit ID " + pruefungId + " nicht gefunden.");
                        continue;
                    }

                    boolean existiert = zuordnungen.stream().anyMatch(z -> z.getPruefung().getPruefungsId() == pruefungId);

                    if (operation == '+') {
                        if (existiert) {
                            System.out.println("Prüfung " + pruefung.getBezeichnung() + " ist bereits zugeordnet.");
                        } else {

                            Integer anteil = null;
                            if (parts.length > 1) {
                                try {
                                    anteil = Integer.parseInt(parts[1]);
                                    System.out.println("Anteil an Gesamtnote auf " + anteil + "% gesetzt.");
                                } catch (NumberFormatException e) {
                                    System.out.println("Ungültiger Anteil: " + parts[1] + ".");
                                }
                            }

                            gehoertZuPruefungDao.save(new GehoertZuPruefung(kursId, pruefungId).kurs(kurs).pruefung(pruefung).anteilGesamtnoteInProzent(anteil));
                            System.out.println("Prüfung " + pruefung.getBezeichnung() + " wurde hinzugefügt.");
                        }
                    } else if (operation == '=') {
                        if (existiert) {
                            GehoertZuPruefung zuordnungToUpdate = zuordnungen.stream().filter(z -> z.getPruefung().getPruefungsId() == pruefungId).findFirst().orElse(null);

                            if (zuordnungToUpdate != null && parts.length > 1) {
                                try {
                                    int anteil = Integer.parseInt(parts[1]);
                                    zuordnungToUpdate.setAnteilGesamtnoteInProzent(anteil);
                                    gehoertZuPruefungDao.update(zuordnungToUpdate);
                                    System.out.println("Anteil für Prüfung " + pruefung.getBezeichnung() + " auf " + anteil + "% aktualisiert.");
                                } catch (NumberFormatException e) {
                                    System.out.println("Ungültiger Anteil: " + parts[1] + ". Keine Änderung vorgenommen.");
                                }
                            } else {
                                System.out.println("Für die Aktualisierung muss ein Anteil angegeben werden (=ID:Anteil).");
                            }
                        } else {
                            System.out.println("Prüfung " + pruefung.getBezeichnung() + " ist nicht zugeordnet und kann nicht aktualisiert werden.");
                        }
                    } else if (operation == '-') {
                        if (existiert) {
                            GehoertZuPruefung zuordnungToDelete = zuordnungen.stream().filter(z -> z.getPruefung().getPruefungsId() == pruefungId).findFirst().orElse(null);

                            if (zuordnungToDelete != null) {
                                long anzahlZuordnungen = pruefung.getZugehoerigeKurse().size();
                                if (anzahlZuordnungen <= 1) {
                                    System.out.printf("Die Kurszuweisung zur Prüfung '%s' kann nicht entfernt werden, da sie nur diesem Kurs zugeordnet ist. Eine Prüfung muss mindestens einem Kurs zugeordnet sein.", pruefung.getBezeichnung());
                                } else {
                                    gehoertZuPruefungDao.deleteById(zuordnungToDelete.getId());
                                    System.out.println("Prüfung " + pruefung.getBezeichnung() + " wurde entfernt.");
                                }
                            }
                        } else {
                            System.out.println("Prüfung " + pruefung.getBezeichnung() + " ist nicht zugeordnet.");
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ungültige Eingabe: " + change);
                }
            }
        }
    }
}
