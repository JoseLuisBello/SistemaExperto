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
            while ((linea = br.readLine()) != null) {
                numLinea++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#") || linea.startsWith("//"))
                    continue;

                // Aceptamos => ENTONCES ->
                String[] partes = linea.split("=>|ENTONCES|->", 2);
                if (partes.length != 2) {
                    System.out.println("Línea ignorada (formato inválido) #" + numLinea + ": " + linea);
                    continue;
                }

                String textoAntecedentes = partes[0].trim();
                String consecuente = partes[1].trim();

                List<Condicion> antecedentes = parsearAntecedentes(textoAntecedentes + "->" + consecuente);
                if (!antecedentes.isEmpty() || consecuente.isEmpty()) {
                    agregarRegla(new Regla(antecedentes, consecuente));
                } else {
                    System.out.println("Regla vacía ignorada en línea #" + numLinea);
                }
            }
        }
    }

    private List<Condicion> parsearAntecedentes(String texto) {
        List<Condicion> lista = new ArrayList<>();

        texto = texto.replaceAll("\\s*->\\s*", "->");
        texto = texto.replaceAll("\\s*&\\s*", "&");

        String[] partes = texto.split("->", 2);
        if (partes.length != 2) {
            return lista;
        }

        String ante = partes[0].trim();
        String[] tokens = ante.split("&");
        for (String token : tokens) {
            token = token.trim();
            if (token.isEmpty())
                continue;

            boolean negada = false;
            if (token.startsWith("!")) {
                negada = true;
                token = token.substring(1).trim();
            }
            else if (token.startsWith("~")) {
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
}