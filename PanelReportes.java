import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class PanelReportes extends JPanel {
    private JTextArea salida;

    public PanelReportes() {
        setLayout(new BorderLayout(8, 8));
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnPorDiscapacidad = new JButton("Listar por Discapacidad");
        JButton btnConNotasPositivas = new JButton("Beneficiarios con Notas Positivas");
        JButton btnBuscarServicio = new JButton("Buscar Servicios por Tipo");
        JButton btnExportar = new JButton("Exportar Reporte TXT");

        botones.add(btnPorDiscapacidad);
        botones.add(btnConNotasPositivas);
        botones.add(btnBuscarServicio);
        botones.add(btnExportar);

        add(botones, BorderLayout.NORTH);

        salida = new JTextArea();
        salida.setEditable(false);
        add(new JScrollPane(salida), BorderLayout.CENTER);

        btnPorDiscapacidad.addActionListener(e -> actionPorDiscapacidad());
        btnConNotasPositivas.addActionListener(e -> actionConNotasPositivas());
        btnBuscarServicio.addActionListener(e -> actionBuscarServicio());
        btnExportar.addActionListener(e -> actionExportar());
    }

    private void actionPorDiscapacidad() {
        String disc = JOptionPane.showInputDialog(this, "Ingrese discapacidad a filtrar:");
        if (disc == null || disc.trim().isEmpty()) return;
        List<Beneficiario> lista = PersonaRepository.listarBeneficiariosPorDiscapacidad(disc.trim());
        if (lista.isEmpty()) {
            salida.setText("No se encontraron beneficiarios con discapacidad: " + disc);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Beneficiario b : lista) {
            sb.append(b.obtenerDetalle()).append("\n------------------\n");
        }
        salida.setText(sb.toString());
    }

    private void actionConNotasPositivas() {
        List<Beneficiario> lista = PersonaRepository.listarBeneficiariosConNotasPositivas();
        if (lista.isEmpty()) {
            salida.setText("No se encontraron beneficiarios con notas positivas.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Beneficiario b : lista) {
            sb.append(b.obtenerDetalle()).append("\n------------------\n");
        }
        salida.setText(sb.toString());
    }

    private void actionBuscarServicio() {
        String tipo = JOptionPane.showInputDialog(this, "Ingrese tipo de servicio a buscar:");
        if (tipo == null || tipo.trim().isEmpty()) return;
        List<String> res = PersonaRepository.buscarServiciosPorTipo(tipo.trim());
        if (res.isEmpty()) {
            salida.setText("No se encontraron servicios del tipo: " + tipo);
            return;
        }
        salida.setText(res.stream().collect(Collectors.joining("\n")));
    }

    private void actionExportar() {
        PersonaRepository.guardarEn("data");
        DataStore.exportarReporteTxt(PersonaRepository.getRegistros(), "data", "reporte.txt");
        JOptionPane.showMessageDialog(this, "Reporte generado en data/reporte.txt (y CSV guardados).");
    }
}