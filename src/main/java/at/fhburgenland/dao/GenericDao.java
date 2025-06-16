package at.fhburgenland.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.List;

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
        try (EntityManager em = emf.createEntityManager()) {
            String query = String.format("SELECT e FROM %s e", entityClass.getSimpleName());
            result = em.createQuery(query, entityClass).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    @Override
    public T findById(ID id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(entityClass, id);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public T save(T entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public T update(T entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T updated = em.merge(entity);
            em.getTransaction().commit();
            return updated;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void delete(T entity) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean deleteById(ID id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
            return entity != null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public List<T> query(String query) {
        List<T> result = Collections.emptyList();
        try (EntityManager em = emf.createEntityManager()) {
            result = em.createQuery(query, entityClass).getResultList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
