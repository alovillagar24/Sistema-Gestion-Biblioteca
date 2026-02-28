import java.util.ArrayList;
import java.util.List;

/**
 * GESTOR DE LÓGICA: Administra las colecciones de libros y usuarios.
 * Aplica Polimorfismo al manejar diferentes tipos de Usuario de forma genérica.
 */
public class Biblioteca {
    private List<Libro> libros = new ArrayList<>();
    private List<Usuario> usuarios = new ArrayList<>();

    public Biblioteca() {
        cargarDatos();
    }

    /**
     * CARGA DE DATOS: Reconstruye objetos específicos (Estudiante/Profesor)
     * basándose en la primera columna del archivo CSV.
     */
    private void cargarDatos() {
        try {
            List<String[]> datosLibros = GestorCSV.leerArchivo("libros.csv");
            for (String[] fila : datosLibros) {
                if (fila.length == 4) {
                    libros.add(new Libro(fila[0], fila[1],
                            Integer.parseInt(fila[2].trim()),
                            Integer.parseInt(fila[3].trim())));
                }
            }

            List<String[]> datosUsuarios = GestorCSV.leerArchivo("usuarios.csv");
            for (String[] fila : datosUsuarios) {
                if (fila.length == 4) { // Formato: Tipo, ID, Nombre, Prestados
                    String tipo = fila[0];
                    String id = fila[1];
                    String nombre = fila[2];
                    int prestados = Integer.parseInt(fila[3].trim());

                    // Instanciación polimórfica según el tipo guardado
                    if (tipo.equals("Estudiante")) {
                        usuarios.add(new Estudiante(id, nombre, prestados));
                    } else if (tipo.equals("Profesor")) {
                        usuarios.add(new Profesor(id, nombre, prestados));
                    }
                }
            }
            System.out.println("[OK] Datos cargados correctamente.");
        } catch (Exception e) {
            System.out.println("Aviso: Iniciando con listas vacías.");
        }
    }

    // --- MÉTODOS DE APOYO PARA INTERFAZ ---

    public String sugerirProximoISBN() {
        if (libros.isEmpty()) return "LIB-001";
        int maxId = libros.stream()
                .map(l -> {
                    try {
                        if (l.getIsbn().startsWith("LIB-")) {
                            return Integer.parseInt(l.getIsbn().substring(4));
                        }
                        return 0;
                    } catch (Exception e) { return 0; }
                })
                .max(Integer::compare)
                .orElse(0);
        return String.format("LIB-%03d", maxId + 1);
    }

    public void generarReporteUsuariosReducido() {
        if (usuarios.isEmpty()) {
            System.out.println("   (No hay usuarios registrados)");
        } else {
            System.out.printf("   %-8s | %-15s | %-12s | %-10s\n", "ID", "TIPO", "NOMBRE", "PRÉSTAMOS");
            System.out.println("   " + "-".repeat(55));
            usuarios.forEach(u -> System.out.printf("   %-8s | %-15s | %-12s | [%d]\n",
                    u.getId(), u.getClass().getSimpleName(), u.getNombre(), u.getLibrosEnPrestamo()));
        }
    }

    public void generarReporteLibrosDisponibles() {
        long disponibles = libros.stream().filter(l -> l.getEjemplaresDisponibles() > 0).count();
        if (disponibles == 0) {
            System.out.println("   (No hay libros con stock disponible en este momento)");
        } else {
            System.out.printf("   %-12s | %-25s | %-8s\n", "ISBN", "TÍTULO", "STOCK");
            System.out.println("   " + "-".repeat(52));
            libros.stream()
                    .filter(l -> l.getEjemplaresDisponibles() > 0)
                    .forEach(l -> System.out.printf("   %-12s | %-25s | %d\n",
                            l.getIsbn(), l.getTitulo(), l.getEjemplaresDisponibles()));
        }
    }

    // --- LÓGICA DE NEGOCIO ---

    public void registrarLibro(String isbn, String titulo, int cantidad) {
        if (buscarLibro(isbn) != null) {
            System.out.println("❌ Error: Ya existe un libro con el ISBN " + isbn);
            return;
        }
        Libro nuevoLibro = new Libro(isbn, titulo, cantidad, cantidad);
        libros.add(nuevoLibro);
        guardarTodo();
        System.out.println("✔️ Libro registrado con éxito: " + titulo + " [" + cantidad + " unidades]");
    }

