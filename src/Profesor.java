/**
 * Especialización de Usuario para docentes.
 * Aplica herencia para definir un límite de préstamo de 5 unidades.
 */
public class Profesor extends Usuario {

    public Profesor(String id, String nombre, int librosEnPrestamo) {
        // Llama al constructor de la clase padre (Usuario)
        super(id, nombre, librosEnPrestamo);
    }

    @Override
    public boolean puedePedirPrestamo() {
        // Los profesores tienen un límite mayor por investigación
        return librosEnPrestamo < 5;
    }
}