import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ServicioDialog extends JDialog {
    private JTextField txtTipo;
    private JTextField txtDescripcion;
    private boolean guardado = false;

    public ServicioDialog(Window owner, ServiciodeApoyo existente) {
        super(owner, "Servicio de Apoyo", ModalityType.APPLICATION_MODAL);
        initUI(existente);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI(ServiciodeApoyo existente) {
        setLayout(new BorderLayout(8, 8));
        JPanel fields = new JPanel(new GridLayout(2, 2, 6, 6));
        fields.add(new JLabel("Tipo:"));
        txtTipo = new JTextField();
        fields.add(txtTipo);
        fields.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        fields.add(txtDescripcion);
        add(fields, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("OK");
        JButton btnCancel = new JButton("Cancelar");
        botones.add(btnOk);
        botones.add(btnCancel);
        add(botones, BorderLayout.SOUTH);

        if (existente != null) {
            txtTipo.setText(existente.getTipoServicio());
            txtDescripcion.setText(existente.getDescripcion());
        }

        btnOk.addActionListener((ActionEvent e) -> {
            if (txtTipo.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tipo vacío");
                return;
            }
            guardado = true;
            setVisible(false);
        });

        btnCancel.addActionListener(e -> {
            guardado = false;
            setVisible(false);
        });
    }

    public boolean isGuardado() {
        return guardado;
    }

    public String getTipo() {
        return txtTipo.getText().trim();
    }

    public String getDescripcion() {
        return txtDescripcion.getText().trim();
    }
}