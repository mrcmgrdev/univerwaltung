package at.fhburgenland.dao;

import at.fhburgenland.model.Note;
import jakarta.persistence.EntityManagerFactory;

public class NoteDao extends GenericDao<Note, Integer> {
    public NoteDao(EntityManagerFactory emf) {
        super(emf, Note.class);
    }
}
