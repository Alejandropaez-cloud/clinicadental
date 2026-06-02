package controllers.controladores;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import util.SharedEntityManagerFactory;

import models.modelos.entidades.CitaTratamiento;

/**
 * Controlador CRUD para la entidad CitaTratamiento (tabla intermedia).
 * 
 * Ahora usa un @Id @GeneratedValue normal, así que findById y delete
 * reciben un Integer (como el resto de controladores), en lugar de
 * un objeto CitaTratamientoPK.
 */
public class CitaTratamientoController {

    private final EntityManagerFactory emf;

    public CitaTratamientoController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo registro en Cita_Tratamiento.
     * @param citaTratamiento El registro a crear.
     */
    public void create(CitaTratamiento citaTratamiento) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(citaTratamiento);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear el detalle de cita-tratamiento", ex);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Busca un registro por su ID.
     * @param id El id del registro.
     * @return El registro encontrado o null.
     */
    public CitaTratamiento findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(CitaTratamiento.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Obtiene todos los registros de Cita_Tratamiento.
     * @return Lista de asociaciones cita-tratamiento.
     */
    public List<CitaTratamiento> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("CitaTratamiento.findAll", CitaTratamiento.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * UPDATE - Actualiza un registro existente.
     * @param citaTratamiento El registro con los datos actualizados.
     */
    public void update(CitaTratamiento citaTratamiento) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(citaTratamiento);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el detalle de cita-tratamiento", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE - Elimina un registro por su ID.
     * @param id El ID del registro a eliminar.
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            CitaTratamiento citaTratamiento = em.find(CitaTratamiento.class, id);
            if (citaTratamiento != null) {
                em.remove(citaTratamiento);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar el detalle de cita-tratamiento", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE ALL - Elimina todos los registros de Cita_Tratamiento
     * y reinicia el AUTO_INCREMENT.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM Cita_Tratamiento").executeUpdate();
            em.createNativeQuery("ALTER TABLE clinica_dental.Cita_Tratamiento AUTO_INCREMENT = 1").executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar todos los registros de cita-tratamiento", ex);
        } finally {
            em.close();
        }
    }

}
