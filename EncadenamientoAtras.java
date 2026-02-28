import java.io.*;
import java.util.*;

class EncadenamientoAtras {
    private final BaseConocimiento bc;
    private final Scanner scanner;

    public EncadenamientoAtras(BaseConocimiento bc, Scanner scanner) {
        this.bc = bc;
        this.scanner = scanner;
    }

    public boolean demostrar(String objetivo, List<String> pila, PrintStream out) {
        objetivo = objetivo.trim();

        if (bc.contieneHecho(objetivo)) {
            out.println(objetivo + ", ya esta en los hechos");
            return true;
        }

        if (pila.contains(objetivo)) {
            out.println(" (REGRESION) Ciclo detectado (SE) asumimos falso: " + objetivo);
            return false;
        }

        pila.add(objetivo);
        out.println("META actualizada a: " + objetivo);

        boolean probado = false;
        List<Regla> reglasCopia = bc.obtenerReglas();

        for (Regla regla : reglasCopia) {
            if (!regla.getConsecuente().equals(objetivo))
                continue;

            out.println(" Probamos " + regla.toStringConNumero());

            boolean todosCumplen = true;
            for (Condicion cond : regla.getAntecedentes()) {
                String subObjetivo = cond.getHecho();
                boolean resultado;

                if (cond.esNegada()) {
                    resultado = !demostrar(subObjetivo, new ArrayList<>(pila), out);
                } else {
                    resultado = demostrar(subObjetivo, pila, out);
                }

                if (!resultado) {
                    todosCumplen = false;
                    break;
                }
            }

            if (todosCumplen) {
                bc.agregarHecho(objetivo);
                out.println(" Disparamos la regla:\n\t" + regla.toStringConNumero());
                out.println(" y agregamos: " + objetivo + " a los hechos");
                probado = true;
                break;
            }
        }

        if (!probado) {
            out.printf("  ¿Es cierto \"%s\"? (s/n): ", objetivo);
            String resp = scanner.nextLine().trim().toLowerCase();
            if (resp.equals("s") || resp.equals("si") || resp.equals("y")) {
                bc.agregarHecho(objetivo);
                out.println("  Aceptado por el usuario.");
                probado = true;
            }
        }

        pila.remove(pila.size() - 1);
        return probado;
    }
}