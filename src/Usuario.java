import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para la jerarquía de usuarios.
 * Se define como abstracta para implementar Polimorfismo en las reglas de préstamo.
 */
public abstract class Usuario {
    protected String id; // Cambiado a protected para que las hijas accedan fácilmente
    protected String nombre;
    protected int librosEnPrestamo; // Contador para validar el límite

    public Usuario(String id, String nombre, int librosEnPrestamo) {
        this.id = id;
        this.nombre = nombre;
        this.librosEnPrestamo = librosEnPrestamo;
    }

    // Getters
    public String getId() { return id; }

    public int getLibrosEnPrestamo() { return librosEnPrestamo; }

    public String getNombre() { return nombre; }

    // Lógica de validación
    /**
     * POLIMORFISMO: Se define como abstracto para que cada tipo de usuario
     * determine su propio límite (Estudiante 2, Profesor 5).
     */
    public abstract boolean puedePedirPrestamo();

    // Métodos de actualización de estado
    public void incrementarLibros() {
        this.librosEnPrestamo++;
    }

    public void decrementarLibros() {
        if (this.librosEnPrestamo > 0) {
            this.librosEnPrestamo--;
        }
    }

    @Override
    public String toString() {
        // Formato CSV modificado: Incluimos el tipo de clase al principio para la persistencia
        return this.getClass().getSimpleName() + "," + id + "," + nombre + "," + librosEnPrestamo;
    }
}