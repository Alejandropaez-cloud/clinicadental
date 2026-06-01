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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entidad que representa la tabla intermedia Cita_Tratamiento.
 * 
 * En una relación N:M entre Cita y Tratamiento, necesitamos una tabla intermedia
 * (tabla puente) que almacene qué tratamientos se realizan en cada cita.
 * 
 * Siguiendo la directriz JPA, esta entidad tiene su propio @Id con
 * @GeneratedValue(strategy = GenerationType.IDENTITY), en lugar de una
 * clave compuesta con @IdClass.
 * 
 * Además, tiene dos @ManyToOne con @JoinColumn hacia Cita y Tratamiento,
 * permitiendo agregar atributos adicionales como Cantidad y FechaRegistro.
 */
@Entity
@Table(name = "Cita_Tratamiento")
@NamedQueries({
    @NamedQuery(name = "CitaTratamiento.findAll", query = "SELECT ct FROM CitaTratamiento ct"),
    @NamedQuery(name = "CitaTratamiento.findById", query = "SELECT ct FROM CitaTratamiento ct WHERE ct.id = :id"),
    @NamedQuery(name = "CitaTratamiento.findByCita", query = "SELECT ct FROM CitaTratamiento ct WHERE ct.cita = :cita"),
    @NamedQuery(name = "CitaTratamiento.findByTratamiento", query = "SELECT ct FROM CitaTratamiento ct WHERE ct.tratamiento = :tratamiento")
})
public class CitaTratamiento implements Serializable {

    private static final long serialVersionUID = 1L;

    // Su propio @Id con @GeneratedValue como indica la directriz JPA
    // La BD genera automáticamente este valor con AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;

    // Relación ManyToOne con Cita
    // @JoinColumn indica la columna foránea en la tabla Cita_Tratamiento
    @ManyToOne(optional = false)
    @JoinColumn(name = "codCita", referencedColumnName = "codCita")
    private Cita cita;

    // Relación ManyToOne con Tratamiento
    @ManyToOne(optional = false)
    @JoinColumn(name = "codTratamiento", referencedColumnName = "codTratamiento")
    private Tratamiento tratamiento;

    @Basic(optional = false)
    @Column(name = "Cantidad")
    private Integer cantidad;

    @Column(name = "FechaRegistro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaRegistro;

    public CitaTratamiento() {
    }

    public CitaTratamiento(Integer id) {
        this.id = id;
    }

    public CitaTratamiento(Cita cita, Tratamiento tratamiento, Integer cantidad) {
        this.cita = cita;
        this.tratamiento = tratamiento;
        this.cantidad = cantidad;
        this.fechaRegistro = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Cita getCita() {
        return cita;
    }

    public void setCita(Cita cita) {
        this.cita = cita;
    }

    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CitaTratamiento)) {
            return false;
        }
        CitaTratamiento other = (CitaTratamiento) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "CitaTratamiento{" + "id=" + id + ", codCita=" + cita.getCodCita() + ", codTratamiento=" + tratamiento.getCodTratamiento() + ", cantidad=" + cantidad + ", tratamiento=" + tratamiento.getNombreTratamiento() + '}';
    }

}
