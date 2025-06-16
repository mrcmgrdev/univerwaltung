package at.fhburgenland.dao;

import at.fhburgenland.model.Student;
import jakarta.persistence.EntityManagerFactory;

public class StudentDao extends GenericDao<Student, Integer> {
    public StudentDao(EntityManagerFactory emf) {
        super(emf, Student.class);
    }
}
