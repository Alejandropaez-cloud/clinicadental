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

    public Integer getCodDoctor() {
        return codDoctor;
    }

    public void setCodDoctor(Integer codDoctor) {
        this.codDoctor = codDoctor;
    }

    public String getNumeroColegiado() {
        return numeroColegiado;
    }

    public void setNumeroColegiado(String numeroColegiado) {
        this.numeroColegiado = numeroColegiado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public Collection<Cita> getCitaCollection() {
        return citaCollection;
    }

    // Sincronización bidireccional: al asignar una colección de citas,
    // a cada cita le decimos que su doctor es este
    public void setCitaCollection(Collection<Cita> citaCollection) {
        this.citaCollection = citaCollection;
        for (Cita cita : citaCollection) {
            cita.setDoctor(this);
        }
    }

    public void addCita(Cita cita) {
        this.citaCollection.add(cita);
        cita.setDoctor(this);
    }

    public void removeCita(Cita cita) {
        this.citaCollection.remove(cita);
        cita.setDoctor(null);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codDoctor != null ? codDoctor.hashCode() : 0);
        return hash;
    }

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
