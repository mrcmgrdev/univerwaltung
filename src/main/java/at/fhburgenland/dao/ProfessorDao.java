package at.fhburgenland.dao;

import at.fhburgenland.model.Professor;
import jakarta.persistence.EntityManagerFactory;

public class ProfessorDao extends GenericDao<Professor, Integer> {
    public ProfessorDao(EntityManagerFactory emf) {
        super(emf, Professor.class);
    }
}
