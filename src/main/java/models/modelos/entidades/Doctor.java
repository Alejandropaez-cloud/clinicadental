package models.modelos.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Entidad que representa a un doctor/odontólogo de la clínica.
 * Un doctor puede tener muchas citas (relación 1:N con Cita).
 */
@Entity
@Table(name = "Doctor")
@NamedQueries({
    @NamedQuery(name = "Doctor.findAll", query = "SELECT d FROM Doctor d"),
    @NamedQuery(name = "Doctor.findById", query = "SELECT d FROM Doctor d WHERE d.codDoctor = :codDoctor"),
    @NamedQuery(name = "Doctor.findByNombre", query = "SELECT d FROM Doctor d WHERE d.nombre = :nombre"),
    @NamedQuery(name = "Doctor.findByEspecialidad", query = "SELECT d FROM Doctor d WHERE d.especialidad = :especialidad")
})
public class Doctor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codDoctor")
    private Integer codDoctor;

    @Basic(optional = false)
    @Column(name = "NumeroColegiado")
    private String numeroColegiado;

    @Basic(optional = false)
    @Column(name = "Nombre")
    private String nombre;

    @Basic(optional = false)
    @Column(name = "Especialidad")
    private String especialidad;

    @Column(name = "TelefonoContacto")
    private String telefonoContacto;

    // Relación 1:N con Cita (un doctor tiene muchas citas)
    // mappedBy = "doctor" -> la entidad Cita es la dueña de la relación
    // (la tabla Cita tiene la FK codDoctor)
    // cascade = CascadeType.PERSIST -> al persistir un Doctor, se persistirán
    // sus Citas asociadas
    // orphanRemoval = true -> si elimino una Cita de la colección,
    // se borra automáticamente de la BD
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Collection<Cita> citaCollection;

    public Doctor() {
    }

    public Doctor(Integer codDoctor) {
        this.codDoctor = codDoctor;
    }

    public Doctor(String numeroColegiado, String nombre, String especialidad) {
        this.numeroColegiado = numeroColegiado;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.citaCollection = new ArrayList<>();
    }

    // Obtiene el código (ID) del doctor
    public Integer getCodDoctor() {
        return codDoctor;
    }

    // Establece el código (ID) del doctor
    public void setCodDoctor(Integer codDoctor) {
        this.codDoctor = codDoctor;
    }

    // Obtiene el número de colegiado del doctor
    public String getNumeroColegiado() {
        return numeroColegiado;
    }

    // Establece el número de colegiado del doctor
    public void setNumeroColegiado(String numeroColegiado) {
        this.numeroColegiado = numeroColegiado;
    }

    // Obtiene el nombre del doctor
    public String getNombre() {
        return nombre;
    }

    // Establece el nombre del doctor
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // Obtiene la especialidad del doctor (ej: Odontología General, Endodoncia, etc)
    public String getEspecialidad() {
        return especialidad;
    }

    // Establece la especialidad del doctor
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    // Obtiene el teléfono de contacto del doctor
    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    // Establece el teléfono de contacto del doctor
    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    // Obtiene la colección de citas atendidas por este doctor
    public Collection<Cita> getCitaCollection() {
        return citaCollection;
    }

    // Establece la colección de citas y sincroniza la relación bidireccional
    public void setCitaCollection(Collection<Cita> citaCollection) {
        this.citaCollection = citaCollection; // Asigna la nueva colección
        // Sincroniza el lado inverso: cada Cita debe saber que es atendida por este Doctor
        for (Cita cita : citaCollection) {
            cita.setDoctor(this);
        }
    }

    // Agrega una cita al doctor y sincroniza la relación bidireccional
    public void addCita(Cita cita) {
        this.citaCollection.add(cita); // Añade a la colección local
        cita.setDoctor(this); // Sincroniza el lado inverso
    }

    // Elimina una cita del doctor y limpia la relación bidireccional
    public void removeCita(Cita cita) {
        this.citaCollection.remove(cita); // Elimina de la colección local
        cita.setDoctor(null); // Limpia la referencia inversa
    }

    // Calcula el código hash basado en el ID del doctor
    @Override
    public int hashCode() {
        int hash = 0; // Inicializa el hash
        // Si el ID no es nulo, suma su código hash
        hash += (codDoctor != null ? codDoctor.hashCode() : 0);
        return hash; // Retorna el hash calculado
    }

    // Compara dos doctores por su ID
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Doctor)) {
            return false;
        }
        Doctor other = (Doctor) object;
        return !((this.codDoctor == null && other.codDoctor != null) || (this.codDoctor != null && !this.codDoctor.equals(other.codDoctor)));
    }

    @Override
    public String toString() {
        return "Doctor{" + "codDoctor=" + codDoctor + ", numeroColegiado=" + numeroColegiado + ", nombre=" + nombre + ", especialidad=" + especialidad + ", telefonoContacto=" + telefonoContacto + '}';
    }

}
