package controllers.controladores;

import java.util.List; // Importa List para retornar colecciones

import javax.persistence.EntityManager; // Gestiona la conexiÃƒÂ³n con la BD
import javax.persistence.EntityManagerFactory; // Factory para crear EntityManager
import javax.persistence.EntityTransaction; // Controla las transacciones
import util.SharedEntityManagerFactory; // FactorÃƒÂ­a compartida de EntityManager

import models.modelos.entidades.HistorialClinico; // Importa la entidad HistorialClinico

/**
 * Controlador CRUD para la entidad HistorialClinico.
 * El historial clÃƒÂ­nico se crea automÃƒÂ¡ticamente junto con el paciente
 * gracias a cascade = CascadeType.ALL en la relaciÃƒÂ³n, pero este controlador
 * proporciona operaciones directas si se necesitan modificaciones independientes.
 */
public class HistorialClinicoController {

    // EntityManagerFactory para crear instancias de EntityManager
    private final EntityManagerFactory emf;

    // Constructor: obtiene la instancia compartida de EntityManagerFactory
    public HistorialClinicoController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    // MÃƒÂ©todo auxiliar: crea un nuevo EntityManager para comunicarse con la BD
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo historial clÃƒÂ­nico en la base de datos.
     * @param historialClinico El historial a crear
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
            throw new RuntimeException("Error al crear el historial clÃƒÂ­nico", ex);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Busca un historial por su ID.
     * @param id El codHistorial a buscar
     * @return El historial encontrado o null si no existe
     */
    public HistorialClinico findById(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Busca el historial en la BD por su clave primaria
            return em.find(HistorialClinico.class, id);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * READ - Obtiene todos los historiales clÃƒÂ­nicos de la base de datos.
     * @return Lista de todos los historiales
     */
    public List<HistorialClinico> findAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Ejecuta la NamedQuery "HistorialClinico.findAll" definida en la entidad
            return em.createNamedQuery("HistorialClinico.findAll", HistorialClinico.class).getResultList();
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * UPDATE - Actualiza un historial existente en la base de datos.
     * @param historialClinico El historial con los datos actualizados
     */
    public void update(HistorialClinico historialClinico) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            em.merge(historialClinico); // Actualiza el registro en la BD
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al actualizar el historial clÃƒÂ­nico", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * DELETE - Elimina un historial por su ID.
     * @param id El ID del historial a eliminar
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            HistorialClinico historialClinico = em.find(HistorialClinico.class, id); // Busca el historial
            if (historialClinico != null) {
                em.remove(historialClinico); // Elimina el registro de la BD
            }
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al eliminar el historial clÃƒÂ­nico", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * DELETE ALL - Elimina todos los historiales clÃƒÂ­nicos.
     * Finalmente reinicia el AUTO_INCREMENT de la tabla.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            // Elimina todos los historiales clÃƒÂ­nicos
            em.createNativeQuery("DELETE FROM Historial_Clinico").executeUpdate();
            // Reinicia el AUTO_INCREMENT a 1
            em.createNativeQuery("ALTER TABLE clinica_dental.Historial_Clinico AUTO_INCREMENT = 1").executeUpdate();
            tx.commit(); // Confirma todos los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace todos los cambios
            }
            throw new RuntimeException("Error al eliminar todos los historiales clÃƒÂ­nicos", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

}
