package at.fhburgenland.service;

import at.fhburgenland.dao.AbsolviertDao;
import at.fhburgenland.dao.Crud;
import at.fhburgenland.dao.ProfessorDao;
import at.fhburgenland.model.Professor;
import jakarta.persistence.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static at.fhburgenland.helper.ScannerHelper.readInt;

public class SpecialQueryService {
    private final Scanner scanner;

    private final AbsolviertDao absolviertDao;
    private final ProfessorDao professorDao;

    public SpecialQueryService(Scanner scanner, AbsolviertDao absolviertDao, ProfessorDao professorDao) {
        this.scanner = scanner;
        this.absolviertDao = absolviertDao;
        this.professorDao = professorDao;
    }

    public void showKurseStatistiken() {
        String sql = """
                SELECT k.Bezeichnung                                            AS Kursname,
                       p.Bezeichnung                                            AS Pruefung,
                       pt.Bezeichnung                                           AS Pruefungstyp,
                       MIN(a.Versuch)                                           AS Min_Versuch,
                       MAX(a.Versuch)                                           AS Max_Versuch,
                       ROUND(AVG(a.Versuch), 2)                                 AS Avg_Versuch,
                       ROUND(AVG(CASE WHEN a.Versuch = 1 THEN a.NoteID END), 2) AS Avg_Note_Versuch_1,
                       ROUND(AVG(CASE WHEN a.Versuch = 2 THEN a.NoteID END), 2) AS Avg_Note_Versuch_2,
                       ROUND(AVG(CASE WHEN a.Versuch = 3 THEN a.NoteID END), 2) AS Avg_Note_Versuch_3
                FROM Absolviert a
                         JOIN Pruefung p ON a.PruefungsID = p.PruefungsID
                         JOIN Pruefungstyp pt ON p.TypID = pt.TypID
                         JOIN GehoertZuPruefung gzp ON p.PruefungsID = gzp.PruefungsID
                         JOIN Kurs k ON gzp.KursID = k.KursID
                GROUP BY 1, 2, 3
                ORDER BY 1, 2;
                """;

        List<Tuple> results = absolviertDao.query(sql, null);

        if (results.isEmpty()) {
            System.out.println("Keine Daten für die Kursstatistiken gefunden.");
            return;
        }

        System.out.println("\n--- Kursstatistiken ---");
        System.out.printf("%-50s | %-50s | %-30s | %-5s | %-5s | %-10s | %-10s | %-10s | %-10s%n", "Kurs", "Prüfung", "Typ", "MinV", "MaxV", "AvgV", "AvgN V1", "AvgN V2", "AvgN V3");
        System.out.println(new String(new char[200]).replace('\0', '-'));

        for (Tuple row : results) {
            System.out.printf("%-50s | %-50s | %-30s | %-5s | %-5s | %-10s | %-10s | %-10s | %-10s%n", row.get("Kursname", String.class), row.get("Pruefung", String.class), row.get("Pruefungstyp", String.class), row.get("Min_Versuch") != null ? row.get("Min_Versuch", Number.class).intValue() : "N/A", row.get("Max_Versuch") != null ? row.get("Max_Versuch", Number.class).intValue() : "N/A", row.get("Avg_Versuch") != null ? row.get("Avg_Versuch", Number.class).doubleValue() : "N/A", row.get("Avg_Note_Versuch_1") != null ? row.get("Avg_Note_Versuch_1", Number.class).doubleValue() : "N/A", row.get("Avg_Note_Versuch_2") != null ? row.get("Avg_Note_Versuch_2", Number.class).doubleValue() : "N/A", row.get("Avg_Note_Versuch_3") != null ? row.get("Avg_Note_Versuch_3", Number.class).doubleValue() : "N/A");
        }
        System.out.println("-----------------------\n");
    }

    public void showOffeneNotenvergabe() {
        System.out.print("Id des Professors: ");
        Professor professor = professorDao.findById(readInt(scanner));

        if (professor == null) {
            System.out.println("Kein Professor mit dieser Id gefunden.");
            return;
        }

        String sql = """
                    SELECT S.Vorname       AS Student_Vorname,
                           S.Nachname      AS Student_Nachname,
                           S.Matrikelnummer,
                           SP_Student.Name AS Student_Studiengang,
                           K.Bezeichnung   AS Kurs_Bezeichnung
                    FROM Professor P
                             JOIN Unterrichtet U ON P.ProfessorID = U.ProfessorID
                             JOIN Kurs K ON U.KursID = K.KursID
                             JOIN Besucht B ON K.KursID = B.KursID
                             JOIN Student S ON B.StudentID = S.StudentID
                             JOIN Waehlt W ON S.StudentID = W.StudentID
                             JOIN Studienprogramm SP_Student ON W.StudienprogrammID = SP_Student.StudienprogrammID
                    WHERE P.ProfessorID = :profId
                      AND B.NoteID IS NULL
                    ORDER BY S.Vorname, S.Nachname, SP_Student.Name, K.Bezeichnung
                """;

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("profId", professor.getProfessorId());

        List<Tuple> results = professorDao.query(sql, parameters);

        if (results.isEmpty()) {
            System.out.println("Keine Studenten gefunden, die von Professor " + professor.getVorname() + " " + professor.getNachname() + " benotet werden müssen.");
            return;
        }

        System.out.println("\n--- Studenten, die von Professor " + professor.getVorname() + " " + professor.getNachname() + " benotet werden müssen ---");
        System.out.printf("%-20s | %-20s | %-15s | %-30s | %-30s%n", "Vorname", "Nachname", "Matrikelnr.", "Studiengang", "Kurs");
        System.out.println(new String(new char[120]).replace('\0', '-'));

        for (Tuple row : results) {
            System.out.printf("%-20s | %-20s | %-15s | %-30s | %-30s%n", row.get("Student_Vorname", String.class), row.get("Student_Nachname", String.class), row.get("Matrikelnummer", String.class), row.get("Student_Studiengang", String.class), row.get("Kurs_Bezeichnung", String.class));
        }
        System.out.println("-------------------------------------------------------------------\n");
    }
}
