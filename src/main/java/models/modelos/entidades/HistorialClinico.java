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

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codHistorial")
    private Integer codHistorial;

    // Relación 1:1 con Paciente (lado propietario)
    // @JoinColumn indica que esta tabla tiene la clave foránea (codPaciente)
    // que referencia a la tabla Paciente
    // referencedColumnName = "codPaciente" -> la columna a la que apunta en Paciente
    // unique = true -> asegura que sea 1:1 (un paciente no puede tener dos historiales)
    @JoinColumn(name = "codPaciente", referencedColumnName = "codPaciente", unique = true)
    @OneToOne(optional = false) // optional = false porque todo paciente debe tener historial
    private Paciente paciente;

    @Column(name = "Alergias")
    private String alergias;

    @Column(name = "EnfermedadesCronicas")
    private String enfermedadesCronicas;

    @Column(name = "GrupoSanguineo")
    private String grupoSanguineo;

    // TEXT se mapea como String en JPA
    @Column(name = "ObservacionesGenerales")
    private String observacionesGenerales;

    // FechaAlta tiene DEFAULT CURRENT_TIMESTAMP en la BD
    // TemporalType.TIMESTAMP guarda fecha y hora completas
    @Column(name = "FechaAlta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;

    public HistorialClinico() {
    }

    public HistorialClinico(Integer codHistorial) {
        this.codHistorial = codHistorial;
    }

    public HistorialClinico(Paciente paciente) {
        this.paciente = paciente;
        this.fechaAlta = new Date(); // Fecha actual por defecto
    }

    public Integer getCodHistorial() {
        return codHistorial;
    }

    public void setCodHistorial(Integer codHistorial) {
        this.codHistorial = codHistorial;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    // Al asignar el paciente, también sincronizamos el otro lado
    // para mantener la relación bidireccional consistente
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
        if (paciente != null && paciente.getHistorialClinico() != this) {
            paciente.setHistorialClinico(this);
        }
    }

    public String getAlergias() {
        return alergias;
    }

    public void setAlergias(String alergias) {
        this.alergias = alergias;
    }

    public String getEnfermedadesCronicas() {
        return enfermedadesCronicas;
    }

    public void setEnfermedadesCronicas(String enfermedadesCronicas) {
        this.enfermedadesCronicas = enfermedadesCronicas;
    }

    public String getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public void setGrupoSanguineo(String grupoSanguineo) {
        this.grupoSanguineo = grupoSanguineo;
    }

    public String getObservacionesGenerales() {
        return observacionesGenerales;
    }

    public void setObservacionesGenerales(String observacionesGenerales) {
        this.observacionesGenerales = observacionesGenerales;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codHistorial != null ? codHistorial.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof HistorialClinico)) {
            return false;
        }
        HistorialClinico other = (HistorialClinico) object;
        return !((this.codHistorial == null && other.codHistorial != null) || (this.codHistorial != null && !this.codHistorial.equals(other.codHistorial)));
    }

    @Override
    public String toString() {
        return "HistorialClinico{" + "codHistorial=" + codHistorial + ", alergias=" + alergias + ", enfermedadesCronicas=" + enfermedadesCronicas + ", grupoSanguineo=" + grupoSanguineo + ", observacionesGenerales=" + observacionesGenerales + ", fechaAlta=" + fechaAlta + '}';
    }

}
