package at.fhburgenland.dao;

import jakarta.persistence.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public abstract class GenericDao<T, ID> implements Crud<T, ID> {
    protected final EntityManagerFactory emf;
    protected final Class<T> entityClass;

    protected GenericDao(EntityManagerFactory emf, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.emf = emf;
    }

    @Override
    public List<T> findAll() {
        List<T> result = Collections.emptyList();
        EntityManager em = null;
        EntityTransaction tx = null;
        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            String query = String.format("SELECT e FROM %s e", entityClass.getSimpleName());
            result = em.createQuery(query, entityClass).getResultList();
            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }

    @Override
    public T findById(ID id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(entityClass, id);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public T save(T entity) {
        EntityManager em = null;
        EntityTransaction tx = null;
        T managedEntity;
        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            managedEntity = em.merge(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return managedEntity;
    }

    @Override
    public T update(T entity) {
        EntityManager em = null;
        EntityTransaction tx = null;
        T updatedEntity;
        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            updatedEntity = em.merge(entity);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return updatedEntity;
    }

    @Override
    public boolean deleteById(ID id) {
        EntityManager em = null;
        EntityTransaction tx = null;
        boolean deleted = false;
        try {
            em = emf.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
                deleted = true;
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            deleted = false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return deleted;
    }

    public List<Tuple> query(String nativeSqlString, Map<String, Object> parameters) {
        List<Tuple> result = Collections.emptyList();
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            Query query = em.createNativeQuery(nativeSqlString, Tuple.class);
            if (parameters != null) {
                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
            result = query.getResultList();
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
        return result;
    }
}