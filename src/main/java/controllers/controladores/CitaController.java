package controllers.controladores;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import util.SharedEntityManagerFactory;

import models.modelos.entidades.Cita;

/**
 * Controlador CRUD para la entidad Cita.
 * Sigue el mismo patrón que los demás controladores.
 * Cada uno habla con la BD a través del EntityManager
 */
public class CitaController {

    private final EntityManagerFactory emf;

    public CitaController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta una nueva cita.
     * Al persistir una Cita, si tiene CitaTratamiento asociados con
     * cascade PERSIST, también se guardarán automáticamente.
     */
    public void create(Cita cita) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(cita);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear la cita", ex);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Busca una cita por su ID.
     * @param id El codCita de la cita.
     * @return La cita encontrada o null.
     */
    public Cita findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cita.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Obtiene todas las citas.
     * @return Lista de citas.
     */
    public List<Cita> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Cita.findAll", Cita.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * UPDATE - Actualiza una cita existente.
     * @param cita La cita con los datos actualizados.
     */
    public void update(Cita cita) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(cita);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar la cita", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE - Elimina una cita por su ID.
     * @param id El ID de la cita a eliminar.
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Cita cita = em.find(Cita.class, id);
            if (cita != null) {
                em.remove(cita);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar la cita", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE ALL - Elimina todas las citas y reinicia el AUTO_INCREMENT.
     * Primero borra Cita_Tratamiento (FK a Cita), luego Cita.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM Cita_Tratamiento").executeUpdate();
            em.createNativeQuery("DELETE FROM Cita").executeUpdate();
            em.createNativeQuery("ALTER TABLE clinica_dental.Cita AUTO_INCREMENT = 1").executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar todas las citas", ex);
        } finally {
            em.close();
        }
    }

}
