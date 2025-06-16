package at.fhburgenland.dao;

import at.fhburgenland.model.Kurs;
import jakarta.persistence.EntityManagerFactory;

public class KursDao extends GenericDao<Kurs, Integer> {
    public KursDao(EntityManagerFactory emf) {
        super(emf, Kurs.class);
    }
}
