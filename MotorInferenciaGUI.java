import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;

public class MotorInferenciaGUI extends JFrame {

    private BaseConocimiento bc = new BaseConocimiento();
    private JTextArea salidaArea;
    private JTextArea hechosArea;
    private JTextArea reglasArea;
    private JTextField metaField;
    private JRadioButton adelanteBtn;
    private JRadioButton atrasBtn;

    public MotorInferenciaGUI() {
        setTitle("Motor de Inferencia - Encadenamiento");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel superior - pestañas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Pestaña 1: Entrada de datos
        tabbedPane.addTab("Entrada de Datos", crearPanelEntrada());

        // Pestaña 2: Inferencia
        tabbedPane.addTab("Ejecutar Inferencia", crearPanelInferencia());

        // Pestaña 3: Salida y hechos
        tabbedPane.addTab("Salida y Hechos", crearPanelSalida());

        add(tabbedPane, BorderLayout.CENTER);

        // Barra de menú
        JMenuBar menuBar = new JMenuBar();
        JMenu archivoMenu = new JMenu("Archivo");
        JMenuItem guardarItem = new JMenuItem("Guardar hechos");
        guardarItem.addActionListener(e -> guardarHechos());
        archivoMenu.add(guardarItem);
        JMenuItem salirItem = new JMenuItem("Salir");
        salirItem.addActionListener(e -> System.exit(0));
        archivoMenu.add(salirItem);
        menuBar.add(archivoMenu);
        setJMenuBar(menuBar);
    }

    private JPanel crearPanelEntrada() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        hechosArea = new JTextArea(8, 50);
        hechosArea.setBorder(BorderFactory.createTitledBorder("Hechos (uno por línea)"));

        reglasArea = new JTextArea(12, 50);
        reglasArea.setBorder(BorderFactory.createTitledBorder("Reglas (ej: A & B -> C)"));

        JButton btnCargar = new JButton("Procesar entrada ingresada");
        btnCargar.addActionListener(e -> procesarEntradaManual());

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            hechosArea.setText("");
            reglasArea.setText("");
        });

        JPanel botones = new JPanel();
        botones.add(btnCargar);
        botones.add(btnLimpiar);

        JPanel centro = new JPanel(new GridLayout(2, 1, 10, 10));
        centro.add(new JScrollPane(hechosArea));
        centro.add(new JScrollPane(reglasArea));

        panel.add(centro, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelInferencia() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel opciones = new JPanel();
        opciones.setLayout(new FlowLayout(FlowLayout.LEFT));

        ButtonGroup grupo = new ButtonGroup();
        adelanteBtn = new JRadioButton("Encadenamiento hacia adelante", true);
        atrasBtn = new JRadioButton("Encadenamiento hacia atrás");
        grupo.add(adelanteBtn);
        grupo.add(atrasBtn);

        opciones.add(adelanteBtn);
        opciones.add(atrasBtn);

        metaField = new JTextField(30);
        metaField.setEnabled(false);
        metaField.setBorder(BorderFactory.createTitledBorder("Meta a demostrar (solo para atrás)"));

        atrasBtn.addActionListener(e -> metaField.setEnabled(true));
        adelanteBtn.addActionListener(e -> metaField.setEnabled(false));

        JButton btnEjecutar = new JButton("Ejecutar inferencia");
        btnEjecutar.addActionListener(e -> ejecutarInferencia());

        JPanel norte = new JPanel(new GridLayout(2, 1, 10, 10));
        norte.add(opciones);
        norte.add(metaField);

        panel.add(norte, BorderLayout.NORTH);
        panel.add(btnEjecutar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelSalida() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        salidaArea = new JTextArea(20, 70);
        salidaArea.setEditable(false);
        salidaArea.setLineWrap(true);
        salidaArea.setWrapStyleWord(true);

        panel.add(new JScrollPane(salidaArea), BorderLayout.CENTER);
        return panel;
    }

    private void procesarEntradaManual() {
        bc = new BaseConocimiento();

        // Hechos
        for (String linea : hechosArea.getText().split("\n")) {
            linea = linea.trim();
            if (!linea.isEmpty()) {
                bc.agregarHecho(linea);
            }
        }

        // Reglas
        int num = 0;
        for (String linea : reglasArea.getText().split("\n")) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split("=>|ENTONCES|->", 2);
            if (partes.length != 2) {
                salidaArea.append("Formato inválido ignorado: " + linea + "\n");
                continue;
            }

            String anteTxt = partes[0].trim();
            String conseq = partes[1].trim();

            List<Condicion> ants = bc.parsearAntecedentes(anteTxt);
            num++;
            bc.agregarRegla(new Regla(ants, conseq, num));
        }

        salidaArea.setText("Datos procesados.\nHechos: " + bc.obtenerTodosHechos().size() +
                           "\nReglas: " + bc.obtenerReglas().size() + "\n\n");
        bc.imprimirEstado(); // imprime en consola por ahora
    }

    private void ejecutarInferencia() {
        salidaArea.setText("");  // limpiar salida

        PrintStream ps = new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                salidaArea.append(String.valueOf((char) b));
                salidaArea.setCaretPosition(salidaArea.getDocument().getLength());
            }
        });

        if (adelanteBtn.isSelected()) {
            EncadenamientoAdelante motor = new EncadenamientoAdelante(bc);
            motor.ejecutar(ps);
        } else if (atrasBtn.isSelected()) {
            String meta = metaField.getText().trim();
            if (meta.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese una meta para encadenamiento hacia atrás", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            EncadenamientoAtras motor = new EncadenamientoAtras(bc, new Scanner(System.in));
            List<String> pila = new ArrayList<>();
            boolean exito = motor.demostrar(meta, pila, ps);
            ps.println("\nMETA '" + meta + "' → " + (exito ? "VERDADERO" : "FALSO"));
        }
    }

    private void guardarHechos() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("Generados"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String ruta = file.getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".txt")) ruta += ".txt";

            try {
                Files.createDirectories(Paths.get("Generados"));
                bc.guardarHechos(ruta);
                JOptionPane.showMessageDialog(this, "Guardado en:\n" + ruta);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MotorInferenciaGUI().setVisible(true);
        });
    }
}