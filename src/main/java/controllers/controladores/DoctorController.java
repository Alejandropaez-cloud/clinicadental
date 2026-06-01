package controllers.controladores;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import util.SharedEntityManagerFactory;

import models.modelos.entidades.Doctor;

/**
 * Controlador CRUD para la entidad Doctor.
 * Sigue el mismo patrón que PacienteController.
 */
public class DoctorController {

    private final EntityManagerFactory emf;

    public DoctorController() {
        this.emf = SharedEntityManagerFactory.getInstance();
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * CREATE - Inserta un nuevo doctor.
     * @param doctor El doctor a crear.
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
     * @param id El codDoctor del doctor.
     * @return El doctor encontrado o null.
     */
    public Doctor findById(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Doctor.class, id);
        } finally {
            em.close();
        }
    }

    /**
     * READ - Obtiene todos los doctores.
     * @return Lista de doctores.
     */
    public List<Doctor> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createNamedQuery("Doctor.findAll", Doctor.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * UPDATE - Actualiza un doctor existente.
     * @param doctor El doctor con los datos actualizados.
     */
    public void update(Doctor doctor) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(doctor);
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al actualizar el doctor", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE - Elimina un doctor por su ID.
     * @param id El ID del doctor a eliminar.
     */
    public void delete(Integer id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Doctor doctor = em.find(Doctor.class, id);
            if (doctor != null) {
                em.remove(doctor);
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar el doctor", ex);
        } finally {
            em.close();
        }
    }

    /**
     * DELETE ALL - Elimina todos los doctores y reinicia el AUTO_INCREMENT.
     */
    public void deleteAll() {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createNativeQuery("DELETE FROM Cita").executeUpdate();
            em.createNativeQuery("DELETE FROM Doctor").executeUpdate();
            em.createNativeQuery("ALTER TABLE clinica_dental.Doctor AUTO_INCREMENT = 1").executeUpdate();
            tx.commit();
        } catch (Exception ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error al eliminar todos los doctores", ex);
        } finally {
            em.close();
        }
    }

}
