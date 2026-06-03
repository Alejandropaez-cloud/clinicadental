package controllers.controladores;

import java.util.List; // Importa List para retornar colecciones

import javax.persistence.EntityManager; // Gestiona la conexión con la BD
import javax.persistence.EntityManagerFactory; // Factory para crear EntityManager
import javax.persistence.EntityTransaction; // Controla las transacciones
import util.SharedEntityManagerFactory; // Factoría compartida de EntityManager

import models.modelos.entidades.HistorialClinico; // Importa la entidad HistorialClinico

/**
 * Controlador CRUD para la entidad HistorialClinico.
 * El historial clínico se crea automáticamente junto con el paciente
 * gracias a cascade = CascadeType.ALL en la relación, pero este controlador
 * proporciona operaciones directas si se necesitan modificaciones independientes.
 */
public class HistorialClinicoController {

    // EntityManagerFactory para crear instancias de EntityManager
    private final EntityManagerFactory emf;

    // Constructor: obtiene la instancia compartida de EntityManagerFactory
    public HistorialClinicoController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    // Método auxiliar: crea un nuevo EntityManager para comunicarse con la BD
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo historial clínico en la base de datos.
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
            throw new RuntimeException("Error al crear el historial clínico", ex);
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
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * READ - Obtiene todos los historiales clínicos de la base de datos.
     * @return Lista de todos los historiales
     */
    public List<HistorialClinico> findAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Ejecuta la NamedQuery "HistorialClinico.findAll" definida en la entidad
            return em.createNamedQuery("HistorialClinico.findAll", HistorialClinico.class).getResultList();
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * UPDATE - Actualiza un historial existente en la base de datos.
     * @param historialClinico El historial con los datos actualizados
     */
    public void update(HistorialClinico historialClinico) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacción
        try {
            tx.begin(); // Inicia la transacción
            em.merge(historialClinico); // Actualiza el registro en la BD
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacción
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al actualizar el historial clínico", ex);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * DELETE - Elimina un historial por su ID.
     * @param id El ID del historial a eliminar
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacción
        try {
            tx.begin(); // Inicia la transacción
            HistorialClinico historialClinico = em.find(HistorialClinico.class, id); // Busca el historial
            if (historialClinico != null) {
                em.remove(historialClinico); // Elimina el registro de la BD
            }
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacción
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al eliminar el historial clínico", ex);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * DELETE ALL - Elimina todos los historiales clínicos.
     * Finalmente reinicia el AUTO_INCREMENT de la tabla.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacción
        try {
            tx.begin(); // Inicia la transacción
            // Elimina todos los historiales clínicos
            em.createNativeQuery("DELETE FROM Historial_Clinico").executeUpdate();
            // Reinicia el AUTO_INCREMENT a 1
            em.createNativeQuery("ALTER TABLE clinica_dental.Historial_Clinico AUTO_INCREMENT = 1").executeUpdate();
            tx.commit(); // Confirma todos los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacción
            if (tx.isActive()) {
                tx.rollback(); // Deshace todos los cambios
            }
            throw new RuntimeException("Error al eliminar todos los historiales clínicos", ex);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

}
