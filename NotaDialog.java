import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NotaDialog extends JDialog {
    private JComboBox<String> cbEfecto;
    private boolean guardado = false;

    public NotaDialog(Window owner, SeguimientoImpacto existente) {
        super(owner, "Nota de Seguimiento", ModalityType.APPLICATION_MODAL);
        initUI(existente);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI(SeguimientoImpacto existente) {
        setLayout(new BorderLayout(8, 8));
        JPanel p = new JPanel(new GridLayout(1, 2, 6, 6));
        p.add(new JLabel("Efecto:"));
        cbEfecto = new JComboBox<>(new String[]{"Positivo", "Negativo"});
        p.add(cbEfecto);
        add(p, BorderLayout.CENTER);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("OK");
        JButton btnCancel = new JButton("Cancelar");
        botones.add(btnOk);
        botones.add(btnCancel);
        add(botones, BorderLayout.SOUTH);

        if (existente != null) {
            cbEfecto.setSelectedItem(existente.getEfecto());
        }

        btnOk.addActionListener((ActionEvent e) -> {
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

    public String getEfecto() {
        return (String) cbEfecto.getSelectedItem();
    }
}