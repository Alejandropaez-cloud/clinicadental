package controllers.controladores;

import java.util.List; // Importa List para retornar colecciones

import javax.persistence.EntityManager; // Gestiona la conexión con la BD
import javax.persistence.EntityManagerFactory; // Factory para crear EntityManager
import javax.persistence.EntityTransaction; // Controla las transacciones
import util.SharedEntityManagerFactory; // Factoría compartida de EntityManager

import models.modelos.entidades.Doctor; // Importa la entidad Doctor

/**
 * Controlador CRUD para la entidad Doctor (Médico/Odontólogo).
 * Implementa operaciones Create, Read, Update, Delete sobre doctores.
 * Sigue el mismo patrón que PacienteController para mantener consistencia.
 */
public class DoctorController {

    // EntityManagerFactory para crear instancias de EntityManager
    private final EntityManagerFactory emf;

    // Constructor: obtiene la instancia compartida de EntityManagerFactory
    public DoctorController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    // Método auxiliar: crea un nuevo EntityManager para comunicarse con la BD
    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo doctor en la base de datos.
     * @param doctor El doctor a crear
     */
    public void create(Doctor doctor) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(doctor);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al crear el doctor", ex);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Busca un doctor por su ID.
     * @param id El codDoctor del doctor a buscar
     * @return El doctor encontrado o null si no existe
     */
    public Doctor findById(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Busca el doctor en la BD por su clave primaria
            return em.find(Doctor.class, id);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * READ - Obtiene todos los doctores de la base de datos.
     * @return Lista de todos los doctores
     */
    public List<Doctor> findAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        try {
            // Ejecuta la NamedQuery "Doctor.findAll" definida en la entidad
            return em.createNamedQuery("Doctor.findAll", Doctor.class).getResultList();
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * UPDATE - Actualiza un doctor existente en la base de datos.
     * @param doctor El doctor con los datos actualizados
     */
    public void update(Doctor doctor) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacción
        try {
            tx.begin(); // Inicia la transacción
            em.merge(doctor); // Actualiza el registro en la BD (UPDATE)
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacción
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al actualizar el doctor", ex);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * DELETE - Elimina un doctor por su ID.
     * @param id El ID del doctor a eliminar
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacción
        try {
            tx.begin(); // Inicia la transacción
            Doctor doctor = em.find(Doctor.class, id); // Busca el doctor
            if (doctor != null) {
                em.remove(doctor); // Elimina el registro de la BD (DELETE)
            }
            tx.commit(); // Confirma los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacción
            if (tx.isActive()) {
                tx.rollback(); // Deshace los cambios
            }
            throw new RuntimeException("Error al eliminar el doctor", ex);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

    /**
     * DELETE ALL - Elimina todos los doctores y sus citas.
     * Primero borra las Citas (tienen FK a Doctor), luego Doctor.
     * Finalmente reinicia el AUTO_INCREMENT de la tabla.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager(); // Crea nuevo EntityManager
        EntityTransaction tx = em.getTransaction(); // Obtiene la transacción
        try {
            tx.begin(); // Inicia la transacción
            // Primero elimina las citas (tienen FK a Doctor)
            em.createNativeQuery("DELETE FROM Cita").executeUpdate();
            // Luego elimina los doctores
            em.createNativeQuery("DELETE FROM Doctor").executeUpdate();
            // Reinicia el AUTO_INCREMENT a 1
            em.createNativeQuery("ALTER TABLE clinica_dental.Doctor AUTO_INCREMENT = 1").executeUpdate();
            tx.commit(); // Confirma todos los cambios
        } catch (Exception ex) {
            // Si ocurre un error, revierte la transacción
            if (tx.isActive()) {
                tx.rollback(); // Deshace todos los cambios
            }
            throw new RuntimeException("Error al eliminar todos los doctores", ex);
        } finally {
            em.close(); // Siempre cierra la conexión
        }
    }

}
