package at.fhburgenland.dao;

import at.fhburgenland.model.Absolviert;
import at.fhburgenland.model.AbsolviertPK;
import jakarta.persistence.EntityManagerFactory;

public class AbsolviertDao extends GenericDao<Absolviert, AbsolviertPK> {
    public AbsolviertDao(EntityManagerFactory emf) {
        super(emf, Absolviert.class);
    }
}
