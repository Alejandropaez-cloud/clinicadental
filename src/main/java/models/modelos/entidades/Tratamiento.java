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
 * Un tratamiento puede estar en muchas citas (relaciÃƒÂ³n N:M con Cita a travÃƒÂ©s de CitaTratamiento).
 */
@Entity
@Table(name = "Tratamiento")
@NamedQueries({
    @NamedQuery(name = "Tratamiento.findAll", query = "SELECT t FROM Tratamiento t"),
    @NamedQuery(name = "Tratamiento.findById", query = "SELECT t FROM Tratamiento t WHERE t.codTratamiento = :codTratamiento"),
    @NamedQuery(name = "Tratamiento.findByNombre", query = "SELECT t FROM Tratamiento t WHERE t.nombreTratamiento = :nombreTratamiento")
})
public class Tratamiento implements Serializable {

    // ID ÃƒÂºnico de serializaciÃƒÂ³n usado para persistencia
    private static final long serialVersionUID = 1L;

    // Clave primaria autoincrementada del tratamiento
    @Id // Marca como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // La BD genera automÃƒÂ¡ticamente el valor
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "codTratamiento") // Nombre de la columna en la BD
    private Integer codTratamiento;

    // Nombre del tratamiento dental (ej: limpieza, endodoncia, etc)
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "NombreTratamiento") // Nombre de la columna en la BD
    private String nombreTratamiento;

    // DescripciÃƒÂ³n detallada del tratamiento
    @Column(name = "Descripcion") // TEXT en MySQL se mapea como String
    private String descripcion;

    // Precio estimado del tratamiento en dinero
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "PrecioEstimado") // Nombre de la columna en la BD
    private Double precioEstimado;

    // DuraciÃƒÂ³n estimada del tratamiento en minutos
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "DuracionMinutos") // Nombre de la columna en la BD
    private Integer duracionMinutos;

    // RelaciÃƒÂ³n 1:N con CitaTratamiento (tabla puente para relaciÃƒÂ³n N:M con Cita)
    // Un tratamiento puede estar asociado a muchas citas a travÃƒÂ©s de CitaTratamiento
    @OneToMany(
        mappedBy = "tratamiento", // El lado no propietario: la entidad CitaTratamiento tiene el atributo "tratamiento"
        cascade = CascadeType.PERSIST, // Propagar la operaciÃƒÂ³n persist a los CitaTratamiento
        orphanRemoval = true // Eliminar CitaTratamiento de la BD si se remove de la colecciÃƒÂ³n
    )
    private Collection<CitaTratamiento> citaTratamientoCollection;

    // Constructor vacÃƒÂ­o requerido por JPA
    public Tratamiento() {
    }

    // Constructor con solo el ID (usado para bÃƒÂºsquedas)
    public Tratamiento(Integer codTratamiento) {
        this.codTratamiento = codTratamiento; // Asigna el ID
    }

    // Constructor con parÃƒÂ¡metros (usado al crear un nuevo tratamiento)
    public Tratamiento(String nombreTratamiento, String descripcion, Double precioEstimado, Integer duracionMinutos) {
        this.nombreTratamiento = nombreTratamiento; // Asigna el nombre
        this.descripcion = descripcion; // Asigna la descripciÃƒÂ³n
        this.precioEstimado = precioEstimado; // Asigna el precio
        this.duracionMinutos = duracionMinutos; // Asigna la duraciÃƒÂ³n
        this.citaTratamientoCollection = new ArrayList<>(); // Inicializa la colecciÃƒÂ³n vacÃƒÂ­a
    }

    // Obtiene el cÃƒÂ³digo (ID) del tratamiento
    public Integer getCodTratamiento() {
        return codTratamiento;
    }

    // Establece el cÃƒÂ³digo (ID) del tratamiento
    public void setCodTratamiento(Integer codTratamiento) {
        this.codTratamiento = codTratamiento;
    }

    // Obtiene el nombre del tratamiento
    public String getNombreTratamiento() {
        return nombreTratamiento;
    }

    // Establece el nombre del tratamiento
    public void setNombreTratamiento(String nombreTratamiento) {
        this.nombreTratamiento = nombreTratamiento;
    }

    // Obtiene la descripciÃƒÂ³n del tratamiento
    public String getDescripcion() {
        return descripcion;
    }

    // Establece la descripciÃƒÂ³n del tratamiento
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // Obtiene el precio estimado del tratamiento
    public Double getPrecioEstimado() {
        return precioEstimado;
    }

    // Establece el precio estimado del tratamiento
    public void setPrecioEstimado(Double precioEstimado) {
        this.precioEstimado = precioEstimado;
    }

    // Obtiene la duraciÃƒÂ³n estimada en minutos
    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    // Establece la duraciÃƒÂ³n estimada en minutos
    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    // Obtiene la colecciÃƒÂ³n de registros CitaTratamiento asociados
    public Collection<CitaTratamiento> getCitaTratamientoCollection() {
        return citaTratamientoCollection;
    }

    // Establece la colecciÃƒÂ³n de CitaTratamiento y sincroniza la relaciÃƒÂ³n bidireccional
    public void setCitaTratamientoCollection(Collection<CitaTratamiento> citaTratamientoCollection) {
        this.citaTratamientoCollection = citaTratamientoCollection; // Asigna la nueva colecciÃƒÂ³n
        // Sincroniza el lado inverso: cada CitaTratamiento debe saber que pertenece a este Tratamiento
        for (CitaTratamiento ct : citaTratamientoCollection) {
            ct.setTratamiento(this);
        }
    }

    // Agrega un CitaTratamiento a esta colecciÃƒÂ³n y sincroniza la relaciÃƒÂ³n
    public void addCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.add(citaTratamiento); // AÃƒÂ±ade a la colecciÃƒÂ³n local
        citaTratamiento.setTratamiento(this); // Sincroniza el lado inverso
    }

    // Elimina un CitaTratamiento de esta colecciÃƒÂ³n y limpia la relaciÃƒÂ³n
    public void removeCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.remove(citaTratamiento); // Elimina de la colecciÃƒÂ³n local
        citaTratamiento.setTratamiento(null); // Limpia la referencia inversa
    }

    // Calcula el cÃƒÂ³digo hash basado en el ID del tratamiento
    @Override
    public int hashCode() {
        int hash = 0; // Inicializa el hash
        // Si el ID no es nulo, suma su cÃƒÂ³digo hash
        hash += (codTratamiento != null ? codTratamiento.hashCode() : 0);
        return hash; // Retorna el hash calculado
    }

    // Compara dos tratamientos por su ID
    @Override
    public boolean equals(Object object) {
        // Verifica si el otro objeto es una instancia de Tratamiento
        if (!(object instanceof Tratamiento)) {
            return false; // No son del mismo tipo
        }
        Tratamiento other = (Tratamiento) object; // Convierte a Tratamiento
        // Retorna verdadero si ambos tienen el mismo ID
        return !((this.codTratamiento == null && other.codTratamiento != null) || 
                 (this.codTratamiento != null && !this.codTratamiento.equals(other.codTratamiento)));
    }

    // Retorna una representaciÃƒÂ³n en texto del tratamiento
    @Override
    public String toString() {
        // Retorna una cadena con los datos principales del tratamiento
        return "Tratamiento{" + "codTratamiento=" + codTratamiento + ", nombreTratamiento=" + nombreTratamiento + 
               ", precioEstimado=" + precioEstimado + ", duracionMinutos=" + duracionMinutos + '}';
    }

}
