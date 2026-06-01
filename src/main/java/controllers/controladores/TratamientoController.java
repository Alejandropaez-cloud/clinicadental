package controllers.controladores;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import util.SharedEntityManagerFactory;

import models.modelos.entidades.Tratamiento;

/**
 * Controlador CRUD para la entidad Tratamiento.
 */
public class TratamientoController {

    private final EntityManagerFactory emf;

    public TratamientoController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo tratamiento.
     * @param tratamiento El tratamiento a crear.
     */
    public void create(Tratamiento tratamiento) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(tratamiento);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear el tratamiento", ex);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Busca un tratamiento por su ID.
     * @param id El codTratamiento.
     * @return El tratamiento encontrado o null.
     */
    public Tratamiento findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tratamiento.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Obtiene todos los tratamientos.
     * @return Lista de tratamientos.
     */
    public List<Tratamiento> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Tratamiento.findAll", Tratamiento.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * UPDATE - Actualiza un tratamiento existente.
     * @param tratamiento El tratamiento con los datos actualizados.
     */
    public void update(Tratamiento tratamiento) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(tratamiento);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el tratamiento", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE - Elimina un tratamiento por su ID.
     * @param id El ID del tratamiento a eliminar.
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Tratamiento tratamiento = em.find(Tratamiento.class, id);
            if (tratamiento != null) {
                em.remove(tratamiento);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar el tratamiento", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE ALL - Elimina todos los tratamientos y reinicia el AUTO_INCREMENT.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM Cita_Tratamiento").executeUpdate();
            em.createNativeQuery("DELETE FROM Tratamiento").executeUpdate();
            em.createNativeQuery("ALTER TABLE clinica_dental.Tratamiento AUTO_INCREMENT = 1").executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar todos los tratamientos", ex);
        } finally {
            em.close();
        }
    }

}
