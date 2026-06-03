package controllers.controladores;

import java.util.List; // Importa List para retornar colecciones

import javax.persistence.EntityManager; // Gestiona la conexiÃƒÂ³n con la BD
import javax.persistence.EntityManagerFactory; // Factory para crear EntityManager
import javax.persistence.EntityTransaction; // Controla las transacciones
import util.SharedEntityManagerFactory; // FactorÃƒÂ­a compartida de EntityManager

import models.modelos.entidades.CitaTratamiento; // Importa la entidad CitaTratamiento

/**
 * Controlador CRUD para la entidad CitaTratamiento (tabla intermedia/puente).
 * Esta es la tabla que implementa la relaciÃƒÂ³n N:M entre Cita y Tratamiento.
 * Ahora usa un @Id @GeneratedValue normal, asÃƒÂ­ que encontrar/eliminar
 * usan Integer (como el resto de controladores).
 */
public class CitaTratamientoController {

    // EntityManagerFactory para crear instancias de EntityManager
    private final EntityManagerFactory emf;

    // Constructor: obtiene la instancia compartida de EntityManagerFactory
    public CitaTratamientoController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    // MÃƒÂ©todo auxiliar: crea un nuevo EntityManager para comunicarse con la BD
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo registro en Cita_Tratamiento.
     * Esto asocia un tratamiento a una cita con una cantidad especificada.
     * @param citaTratamiento El registro a crear
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
     * READ - Busca un registro de CitaTratamiento por su ID.
     * @param id El ID del registro en la tabla Cita_Tratamiento
     * @return El registro encontrado o null si no existe
     */
    public CitaTratamiento findById(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Busca el registro en la BD por su clave primaria
            return em.find(CitaTratamiento.class, id);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * READ - Obtiene todos los registros de CitaTratamiento.
     * @return Lista de todas las asociaciones cita-tratamiento
     */
    public List<CitaTratamiento> findAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Ejecuta la NamedQuery "CitaTratamiento.findAll" definida en la entidad
            return em.createNamedQuery("CitaTratamiento.findAll", CitaTratamiento.class).getResultList();
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * UPDATE - Actualiza un registro existente en la tabla Cita_Tratamiento.
     * Se usa para cambiar la cantidad de tratamiento en una cita.
     * @param citaTratamiento El registro con los datos actualizados
     */
    public void update(CitaTratamiento citaTratamiento) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            em.merge(citaTratamiento); // Actualiza el registro en la BD
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al actualizar el detalle de cita-tratamiento", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * DELETE - Elimina un registro de CitaTratamiento por su ID.
     * @param id El ID del registro a eliminar
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            CitaTratamiento citaTratamiento = em.find(CitaTratamiento.class, id); // Busca el registro
            if (citaTratamiento != null) {
                em.remove(citaTratamiento); // Elimina el registro de la BD
            }
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al eliminar el detalle de cita-tratamiento", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * DELETE ALL - Elimina todos los registros de Cita_Tratamiento.
     * Finalmente reinicia el AUTO_INCREMENT de la tabla.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            // Elimina todos los registros de la tabla Cita_Tratamiento
            em.createNativeQuery("DELETE FROM Cita_Tratamiento").executeUpdate();
            // Reinicia el AUTO_INCREMENT a 1
            em.createNativeQuery("ALTER TABLE clinica_dental.Cita_Tratamiento AUTO_INCREMENT = 1").executeUpdate();
            tx.commit(); // Confirma todos los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace todos los cambios
            }
            throw new RuntimeException("Error al eliminar todos los registros de cita-tratamiento", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

}
