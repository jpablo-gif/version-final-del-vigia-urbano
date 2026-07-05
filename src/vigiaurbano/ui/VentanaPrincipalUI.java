package vigiaurbano.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import vigiaurbano.logic.GestorIncidentes;
import vigiaurbano.logic.GestorUsuarios;
import vigiaurbano.model.Autoridad;
import vigiaurbano.model.Civil;
import vigiaurbano.model.Incidente;
import vigiaurbano.model.Usuario;

public class VentanaPrincipalUI extends JFrame {
    private Usuario usuarioActual;
    private GestorUsuarios gestorUsuarios;
    private GestorIncidentes gestorIncidentes;

    // Componentes gráficos
    private JTable tablaIncidentes;
    private DefaultTableModel modeloTabla;
    private JButton btnReportar;
    private JButton btnAtender;
    private JButton btnAtendidos;
    private JButton btnEstadisticas;
    private JButton btnCerrarSesion;
    private JLabel lblInfoUsuario;

    public VentanaPrincipalUI(Usuario usuario, GestorUsuarios gestorUsuarios, GestorIncidentes gestorIncidentes) {
        this.usuarioActual = usuario;
        this.gestorUsuarios = gestorUsuarios;
        this.gestorIncidentes = gestorIncidentes;
        initComponents();
        actualizarTabla();
    }

    private void initComponents() {
        setTitle("VigíaUrbano - Panel Principal");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- PANEL SUPERIOR: Información del Usuario Logueado ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelSuperior.setBackground(new Color(230, 240, 250));

        String infoTexto = "👤 Usuario: " + usuarioActual.getNombre() + " | Rol: " + usuarioActual.getRol();
        // --- Versión corregida sin el error de getCorreo() ---
        if (usuarioActual instanceof Autoridad) {
            Autoridad aut = (Autoridad) usuarioActual;
            
            // Evaluamos únicamente por el Organismo asignado en la base de datos
            if (aut.getOrganismo().equalsIgnoreCase("Bomberos")) {
                infoTexto = "👨‍🚒 Usuario: " + aut.getNombre() + " | Rol: Comandante del Cuerpo de Bomberos";
            } else if (aut.getOrganismo().equalsIgnoreCase("Policia")) {
                infoTexto = "👮 Usuario: " + aut.getNombre() + " | Rol: Oficial de la Policía Nacional";
            } else {
                infoTexto = "👤 Usuario: " + aut.getNombre() + " | Rol: " + aut.getRol() + " (" + aut.getOrganismo() + ")";
            }
        }
        lblInfoUsuario = new JLabel(infoTexto);
        lblInfoUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        
        btnCerrarSesion = new JButton("Cerrar Sesión");
        panelSuperior.add(lblInfoUsuario, BorderLayout.WEST);
        panelSuperior.add(btnCerrarSesion, BorderLayout.EAST);

        // --- PANEL CENTRAL: Tabla de Incidentes ---
        // Definimos las columnas requeridas para listar los incidentes
        String[] columnas = {"ID", "Categoría", "Descripción", "Ubicación", "Fecha/Hora", "Estado", "Reportado Por"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // La tabla es de solo lectura en la interfaz
            }
        };
        tablaIncidentes = new JTable(modeloTabla);
        JScrollPane scrollTabla = new JScrollPane(tablaIncidentes);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Alertas Urbanas Activas"));

        // --- PANEL INFERIOR: Botones de Acción según el Rol ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        btnReportar = new JButton("🚨 Reportar Incidente");
        btnReportar.setFont(new Font("Arial", Font.BOLD, 12));
        btnReportar.setBackground(new Color(220, 80, 80));
        btnReportar.setForeground(Color.WHITE);

        btnAtender = new JButton("✅ Atender Incidente");
        btnAtender.setFont(new Font("Arial", Font.BOLD, 12));
        btnAtender.setBackground(new Color(60, 160, 90));
        btnAtender.setForeground(Color.WHITE);

