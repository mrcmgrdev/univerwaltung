package at.fhburgenland.dao;

import at.fhburgenland.model.Studienprogramm;
import jakarta.persistence.EntityManagerFactory;

public class StudienprogrammDao extends GenericDao<Studienprogramm, Integer> {
    public StudienprogrammDao(EntityManagerFactory emf) {
        super(emf, Studienprogramm.class);
    }
}
