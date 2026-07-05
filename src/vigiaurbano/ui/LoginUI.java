package vigiaurbano.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import vigiaurbano.logic.GestorIncidentes;
import vigiaurbano.logic.GestorUsuarios;
import vigiaurbano.model.Civil;
import vigiaurbano.model.Usuario;

public class LoginUI extends JFrame {
    private GestorUsuarios gestorUsuarios;
    private GestorIncidentes gestorIncidentes;

    // Componentes gráficos
    private JTextField txtEmail;
    private JPasswordField txtContrasena;
    private JButton btnIngresar;
    private JButton btnRegistrar;

    public LoginUI(GestorUsuarios gestorUsuarios, GestorIncidentes gestorIncidentes) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorIncidentes = gestorIncidentes;
        initComponents();
    }

    private void initComponents() {
        // Configuración básica de la ventana
        setTitle("VigíaUrbano - Iniciar Sesión");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar pantalla
        setLayout(new BorderLayout(10, 10));

        // Panel de formulario (Campos de texto)
        JPanel panelFormulario = new JPanel(new GridLayout(2, 2, 5, 10));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblEmail = new JLabel("Correo Electrónico:");
        txtEmail = new JTextField();

        JLabel lblContrasena = new JLabel("Contraseña:");
        txtContrasena = new JPasswordField();

        panelFormulario.add(lblEmail);
        panelFormulario.add(txtEmail);
        panelFormulario.add(lblContrasena);
        panelFormulario.add(txtContrasena);

        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnIngresar = new JButton("Ingresar");
        btnRegistrar = new JButton("Registrar Ciudadano");
        JButton btnModoAutoridad = new JButton("Iniciar como Autoridad");
        panelBotones.add(btnModoAutoridad);
        panelBotones.add(btnIngresar);
        panelBotones.add(btnRegistrar);

        // Agregar paneles a la ventana
        //  Forzamos al panel a tener espacio para 3 botones uno al lado del otro
       // ====================================================================
        //  1. BOTÓN INGRESAR (Exclusivo para Civiles / Ciudadanos)
        // ====================================================================
        btnIngresar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = txtEmail.getText().trim();
                String contrasena = new String(txtContrasena.getPassword()).trim();

                if (email.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginUI.this, "Por favor complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Usuario usuarioLogueado = gestorUsuarios.autenticar(email, contrasena);

                if (usuarioLogueado != null) {
                    // 🛡️ Validación de Rol
                    if ("Civil".equalsIgnoreCase(usuarioLogueado.getRol())) {
                        JOptionPane.showMessageDialog(LoginUI.this, "¡Bienvenido Ciudadano, " + usuarioLogueado.getNombre() + "!");
                        abrirVentanaPrincipal(usuarioLogueado);
                    } else {
                        JOptionPane.showMessageDialog(LoginUI.this, "Este botón es exclusivo para ciudadanos. Si eres autoridad, usa el botón correspondiente.", "Acceso Denegado", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(LoginUI.this, "Correo o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

       // =========================================================================
//  2. BOTÓN INICIAR COMO AUTORIDAD 
// =========================================================================
btnModoAutoridad.addActionListener(e -> {
            String correo = txtEmail.getText().trim();
            String password = new String(txtContrasena.getPassword());

            // 🔍 1. Filtro de seguridad: Validamos si alguno de los campos está vacío
            if (correo.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor complete todos los campos.", 
                    "Campos Vacíos", 
                    JOptionPane.WARNING_MESSAGE);
                return; // 🛑 Detiene la ejecución aquí para que no valide en la base de datos
            }

            // 2. Si no están vacíos, procedemos a validar credenciales reales
            Usuario usuario = gestorUsuarios.autenticar(correo, password);

            if (usuario != null && usuario instanceof vigiaurbano.model.Autoridad) {
                gestorUsuarios.setUsuarioActual(usuario);

                VentanaPrincipalUI ventana = new VentanaPrincipalUI(usuario, gestorUsuarios, gestorIncidentes);
                ventana.setVisible(true);
                dispose(); 
            } else {
                // 3. Este solo saltará si escribieron datos pero quedaron incorrectos
                JOptionPane.showMessageDialog(this, 
                    "Credenciales incorrectas o el usuario no pertenece a una Autoridad.", 
                    "Error de Ingreso", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // ====================================================================
        //  3. BOTÓN REGISTRAR CIUDADANO
        // ====================================================================
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrar(); 
            }
        });

        // Agregar paneles a la ventana
        add(panelFormulario, java.awt.BorderLayout.CENTER);
        add(panelBotones, java.awt.BorderLayout.SOUTH);

        // --- Eventos (ActionListeners) ---
        
        }

    public void autenticar() {
        String email = txtEmail.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (email.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        Usuario usuarioLogueado = gestorUsuarios.autenticar(email, contrasena);

        if (usuarioLogueado != null) {
            JOptionPane.showMessageDialog(this, "¡Bienvenido, " + usuarioLogueado.getNombre() + "!", "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
            abrirVentanaPrincipal(usuarioLogueado);
        } else {
            JOptionPane.showMessageDialog(this, "Correo o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void registrar() {
        // Formulario rápido utilizando campos dinámicos en un diálogo
        JTextField txtRegId = new JTextField();
        JTextField txtRegNombre = new JTextField();
        JTextField txtRegEmail = new JTextField();
        JTextField txtRegPass = new JPasswordField();
        JTextField txtRegDireccion = new JTextField();
        JTextField txtRegTelefono = new JTextField();

        Object[] formulario = {
            "Cédula / ID:", txtRegId,
            "Nombre Completo:", txtRegNombre,
            "Correo Electrónico:", txtRegEmail,
            "Contraseña:", txtRegPass,
            "Dirección de Residencia:", txtRegDireccion,
            "Teléfono (10 dígitos):", txtRegTelefono
        };

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Registro de Nuevo Ciudadano", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String id = txtRegId.getText().trim();
                String nombre = txtRegNombre.getText().trim();
                String email = txtRegEmail.getText().trim();
                String pass = new String(((JPasswordField) txtRegPass).getPassword()).trim();
                String direccion = txtRegDireccion.getText().trim();
                String telefono = txtRegTelefono.getText().trim();

                // Validaciones básicas de campos vacíos antes de instanciar
                if (id.isEmpty() || nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || direccion.isEmpty() || telefono.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios para el registro.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Creamos el nuevo objeto de tipo Civil
                Civil nuevoCivil = new Civil(id, nombre, email, pass, direccion, telefono);

                // Lo guardamos usando el método del backend de tu compañero
                gestorUsuarios.registrarUsuario(nuevoCivil);
                JOptionPane.showMessageDialog(this, "Ciudadano registrado exitosamente.\nYa puede iniciar sesión.", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
                
                // Autocompletar el correo recién registrado para facilidad del usuario
                txtEmail.setText(email);

            } catch (IllegalArgumentException ex) {
                // Captura los errores de validación de teléfono o dirección que tu compañero programó en Civil.java
                JOptionPane.showMessageDialog(this, "Error en los datos: " + ex.getMessage(), "Datos Inválidos", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el usuario en el archivo.", "Error de Persistencia", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void abrirVentanaPrincipal(Usuario usuario) {
        // Ocultamos y destruimos la ventana de Login
        this.dispose();

        // Inicializamos la Ventana Principal (que programaremos a continuación)
        // Pasándole el usuario logueado y los gestores necesarios
        java.awt.EventQueue.invokeLater(() -> {
            new VentanaPrincipalUI(usuario, gestorUsuarios, gestorIncidentes).setVisible(true);
        });
    }
}