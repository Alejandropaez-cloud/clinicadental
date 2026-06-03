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
 * Cada cita está asociada a un paciente y a un doctor,
 * y puede contener varios tratamientos a través de CitaTratamiento.
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

    private static final long serialVersionUID = 1L; // Identificador de versión para Serial

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codCita")
    private Integer codCita; // ID de la cita

    @JoinColumn(name = "codPaciente", referencedColumnName = "codPaciente")
    @ManyToOne(optional = false)
    private Paciente paciente; // Paciente asociado a la cita

    @JoinColumn(name = "codDoctor", referencedColumnName = "codDoctor")
    @ManyToOne(optional = false)
    private Doctor doctor; // Doctor que atiende la cita

    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha; // Fecha de la cita (solo día)

    @Basic(optional = false)
    @Column(name = "horaInicio")
    @Temporal(TemporalType.TIME)
    private Date horaInicio; // Hora de inicio de la cita

    @Basic(optional = false)
    @Column(name = "horaFin")
    @Temporal(TemporalType.TIME)
    private Date horaFin; // Hora de fin de la cita

    @Basic(optional = false)
    @Column(name = "estado")
    private String estado; // Estado de la cita: programada, completada, cancelada

    @Basic(optional = false)
    @Column(name = "fechaCreacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion; // Fecha y hora en que se creó el registro

    @OneToMany(mappedBy = "cita", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Collection<CitaTratamiento> citaTratamientoCollection; // Tratamientos asociados a la cita

    public Cita() {
        this.citaTratamientoCollection = new ArrayList<>(); // Inicializa colección vacía
    }

    public Cita(Integer codCita) {
        this();
        this.codCita = codCita; // Asigna solo el ID
    }

    public Cita(Paciente paciente, Doctor doctor, Date fecha, Date horaInicio, Date horaFin, String estado) {
        this();
        this.paciente = paciente; // Asigna el paciente
        this.doctor = doctor; // Asigna el doctor
        this.fecha = fecha; // Asigna la fecha
        this.horaInicio = horaInicio; // Asigna la hora de inicio
        this.horaFin = horaFin; // Asigna la hora de fin
        this.estado = estado; // Asigna el estado
        this.fechaCreacion = new Date(); // Establece la fecha de creación actual
    }

    public Integer getCodCita() {
        return codCita; // Retorna el ID de la cita
    }

    public void setCodCita(Integer codCita) {
        this.codCita = codCita; // Establece el ID de la cita
    }

    public Paciente getPaciente() {
        return paciente; // Retorna el paciente asociado
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente; // Establece el paciente asociado
    }

    public Doctor getDoctor() {
        return doctor; // Retorna el doctor asociado
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor; // Establece el doctor asociado
    }

    public Date getFecha() {
        return fecha; // Retorna la fecha de la cita
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha; // Establece la fecha de la cita
    }

    public Date getHoraInicio() {
        return horaInicio; // Retorna la hora de inicio
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio; // Establece la hora de inicio
    }

    public Date getHoraFin() {
        return horaFin; // Retorna la hora de fin
    }

    public void setHoraFin(Date horaFin) {
        this.horaFin = horaFin; // Establece la hora de fin
    }

    public String getEstado() {
        return estado; // Retorna el estado de la cita
    }

    public void setEstado(String estado) {
        this.estado = estado; // Establece el estado de la cita
    }

    public Date getFechaCreacion() {
        return fechaCreacion; // Retorna la fecha de creación
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion; // Establece la fecha de creación
    }

    public Collection<CitaTratamiento> getCitaTratamientoCollection() {
        return citaTratamientoCollection; // Retorna los tratamientos asociados
    }

    public void setCitaTratamientoCollection(Collection<CitaTratamiento> citaTratamientoCollection) {
        this.citaTratamientoCollection = citaTratamientoCollection; // Asigna la colección
        for (CitaTratamiento ct : citaTratamientoCollection) {
            ct.setCita(this); // Sincroniza la relación inversa
        }
    }

    public void addCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.add(citaTratamiento); // Añade el tratamiento
        citaTratamiento.setCita(this); // Sincroniza la relación inversa
    }

    public void removeCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.remove(citaTratamiento); // Elimina el tratamiento
        citaTratamiento.setCita(null); // Limpia la referencia inversa
    }

    @Override
    public int hashCode() {
        int hash = 0; // Inicializa el hash
        hash += (codCita != null ? codCita.hashCode() : 0); // Usa el ID como hash
        return hash; // Retorna el hash calculado
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Cita)) {
            return false; // No son del mismo tipo
        }
        Cita other = (Cita) object; // Convierte al tipo correcto
        return !((this.codCita == null && other.codCita != null) || (this.codCita != null && !this.codCita.equals(other.codCita)));
    }

    @Override
    public String toString() {
        String tmp = ""; // Construye una lista de tratamientos
        for (CitaTratamiento ct : citaTratamientoCollection) {
            tmp += ct + "\n"; // Añade cada tratamiento en nueva línea
        }
        return "Cita{" + "codCita=" + codCita + ", paciente=" + (paciente != null ? paciente.getNombre() + " " + paciente.getApellidos() : "null")
                + ", doctor=" + (doctor != null ? doctor.getNombre() : "null")
                + ", fecha=" + fecha + ", horaInicio=" + horaInicio + ", horaFin=" + horaFin
                + ", estado=" + estado + ", fechaCreacion=" + fechaCreacion + ", tratamientos=\n" + tmp + '}';
    }

}
