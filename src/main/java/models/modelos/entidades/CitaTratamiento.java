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
 * En una relaciÃƒÂ³n N:M entre Cita y Tratamiento, necesitamos una tabla intermedia
 * (tabla puente) que almacene quÃƒÂ© tratamientos se realizan en cada cita.
 * 
 * Siguiendo la directriz JPA, esta entidad tiene su propio @Id con
 * @GeneratedValue(strategy = GenerationType.IDENTITY), en lugar de una
 * clave compuesta con @IdClass.
 * 
 * AdemÃƒÂ¡s, tiene dos @ManyToOne con @JoinColumn hacia Cita y Tratamiento,
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

    // ID ÃƒÂºnico de serializaciÃƒÂ³n usado para persistencia
    private static final long serialVersionUID = 1L;

    // Clave primaria autoincrementada de la tabla Cita_Tratamiento
    @Id // Marca como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // La BD genera automÃƒÂ¡ticamente el valor
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "id") // Nombre de la columna en la BD
    private Integer id;

    // RelaciÃƒÂ³n ManyToOne con Cita (muchos CitaTratamiento pueden pertenecer a una Cita)
    @ManyToOne(optional = false) // RelaciÃƒÂ³n obligatoria: todo CitaTratamiento necesita una Cita
    @JoinColumn(name = "codCita", referencedColumnName = "codCita") // Define la clave forÃƒÂ¡nea
    private Cita cita;

    // RelaciÃƒÂ³n ManyToOne con Tratamiento (muchos CitaTratamiento pueden referenciarse a un Tratamiento)
    @ManyToOne(optional = false) // RelaciÃƒÂ³n obligatoria: todo CitaTratamiento necesita un Tratamiento
    @JoinColumn(name = "codTratamiento", referencedColumnName = "codTratamiento") // Define la clave forÃƒÂ¡nea
    private Tratamiento tratamiento;

    // Cantidad de unidades de este tratamiento a realizar en la cita
    @Basic(optional = false) // Campo obligatorio
    @Column(name = "Cantidad") // Nombre de la columna en la BD
    private Integer cantidad;

    // Fecha y hora de registro de este CitaTratamiento
    @Column(name = "FechaRegistro") // Nombre de la columna en la BD
    @Temporal(TemporalType.TIMESTAMP) // Mapea a TIMESTAMP en MySQL
    private Date fechaRegistro;

    // Constructor vacÃƒÂ­o requerido por JPA
    public CitaTratamiento() {
    }

    // Constructor con solo el ID (usado para bÃƒÂºsquedas)
    public CitaTratamiento(Integer id) {
        this.id = id; // Asigna el ID
    }

    // Constructor con parÃƒÂ¡metros (usado al crear un nuevo CitaTratamiento)
    public CitaTratamiento(Cita cita, Tratamiento tratamiento, Integer cantidad) {
        this.cita = cita; // Asigna la cita
        this.tratamiento = tratamiento; // Asigna el tratamiento
        this.cantidad = cantidad; // Asigna la cantidad
        this.fechaRegistro = new Date(); // Establece la fecha actual como fecha de registro
    }

    // Obtiene el ID del registro CitaTratamiento
    public Integer getId() {
        return id;
    }

    // Establece el ID del registro CitaTratamiento
    public void setId(Integer id) {
        this.id = id;
    }

    // Obtiene la cita asociada a este CitaTratamiento
    public Cita getCita() {
        return cita;
    }

    // Establece la cita asociada a este CitaTratamiento
    public void setCita(Cita cita) {
        this.cita = cita;
    }

    // Obtiene el tratamiento asociado a este CitaTratamiento
    public Tratamiento getTratamiento() {
        return tratamiento;
    }

    // Establece el tratamiento asociado a este CitaTratamiento
    public void setTratamiento(Tratamiento tratamiento) {
        this.tratamiento = tratamiento;
    }

    // Obtiene la cantidad de unidades de tratamiento
    public Integer getCantidad() {
        return cantidad;
    }

    // Establece la cantidad de unidades de tratamiento
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    // Obtiene la fecha y hora de registro
    public Date getFechaRegistro() {
        return fechaRegistro;
    }

    // Establece la fecha y hora de registro
    public void setFechaRegistro(Date fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    // Calcula el cÃƒÂ³digo hash basado en el ID del registro
    @Override
    public int hashCode() {
        int hash = 0; // Inicializa el hash
        // Si el ID no es nulo, suma su cÃƒÂ³digo hash
        hash += (id != null ? id.hashCode() : 0);
        return hash; // Retorna el hash calculado
    }

    // Compara dos CitaTratamiento por su ID
    @Override
    public boolean equals(Object object) {
        // Verifica si el otro objeto es una instancia de CitaTratamiento
        if (!(object instanceof CitaTratamiento)) {
            return false; // No son del mismo tipo
        }
        CitaTratamiento other = (CitaTratamiento) object; // Convierte a CitaTratamiento
        // Retorna verdadero si ambos tienen el mismo ID
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    // Retorna una representaciÃƒÂ³n en texto del CitaTratamiento
    @Override
    public String toString() {
        // Retorna una cadena con los datos principales del CitaTratamiento
        return "CitaTratamiento{" + "id=" + id + ", codCita=" + cita.getCodCita() + 
               ", codTratamiento=" + tratamiento.getCodTratamiento() + ", cantidad=" + cantidad + 
               ", tratamiento=" + tratamiento.getNombreTratamiento() + '}';
    }

}
