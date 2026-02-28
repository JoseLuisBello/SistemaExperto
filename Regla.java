import java.util.*;

class Regla {
    private final List<Condicion> antecedentes;
    private final String consecuente;

    public Regla(List<Condicion> antecedentes, String consecuente) {
        this.antecedentes = new ArrayList<>(antecedentes);
        this.consecuente = consecuente.trim();
    }

    public List<Condicion> getAntecedentes() {
        return antecedentes;
    }

    public String getConsecuente() {
        return consecuente;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(" Y ");
        for (Condicion c : antecedentes) {
            sj.add(c.toString());
        }
        return sj.toString() + " ENTONCES " + consecuente;
    }
}