        btnEstadisticas = new JButton("📊 Ver Estadísticas");

        btnAtendidos = new JButton("📁 Ver Atendidos");


        if (usuarioActual instanceof Civil) {
            panelBotones.add(btnReportar); // El ciudadano común puede reportar
            btnAtender.setVisible(false);  // El ciudadano NO puede atender incidentes
        } else if (usuarioActual instanceof Autoridad) {
            btnReportar.setVisible(false); // La autoridad NO reporta incidentes desde aquí
            panelBotones.add(btnAtender);  // La autoridad puede dar por atendido un incidente
        }

        panelBotones.add(btnAtendidos);   // Todos pueden consultar el historial de atendidos
        panelBotones.add(btnEstadisticas);

        // Agregar paneles principales a la ventana
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);

        // --- Control de Eventos ---

        // Abrir diálogo de reporte (Para Civiles)
        btnReportar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirDialogoReporte();
            }
        });

        // Cambiar estado a ATENDIDO (Para Autoridades)
        btnAtender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                atenderIncidente();
            }
        });

       
        btnEstadisticas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarEstadisticas();
            }
        });

        // Abrir la ventana con el historial de incidentes atendidos
        btnAtendidos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verAtendidos();
            }
        });

        // Volver a la pantalla de Login
        btnCerrarSesion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cerrarSesion();
            }
        });
    }

    public void actualizarTabla() {
        // Limpiamos los datos anteriores del modelo de la tabla
        modeloTabla.setRowCount(0);

        // La tabla principal muestra únicamente incidentes ACTIVOS. Si quien
        // ingresó es una autoridad, además solo ve los de su competencia
        // (el CAI ve robos/tiroteos/sospechosos, la estación ve incendios).
        List<Incidente> visibles;
        if (usuarioActual instanceof Autoridad) {
            String organismo = ((Autoridad) usuarioActual).getOrganismo();
            visibles = gestorIncidentes.getActivosPorOrganismo(organismo);
        } else {
            visibles = gestorIncidentes.getIncidentesActivos();
        }

        for (Incidente inc : visibles) {
            Object[] fila = {
                inc.getId(),
                inc.getCategoria(),
                inc.getDescripcion(),
                inc.getUbicacion(),
                inc.getFechaHora(),
                inc.getEstado(),
                inc.getReportadoPor() != null ? inc.getReportadoPor().getNombre() : "Anónimo"
            };
            modeloTabla.addRow(fila);
        }
    }

    // Abre una ventana aparte con el historial de incidentes ATENDIDOS. Para
    // autoridades solo muestra los de su competencia; el ciudadano ve todos.
    public void verAtendidos() {
        List<Incidente> atendidos;
        if (usuarioActual instanceof Autoridad) {
            String organismo = ((Autoridad) usuarioActual).getOrganismo();
            atendidos = gestorIncidentes.getAtendidosPorOrganismo(organismo);
        } else {
            atendidos = gestorIncidentes.getIncidentesAtendidos();
        }

        VentanaAtendidosUI ventana = new VentanaAtendidosUI(this, atendidos);
        ventana.setVisible(true);
    }

    public void abrirDialogoReporte() {
        // Instancia el diálogo flotante (que programaremos a continuación en el paso 3)
        // Pasándole "this" para indicar que esta ventana principal es su padre
        DialogoReporteUI dialogo = new DialogoReporteUI(this, usuarioActual, gestorIncidentes);
        dialogo.setVisible(true);
    }

    public void atenderIncidente() {

        int filaSeleccionada = tablaIncidentes.getSelectedRow();
        if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione un incidente de la tabla para atender.", "Atención", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Recuperamos el ID que se encuentra en la columna 0...
    String idIncidente = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un incidente de la tabla para atender.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Recuperamos el ID que se encuentra en la columna 0 de la fila seleccionada
         idIncidente = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
        String estadoActual = modeloTabla.getValueAt(filaSeleccionada, 5).toString();

        if (estadoActual.equals("ATENDIDO")) {
            JOptionPane.showMessageDialog(this, "Este incidente ya ha sido atendido previamente.", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this, "¿Está seguro de cambiar el estado del incidente a ATENDIDO?", "Confirmar Acción", JOptionPane.YES_NO_OPTION);
        
        if (confirmar == JOptionPane.YES_OPTION) {
            try {
            // 1. Organismo de la autoridad conectada
            String rolOrganismo = "";
            if (usuarioActual instanceof Autoridad) {
                rolOrganismo = ((Autoridad) usuarioActual).getOrganismo();
            }

            // 2. Recuperamos el incidente real por su ID
            Incidente incidente = gestorIncidentes.buscarPorId(idIncidente);
            if (incidente == null) {
                JOptionPane.showMessageDialog(this, "No se pudo encontrar el incidente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. REGLA DE NEGOCIO: solo los organismos responsables de ese tipo de
            // incidente pueden atenderlo. Los incidentes "Otro" los puede atender
            // cualquier autoridad (esCompetenciaDe está sobreescrito en la clase Otro).
            if (!incidente.esCompetenciaDe(rolOrganismo)) {
                JOptionPane.showMessageDialog(this,
                    "¡Permiso Denegado!\nUn incidente de tipo \"" + incidente.getCategoria()
                    + "\" solo puede ser atendido por: " + String.join(", ", incidente.getOrganismosResponsables()) + ".",
                    "Control de Competencia",
                    JOptionPane.ERROR_MESSAGE);
                return; // Frena el proceso
            }

         boolean exito = gestorIncidentes.marcarComoAtendido(idIncidente);

            if (exito) {
                JOptionPane.showMessageDialog(this, "Incidente marcado como atendido exitosamente. Alerta removida.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTabla(); // Refrescamos la JTable de inmediato
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo encontrar el incidente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el estado en el archivo de incidentes.", "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
        }
      }  
    }
    public void mostrarEstadisticas() {
        
        Map<String, Integer> porCategoria = gestorIncidentes.getEstadisticaPorCategoria();
        Map<String, Integer> porUbicacion = gestorIncidentes.getEstadisticasPorUbicacion();

        StringBuilder sb = new StringBuilder();
        sb.append("=== 📊 ESTADÍSTICAS DEL SISTEMA ===\n\n");
        sb.append("📌 ALERTAS POR CATEGORÍA:\n");
        if (porCategoria.isEmpty()) {
            sb.append("  No hay reportes registrados.\n");
        } else {
            for (Map.Entry<String, Integer> entry : porCategoria.entrySet()) {
                sb.append("  • ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        sb.append("\n📌 ALERTAS POR UBICACIÓN (BARRIO):\n");
        if (porUbicacion.isEmpty()) {
            sb.append("  No hay ubicaciones registradas.\n");
        } else {
            for (Map.Entry<String, Integer> entry : porUbicacion.entrySet()) {
                String barrio = entry.getKey().isEmpty() ? "(Sin ubicación)" : entry.getKey();
                sb.append("  • ").append(barrio).append(": ").append(entry.getValue()).append("\n");
            }
        }

        sb.append("\n📌 RESUMEN GENERAL:\n");
        sb.append("  • Total Incidentes: ").append(gestorIncidentes.getTotalIncidentes()).append("\n");
        sb.append("  • Incidentes Activos: ").append(gestorIncidentes.getTotalActivos()).append("\n");
        sb.append("  • Incidentes Atendidos: ").append(gestorIncidentes.getTotalAtendidos()).append("\n");

        // Mostramos toda la información formateada en un cuadro flotante limpio
        JOptionPane.showMessageDialog(this, sb.toString(), "Módulo de Estadísticas - VigíaUrbano", JOptionPane.INFORMATION_MESSAGE);
    }

    public void cerrarSesion() {
        this.dispose();
        java.awt.EventQueue.invokeLater(() -> {
            new LoginUI(gestorUsuarios, gestorIncidentes).setVisible(true);
        });
    }
}