package controllers.controladores;

import java.util.List; // Importa List para retornar colecciones

import javax.persistence.EntityManager; // Gestiona la conexión con la BD
import javax.persistence.EntityManagerFactory; // Factory para crear EntityManager
import javax.persistence.EntityTransaction; // Controla las transacciones
import util.SharedEntityManagerFactory; // Factorí­a compartida de EntityManager

import models.modelos.entidades.Cita; // Importa la entidad Cita

/**
 * Controlador CRUD para la entidad Cita.
 * Implementa operaciones Create, Read, Update, Delete sobre citas.
 * Sigue el mismo patrón que los demás controladores: manejo de transacciones
 * y cierre de recursos garantizado en bloques finally.
 */
public class CitaController {

    // EntityManagerFactory para crear instancias de EntityManager
    private final EntityManagerFactory emf;

    // Constructor: obtiene la instancia compartida de EntityManagerFactory
    public CitaController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    // MÃƒÂ©todo auxiliar: crea un nuevo EntityManager para comunicarse con la BD
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta una nueva cita en la base de datos.
     * Al persistir una Cita, si tiene CitaTratamiento asociados con
     * cascade PERSIST, también se guardaron automáticamente.
     * @param cita La cita a crear
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
     * @param id El codCita de la cita a buscar
     * @return La cita encontrada o null si no existe
     */
    public Cita findById(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Busca la cita en la BD por su clave primaria
            return em.find(Cita.class, id);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * READ - Obtiene todas las citas de la base de datos.
     * @return Lista de todas las citas
     */
    public List<Cita> findAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Ejecuta la NamedQuery "Cita.findAll" definida en la entidad
            return em.createNamedQuery("Cita.findAll", Cita.class).getResultList();
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * UPDATE - Actualiza una cita existente en la base de datos.
     * @param cita La cita con los datos actualizados
     */
    public void update(Cita cita) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacción
        try {
            tx.begin(); // Inicia la transacción
            em.merge(cita); // Actualiza el registro en la BD (UPDATE)
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacción
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al actualizar la cita", ex);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * DELETE - Elimina una cita por su ID.
     * @param id El ID de la cita a eliminar
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacción
        try {
            tx.begin(); // Inicia la transacción
            Cita cita = em.find(Cita.class, id); // Busca la cita
            if (cita != null) {
                em.remove(cita); // Elimina el registro de la BD (DELETE)
            }
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacción
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al eliminar la cita", ex);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * DELETE ALL - Elimina todas las citas y sus relaciones.
     * Primero borra Cita_Tratamiento (tiene FK a Cita), luego Cita.
     * Finalmente reinicia el AUTO_INCREMENT de la tabla.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacción
        try {
            tx.begin(); // Inicia la transacción
            // Primero elimina los registros de la tabla puente
            em.createNativeQuery("DELETE FROM Cita_Tratamiento").executeUpdate();
            // Luego elimina las citas
            em.createNativeQuery("DELETE FROM Cita").executeUpdate();
            // Reinicia el AUTO_INCREMENT a 1
            em.createNativeQuery("ALTER TABLE clinica_dental.Cita AUTO_INCREMENT = 1").executeUpdate();
            tx.commit(); // Confirma todos los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacción
            if (tx.isActive()) {
                tx.rollback(); // Deshace todos los cambios
            }
            throw new RuntimeException("Error al eliminar todas las citas", ex);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

}
