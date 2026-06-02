package controllers.controladores;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import util.SharedEntityManagerFactory;

import models.modelos.entidades.Paciente;

/**
 * Controlador CRUD para la entidad Paciente.
 * Cada controlador sigue el mismo patrón:
 * 1. Obtiene un EntityManager (conexión a BD)
 * 2. Inicia una transacción
 * 3. Ejecuta la operación (persist, merge, remove, find...)
 * 4. Confirma la transacción (commit) o la deshace si hay error (rollback)
 * 5. Cierra el EntityManager
 */
public class PacienteController {

    private final EntityManagerFactory emf;

    public PacienteController() {
        // El nombre "clinica_dental" debe coincidir con el persistence-unit definido
        // en persistence.xml
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    // Cada vez que necesitamos hablar con la BD, pedimos un EntityManager nuevo
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo paciente en la base de datos.
     * em.persist() guarda el objeto en la BD.
     */
    public void create(Paciente paciente) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();       // Inicia la transacción
            em.persist(paciente); // Guarda el paciente en la BD
            tx.commit();      // Confirma los cambios
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback(); // Si algo falla, deshace los cambios
            }
            throw new RuntimeException("Error al crear el paciente", ex);
        } finally {
            em.close(); // Siempre cierra el EntityManager
        }
    }

    /**
     * READ - Busca un paciente por su ID.
     * em.find() busca en la BD por la clave primaria.
     * Devuelve el objeto si existe, o null si no.
     */
    public Paciente findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Paciente.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Obtiene TODOS los pacientes.
     * Usa una NamedQuery definida en la entidad Paciente.
     */
    public List<Paciente> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Paciente.findAll", Paciente.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * UPDATE - Actualiza un paciente existente.
     * em.merge() sincroniza el objeto Java con la BD.
     * Si el paciente no existe, lo crea.
     */
    public void update(Paciente paciente) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(paciente); // Actualiza el registro en la BD
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el paciente", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE - Elimina un paciente por su ID.
     * Primero lo busca con find(), y si existe, lo elimina con remove().
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Paciente paciente = em.find(Paciente.class, id);
            if (paciente != null) {
                em.remove(paciente); // Borra el registro de la BD
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar el paciente", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE ALL - Elimina todos los pacientes y reinicia el AUTO_INCREMENT.
     * Usa consultas nativas (SQL directamente) porque JPA no tiene un
     * método "borrar todo" estándar.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM Cita_Tratamiento").executeUpdate();
            em.createNativeQuery("DELETE FROM Historial_Clinico").executeUpdate();
            em.createNativeQuery("DELETE FROM Cita").executeUpdate();
            em.createNativeQuery("DELETE FROM Paciente").executeUpdate();
            em.createNativeQuery("ALTER TABLE clinica_dental.Paciente AUTO_INCREMENT = 1").executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar todos los pacientes", ex);
        } finally {
            em.close();
        }
    }

}
