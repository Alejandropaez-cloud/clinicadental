package models.modelos.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entidad que representa una cita en la clínica dental.
 * Una cita pertenece a un paciente y es atendida por un doctor.
 * En cada cita se pueden realizar varios tratamientos (relación N:M).
 */
@Entity
@Table(name = "Cita")
@NamedQueries({
    @NamedQuery(name = "Cita.findAll", query = "SELECT c FROM Cita c"),
    @NamedQuery(name = "Cita.findById", query = "SELECT c FROM Cita c WHERE c.codCita = :codCita"),
    @NamedQuery(name = "Cita.findByFecha", query = "SELECT c FROM Cita c WHERE c.fecha = :fecha"),
    @NamedQuery(name = "Cita.findByEstado", query = "SELECT c FROM Cita c WHERE c.estado = :estado")
})
public class Cita implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codCita")
    private Integer codCita;

    // Relación N:1 con Paciente (muchas citas pueden pertenecer a un paciente)
    // @JoinColumn indica que esta tabla (Cita) tiene la FK codPaciente
    // Este es el lado propietario de la relación (tiene la FK)
    @JoinColumn(name = "codPaciente", referencedColumnName = "codPaciente")
    @ManyToOne(optional = false) // optional = false porque una cita siempre tiene paciente
    private Paciente paciente;

    // Relación N:1 con Doctor (muchas citas pueden ser atendidas por un doctor)
    @JoinColumn(name = "codDoctor", referencedColumnName = "codDoctor")
    @ManyToOne(optional = false) // optional = false porque una cita siempre tiene doctor
    private Doctor doctor;

    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE) // Solo fecha (sin hora)
    private Date fecha;

    @Basic(optional = false)
    @Column(name = "horaInicio")
    @Temporal(TemporalType.TIME) // Solo hora (sin fecha)
    private Date horaInicio;

    @Basic(optional = false)
    @Column(name = "horaFin")
    @Temporal(TemporalType.TIME) // Solo hora (sin fecha)
    private Date horaFin;

    @Basic(optional = false)
    @Column(name = "estado")
    private String estado;

    @Column(name = "fechaCreacion")
    @Temporal(TemporalType.TIMESTAMP) // Fecha y hora completa
    private Date fechaCreacion;

    // Relación 1:N con CitaTratamiento (tabla puente para N:M con Tratamiento)
    // Una cita puede tener varios tratamientos asociados
    // cascade = CascadeType.PERSIST -> al persistir una Cita, se persistirán
    // sus CitaTratamiento asociados
    // orphanRemoval = true -> al eliminar un CitaTratamiento de la colección,
    // se elimina de la BD
    @OneToMany(mappedBy = "cita", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Collection<CitaTratamiento> citaTratamientoCollection;

    public Cita() {
    }

    public Cita(Integer codCita) {
        this.codCita = codCita;
    }

    public Cita(Paciente paciente, Doctor doctor, Date fecha, Date horaInicio, Date horaFin, String estado) {
        this.paciente = paciente;
        this.doctor = doctor;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
        this.fechaCreacion = new Date(); // Fecha actual por defecto
        this.citaTratamientoCollection = new ArrayList<>();
    }

    public Integer getCodCita() {
        return codCita;
    }

    public void setCodCita(Integer codCita) {
        this.codCita = codCita;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(Date horaFin) {
        this.horaFin = horaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Collection<CitaTratamiento> getCitaTratamientoCollection() {
        return citaTratamientoCollection;
    }

    // Sincronización bidireccional con CitaTratamiento
    public void setCitaTratamientoCollection(Collection<CitaTratamiento> citaTratamientoCollection) {
        this.citaTratamientoCollection = citaTratamientoCollection;
        for (CitaTratamiento ct : citaTratamientoCollection) {
            ct.setCita(this);
        }
    }

    public void addCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.add(citaTratamiento);
        citaTratamiento.setCita(this);
    }

    public void removeCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.remove(citaTratamiento);
        citaTratamiento.setCita(null);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codCita != null ? codCita.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Cita)) {
            return false;
        }
        Cita other = (Cita) object;
        return !((this.codCita == null && other.codCita != null) || (this.codCita != null && !this.codCita.equals(other.codCita)));
    }

    @Override
    public String toString() {
        String tmp = "";
        for (CitaTratamiento ct : citaTratamientoCollection) {
            tmp += ct + "\n";
        }
        return "Cita{" + "codCita=" + codCita + ", paciente=" + paciente.getNombre() + " " + paciente.getApellidos() + ", doctor=" + doctor.getNombre() + ", fecha=" + fecha + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin + ", estado=" + estado + ", tratamientos=\n" + tmp + '}';
    }

}
