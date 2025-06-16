package at.fhburgenland.dao;

import at.fhburgenland.model.Besucht;
import at.fhburgenland.model.BesuchtPK;
import jakarta.persistence.EntityManagerFactory;

public class BesuchtDao extends GenericDao<Besucht, BesuchtPK> {
    public BesuchtDao(EntityManagerFactory emf) {
        super(emf, Besucht.class);
    }
}
