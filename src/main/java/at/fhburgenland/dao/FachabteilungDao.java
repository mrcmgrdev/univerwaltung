package at.fhburgenland.dao;

import at.fhburgenland.model.Fachabteilung;
import jakarta.persistence.EntityManagerFactory;

public class FachabteilungDao extends GenericDao<Fachabteilung, Integer> {
    public FachabteilungDao(EntityManagerFactory emf) {
        super(emf, Fachabteilung.class);
    }
}
