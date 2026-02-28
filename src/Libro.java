public class Libro {
    private String isbn;
    private String titulo;
    private int ejemplaresTotales;
    private int ejemplaresDisponibles;

    public Libro(String isbn, String titulo, int totales, int disponibles) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.ejemplaresTotales = totales;
        this.ejemplaresDisponibles = disponibles;
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitulo() { return titulo; }
    public int getEjemplaresDisponibles() { return ejemplaresDisponibles; }

    // Lógica de Negocio
    public void prestar() {
        if (this.ejemplaresDisponibles > 0) {
            this.ejemplaresDisponibles--;
        }
    }

    public void devolver() {
        // Validación: no permitir que disponibles supere al total original
        if (this.ejemplaresDisponibles < ejemplaresTotales) {
            this.ejemplaresDisponibles++;
        }
    }

    @Override
    public String toString() {
        // Formato CSV exacto para que el GestorCSV lo lea sin errores
        return isbn + "," + titulo + "," + ejemplaresTotales + "," + ejemplaresDisponibles;
    }
}