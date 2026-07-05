package vigiaurbano.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import vigiaurbano.logic.GestorIncidentes;
import vigiaurbano.model.*;

public class DialogoReporteUI extends JDialog {
    private Usuario usuarioActual;
    private GestorIncidentes gestorIncidentes;
    private VentanaPrincipalUI ventanaPadre;

    // Componentes gráficos
    private JComboBox<String> cmbCategoria;
    private JTextField txtUbicacion;
    private JTextArea txtDescripcion;
    private JButton btnEnviar;
    private JButton btnCancelar;
    private JLabel lblContador;

    // Control del temporizador de 10 segundos
    private Timer timerEnvio;
    private int segundosRestantes = 10;

    public DialogoReporteUI(VentanaPrincipalUI parent, Usuario usuario, GestorIncidentes gestorIncidentes) {
        super(parent, "🚨 Reportar Nuevo Incidente", true); // True para hacerlo modal (bloquea la ventana de atrás)
        this.ventanaPadre = parent;
        this.usuarioActual = usuario;
        this.gestorIncidentes = gestorIncidentes;
        initComponents();
    }

    private void initComponents() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));

        // --- Panel de Formulario ---
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 5, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblCategoria = new JLabel("Tipo de Incidente:");
        
        String[] categorias = {"Robo", "Incendio", "Tiroteo", "Sospechoso", "Otro"};
        cmbCategoria = new JComboBox<>(categorias);

        JLabel lblUbicacion = new JLabel("Ubicación (Barrio / Dirección):");
        txtUbicacion = new JTextField();

        JLabel lblDescripcion = new JLabel("Descripción de lo sucedido:");
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);

        panelFormulario.add(lblCategoria);
        panelFormulario.add(cmbCategoria);
        panelFormulario.add(lblUbicacion);
        panelFormulario.add(txtUbicacion);
        panelFormulario.add(lblDescripcion);
        panelFormulario.add(scrollDesc);

        // --- Panel de Control e Indicadores (Contador) ---
        JPanel panelInferior = new JPanel(new BorderLayout(10, 5));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // Etiqueta que mostrará la cuenta regresiva en tiempo real
        lblContador = new JLabel(" ", SwingConstants.CENTER);
        lblContador.setFont(new Font("Arial", Font.BOLD, 13));
        lblContador.setForeground(new Color(180, 50, 50));

        // Panel de Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnEnviar = new JButton("Enviar Alerta");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnEnviar);
        panelBotones.add(btnCancelar);

        panelInferior.add(lblContador, BorderLayout.NORTH);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        // Agregar al JDialog
        add(panelFormulario, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // --- MANEJO DE EVENTOS ---

        // Presionar Enviar: Activa la cuenta de advertencia de 10 segundos
        btnEnviar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAdvertencia10Segundos();
            }
        });

        // Presionar Cancelar: Puede abortar el Timer en marcha o cerrar la ventana
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timerEnvio != null && timerEnvio.isRunning()) {
                    // Si el cronómetro estaba corriendo, lo frenamos
                    timerEnvio.stop();
                    segundosRestantes = 10;
                    lblContador.setText("🚨 Alerta cancelada exitosamente. No se envió ningún reporte.");
                    btnEnviar.setEnabled(true);
                    cmbCategoria.setEnabled(true);
                    txtUbicacion.setEnabled(true);
                    txtDescripcion.setEnabled(true);
                    btnCancelar.setText("Cerrar");
                } else {
                    // Si no estaba corriendo, simplemente cerramos el diálogo
                    dispose();
                }
            }
        });
    }

    public void mostrarAdvertencia10Segundos() {
        String ubicacion = txtUbicacion.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        if (ubicacion.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete la ubicación y descripción del incidente.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Deshabilitamos campos y botón de envío para evitar interacciones repetidas
        btnEnviar.setEnabled(false);
        cmbCategoria.setEnabled(false);
        txtUbicacion.setEnabled(false);
        txtDescripcion.setEnabled(false);
        btnCancelar.setText("🛑 ABORTAR ENVÍO");

        segundosRestantes = 10;
        lblContador.setText("⏱️ Enviando alerta masiva en " + segundosRestantes + " segundos... Presione abortar si fue un error.");

        // Inicializamos el javax.swing.Timer para que se ejecute cada 1000 milisegundos (1 segundo)
        timerEnvio = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                segundosRestantes--;
                if (segundosRestantes > 0) {
                    lblContador.setText("⏱️ Enviando alerta masiva en " + segundosRestantes + " segundos... Presione abortar si fue un error.");
                } else {
                    // Llegó a cero, detenemos el cronómetro y mandamos el reporte
                    timerEnvio.stop();
                    enviarReporte();
                }
            }
        });
        
        timerEnvio.start(); // Inicia la cuenta regresiva
    }

    public void enviarReporte() {
        try { 
            // ====================================================================
            //  CONTROL PREVENTIVO Y ADVERTENCIA LEGAL CONTRA REPORTES FALSOS
            // ====================================================================
            String ubicacionTexto = txtUbicacion.getText().trim(); 

            // 1. Validar el formato obligatorio de la dirección (Debe tener una coma)
            if (!ubicacionTexto.contains(",")) {
                JOptionPane.showMessageDialog(this, 
                    "Formato de ubicación inválido.\nDebe incluir una coma para separar la dirección del barrio.\nEjemplo: Calle 45 #12-34, Centro", 
                    "Formato Obligatorio", 
                    JOptionPane.WARNING_MESSAGE);
                return; // Frena el proceso
            }

            // 2. Desplegar la advertencia disuasoria sobre multas y bromas
            String mensajeAdvertencia = "¡ADVERTENCIA DE SEGURIDAD!\n\n"
                    + "Recuerde que este medio es estrictamente para reportar emergencias REALES.\n"
                    + "En caso de que este reporte sea falso o una broma, absténgase de continuar.\n"
                    + "De lo contrario, usted podría ser sancionado con multas equivalentes de hasta\n"
                    + "16 Salarios Mínimos Legales Mensuales Vigentes (SMLMV) según el Código de Convivencia Ciudadana.\n\n"
                    + "¿Desea confirmar que este incidente es real y proceder con el reporte?";

            int respuesta = JOptionPane.showConfirmDialog(this, 
                    mensajeAdvertencia, 
                    "Confirmación de Emergencia Real", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);

            // Si se arrepienten, cancelamos
            if (respuesta != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Reporte cancelado por el usuario.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                return; 
            }
            // ====================================================================
            
            
            Incidente nuevoIncidente = crearIncidente(); //

            // Si el usuario canceló la selección de organismos (caso "Otro"),
            // no se crea ningún incidente.
            if (nuevoIncidente == null) {
                return;
            }

            gestorIncidentes.agregarIncidente(nuevoIncidente); //
            
            JOptionPane.showMessageDialog(this, "¡Alerta enviada con éxito a las centrales de emergencia!"); //
            
            ventanaPadre.actualizarTabla(); //
            dispose(); //

        } catch (IOException ex) { // <-- REVISA QUE ESTA LLAVE DE CIERRE '}' DEL TRY ESTÉ AQUÍ
            JOptionPane.showMessageDialog(this, "Error al escribir el incidente en la base de datos CSV."); //
            btnEnviar.setEnabled(true); //
        }
    }
       

    public Incidente crearIncidente() {
        // Generamos un ID único y capturamos datos limpios de la vista
        String idUnico = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String categoriaSeleccionada = cmbCategoria.getSelectedItem().toString().toLowerCase(); // En minúscula para calzar con CargarIncidentes
        String descripcion = txtDescripcion.getText().trim();
        String ubicacion = txtUbicacion.getText().trim();

        Incidente incidente = null;

        // Polimorfismo puro: Instanciamos la clase exacta que corresponde según la categoría seleccionada
        switch (categoriaSeleccionada) {
            case "robo":
                incidente = new Robo(idUnico, "robo", descripcion, ubicacion, usuarioActual);
                break;
            case "incendio":
                incidente = new Incendio(idUnico, "incendio", descripcion, ubicacion, usuarioActual);
                break;
            case "tiroteo":
                incidente = new Tiroteo(idUnico, "tiroteo", descripcion, ubicacion, usuarioActual);
                break;
            case "sospechoso":
                incidente = new Sospechoso(idUnico, "sospechoso", descripcion, ubicacion, usuarioActual);
                break;
            default:
                // Incidente "Otro": el ciudadano elige qué organismos deben atenderlo.
                List<String> organismosElegidos = seleccionarOrganismos();
                if (organismosElegidos == null) {
                    return null; // El usuario canceló o no seleccionó ninguno
                }
                incidente = new Otro(idUnico, "otro", descripcion, ubicacion, usuarioActual, organismosElegidos);
                break;
        }

        // Por defecto, todo incidente nuevo se inicializa en estado ACTIVO
        incidente.setEstado(EstadoIncidente.ACTIVO);
        return incidente;
    }

    // Muestra una lista de casillas para que el ciudadano elija qué organismos
    // deben atender un incidente de tipo "Otro". Devuelve la lista elegida, o
    // null si canceló o no marcó ninguno.
    public List<String> seleccionarOrganismos() {
        String[] opciones = {
            "Policía Nacional", "Bomberos", "Defensa Civil", "Cruz Roja",
            "Hospitales", "Fiscalía", "UNGRD", "Ejército", "Medicina Legal"
        };

        JCheckBox[] casillas = new JCheckBox[opciones.length];
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("¿Qué organismos deben atender este incidente?"));
        for (int i = 0; i < opciones.length; i++) {
            casillas[i] = new JCheckBox(opciones[i]);
            panel.add(casillas[i]);
        }

        int opcion = JOptionPane.showConfirmDialog(this, panel,
            "Organismos responsables (Otro)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        List<String> seleccionados = new ArrayList<>();
        for (int i = 0; i < casillas.length; i++) {
            if (casillas[i].isSelected()) {
                seleccionados.add(opciones[i]);
            }
        }

        if (seleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Debe seleccionar al menos un organismo para reportar un incidente de tipo \"Otro\".",
                "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return seleccionados;
    }
}