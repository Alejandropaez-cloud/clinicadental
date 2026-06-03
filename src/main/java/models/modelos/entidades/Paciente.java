package models.modelos.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entidad que representa a un paciente de la clÃƒÂ­nica dental.
 * Cada paciente puede tener un historial clÃƒÂ­nico (1:1) y varias citas (1:N).
 */
@Entity // Indica que esta clase es una entidad JPA y se mapea a una tabla de la BD
@Table(name = "Paciente") // Nombre exacto de la tabla en la base de datos

// Las NamedQueries son consultas predefinidas en JPQL que podemos reutilizar
// desde el cÃƒÂ³digo sin escribir la consulta completa cada vez
@NamedQueries({
    @NamedQuery(name = "Paciente.findAll", query = "SELECT p FROM Paciente p"),
    @NamedQuery(name = "Paciente.findById", query = "SELECT p FROM Paciente p WHERE p.codPaciente = :codPaciente"),
    @NamedQuery(name = "Paciente.findByDNI", query = "SELECT p FROM Paciente p WHERE p.dni = :dni"),
    @NamedQuery(name = "Paciente.findByNombre", query = "SELECT p FROM Paciente p WHERE p.nombre = :nombre"),
    @NamedQuery(name = "Paciente.findByApellidos", query = "SELECT p FROM Paciente p WHERE p.apellidos = :apellidos")
})
// Implementamos Serializable porque es una buena prÃƒÂ¡ctica en JPA,
// aunque no es obligatorio. Sirve para poder serializar los objetos
// (por ejemplo, si se envÃƒÂ­an por red o se guardan en sesiÃƒÂ³n)
public class Paciente implements Serializable {

    private static final long serialVersionUID = 1L;

    // @Id marca este campo como clave primaria de la tabla
    // @GeneratedValue indica que el valor lo genera automÃƒÂ¡ticamente la BD
    // GenerationType.IDENTITY se usa con AUTO_INCREMENT en MySQL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false) // Indica que este campo es obligatorio (NOT NULL)
    @Column(name = "codPaciente") // Nombre exacto de la columna en la BD
    private Integer codPaciente;

    // @Basic(optional = false) significa que en la BD es NOT NULL
    @Basic(optional = false)
    @Column(name = "DNI")
    private String dni;

    @Basic(optional = false)
    @Column(name = "Nombre")
    private String nombre;

    @Basic(optional = false)
    @Column(name = "Apellidos")
    private String apellidos;

    // @Temporal(TemporalType.DATE) se usa para campos de tipo fecha (sin hora)
    // TemporalType.DATE -> solo fecha (java.sql.Date)
    // TemporalType.TIMESTAMP -> fecha y hora (java.sql.Timestamp)
    // TemporalType.TIME -> solo hora (java.sql.Time)
    @Basic(optional = false)
    @Column(name = "Fecha_Nacimiento")
    @Temporal(TemporalType.DATE)
    private Date fechaNacimiento;

    // Sin @Basic(optional = false) significa que en la BD puede ser NULL
    @Column(name = "telefono")
    private String telefono;

    @Column(name = "email")
    private String email;

    @Column(name = "direccion")
    private String direccion;

    // RelaciÃƒÂ³n 1:1 con HistorialClinico
    // mappedBy = "paciente" -> la otra entidad (HistorialClinico) es la dueÃƒÂ±a
    // de la relaciÃƒÂ³n (tiene la FK). El nombre "paciente" es el atributo en
    // la clase HistorialClinico que hace referencia a esta entidad.
    // cascade = CascadeType.ALL -> todas las operaciones (persist, merge, remove...)
    // se propagan al historial. Si guardo un Paciente, tambiÃƒÂ©n se guarda su Historial.
    // orphanRemoval = true -> si elimino el historial de la colecciÃƒÂ³n (o lo pongo a null),
    // tambiÃƒÂ©n se borra de la BD automÃƒÂ¡ticamente
    @OneToOne(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private HistorialClinico historialClinico;

    // RelaciÃƒÂ³n 1:N con Cita (Un paciente puede tener muchas citas)
    // mappedBy = "paciente" -> la entidad Cita es la dueÃƒÂ±a de la relaciÃƒÂ³n
    // (la tabla Cita tiene la FK codPaciente)
    // cascade = CascadeType.PERSIST -> al persistir un Paciente, tambiÃƒÂ©n se
    // persistirÃƒÂ¡n sus Citas asociadas
    // orphanRemoval = true -> si elimino una Cita de la colecciÃƒÂ³n del Paciente,
    // esa Cita se elimina automÃƒÂ¡ticamente de la BD
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private Collection<Cita> citaCollection;

    // Constructor vacÃƒÂ­o (obligatorio para JPA)
    public Paciente() {
    }

    // Constructor solo con la PK
    public Paciente(Integer codPaciente) {
        this.codPaciente = codPaciente;
    }

    // Constructor con los campos obligatorios (sin ID porque es autoincremental)
    public Paciente(String dni, String nombre, String apellidos, Date fechaNacimiento) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.citaCollection = new ArrayList<>(); // Inicializamos la colecciÃƒÂ³n vacÃƒÂ­a
    }

