class Condicion {
    private final boolean negada;
    private final String hecho;

    public Condicion(boolean negada, String hecho) {
        this.negada = negada;
        this.hecho = hecho.trim();
    }

    public boolean esNegada() { return negada; }
    public String getHecho()  { return hecho; }

    @Override
    public String toString() {
        return (negada ? "!" : "") + hecho;
    }
}