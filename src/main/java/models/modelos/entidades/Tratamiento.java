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

    // ID único de serialización usado para persistencia
    private static final long serialVersionUID = 1L;

    // Clave primaria autoincrementada del tratamiento
    @Id // Marca como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // La BD genera automáticamente el valor
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "codTratamiento") // Nombre de la columna en la BD
    private Integer codTratamiento;

    // Nombre del tratamiento dental (ej: limpieza, endodoncia, etc)
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "NombreTratamiento") // Nombre de la columna en la BD
    private String nombreTratamiento;

    // Descripción detallada del tratamiento
    @Column(name = "Descripcion") // TEXT en MySQL se mapea como String
    private String descripcion;

    // Precio estimado del tratamiento en dinero
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "PrecioEstimado") // Nombre de la columna en la BD
    private Double precioEstimado;

    // Duración estimada del tratamiento en minutos
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "DuracionMinutos") // Nombre de la columna en la BD
    private Integer duracionMinutos;

    // Relación 1:N con CitaTratamiento (tabla puente para relación N:M con Cita)
    // Un tratamiento puede estar asociado a muchas citas a través de CitaTratamiento
    @OneToMany(
        mappedBy = "tratamiento", // El lado no propietario: la entidad CitaTratamiento tiene el atributo "tratamiento"
        cascade = CascadeType.PERSIST, // Propagar la operación persist a los CitaTratamiento
        orphanRemoval = true // Eliminar CitaTratamiento de la BD si se remove de la colección
    )
    private Collection<CitaTratamiento> citaTratamientoCollection;

    // Constructor vacío requerido por JPA
    public Tratamiento() {
    }

    // Constructor con solo el ID (usado para búsquedas)
    public Tratamiento(Integer codTratamiento) {
        this.codTratamiento = codTratamiento; // Asigna el ID
    }

    // Constructor con parámetros (usado al crear un nuevo tratamiento)
    public Tratamiento(String nombreTratamiento, String descripcion, Double precioEstimado, Integer duracionMinutos) {
        this.nombreTratamiento = nombreTratamiento; // Asigna el nombre
        this.descripcion = descripcion; // Asigna la descripción
        this.precioEstimado = precioEstimado; // Asigna el precio
        this.duracionMinutos = duracionMinutos; // Asigna la duración
        this.citaTratamientoCollection = new ArrayList<>(); // Inicializa la colección vacía
    }

    // Obtiene el código (ID) del tratamiento
    public Integer getCodTratamiento() {
        return codTratamiento;
    }

    // Establece el código (ID) del tratamiento
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

    // Obtiene la descripción del tratamiento
    public String getDescripcion() {
        return descripcion;
    }

    // Establece la descripción del tratamiento
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

    // Obtiene la duración estimada en minutos
    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    // Establece la duración estimada en minutos
    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    // Obtiene la colección de registros CitaTratamiento asociados
    public Collection<CitaTratamiento> getCitaTratamientoCollection() {
        return citaTratamientoCollection;
    }

    // Establece la colección de CitaTratamiento y sincroniza la relación bidireccional
    public void setCitaTratamientoCollection(Collection<CitaTratamiento> citaTratamientoCollection) {
        this.citaTratamientoCollection = citaTratamientoCollection; // Asigna la nueva colección
        // Sincroniza el lado inverso: cada CitaTratamiento debe saber que pertenece a este Tratamiento
        for (CitaTratamiento ct : citaTratamientoCollection) {
            ct.setTratamiento(this);
        }
    }

    // Agrega un CitaTratamiento a esta colección y sincroniza la relación
    public void addCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.add(citaTratamiento); // Añade a la colección local
        citaTratamiento.setTratamiento(this); // Sincroniza el lado inverso
    }

    // Elimina un CitaTratamiento de esta colección y limpia la relación
    public void removeCitaTratamiento(CitaTratamiento citaTratamiento) {
        this.citaTratamientoCollection.remove(citaTratamiento); // Elimina de la colección local
        citaTratamiento.setTratamiento(null); // Limpia la referencia inversa
    }

    // Calcula el código hash basado en el ID del tratamiento
    @Override
    public int hashCode() {
        int hash = 0; // Inicializa el hash
        // Si el ID no es nulo, suma su código hash
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

    // Retorna una representación en texto del tratamiento
    @Override
    public String toString() {
        // Retorna una cadena con los datos principales del tratamiento
        return "Tratamiento{" + "codTratamiento=" + codTratamiento + ", nombreTratamiento=" + nombreTratamiento + 
               ", precioEstimado=" + precioEstimado + ", duracionMinutos=" + duracionMinutos + '}';
    }

}
