import java.io.*;
import java.util.*;

public class SistemaEncadenamientoAdelante {
    
    // Clase que representa una regla
    static class Regla {
        List<String> antecedentes;  // ej: ["clic_link_sospechoso", "!pagina_pide_credenciales"]
        String consecuente;

        public Regla(List<String> antecedentes, String consecuente) {
            this.antecedentes = antecedentes;
            this.consecuente = consecuente;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("SI ");
            for (int i = 0; i < antecedentes.size(); i++) {
                sb.append(antecedentes.get(i));
                if (i < antecedentes.size() - 1) {
                    sb.append(" Y ");
                }
            }
            sb.append(" ENTONCES ").append(consecuente);
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> hechos = new ArrayList<>();
        List<Regla> reglas = new ArrayList<>();

        System.out.println("=== SISTEMA EXPERTO - Encadenamiento Hacia Adelante ===");
        System.out.println("¿Como quieres cargar la base de conocimiento?");
        System.out.println("1.- Desde archivos de texto");
        System.out.println("2.- Ingresar manualmente hechos y reglas");
        System.out.print("Opción: ");
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        if (opcion == 1) {
            System.out.print("Nombre del archivo de HECHOS: ");
            String archivoHechos = scanner.nextLine().trim();
            System.out.print("Nombre del archivo de REGLAS: ");
            String archivoReglas = scanner.nextLine().trim();

            hechos = cargarHechos(archivoHechos);
            reglas = cargarReglas(archivoReglas);
        } else if (opcion == 2) {
            System.out.println("\nIngresa los HECHOS iniciales (uno por línea, línea vacía para terminar):");
            while (true) {
                String hecho = scanner.nextLine().trim();
                if (hecho.isEmpty()) break;
                if (!hechos.contains(hecho)) hechos.add(hecho);
            }

            System.out.println("\nIngresa las REGLAS (formato: A Y !B ENTONCES C, línea vacía para terminar):");
            while (true) {
                String linea = scanner.nextLine().trim();
                if (linea.isEmpty()) break;
                Regla regla = parsearRegla(linea);
                if (regla != null) reglas.add(regla);
            }
        } else {
            System.out.println("Opción inválida. Terminando programa.");
            return;
        }

        System.out.println("\nHechos iniciales: " + hechos);
        System.out.println("Número de reglas cargadas: " + reglas.size());

        // Ejecutamos el encadenamiento hacia adelante
        encadenamientoHaciaAdelante(hechos, reglas);

        System.out.println("\nHechos finales: " + hechos);

        // Preguntamos si guardar
        System.out.print("\n¿Deseas guardar los hechos actualizados? (s/n): ");
        String respuesta = scanner.nextLine().trim().toLowerCase();
        if (respuesta.equals("s") || respuesta.equals("si")) {
            System.out.print("Nombre del archivo para guardar hechos: ");
            String archivoSalida = scanner.nextLine().trim();
            guardarHechos(hechos, archivoSalida);
        }

        System.out.println("\n¡Programa terminado! Gracias por usarlo.");
        scanner.close();
    }

    // Carga hechos desde archivo (uno por línea)
    private static List<String> cargarHechos(String nombreArchivo) {
        List<String> hechos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) hechos.add(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer archivo de hechos: " + e.getMessage());
        }
        return hechos;
    }

    // Carga reglas desde archivo
    private static List<Regla> cargarReglas(String nombreArchivo) {
        List<Regla> reglas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    Regla r = parsearRegla(linea);
                    if (r != null) reglas.add(r);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer archivo de reglas: " + e.getMessage());
        }
        return reglas;
    }

    // Parsea una regla del estilo: "clic_link Y !pagina_segura ENTONCES phishing_confirmado"
    private static Regla parsearRegla(String texto) {
        if (!texto.contains("ENTONCES")) {
            System.out.println("Formato inválido (falta ENTONCES): " + texto);
            return null;
        }

        String[] partes = texto.split("ENTONCES");
        if (partes.length != 2) return null;

        String consecuente = partes[1].trim();
        String antecedentesStr = partes[0].replace("SI", "").trim();

        List<String> antecedentes = new ArrayList<>();
        String[] items = antecedentesStr.split("Y");
        for (String item : items) {
            antecedentes.add(item.trim());
        }

        return new Regla(antecedentes, consecuente);
    }

    // Motor de inferencia: Encadenamiento hacia adelante
    private static void encadenamientoHaciaAdelante(List<String> hechos, List<Regla> reglas) {
        boolean seAgregoAlgo = true;

        while (seAgregoAlgo) {
            seAgregoAlgo = false;

            for (Regla regla : reglas) {
                System.out.println("\nEvaluando regla → " + regla);

                if (puedeDispararse(regla, hechos)) {
                    if (!hechos.contains(regla.consecuente)) {
                        System.out.println("¡REGLA DISPARADA! Se agrega: " + regla.consecuente);
                        hechos.add(regla.consecuente);
                        seAgregoAlgo = true;

                    } else {
                        System.out.println("  (ya existe el hecho: " + regla.consecuente + ")");
                    }
                } else {
                    System.out.println("  Condiciones no cumplidas");
                }
            }
        }

        System.out.println("\nNo se pueden inferir más hechos. Proceso terminado.");
    }

    // Verifica si TODOS los antecedentes de la regla se cumplen
    private static boolean puedeDispararse(Regla regla, List<String> hechos) {
        for (String antecedente : regla.antecedentes) {
            boolean negado = antecedente.startsWith("!");
            String hechoBuscado = negado ? antecedente.substring(1) : antecedente;

            boolean existe = hechos.contains(hechoBuscado);

            if (negado) {
                if (existe) return false;   // !A pero A es verdadero → falla
            } else {
                if (!existe) return false;  // A pero A no está → falla
            }
        }
        return true; // Todos los antecedentes se cumplen
    }

    // Guarda los hechos en un archivo
    private static void guardarHechos(List<String> hechos, String nombreArchivo) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo))) {
            for (String hecho : hechos) {
                bw.write(hecho);
                bw.newLine();
            }
            System.out.println("Hechos guardados exitosamente en: " + nombreArchivo);
        } catch (IOException e) {
            System.out.println("Error al guardar hechos: " + e.getMessage());
        }
    }
}