    // Getters y setters ------------------------------------------------
    public Integer getCodPaciente() {
        return codPaciente;
    }

    public void setCodPaciente(Integer codPaciente) {
        this.codPaciente = codPaciente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public HistorialClinico getHistorialClinico() {
        return historialClinico;
    }

    // Al establecer el historial, tambiÃƒÂ©n sincronizamos la relaciÃƒÂ³n
    // bidireccional: le decimos al historial que su paciente es este
    public void setHistorialClinico(HistorialClinico historialClinico) {
        this.historialClinico = historialClinico;
        if (historialClinico != null) {
            historialClinico.setPaciente(this);
        }
    }

    public Collection<Cita> getCitaCollection() {
        return citaCollection; // Retorna la colecciÃƒÂ³n de citas del paciente
    }

    // Establece la colecciÃƒÂ³n de citas y sincroniza la relaciÃƒÂ³n bidireccional
    // Al asignar una colecciÃƒÂ³n de citas, tambiÃƒÂ©n actualiza el lado inverso
    public void setCitaCollection(Collection<Cita> citaCollection) {
        this.citaCollection = citaCollection; // Asigna la nueva colecciÃƒÂ³n
        // Sincroniza el lado inverso: cada Cita debe saber que pertenece a este Paciente
        for (Cita cita : citaCollection) {
            cita.setPaciente(this);
        }
    }

    // Agrega una cita al paciente y sincroniza la relaciÃƒÂ³n bidireccional
    public void addCita(Cita cita) {
        this.citaCollection.add(cita); // AÃƒÂ±ade a la colecciÃƒÂ³n local
        cita.setPaciente(this); // Sincroniza el lado inverso
    }

    // Elimina una cita del paciente y limpia la relaciÃƒÂ³n bidireccional
    public void removeCita(Cita cita) {
        this.citaCollection.remove(cita); // Elimina de la colecciÃƒÂ³n local
        cita.setPaciente(null); // Limpia la referencia inversa
    }

    // Calcula el cÃƒÂ³digo hash basado en el ID del paciente
    @Override
    public int hashCode() {
        int hash = 0; // Inicializa el hash
        // Si el ID no es nulo, suma su cÃƒÂ³digo hash
        hash += (codPaciente != null ? codPaciente.hashCode() : 0);
        return hash; // Retorna el hash calculado
    }

    // Compara dos pacientes por su ID
    @Override
    public boolean equals(Object object) {
        // Verifica si el otro objeto es una instancia de Paciente
        if (!(object instanceof Paciente)) {
            return false; // No son del mismo tipo
        }
        Paciente other = (Paciente) object; // Convierte a Paciente
        // Retorna verdadero si ambos tienen el mismo ID (o ambos son nulos)
        return !((this.codPaciente == null && other.codPaciente != null) || 
                 (this.codPaciente != null && !this.codPaciente.equals(other.codPaciente)));
    }

    // Retorna una representaciÃƒÂ³n en texto del paciente
    @Override
    public String toString() {
        String tmp = ""; // Variable temporal para el historial
        // Si existe historial, lo agrega a la representaciÃƒÂ³n
        if (historialClinico != null) {
            tmp += historialClinico + "\n";
        }
        // Retorna una cadena con todos los datos del paciente
        return "Paciente{" + "codPaciente=" + codPaciente + ", dni=" + dni + ", nombre=" + nombre + 
               ", apellidos=" + apellidos + ", fechaNacimiento=" + fechaNacimiento + 
               ", telefono=" + telefono + ", email=" + email + ", direccion=" + direccion + 
               ", historial=" + tmp + '}';
    }

}
