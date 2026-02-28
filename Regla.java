import java.util.*;

class Regla {
    private final List<Condicion> antecedentes;
    private final String consecuente;
    private final int numero;

    public Regla(List<Condicion> antecedentes, String consecuente, int numero) {
        this.antecedentes = new ArrayList<>(antecedentes);
        this.consecuente  = consecuente.trim();
        this.numero       = numero;
    }

    public Regla(List<Condicion> antecedentes, String consecuente) {
        this(antecedentes, consecuente, 0);
    }

    public List<Condicion> getAntecedentes() { return antecedentes; }
    public String getConsecuente()           { return consecuente;  }
    public int getNumero()                   { return numero;       }

    @Override
    public String toString() {
        if (antecedentes.isEmpty()) {
            return "(sin antecedentes) -> " + consecuente;
        }
        StringJoiner sj = new StringJoiner(" & ");
        for (Condicion c : antecedentes) {
            sj.add(c.toString());
        }
        return sj.toString() + " -> " + consecuente;
    }

    public String toStringConNumero() {
        if (numero > 0) {
            return "R" + numero + ": " + toString();
        }
        return toString();
    }
}