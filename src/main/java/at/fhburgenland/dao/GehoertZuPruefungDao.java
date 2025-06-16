package at.fhburgenland.dao;

import at.fhburgenland.model.GehoertZuPruefung;
import at.fhburgenland.model.GehoertZuPruefungPK;
import jakarta.persistence.EntityManagerFactory;

public class GehoertZuPruefungDao extends GenericDao<GehoertZuPruefung, GehoertZuPruefungPK> {
    public GehoertZuPruefungDao(EntityManagerFactory emf) {
        super(emf, GehoertZuPruefung.class);
    }
}
