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
            String destino = archivoHechos.isEmpty() ? "hechos_actualizados.txt" : archivoHechos;
            destino = ui.preguntar("Ruta para guardar [" + destino + "]: ");
            if (destino.isEmpty()) destino = archivoHechos.isEmpty() ? "hechos_actualizados.txt" : archivoHechos;

            try {
                bc.guardarHechos(destino);
                System.out.println("Guardado en: " + destino);
            } catch (IOException e) {
                System.out.println("Error al guardar: " + e.getMessage());
            }
        }

        System.out.println("\nFin del programa.");
    }
}