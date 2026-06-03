package controllers.controladores;

import java.util.List; // Importa List para retornar colecciones

import javax.persistence.EntityManager; // Gestiona la conexiÃƒÂ³n con la BD
import javax.persistence.EntityManagerFactory; // Factory para crear EntityManager
import javax.persistence.EntityTransaction; // Controla las transacciones
import util.SharedEntityManagerFactory; // FactorÃƒÂ­a compartida de EntityManager

import models.modelos.entidades.Tratamiento; // Importa la entidad Tratamiento

/**
 * Controlador CRUD para la entidad Tratamiento (servicios dentales).
 * Implementa operaciones Create, Read, Update, Delete sobre tratamientos.
 * Los tratamientos pueden estar asociados a mÃƒÂºltiples citas (relaciÃƒÂ³n N:M).
 */
public class TratamientoController {

    // EntityManagerFactory para crear instancias de EntityManager
    private final EntityManagerFactory emf;

    // Constructor: obtiene la instancia compartida de EntityManagerFactory
    public TratamientoController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    // MÃƒÂ©todo auxiliar: crea un nuevo EntityManager para comunicarse con la BD
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo tratamiento en la base de datos.
     * @param tratamiento El tratamiento a crear
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
