CREATE TABLE Fachabteilung
(
    AbteilungsID SERIAL PRIMARY KEY,
    Name         VARCHAR(100) NOT NULL,
    Standort     VARCHAR(100) NOT NULL
);

CREATE TABLE Pruefungstyp
(
    TypID       SERIAL PRIMARY KEY,
    Bezeichnung VARCHAR(50) NOT NULL
);

CREATE TABLE Note
(
    NoteID      INT PRIMARY KEY,
    Bezeichnung VARCHAR(50) NOT NULL
);

CREATE TABLE Professor
(
    ProfessorID  SERIAL PRIMARY KEY,
    Vorname      VARCHAR(50) NOT NULL,
    Nachname     VARCHAR(50) NOT NULL,
    Email        VARCHAR(255) UNIQUE,
    AbteilungsID INT         NOT NULL,
    FOREIGN KEY (AbteilungsID) REFERENCES Fachabteilung (AbteilungsID)
);

CREATE TABLE Studienprogramm
(
    StudienprogrammID            SERIAL PRIMARY KEY,
    Name                         VARCHAR(100) NOT NULL,
    Abschluss                    VARCHAR(10)  NOT NULL,
    Regelstudienzeit_in_Semester INT          NOT NULL,
    ProfessorID                  INT          NOT NULL,
    FOREIGN KEY (ProfessorID) REFERENCES Professor (ProfessorID)
);

CREATE TABLE Kurs
(
    KursID            SERIAL PRIMARY KEY,
    Bezeichnung       VARCHAR(100) NOT NULL,
    Semester          INT          NOT NULL,
    ECTS              INT          NOT NULL,
    StudienprogrammID INT          NOT NULL,
    FOREIGN KEY (StudienprogrammID) REFERENCES Studienprogramm (StudienprogrammID)
);

CREATE TABLE Pruefung
(
    PruefungsID SERIAL PRIMARY KEY,
    Bezeichnung VARCHAR(100) NOT NULL,
    Datum       DATE,
    TypID       INT          NOT NULL,
    FOREIGN KEY (TypID) REFERENCES Pruefungstyp (TypID)
);

CREATE TABLE Student
(
    StudentID      SERIAL PRIMARY KEY,
    Vorname        VARCHAR(50)        NOT NULL,
    Nachname       VARCHAR(50)        NOT NULL,
    Matrikelnummer VARCHAR(20) UNIQUE NOT NULL,
    Email          VARCHAR(255) UNIQUE,
    Geburtsdatum   DATE
);

-- Zwischentabellen
CREATE TABLE Waehlt
(
    StudentID         INT,
    StudienprogrammID INT,
    PRIMARY KEY (StudentID, StudienprogrammID),
    FOREIGN KEY (StudentID) REFERENCES Student (StudentID),
    FOREIGN KEY (StudienprogrammID) REFERENCES Studienprogramm (StudienprogrammID)
);

CREATE TABLE Unterrichtet
(
    ProfessorID INT,
    KursID      INT,
    PRIMARY KEY (ProfessorID, KursID),
    FOREIGN KEY (ProfessorID) REFERENCES Professor (ProfessorID),
    FOREIGN KEY (KursID) REFERENCES Kurs (KursID)
);

CREATE TABLE Besucht
(
    StudentID INT,
    KursID    INT,
    NoteID    INT,
    PRIMARY KEY (StudentID, KursID),
    FOREIGN KEY (StudentID) REFERENCES Student (StudentID),
    FOREIGN KEY (KursID) REFERENCES Kurs (KursID),
    FOREIGN KEY (NoteID) REFERENCES Note (NoteID)
);

CREATE TABLE GehoertZuPruefung
(
    KursID                      INT,
    PruefungsID                 INT,
    AnteilGesamtnote_in_Prozent INT,
    PRIMARY KEY (KursID, PruefungsID),
    FOREIGN KEY (KursID) REFERENCES Kurs (KursID),
    FOREIGN KEY (PruefungsID) REFERENCES Pruefung (PruefungsID)
);

CREATE TABLE Absolviert
(
    StudentID   INT,
    PruefungsID INT,
    Versuch     INT,
    NoteID      INT,
    PRIMARY KEY (StudentID, PruefungsID, Versuch),
    FOREIGN KEY (StudentID) REFERENCES Student (StudentID),
    FOREIGN KEY (PruefungsID) REFERENCES Pruefung (PruefungsID),
    FOREIGN KEY (NoteID) REFERENCES Note (NoteID)
);

----- inserts

INSERT INTO Fachabteilung (Name, Standort)
VALUES ('Informatik', 'Pinkafeld'),
       ('Wirtschaft', 'Eisenstadt'),
       ('Medizin', 'Pinkafeld'),
       ('Energie', 'Eisenstadt'),
       ('Soziales', 'Pinkafeld');

INSERT INTO Pruefungstyp (Bezeichnung)
VALUES ('Klausur'),
       ('Seminararbeit'),
       ('Mündliche Prüfung');

INSERT INTO Note (NoteID, Bezeichnung)
VALUES (1, 'Sehr Gut'),
       (2, 'Gut'),
       (3, 'Befriedigend'),
       (4, 'Genügend'),
       (5, 'Nicht Genügend'),
       (6, 'Angerechnet');

