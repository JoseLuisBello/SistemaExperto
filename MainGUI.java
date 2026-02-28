import javax.swing.*;

/**
 * Clase principal que inicia la aplicación con interfaz gráfica.
 * Solo lanza la ventana principal y nada más.
 */
public class MainGUI {

    public static void main(String[] args) {
        // Ejecutamos la interfaz gráfica en el hilo de eventos de Swing (buena práctica)
        SwingUtilities.invokeLater(() -> {
            try {
                // Opcional: Look and Feel más moderno (opcional, pero mejora mucho la apariencia)
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // Alternativas:
                // UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                // UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                System.err.println("No se pudo aplicar Look and Feel: " + e.getMessage());
            }

            MotorInferenciaGUI ventana = new MotorInferenciaGUI();
            ventana.setVisible(true);
        });
    }
}