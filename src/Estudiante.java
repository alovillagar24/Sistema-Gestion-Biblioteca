/**
 * Especialización de Usuario para alumnos.
 * Aplica herencia para definir un límite de préstamo de 2 unidades.
 */
public class Estudiante extends Usuario {

    public Estudiante(String id, String nombre, int librosEnPrestamo) {
        // Llama al constructor de la clase padre (Usuario)
        super(id, nombre, librosEnPrestamo);
    }

    @Override
    public boolean puedePedirPrestamo() {
        // Implementación de la regla específica para estudiantes
        return librosEnPrestamo < 2;
    }
}