package at.fhburgenland.dao;

import at.fhburgenland.model.Pruefung;
import jakarta.persistence.EntityManagerFactory;

public class PruefungDao extends GenericDao<Pruefung, Integer> {
    public PruefungDao(EntityManagerFactory emf) {
        super(emf, Pruefung.class);
    }
}