    /**
     * REGISTRO ESPECIALIZADO: Crea la instancia correcta (Estudiante/Profesor)
     * según la elección del usuario en el menú.
     */
    public void registrarUsuario(String nombre, int tipoSeleccionado) {
        String nuevoId = generarSiguienteID();
        Usuario nuevoUsuario;

        if (tipoSeleccionado == 1) {
            nuevoUsuario = new Estudiante(nuevoId, nombre, 0);
        } else {
            nuevoUsuario = new Profesor(nuevoId, nombre, 0);
        }

        usuarios.add(nuevoUsuario);
        guardarTodo();
        System.out.println("✔️ " + nuevoUsuario.getClass().getSimpleName() + " registrado: " + nombre + " (ID: " + nuevoId + ")");
    }

    public String generarSiguienteID() {
        int maxId = usuarios.stream()
                .map(u -> {
                    try {
                        return Integer.parseInt(u.getId().substring(1));
                    } catch (Exception e) { return 0; }
                })
                .max(Integer::compare)
                .orElse(0);
        return String.format("U%03d", maxId + 1);
    }

    /**
     * PRÉSTAMO POLIMÓRFICO: El método 'puedePedirPrestamo' ejecutará
     * la lógica de Estudiante (2) o Profesor (5) automáticamente.
     */
    public void realizarPrestamo(String idUsuario, String isbnLibro) {
        Usuario u = buscarUsuario(idUsuario);
        Libro l = buscarLibro(isbnLibro);

        if (u == null || l == null) {
            System.out.println("❌ Error: Usuario o Libro no encontrado.");
            return;
        }

        // POLIMORFISMO: u.puedePedirPrestamo() decide según el objeto real
        if (l.getEjemplaresDisponibles() > 0 && u.puedePedirPrestamo()) {
            l.prestar();
            u.incrementarLibros();
            System.out.println("✨ Préstamo realizado: " + l.getTitulo());
            guardarTodo();
        } else {
            System.out.println("🚫 No se pudo realizar el préstamo:");
            if (l.getEjemplaresDisponibles() <= 0) System.out.println("   - Sin stock disponible.");
            if (!u.puedePedirPrestamo()) System.out.println("   - Límite de libros alcanzado para este tipo de usuario.");
        }
    }

    public void devolverLibro(String idUsuario, String isbnLibro) {
        Usuario u = buscarUsuario(idUsuario);
        Libro l = buscarLibro(isbnLibro);

        if (u != null && l != null) {
            if (u.getLibrosEnPrestamo() > 0) {
                l.devolver();
                u.decrementarLibros();
                System.out.println("✔️ Devolución exitosa. Stock actualizado.");
                guardarTodo();
            } else {
                System.out.println("❌ El usuario no tiene libros pendientes.");
            }
        } else {
            System.out.println("❌ Error: Datos incorrectos para la devolución.");
        }
    }

    private Usuario buscarUsuario(String id) {
        return usuarios.stream().filter(u -> u.getId().equalsIgnoreCase(id)).findFirst().orElse(null);
    }

    private Libro buscarLibro(String isbn) {
        return libros.stream().filter(l -> l.getIsbn().equals(isbn)).findFirst().orElse(null);
    }

    public void generarReporte() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("            REPORTE GENERAL DE ESTADO");
        System.out.println("=".repeat(50));
        System.out.println("\n[ CATALOGO DE LIBROS ]");
        libros.forEach(l -> System.out.printf("ISBN: %-10s | %-20s | Stock: %d\n",
                l.getIsbn(), l.getTitulo(), l.getEjemplaresDisponibles()));

        System.out.println("\n[ PADRÓN DE USUARIOS ]");
        usuarios.forEach(u -> System.out.printf("ID: %-8s (%-10s) | %-20s | Libros: %d\n",
                u.getId(), u.getClass().getSimpleName(), u.getNombre(), u.getLibrosEnPrestamo()));
        System.out.println("=".repeat(50) + "\n");
    }

    public void guardarTodo() {
        GestorCSV.guardarArchivo("libros.csv", libros);
        GestorCSV.guardarArchivo("usuarios.csv", usuarios);
    }
}