import java.util.*;

class InterfazUsuario {
    private final Scanner scanner = new Scanner(System.in);

    public String preguntar(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public boolean preguntarSiNo(String mensaje) {
        while (true) {
            String r = preguntar(mensaje + " (s/n): ").toLowerCase();
            if (r.equals("s") || r.equals("si") || r.equals("y")) return true;
            if (r.equals("n") || r.equals("no")) return false;
            System.out.println("Respuesta inválida, usa s/n");
        }
    }

    public void mostrarHechos(List<String> hechos) {
        System.out.println("\nHechos actualizados:");
        if (hechos.isEmpty()) {
            System.out.println("  (ninguno)");
        } else {
            for (String h : hechos) {
                System.out.println("  • " + h);
            }
        }
    }
}
