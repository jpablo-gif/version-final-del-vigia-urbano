package vigiaurbano.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import vigiaurbano.model.Incidente;

/**
 * Ventana independiente que muestra el historial de incidentes ya ATENDIDOS,
 * separándolos de los incidentes activos de la ventana principal. Es de solo
 * lectura: aquí no se atienden ni se modifican incidentes.
 */
public class VentanaAtendidosUI extends JDialog {

    public VentanaAtendidosUI(JFrame padre, List<Incidente> atendidos) {
        super(padre, "VigíaUrbano - Incidentes Atendidos", true);
        setSize(820, 420);
        setLocationRelativeTo(padre);
        setLayout(new BorderLayout(10, 10));

        // --- Encabezado ---
        JLabel lblInfo = new JLabel(atendidos.isEmpty()
                ? "   No hay incidentes atendidos todavía."
                : "   Historial de incidentes atendidos — Total: " + atendidos.size());
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        // --- Tabla (solo lectura) ---
        String[] columnas = {"ID", "Categoría", "Descripción", "Ubicación", "Fecha/Hora", "Estado", "Reportado Por"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Incidente inc : atendidos) {
            Object[] fila = {
                inc.getId(),
                inc.getCategoria(),
                inc.getDescripcion(),
                inc.getUbicacion(),
                inc.getFechaHora(),
                inc.getEstado(),
                inc.getReportadoPor() != null ? inc.getReportadoPor().getNombre() : "Anónimo"
            };
            modelo.addRow(fila);
        }

        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createTitledBorder("Incidentes Atendidos"));

        // --- Botón cerrar ---
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelSur.add(btnCerrar);

        add(lblInfo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelSur, BorderLayout.SOUTH);
    }
}
