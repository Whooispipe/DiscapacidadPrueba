import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PanelServiciosNotas extends JPanel {
    private JComboBox<String> cbBeneficiarios;
    private DefaultListModel<String> serviciosModel;
    private JList<String> serviciosList;
    private DefaultListModel<String> notasModel;
    private JList<String> notasList;

    public PanelServiciosNotas() {
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Beneficiario:"));
        cbBeneficiarios = new JComboBox<>();
        cbBeneficiarios.setPreferredSize(new Dimension(350, 25));
        top.add(cbBeneficiarios);
        JButton btnRefrescar = new JButton("Refrescar");
        top.add(btnRefrescar);
        add(top, BorderLayout.NORTH);

        serviciosModel = new DefaultListModel<>();
        serviciosList = new JList<>(serviciosModel);
        notasModel = new DefaultListModel<>();
        notasList = new JList<>(notasModel);

        JPanel center = new JPanel(new GridLayout(1, 2, 8, 8));
        center.add(new JScrollPane(serviciosList));
        center.add(new JScrollPane(notasList));
        add(center, BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregarServicio = new JButton("Agregar Servicio");
        JButton btnEditarServicio = new JButton("Editar Servicio");
        JButton btnEliminarServicio = new JButton("Eliminar Servicio");

        JButton btnAgregarNota = new JButton("Agregar Nota");
        JButton btnEditarNota = new JButton("Editar Nota");
        JButton btnEliminarNota = new JButton("Eliminar Nota");

        acciones.add(btnAgregarServicio);
        acciones.add(btnEditarServicio);
        acciones.add(btnEliminarServicio);
        acciones.add(new JSeparator(SwingConstants.VERTICAL));
        acciones.add(btnAgregarNota);
        acciones.add(btnEditarNota);
        acciones.add(btnEliminarNota);

        add(acciones, BorderLayout.SOUTH);

        // Eventos
        cbBeneficiarios.addActionListener(e -> cargarListas());
        btnRefrescar.addActionListener(e -> recargarBeneficiarios());

        btnAgregarServicio.addActionListener(e -> accionAgregarServicio());
        btnEditarServicio.addActionListener(e -> accionEditarServicio());
        btnEliminarServicio.addActionListener(e -> accionEliminarServicio());

        btnAgregarNota.addActionListener(e -> accionAgregarNota());
        btnEditarNota.addActionListener(e -> accionEditarNota());
        btnEliminarNota.addActionListener(e -> accionEliminarNota());

        recargarBeneficiarios();
    }

    private void recargarBeneficiarios() {
        cbBeneficiarios.removeAllItems();
        Map<String, Persona> regs = PersonaRepository.getRegistros();
        List<Beneficiario> bs = regs.values().stream()
                .filter(p -> p instanceof Beneficiario)
                .map(p -> (Beneficiario) p)
                .sorted((a, b) -> a.getRut().compareTo(b.getRut()))
                .collect(Collectors.toList());
        for (Beneficiario b : bs) {
            cbBeneficiarios.addItem(b.getRut() + " - " + b.getNombre());
        }
        cargarListas();
    }

    private String getRutSeleccionado() {
        String sel = (String) cbBeneficiarios.getSelectedItem();
        if (sel == null) return null;
        int idx = sel.indexOf(" - ");
        return idx < 0 ? sel.trim() : sel.substring(0, idx).trim();
    }

    private void cargarListas() {
        serviciosModel.clear();
        notasModel.clear();
        String rut = getRutSeleccionado();
        if (rut == null) return;
        Persona p = PersonaRepository.buscarPorRut(rut);
        if (!(p instanceof Beneficiario)) return;
        Beneficiario b = (Beneficiario) p;
        for (int i = 0; i < b.getServiciosDeApoyo().size(); i++) {
            ServiciodeApoyo s = b.getServiciosDeApoyo().get(i);
            serviciosModel.addElement("[" + i + "] " + s.getTipoServicio() + " - " + s.getDescripcion());
        }
        for (int i = 0; i < b.getSeguimientoImpacto().size(); i++) {
            SeguimientoImpacto n = b.getSeguimientoImpacto().get(i);
            notasModel.addElement("[" + i + "] " + n.getEfecto());
        }
    }

    private void accionAgregarServicio() {
        String rut = getRutSeleccionado();
        if (rut == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un beneficiario.");
            return;
        }
        ServicioDialog dlg = new ServicioDialog(SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (!dlg.isGuardado()) return;
        Beneficiario b = (Beneficiario) PersonaRepository.buscarPorRut(rut);
        b.agregarServicio(dlg.getTipo(), dlg.getDescripcion());
        cargarListas();
    }

    private void accionEditarServicio() {
        String rut = getRutSeleccionado();
        if (rut == null) return;
        int sel = serviciosList.getSelectedIndex();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para editar.");
            return;
        }
        Beneficiario b = (Beneficiario) PersonaRepository.buscarPorRut(rut);
        ServiciodeApoyo s = b.getServiciosDeApoyo().get(sel);
        ServicioDialog dlg = new ServicioDialog(SwingUtilities.getWindowAncestor(this), s);
        dlg.setVisible(true);
        if (!dlg.isGuardado()) return;
        b.editarServicio(sel, dlg.getTipo(), dlg.getDescripcion());
        cargarListas();
    }

    private void accionEliminarServicio() {
        String rut = getRutSeleccionado();
        if (rut == null) return;
        int sel = serviciosList.getSelectedIndex();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para eliminar.");
            return;
        }
        Beneficiario b = (Beneficiario) PersonaRepository.buscarPorRut(rut);
        int r = JOptionPane.showConfirmDialog(this, "Eliminar servicio seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            b.eliminarServicioPorIndice(sel);
            cargarListas();
        }
    }

    private void accionAgregarNota() {
        String rut = getRutSeleccionado();
        if (rut == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un beneficiario.");
            return;
        }
        NotaDialog dlg = new NotaDialog(SwingUtilities.getWindowAncestor(this), null);
        dlg.setVisible(true);
        if (!dlg.isGuardado()) return;
        Beneficiario b = (Beneficiario) PersonaRepository.buscarPorRut(rut);
        b.agregarNota(dlg.getEfecto());
        cargarListas();
    }

    private void accionEditarNota() {
        String rut = getRutSeleccionado();
        if (rut == null) return;
        int sel = notasList.getSelectedIndex();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una nota para editar.");
            return;
        }
        Beneficiario b = (Beneficiario) PersonaRepository.buscarPorRut(rut);
        SeguimientoImpacto n = b.getSeguimientoImpacto().get(sel);
        NotaDialog dlg = new NotaDialog(SwingUtilities.getWindowAncestor(this), n);
        dlg.setVisible(true);
        if (!dlg.isGuardado()) return;
        b.editarNota(sel, dlg.getEfecto());
        cargarListas();
    }

    private void accionEliminarNota() {
        String rut = getRutSeleccionado();
        if (rut == null) return;
        int sel = notasList.getSelectedIndex();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una nota para eliminar.");
            return;
        }
        Beneficiario b = (Beneficiario) PersonaRepository.buscarPorRut(rut);
        int r = JOptionPane.showConfirmDialog(this, "Eliminar nota seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            b.eliminarNotaPorIndice(sel);
            cargarListas();
        }
    }
}