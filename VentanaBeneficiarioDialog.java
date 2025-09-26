import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class VentanaBeneficiarioDialog extends JDialog {
    private JTextField txtRut;
    private JTextField txtNombre;
    private JTextField txtFecha;
    private JTextField txtDiscapacidad;
    private boolean guardado = false;
    private Beneficiario beneficiarioExistente;

    public VentanaBeneficiarioDialog(Window owner, Beneficiario existente) {
        super(owner, "Beneficiario", ModalityType.APPLICATION_MODAL);
        this.beneficiarioExistente = existente;
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(8, 8));
        JPanel fields = new JPanel(new GridLayout(4, 2, 6, 6));
        fields.add(new JLabel("RUT:"));
        txtRut = new JTextField();
        fields.add(txtRut);
        fields.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        fields.add(txtNombre);
        fields.add(new JLabel("Fecha de Nacimiento:"));
        txtFecha = new JTextField();
        fields.add(txtFecha);
        fields.add(new JLabel("Discapacidad:"));
        txtDiscapacidad = new JTextField();
        fields.add(txtDiscapacidad);
        add(fields, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        add(botones, BorderLayout.SOUTH);

        if (beneficiarioExistente != null) {
            txtRut.setText(beneficiarioExistente.getRut());
            txtRut.setEditable(false);
            txtNombre.setText(beneficiarioExistente.getNombre());
            txtFecha.setText(beneficiarioExistente.getFechaNacimiento());
            txtDiscapacidad.setText(beneficiarioExistente.getDiscapacidad());
        }

        btnGuardar.addActionListener((ActionEvent e) -> {
            guardarAccion();
        });

        btnCancelar.addActionListener(e -> {
            guardado = false;
            setVisible(false);
        });
    }

    private void guardarAccion() {
        String rut = txtRut.getText().trim();
        String nombre = txtNombre.getText().trim();
        String fecha = txtFecha.getText().trim();
        String disc = txtDiscapacidad.getText().trim();

        if (rut.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "RUT y Nombre obligatorios");
            return;
        }

        try {
            if (beneficiarioExistente == null) {
                Beneficiario b = new Beneficiario(rut, nombre, fecha, disc);
                boolean ok = PersonaRepository.agregar(b);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "RUT ya existe");
                    return;
                }
            } else {
                beneficiarioExistente.setNombre(nombre);
                beneficiarioExistente.setFechaNacimiento(fecha);
                beneficiarioExistente.setDiscapacidad(disc);
                PersonaRepository.editar(beneficiarioExistente);
            }
            guardado = true;
            setVisible(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public boolean isGuardado() {
        return guardado;
    }
}