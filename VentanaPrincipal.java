import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaPrincipal extends JFrame {
    private static final String DATA_DIR = "data";

    public VentanaPrincipal() {
        super("SIA - Sistema de Información (Ventana)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Cargar datos al iniciar
        PersonaRepository.cargarDesde(DATA_DIR);
        if (PersonaRepository.getRegistros().isEmpty()) {
            PersonaRepository.inicializarDatosPrueba();
        }

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Beneficiarios", new PanelBeneficiarios());
        tabs.addTab("Servicios y Notas", new PanelServiciosNotas());
        tabs.addTab("Reportes y Filtros", new PanelReportes());

        getContentPane().add(tabs, BorderLayout.CENTER);

        // Guardar datos al cerrar
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int resp = JOptionPane.showConfirmDialog(VentanaPrincipal.this,
                        "¿Guardar cambios y salir?", "Confirmar salida",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    PersonaRepository.guardarEn(DATA_DIR);
                    DataStore.exportarReporteTxt(PersonaRepository.getRegistros(), DATA_DIR, "reporte.txt");
                    dispose();
                    System.exit(0);
                } else if (resp == JOptionPane.NO_OPTION) {
                    dispose();
                    System.exit(0);
                } // else cancelar -> no hacer nada
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal vp = new VentanaPrincipal();
            vp.setVisible(true);
        });
    }
}