INSERT INTO Professor (Vorname, Nachname, Email, AbteilungsID)
VALUES ('Julia', 'Keller', 'julia.keller@uni.at', 1),
       ('Peter', 'Schmidt', 'peter.schmidt@uni.at', 2),
       ('Manfred', 'Torbogen', 'm.torbogen@uni.at', 3),
       ('Herbert', 'Unger', 'herbert.unger@uni.at', 2),
       ('Hannelore', 'Grandits', 'hannelore.grandits@uni.at', 5),
       ('Gerwald', 'Pekarek', 'gerwald.pekarek@uni.at', 4),
       ('Maria', 'Dorn', 'maria.dorn@uni.at', 4),
       ('Romana', 'Steirer', 'romana.steirer@uni.at', 2);

INSERT INTO Studienprogramm (Name, Abschluss, Regelstudienzeit_in_Semester, ProfessorID)
VALUES ('Informatik', 'BSc', 6, 1),
       ('Wirtschaftsinformatik', 'BSc', 6, 2),
       ('Ergotherapie', 'BSc', 6, 3),
       ('Erneuerbare Energien im Bauwesen', 'MSc', 4, 6),
       ('Genderstudies', 'MSc', 4, 5);

INSERT INTO Kurs (Bezeichnung, Semester, ECTS, StudienprogrammID)
VALUES ('Datenbanken', 2, 6, 1),
       ('BWL Grundlagen', 1, 3, 2),
       ('Technische Mechanik 1', 1, 3, 4),
       ('Technische Mechanik 2', 2, 4, 4),
       ('Gesellschaft und Technik', 3, 6, 5),
       ('Ernährungslehre', 4, 2, 3),
       ('Anorganische Chemie', 2, 4, 3),
       ('Thermodynamik', 4, 6, 4),
       ('Anatomie 1', 2, 6, 3),
       ('Toxikologie 2', 5, 3, 3);

INSERT INTO Pruefung (Bezeichnung, Datum, TypID)
VALUES ('Datenbanken Klausur', '2024-06-01', 1),
       ('BWL Grundlagen mündlich', '2024-06-15', 2),
       ('Technische Mechanik 1 Klausur', '2024-06-29', 1),
       ('Technische Mechanik 2 Klausur', '2024-06-02', 1),
       ('Gesellschaft und Technik Projektarbeit', '2025-06-19', 3);

INSERT INTO Student (Vorname, Nachname, Matrikelnummer, Email, Geburtsdatum)
VALUES ('Lisa', 'Meier', '123451', 'lisa.meier@uni.at', '2001-05-21'),
       ('Max', 'Braun', '123452', 'max.braun@uni.at', '2000-12-11'),
       ('Tom', 'Bayer', '123453', 'tom.bayer@uni.at', '1999-12-21'),
       ('Anna', 'Halper', '123454', 'anna.halper@uni.at', '1997-01-31'),
       ('Nikolas', 'Gerber', '123455', 'nikolas.gerber@uni.at', '2002-05-20'),
       ('Daniel', 'Sander', '123456', 'daniel.sander@uni.at', '1998-08-24'),
       ('Anna', 'Müller', '123457', 'anna.mueler@uni.at', '2001-07-29'),
       ('Gert', 'Liebherr', '123458', 'gert.liebherr@uni.at', '2000-04-15'),
       ('David', 'Prangger', '123459', 'david.prangger@uni.at', '2004-03-23'),
       ('Markus', 'Stocker', '123460', 'markus.stocker@uni.at', '2001-02-20');

-- Zwischentabellen
INSERT INTO Waehlt (StudentID, StudienprogrammID)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 5),
       (5, 4),
       (6, 2),
       (7, 2),
       (8, 1),
       (9, 5),
       (10, 2);

INSERT INTO Unterrichtet (ProfessorID, KursID)
VALUES (1, 1),
       (2, 2),
       (6, 3),
       (6, 4),
       (5, 5),
       (3, 6),
       (3, 7),
       (6, 8),
       (3, 9),
       (3, 10);

INSERT INTO Besucht (StudentID, KursID, NoteID)
VALUES (1, 1, 6),
       (2, 2, 2),
       (3, 6, 4),
       (4, 5, 3),
       (5, 3, 1),
       (5, 4, 3),
       (6, 2, 4),
       (7, 2, 2),
       (8, 1, 1),
       (9, 5, 2),
       (10, 2, 2);

INSERT INTO GehoertZuPruefung (KursID, PruefungsID, AnteilGesamtnote_in_Prozent)
VALUES (1, 1, 40),
       (2, 2, 100),
       (3, 3, 10),
       (4, 4, 20),
       (5, 5, 65);

INSERT INTO Absolviert (StudentID, PruefungsID, NoteID, Versuch)
VALUES (1, 1, 1, 1),
       (2, 2, 2, 1),
       (3, 5, 2, 1),
       (4, 5, 1, 1),
       (5, 3, 4, 1),
       (5, 4, 2, 1),
       (6, 2, 3, 2),
       (7, 2, 2, 1),
       (8, 1, 4, 1),
       (9, 5, 1, 1),
       (10, 2, 3, 1);
