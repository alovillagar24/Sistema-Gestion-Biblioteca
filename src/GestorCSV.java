import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorCSV {
    public static List<String[]> leerArchivo(String ruta) {
        List<String[]> datos = new ArrayList<>();
        File archivo = new File(ruta);

        // Verificamos si el archivo existe antes de intentar leerlo
        if (!archivo.exists()) {
            System.out.println("Advertencia: El archivo " + ruta + " no existe. Se creará uno nuevo.");
            return datos;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Solo procesamos la línea si no está vacía
                if (!linea.trim().isEmpty()) {
                    datos.add(linea.split(","));
                }
            }
        } catch (IOException e) {
            System.out.println("Error crítico al leer: " + e.getMessage());
        }
        return datos;
    }

    public static void guardarArchivo(String ruta, List<?> objetos) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ruta))) {
            for (Object obj : objetos) {
                pw.println(obj.toString());
            }
        } catch (IOException e) {
            System.out.println("Error crítico al guardar: " + e.getMessage());
        }
    }
}