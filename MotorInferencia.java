import java.io.*;
import java.util.*;

public class MotorInferencia {
    public static void main(String[] args) {
        InterfazUsuario ui = new InterfazUsuario();

        String archivoHechos = ui.preguntar("Archivo de hechos: ");
        String archivoReglas = ui.preguntar("Archivo de reglas: ");

        BaseConocimiento bc = new BaseConocimiento();

        try {
            if (!archivoHechos.isEmpty()) {
                bc.cargarHechos(archivoHechos);
                System.out.println("Hechos cargados: " + bc.obtenerTodosHechos().size());
            }
            bc.cargarReglas(archivoReglas);
            System.out.println("Reglas cargadas: " + bc.obtenerReglas().size());
        } catch (IOException e) {
            System.out.println("Error al leer archivos: " + e.getMessage());
            return;
        }

        bc.imprimirEstadoInicial();

        System.out.println("\nOpciones:");
        System.out.println("  1 = Encadenamiento hacia adelante");
        System.out.println("  2 = Encadenamiento hacia atras");
        String opcion = ui.preguntar("Elige (1/2): ");

        if ("1".equals(opcion)) {
            EncadenamientoAdelante motor = new EncadenamientoAdelante(bc);
            motor.ejecutar(System.out);
        } else if ("2".equals(opcion)) {
            String objetivo = ui.preguntar("Meta a demostrar: ");
            if (objetivo.isEmpty()) {
                System.out.println("No se ingreso objetivo.");
                return;
            }
            EncadenamientoAtras motor = new EncadenamientoAtras(bc, new Scanner(System.in));
            List<String> pila = new ArrayList<>();
            boolean exito = motor.demostrar(objetivo, pila, System.out);
            System.out.println("\nMETA: '" + objetivo + "' fue " + (exito ? "VERDADERO" : "FALSO"));
        } else {
            System.out.println("Opción inválida.");
            return;
        }

        ui.mostrarHechos(bc.obtenerTodosHechos());

        if (ui.preguntarSiNo("¿Guardar los hechos actualizados?")) {
            String nombreDefecto = "hechos_actualizados.txt";

            System.out.println("Se guardará en la carpeta 'Generados'");
            String nombre = ui.preguntar(
                    "Ingrese el nombre del archivo (Enter = " + nombreDefecto + "): ");

            if (nombre.trim().isEmpty()) {
                nombre = nombreDefecto;
            }

            if (!nombre.toLowerCase().endsWith(".txt")) {
                nombre += ".txt";
            }

            String ruta = "Generados/" + nombre;

            try {
                java.nio.file.Files.createDirectories(java.nio.file.Paths.get("Generados"));

                bc.guardarHechos(ruta);
                System.out.println("Archivo guardado en: Generados/" + nombre);
            } catch (IOException e) {
                System.out.println("No se pudo guardar el archivo:");
                System.out.println("  → " + e.getMessage());
                System.out.println("  Ruta intentada: " + ruta);
            }
        }

        System.out.println("\nFin del programa.");
    }
}