import java.util.Scanner;

/**
 * CAPA DE PRESENTACIÓN: Gestiona la interacción con el usuario.
 * Aplica principios de robustez y delegación de responsabilidades.
 */
public class Main {
    private static Scanner sn = new Scanner(System.in);
    private static Biblioteca biblioteca = new Biblioteca();

    public static void main(String[] args) {
        boolean salir = false;

        while (!salir) {
            imprimirEncabezado();
            System.out.println("  [1] 📖 Solicitar un Préstamo");
            System.out.println("  [2] 🔄 Devolver Libro");
            System.out.println("  [3] 📊 Ver Reporte de Estado");
            System.out.println("  [4] 👤 Registrar Nuevo Usuario");
            System.out.println("  [5] 📚 Registrar Nuevo Libro");
            System.out.println("  [6] 🚪 Salir");
            System.out.print("\n  > Selecciona una opción: ");

            try {
                // Captura segura de teclado para evitar errores de buffer
                int opcion = Integer.parseInt(sn.nextLine());

                switch (opcion) {
                    case 1: menuPrestamo(); break;
                    case 2: menuDevolucion(); break;
                    case 3:
                        biblioteca.generarReporte();
                        presionarEnter();
                        break;
                    case 4: menuRegistroUsuario(); break;
                    case 5: menuRegistroLibro(); break;
                    case 6:
                        System.out.println("\n  [!] Sincronizando datos... ¡Hasta pronto!");
                        salir = true;
                        break;
                    default:
                        System.out.println("\n  ⚠️  Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n  ❌ Error: Ingrese un número válido (1-6).");
            }
        }
    }

    /**
     * FLUJO DE PRÉSTAMO: Implementa UX asistida mostrando opciones
     * de usuarios y libros con stock antes de solicitar IDs.
     */
    private static void menuPrestamo() {
        System.out.println("\n" + "-".repeat(20) + " MÓDULO DE PRÉSTAMOS " + "-".repeat(20));
        System.out.println("(Escriba '0' para cancelar)");

        System.out.println("\n[ LISTA DE USUARIOS ]");
        biblioteca.generarReporteUsuariosReducido();
        System.out.print("\n✍️  Ingrese ID del Usuario: ");
        String id = sn.nextLine();
        if (id.equals("0")) return;

        System.out.println("\n[ LIBROS DISPONIBLES ]");
        biblioteca.generarReporteLibrosDisponibles();
        System.out.print("\n✍️  Ingrese ISBN del Libro: ");
        String isbn = sn.nextLine();
        if (isbn.equals("0")) return;

        System.out.println();
        biblioteca.realizarPrestamo(id, isbn); // Delegación de lógica
        presionarEnter();
    }

    /**
     * FLUJO DE DEVOLUCIÓN: Actualiza estado de usuario y stock de libro.
     * MEJORA: Ahora muestra los usuarios registrados para facilitar la búsqueda del ID.
     */
    private static void menuDevolucion() {
        System.out.println("\n" + "-".repeat(20) + " MÓDULO DE DEVOLUCIONES " + "-".repeat(20));
        System.out.println("(Escriba '0' para cancelar)");

        // Muestra la lista de usuarios para que el bibliotecario sepa a quién procesar
        System.out.println("\n[ LISTA DE USUARIOS ]");
        biblioteca.generarReporteUsuariosReducido();

        System.out.print("\n✍️  ID del Usuario: ");
        String idDev = sn.nextLine();
        if (idDev.equals("0")) return;

        System.out.println("\n[ LIBROS DISPONIBLES ]");
        biblioteca.generarReporteLibrosDisponibles();
        System.out.print("✍️  ISBN del Libro: ");
        String isbnDev = sn.nextLine();
        if (isbnDev.equals("0")) return;

        System.out.println();
        biblioteca.devolverLibro(idDev, isbnDev);
        presionarEnter();
    }

    /**
     * FLUJO DE REGISTRO USUARIO: Captura nombre y delega creación de ID.
     */
    private static void menuRegistroUsuario() {
        System.out.println("\n" + "-".repeat(20) + " REGISTRO DE USUARIO " + "-".repeat(20));
        System.out.println("(Escriba '0' para cancelar)");

        // Selección de tipo para aplicar Herencia y Polimorfismo
        System.out.println("Seleccione tipo de cuenta:");
        System.out.println("  [1] Estudiante (Límite 2 libros)");
        System.out.println("  [2] Profesor   (Límite 5 libros)");
        System.out.print("✍️  Opción: ");

        String entradaTipo = sn.nextLine();
        if (entradaTipo.equals("0")) return;

        try {
            int tipo = Integer.parseInt(entradaTipo);
            if (tipo != 1 && tipo != 2) {
                System.out.println("⚠️  Opción no válida. Registro cancelado.");
                return;
            }

            System.out.print("👤 Nombre completo: ");
            String nombre = sn.nextLine();

            if (nombre.equals("0")) return;

            if(!nombre.trim().isEmpty()) {
                // Se envía el tipo a la biblioteca para instanciar la clase correcta
                biblioteca.registrarUsuario(nombre, tipo);
            } else {
                System.out.println("❌ El nombre es obligatorio.");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Error: Debe ingresar un número (1 o 2).");
        }
        presionarEnter();
    }

    /**
     * FLUJO DE REGISTRO LIBRO: Validación de entrada numérica para stock inicial.
     * MEJORA: Incluye sugerencia automática de ISBN.
     */
    private static void menuRegistroLibro() {
        System.out.println("\n" + "-".repeat(20) + " REGISTRO DE NUEVO LIBRO " + "-".repeat(20));
        System.out.println("(Escriba '0' para cancelar)");

        // Sugerencia de ID basada en el estado actual de la biblioteca
        System.out.println("💡 Sugerencia de ISBN disponible: " + biblioteca.sugerirProximoISBN());
        System.out.print("✍️  Ingrese ISBN (o el sugerido): ");
        String isbn = sn.nextLine();
        if (isbn.equals("0")) return;

        System.out.print("✍️  Título del libro: ");
        String titulo = sn.nextLine();
        if (titulo.equals("0")) return;

        int cantidad = 0;
        boolean cantidadValida = false;

        while (!cantidadValida) {
            try {
                System.out.print("✍️  Cantidad inicial: ");
                String entrada = sn.nextLine();
                if (entrada.equals("0")) return;

                cantidad = Integer.parseInt(entrada);
                if (cantidad > 0) cantidadValida = true;
                else System.out.println("⚠️  Debe ser mayor a 0.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Error: Ingrese un valor numérico.");
            }
        }

        if(!isbn.trim().isEmpty() && !titulo.trim().isEmpty()) {
            biblioteca.registrarLibro(isbn, titulo, cantidad);
        } else {
            System.out.println("❌ ISBN y Título son requeridos.");
        }
        presionarEnter();
    }

    private static void presionarEnter() {
        System.out.println("\n[ Presione ENTER para volver al menú ]");
        sn.nextLine();
    }

    private static void imprimirEncabezado() {
        System.out.println("\n\n" + "=".repeat(60));
        System.out.println("   ____  _ ____  _     ___ ___  _____ _____ ____  ____ ");
        System.out.println("  | __ )| | __ )| |   |_ _/ _ \\|_   _| ____/ ___|/ ___|");
        System.out.println("  |  _ \\| |  _ \\| |    | | | | | | | |  _| | |   | |    ");
        System.out.println("  | |_) | | |_) | |___ | | |_| | | | | |___| |___| |___ ");
        System.out.println("  |____/|_|____/|_____|___\\___/  |_| |_____|\\____|\\____|");
        System.out.println("\n" + " ".repeat(15) + "SISTEMA DE GESTIÓN v1.2");
        System.out.println("=".repeat(60));
    }
}