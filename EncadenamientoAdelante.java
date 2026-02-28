import java.io.*;
import java.util.*;

class EncadenamientoAdelante {
    private final BaseConocimiento bc;

    public EncadenamientoAdelante(BaseConocimiento bc) {
        this.bc = bc;
    }

    public void ejecutar(PrintStream out) {
        out.println("\n=== ENCADENAMIENTO HACIA ADELANTE ===\n");

        boolean cambio;
        int iteracion = 0;

        do {
            cambio = false;
            iteracion++;
            out.printf("Iteracion %d ------------------------------------\n", iteracion);

            List<Regla> reglasCopia = bc.obtenerReglas();
            for (Regla regla : reglasCopia) {
                out.println("Evaluando " + regla.toStringConNumero());

                boolean puedeDispararse = true;
                for (Condicion cond : regla.getAntecedentes()) {
                    boolean cumple = cond.esNegada()
                            ? !bc.contieneHecho(cond.getHecho())
                            : bc.contieneHecho(cond.getHecho());
                    if (!cumple) {
                        puedeDispararse = false;
                        break;
                    }
                }

                if (puedeDispararse && !bc.contieneHecho(regla.getConsecuente())) {
                    bc.agregarHecho(regla.getConsecuente());
                    out.println(" Inferido: " + regla.getConsecuente());
                    cambio = true;
                }
            }
        } while (cambio);

        out.println("\nNo se pueden inferir más hechos.");
    }
}