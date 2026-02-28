import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MotorInferencia {
    public static void main(String[] args) {
        InterfazUsuario ui = new InterfazUsuario();
        BaseConocimiento bc = new BaseConocimiento();

        System.out.println("=====================================");
        System.out.println("     MOTOR DE INFERENCIA - 2025     ");
        System.out.println("=====================================\n");

        System.out.println("¿Cómo desea ingresar los datos?");
        System.out.println("  1) Cargar desde archivos");
        System.out.println("  2) Ingresar hechos y reglas por teclado");
        String modo = ui.preguntar("Elige (1/2): ").trim();

        if ("1".equals(modo)) {
            // Modo archivos
            String archivoHechos = ui.preguntar("Archivo de hechos (Enter = ninguno): ");
            String archivoReglas = ui.preguntar("Archivo de reglas: ");

            try {
                if (!archivoHechos.isEmpty()) {
                    bc.cargarHechos(archivoHechos);
                }
                bc.cargarReglas(archivoReglas);
            } catch (IOException e) {
                System.out.println("Error al leer archivos: " + e.getMessage());
                return;
            }

            bc.imprimirEstado();

        } else if ("2".equals(modo)) {
            // Modo manual
            System.out.println("\n--- Ingrese los HECHOS (uno por línea) ---");
            System.out.println("Terminar con línea en blanco (Enter)\n");

            while (true) {
                String linea = ui.preguntar("> ");
                if (linea.isEmpty()) break;
                bc.agregarHecho(linea);
            }

            System.out.println("\n--- Ingrese las REGLAS (una por línea) ---");
            System.out.println("Ejemplo: motor_no_arranca & llave_gira -> llamar_mecanico");
            System.out.println("Terminar con línea en blanco (Enter)\n");

            int numRegla = 0;
            while (true) {
                String linea = ui.preguntar("> ");
                if (linea.isEmpty()) break;

                String[] partes = linea.split("=>|ENTONCES|->", 2);
                if (partes.length != 2) {
                    System.out.println("Formato inválido → ignorado: " + linea);
                    continue;
                }

                String anteTxt = partes[0].trim();
                String conseq = partes[1].trim();

                List<Condicion> antecedentes = bc.parsearAntecedentes(anteTxt);

                numRegla++;
                Regla regla = new Regla(antecedentes, conseq, numRegla);
                bc.agregarRegla(regla);
            }

            bc.imprimirEstado();

        } else {
            System.out.println("Opción inválida. Terminando.");
            return;
        }

        // Selección de método de inferencia
        System.out.println("\nOpciones:");
        System.out.println("  1 = Encadenamiento hacia adelante");
        System.out.println("  2 = Encadenamiento hacia atrás");
        String opcion = ui.preguntar("Elige (1/2): ").trim();

        if ("1".equals(opcion)) {
            EncadenamientoAdelante motor = new EncadenamientoAdelante(bc);
            motor.ejecutar(System.out);
        } else if ("2".equals(opcion)) {
            String meta = ui.preguntar("Meta a demostrar: ").trim();
            if (meta.isEmpty()) {
                System.out.println("No se ingresó meta.");
                return;
            }
            EncadenamientoAtras motor = new EncadenamientoAtras(bc, new Scanner(System.in));
            List<String> pila = new ArrayList<>();
            boolean exito = motor.demostrar(meta, pila, System.out);
            System.out.println("\nMETA '" + meta + "' → " + (exito ? "VERDADERO" : "FALSO"));
        } else {
            System.out.println("Opción inválida.");
            return;
        }

        // Mostrar resultados finales
        ui.mostrarHechos(bc.obtenerTodosHechos());

        // Guardar
        if (ui.preguntarSiNo("¿Guardar los hechos actualizados?")) {
            String nombreDefecto = "hechos_actualizados.txt";
            String nombre = ui.preguntar(
                "Nombre del archivo (Enter = " + nombreDefecto + "): "
            );

            if (nombre.isEmpty()) nombre = nombreDefecto;
            if (!nombre.toLowerCase().endsWith(".txt")) nombre += ".txt";

            String ruta = "Generados/" + nombre;

            try {
                Files.createDirectories(Paths.get("Generados"));
                bc.guardarHechos(ruta);
                System.out.println("Guardado en: " + ruta);
            } catch (IOException e) {
                System.out.println("Error al guardar:");
                System.out.println(" → " + e.getMessage());
            }
        }

        System.out.println("\nFin del programa.");
    }
}