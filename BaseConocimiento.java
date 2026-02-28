import java.io.*;
import java.util.*;

class BaseConocimiento {
    private final List<String> hechos = new ArrayList<>();
    private final List<Regla> reglas = new ArrayList<>();

    public void agregarHecho(String hecho) {
        hecho = hecho.trim();
        if (!hechos.contains(hecho)) {
            hechos.add(hecho);
        }
    }

    public boolean contieneHecho(String hecho) {
        return hechos.contains(hecho.trim());
    }

    public void agregarRegla(Regla regla) {
        reglas.add(regla);
    }

    public List<Regla> obtenerReglas() {
        return new ArrayList<>(reglas);
    }

    public List<String> obtenerTodosHechos() {
        return new ArrayList<>(hechos);
    }

    public void cargarHechos(String ruta) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    agregarHecho(linea);
                }
            }
        }
    }

    public void cargarReglas(String ruta) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            int numLinea = 0;
            int numeroRegla = 0;

            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#") || linea.startsWith("//")) continue;

                String[] partes = linea.split("=>|ENTONCES|->", 2);
                if (partes.length != 2) {
                    System.out.println("Línea ignorada #" + numLinea + ": " + linea);
                    continue;
                }

                String textoAntecedentes = partes[0].trim();
                String consecuente = partes[1].trim();

                List<Condicion> antecedentes = parsearAntecedentes(textoAntecedentes);

                numeroRegla++;
                Regla regla = new Regla(antecedentes, consecuente, numeroRegla);
                agregarRegla(regla);
            }
        }
    }

    public List<Condicion> parsearAntecedentes(String texto) {
        List<Condicion> lista = new ArrayList<>();
        texto = texto.replaceAll("\\s*&\\s*", " & ");
        String[] tokens = texto.split("\\s*&\\s*");

        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty()) continue;

            boolean negada = false;
            if (token.startsWith("!")) {
                negada = true;
                token = token.substring(1).trim();
            } else if (token.startsWith("~")) {
                negada = true;
                token = token.substring(1).trim();
            }

            if (!token.isEmpty()) {
                lista.add(new Condicion(negada, token));
            }
        }
        return lista;
    }

    public void guardarHechos(String ruta) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (String hecho : hechos) {
                bw.write(hecho);
                bw.newLine();
            }
        }
    }

    public void imprimirEstado() {
        System.out.println("\nHechos actuales (" + hechos.size() + "):");
        if (hechos.isEmpty()) {
            System.out.println("  (ninguno)");
        } else {
            for (String h : hechos) {
                System.out.println("  • " + h);
            }
        }

        System.out.println("\nReglas cargadas (" + reglas.size() + "):");
        if (reglas.isEmpty()) {
            System.out.println("  (ninguna)");
        } else {
            for (Regla r : reglas) {
                System.out.println("  " + r.toStringConNumero());
            }
        }
        System.out.println();
    }
}