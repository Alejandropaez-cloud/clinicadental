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
 * Entidad que representa un tratamiento dental (ej: limpieza, ortodoncia, endodoncia...).
 * Un tratamiento puede estar en muchas citas (relación N:M con Cita a través de CitaTratamiento).
 */
@Entity
@Table(name = "Tratamiento")
@NamedQueries({
    @NamedQuery(name = "Tratamiento.findAll", query = "SELECT t FROM Tratamiento t"),
    @NamedQuery(name = "Tratamiento.findById", query = "SELECT t FROM Tratamiento t WHERE t.codTratamiento = :codTratamiento"),
    @NamedQuery(name = "Tratamiento.findByNombre", query = "SELECT t FROM Tratamiento t WHERE t.nombreTratamiento = :nombreTratamiento")
})
public class Tratamiento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codTratamiento")
    private Integer codTratamiento;

    @Basic(optional = false)
    @Column(name = "NombreTratamiento")
    private String nombreTratamiento;

    // TEXT en MySQL se mapea como String en JPA
    @Column(name = "Descripcion")
    private String descripcion;

    @Basic(optional = false)
    @Column(name = "PrecioEstimado")
    private Double precioEstimado;

    @Basic(optional = false)
    @Column(name = "DuracionMinutos")
    private Integer duracionMinutos;

    // Relación 1:N con CitaTratamiento (tabla puente para N:M con Cita)
    // Un tratamiento puede estar asociado a muchas citas a través de CitaTratamiento
    @OneToMany(mappedBy = "tratamiento", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Collection<CitaTratamiento> citaTratamientoCollection;

    public Tratamiento() {
    }

    public Tratamiento(Integer codTratamiento) {
        this.codTratamiento = codTratamiento;
    }

    public Tratamiento(String nombreTratamiento, String descripcion, Double precioEstimado, Integer duracionMinutos) {
        this.nombreTratamiento = nombreTratamiento;
        this.descripcion = descripcion;
        this.precioEstimado = precioEstimado;
        this.duracionMinutos = duracionMinutos;
        this.citaTratamientoCollection = new ArrayList<>();
    }

    public Integer getCodTratamiento() {
        return codTratamiento;
    }

    public void setCodTratamiento(Integer codTratamiento) {
        this.codTratamiento = codTratamiento;
    }

    public String getNombreTratamiento() {
        return nombreTratamiento;
    }

    public void setNombreTratamiento(String nombreTratamiento) {
        this.nombreTratamiento = nombreTratamiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioEstimado() {
        return precioEstimado;
    }

    public void setPrecioEstimado(Double precioEstimado) {
        this.precioEstimado = precioEstimado;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Collection<CitaTratamiento> getCitaTratamientoCollection() {
        return citaTratamientoCollection;
    }

    public void setCitaTratamientoCollection(Collection<CitaTratamiento> citaTratamientoCollection) {
        this.citaTratamientoCollection = citaTratamientoCollection;
        for (CitaTratamiento ct : citaTratamientoCollection) {
            ct.setTratamiento(this);
        }
    }

    public void addCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.add(citaTratamiento);
        citaTratamiento.setTratamiento(this);
    }

    public void removeCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.remove(citaTratamiento);
        citaTratamiento.setTratamiento(null);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codTratamiento != null ? codTratamiento.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Tratamiento)) {
            return false;
        }
        Tratamiento other = (Tratamiento) object;
        return !((this.codTratamiento == null && other.codTratamiento != null) || (this.codTratamiento != null && !this.codTratamiento.equals(other.codTratamiento)));
    }

    @Override
    public String toString() {
        return "Tratamiento{" + "codTratamiento=" + codTratamiento + ", nombreTratamiento=" + nombreTratamiento + ", precioEstimado=" + precioEstimado + ", duracionMinutos=" + duracionMinutos + '}';
    }

}
