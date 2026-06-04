package models.modelos.entidades;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entidad que representa el historial clínico de un paciente.
 * Relación 1:1 con Paciente (un paciente tiene un historial, un historial pertenece a un paciente).
 * Esta entidad es la dueña de la relación (tiene la FK codPaciente).
 */
@Entity
@Table(name = "Historial_Clinico")
@NamedQueries({
    @NamedQuery(name = "HistorialClinico.findAll", query = "SELECT h FROM HistorialClinico h"),
    @NamedQuery(name = "HistorialClinico.findById", query = "SELECT h FROM HistorialClinico h WHERE h.codHistorial = :codHistorial"),
    @NamedQuery(name = "HistorialClinico.findByPaciente", query = "SELECT h FROM HistorialClinico h WHERE h.paciente = :paciente")
})
public class HistorialClinico implements Serializable {

    // ID único de serialización usado para persistencia
    private static final long serialVersionUID = 1L;

    // Clave primaria autoincrementada del historial clínico
    @Id // Marca como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // La BD genera automáticamente el valor
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "codHistorial") // Nombre de la columna en la BD
    private Integer codHistorial;

    // Relación 1:1 con Paciente (lado propietario - tiene la clave foránea)
    // Esta es la tabla dueña de la relación (Historial_Clinico tiene la FK codPaciente)
    @JoinColumn(name = "codPaciente", referencedColumnName = "codPaciente", unique = true) // unique=true asegura 1:1
    @OneToOne(optional = false) // Relación obligatoria: todo historial debe tener un paciente
    private Paciente paciente;

    // Alergias del paciente registradas en el historial
    @Column(name = "Alergias") // Nombre de la columna en la BD (puede ser NULL)
    private String alergias;

    // Enfermedades crónicas del paciente registradas
    @Column(name = "EnfermedadesCronicas") // Nombre de la columna en la BD (puede ser NULL)
    private String enfermedadesCronicas;

    // Grupo sanguíneo del paciente (ej: O+, B-, AB+, etc)
    @Column(name = "GrupoSanguineo") // Nombre de la columna en la BD (puede ser NULL)
    private String grupoSanguineo;

    // Observaciones generales o notas importantes del médico
    @Column(name = "ObservacionesGenerales") // TEXT en MySQL se mapea como String
    private String observacionesGenerales;

    // Fecha y hora de creación del historial en la BD
    // FechaAlta tiene DEFAULT CURRENT_TIMESTAMP en la BD
    @Column(name = "FechaAlta") // Nombre de la columna en la BD
    @Temporal(TemporalType.TIMESTAMP) // Mapea a TIMESTAMP en MySQL (fecha y hora completa)
    private Date fechaAlta;

    // Constructor vacío requerido por JPA
    public HistorialClinico() {
    }

    // Constructor con solo el ID (usado para búsquedas)
    public HistorialClinico(Integer codHistorial) {
        this.codHistorial = codHistorial; // Asigna el ID
    }

    // Constructor con parámetro (usado al crear un historial para un paciente)
    public HistorialClinico(Paciente paciente) {
        this.paciente = paciente; // Asigna el paciente
        this.fechaAlta = new Date(); // Establece la fecha actual como fecha de creación
    }

    // Obtiene el código (ID) del historial
    public Integer getCodHistorial() {
        return codHistorial;
    }

    // Establece el código (ID) del historial
    public void setCodHistorial(Integer codHistorial) {
        this.codHistorial = codHistorial;
    }

    // Obtiene el paciente a quien pertenece este historial
    public Paciente getPaciente() {
        return paciente;
    }

    // Establece el paciente y sincroniza la relación bidireccional
    // Al asignar el paciente, también le decimos al paciente que este es su historial
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente; // Asigna el paciente
        // Sincroniza el lado inverso solo si el paciente es diferente al actual
        if (paciente != null && paciente.getHistorialClinico() != this) {
            paciente.setHistorialClinico(this);
        }
    }

    // Obtiene las alergias registradas del paciente
    public String getAlergias() {
        return alergias;
    }

    // Establece las alergias del paciente
    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    // Obtiene las enfermedades crónicas registradas
    public String getEnfermedadesCronicas() {
        return enfermedadesCronicas;
    }

    // Establece las enfermedades crónicas del paciente
    public void setEnfermedadesCronicas(String enfermedadesCronicas) {
        this.enfermedadesCronicas = enfermedadesCronicas;
    }

    // Obtiene el grupo sanguíneo del paciente
    public String getGrupoSanguineo() {
        return grupoSanguineo;
    }

    // Establece el grupo sanguíneo del paciente
    public void setGrupoSanguineo(String grupoSanguineo) {
        this.grupoSanguineo = grupoSanguineo;
    }

    // Obtiene las observaciones generales del médico
    public String getObservacionesGenerales() {
        return observacionesGenerales;
    }

    // Establece las observaciones generales del médico
    public void setObservacionesGenerales(String observacionesGenerales) {
        this.observacionesGenerales = observacionesGenerales;
    }

    // Obtiene la fecha y hora de creación del historial
    public Date getFechaAlta() {
        return fechaAlta;
    }

    // Establece la fecha y hora de creación del historial
    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    // Calcula el código hash basado en el ID del historial
    @Override
    public int hashCode() {
        int hash = 0; // Inicializa el hash
        // Si el ID no es nulo, suma su código hash
        hash += (codHistorial != null ? codHistorial.hashCode() : 0);
        return hash; // Retorna el hash calculado
    }

    // Compara dos historiales por su ID
    @Override
    public boolean equals(Object object) {
        // Verifica si el otro objeto es una instancia de HistorialClinico
        if (!(object instanceof HistorialClinico)) {
            return false; // No son del mismo tipo
        }
        HistorialClinico other = (HistorialClinico) object; // Convierte a HistorialClinico
        // Retorna verdadero si ambos tienen el mismo ID
        return !((this.codHistorial == null && other.codHistorial != null) || 
                 (this.codHistorial != null && !this.codHistorial.equals(other.codHistorial)));
    }

    // Retorna una representación en texto del historial clínico
    @Override
    public String toString() {
        // Retorna una cadena con todos los datos del historial
        return "HistorialClinico{" + "codHistorial=" + codHistorial + ", alergias=" + alergias + 
               ", enfermedadesCronicas=" + enfermedadesCronicas + ", grupoSanguineo=" + grupoSanguineo + 
               ", observacionesGenerales=" + observacionesGenerales + ", fechaAlta=" + fechaAlta + '}';
    }

}
