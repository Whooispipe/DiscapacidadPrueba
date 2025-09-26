import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelBeneficiarios extends JPanel {
    private DefaultListModel<String> listaModel;
    private JList<String> lista;
    private JTextArea detalleArea;

    public PanelBeneficiarios() {
        setLayout(new BorderLayout(8, 8));

        // Izquierda: lista de beneficiarios
        listaModel = new DefaultListModel<>();
        lista = new JList<>(listaModel);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spLista = new JScrollPane(lista);
        spLista.setPreferredSize(new Dimension(300, 400));

        // Derecha: detalle
        detalleArea = new JTextArea();
        detalleArea.setEditable(false);
        JScrollPane spDetalle = new JScrollPane(detalleArea);

        // Botones superior
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnRefrescar = new JButton("Refrescar");

        topButtons.add(btnAgregar);
        topButtons.add(btnEditar);
        topButtons.add(btnEliminar);
        topButtons.add(btnRefrescar);

        add(topButtons, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spLista, spDetalle);
        add(split, BorderLayout.CENTER);

        // Eventos
        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) mostrarDetalleSeleccion();
        });

        btnAgregar.addActionListener((ActionEvent e) -> {
            VentanaBeneficiarioDialog dlg = new VentanaBeneficiarioDialog(SwingUtilities.getWindowAncestor(this), null);
            dlg.setVisible(true);
            if (dlg.isGuardado()) refrescarLista();
        });

        btnEditar.addActionListener((ActionEvent e) -> {
            String selRut = obtenerRutSeleccionado();
            if (selRut == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un beneficiario.");
                return;
            }
            Beneficiario b = (Beneficiario) PersonaRepository.buscarPorRut(selRut);
            VentanaBeneficiarioDialog dlg = new VentanaBeneficiarioDialog(SwingUtilities.getWindowAncestor(this), b);
            dlg.setVisible(true);
            if (dlg.isGuardado()) refrescarLista();
        });

        btnEliminar.addActionListener((ActionEvent e) -> {
            String selRut = obtenerRutSeleccionado();
            if (selRut == null) {
                JOptionPane.showMessageDialog(this, "Seleccione un beneficiario.");
                return;
            }
            int r = JOptionPane.showConfirmDialog(this, "Eliminar beneficiario " + selRut + " ?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                PersonaRepository.eliminar(selRut);
                refrescarLista();
            }
        });

        btnRefrescar.addActionListener(e -> refrescarLista());

        refrescarLista();
    }

    private void refrescarLista() {
        listaModel.clear();
        Map<String, Persona> regs = PersonaRepository.getRegistros();
        List<String> ruts = new ArrayList<>();
        for (Persona p : regs.values()) {
            if (p instanceof Beneficiario) ruts.add(p.getRut());
        }
        ruts.sort(String::compareTo);
        for (String rut : ruts) {
            Beneficiario b = (Beneficiario) PersonaRepository.buscarPorRut(rut);
            listaModel.addElement(rut + " - " + b.getNombre());
        }
        detalleArea.setText("");
    }

    private String obtenerRutSeleccionado() {
        String sel = lista.getSelectedValue();
        if (sel == null) return null;
        // formato "rut - nombre"
        int idx = sel.indexOf(" - ");
        if (idx < 0) return sel.trim();
        return sel.substring(0, idx).trim();
    }

    private void mostrarDetalleSeleccion() {
        String rut = obtenerRutSeleccionado();
        if (rut == null) {
            detalleArea.setText("");
            return;
        }
        Persona p = PersonaRepository.buscarPorRut(rut);
        if (p != null) {
            detalleArea.setText(p.obtenerDetalle());
        } else {
            detalleArea.setText("No se encontró detalle.");
        }
    }
}