package controllers.controladores;

import java.util.List; // Importa List para retornar colecciones

import javax.persistence.EntityManager; // Gestiona la conexiÃƒÂ³n y transacciones con la BD
import javax.persistence.EntityManagerFactory; // Factory para crear EntityManager
import javax.persistence.EntityTransaction; // Controla las transacciones
import util.SharedEntityManagerFactory; // FactorÃƒÂ­a compartida de EntityManager

import models.modelos.entidades.Paciente; // Importa la entidad Paciente

/**
 * Controlador CRUD para la entidad Paciente.
 * Implementa los patrones Create, Read, Update, Delete sobre la base de datos.
 * Cada operaciÃƒÂ³n abre una transacciÃƒÂ³n, ejecuta la operaciÃƒÂ³n y la confirma o revierte.
 */
public class PacienteController {

    // EntityManagerFactory para crear instancias de EntityManager
    private final EntityManagerFactory emf;

    // Constructor: obtiene la instancia compartida de EntityManagerFactory
    public PacienteController() {
        // El nombre "clinica_dental" debe coincidir con el persistence-unit en persistence.xml
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    // MÃƒÂ©todo auxiliar: crea un nuevo EntityManager para comunicarse con la BD
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo paciente en la base de datos.
     * Abre una transacciÃƒÂ³n, persiste el objeto y confirma los cambios.
     */
    public void create(Paciente paciente) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            em.persist(paciente); // Guarda el paciente en la BD (INSERT)
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al crear el paciente", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * READ - Busca un paciente por su ID.
     * Retorna el paciente si existe, o null si no.
     */
    public Paciente findById(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Busca en la BD por la clave primaria
            return em.find(Paciente.class, id);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * READ - Obtiene TODOS los pacientes de la base de datos.
     * Usa una NamedQuery definida en la entidad Paciente.
     */
    public List<Paciente> findAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Ejecuta la NamedQuery "Paciente.findAll" definida en la entidad
            return em.createNamedQuery("Paciente.findAll", Paciente.class).getResultList();
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * UPDATE - Actualiza un paciente existente en la base de datos.
     * em.merge() sincroniza el objeto Java con los cambios en la BD.
     */
    public void update(Paciente paciente) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            em.merge(paciente); // Actualiza el registro en la BD (UPDATE)
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al actualizar el paciente", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * DELETE - Elimina un paciente por su ID.
     * Primero lo busca, y si existe, lo elimina.
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            Paciente paciente = em.find(Paciente.class, id); // Busca el paciente
            if (paciente != null) {
                em.remove(paciente); // Elimina el registro de la BD (DELETE)
            }
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al eliminar el paciente", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

    /**
     * DELETE ALL - Elimina todos los pacientes y todas sus relaciones.
     * Usa consultas nativas SQL porque necesita eliminar en un orden especÃƒÂ­fico
     * debido a las restricciones de clave forÃƒÂ¡nea.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacciÃƒÂ³n
        try {
            tx.begin(); // Inicia la transacciÃƒÂ³n
            // Orden especÃƒÂ­fico de eliminaciÃƒÂ³n para respetar las claves forÃƒÂ¡neas:
            em.createNativeQuery("DELETE FROM Cita_Tratamiento").executeUpdate(); // Elimina los registros de la tabla puente
            em.createNativeQuery("DELETE FROM Historial_Clinico").executeUpdate(); // Elimina los historiales
            em.createNativeQuery("DELETE FROM Cita").executeUpdate(); // Elimina las citas
            em.createNativeQuery("DELETE FROM Paciente").executeUpdate(); // Elimina los pacientes
            // Reinicia el AUTO_INCREMENT de la tabla Paciente a 1
            em.createNativeQuery("ALTER TABLE clinica_dental.Paciente AUTO_INCREMENT = 1").executeUpdate();
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacciÃƒÂ³n
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al eliminar todos los pacientes", ex);
        } finally {
            em.close(); // Siempre cierra la conexiÃƒÂ³n
        }
    }

}
