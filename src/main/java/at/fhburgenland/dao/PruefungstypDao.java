package at.fhburgenland.dao;

import at.fhburgenland.model.Pruefungstyp;
import jakarta.persistence.EntityManagerFactory;

public class PruefungstypDao extends GenericDao<Pruefungstyp,Integer> {
    public PruefungstypDao(EntityManagerFactory emf) {
        super(emf, Pruefungstyp.class);
    }
}
