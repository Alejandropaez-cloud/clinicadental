package controllers.controladores;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import util.SharedEntityManagerFactory;

import models.modelos.entidades.HistorialClinico;

/**
 * Controlador CRUD para la entidad HistorialClinico.
 * Nota: Normalmente el historial se crea junto con el paciente
 * (gracias a cascade = CascadeType.ALL en la relación), pero
 * igualmente tenemos operaciones directas por si se necesitan.
 */
public class HistorialClinicoController {

    private final EntityManagerFactory emf;

    public HistorialClinicoController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo historial clínico.
     * @param historialClinico El historial a crear.
     */
    public void create(HistorialClinico historialClinico) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(historialClinico);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear el historial clínico", ex);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Busca un historial por su ID.
     * @param id El codHistorial.
     * @return El historial encontrado o null.
     */
    public HistorialClinico findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(HistorialClinico.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Obtiene todos los historiales clínicos.
     * @return Lista de historiales.
     */
    public List<HistorialClinico> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("HistorialClinico.findAll", HistorialClinico.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * UPDATE - Actualiza un historial existente.
     * @param historialClinico El historial con los datos actualizados.
     */
    public void update(HistorialClinico historialClinico) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(historialClinico);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el historial clínico", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE - Elimina un historial por su ID.
     * @param id El ID del historial a eliminar.
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            HistorialClinico historialClinico = em.find(HistorialClinico.class, id);
            if (historialClinico != null) {
                em.remove(historialClinico);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar el historial clínico", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE ALL - Elimina todos los historiales y reinicia el AUTO_INCREMENT.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM Historial_Clinico").executeUpdate();
            em.createNativeQuery("ALTER TABLE clinica_dental.Historial_Clinico AUTO_INCREMENT = 1").executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar todos los historiales clínicos", ex);
        } finally {
            em.close();
        }
    }

}
