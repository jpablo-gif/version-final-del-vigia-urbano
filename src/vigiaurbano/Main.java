package vigiaurbano;

import java.io.IOException;
import vigiaurbano.data.GestorArchivos;
import vigiaurbano.logic.*;
import vigiaurbano.ui.LoginUI;

public class Main {
    public static void main(String[] args) {
        String rutaIncidentes = "data/incidentes_aleatorios.csv";
        String rutaUsuarios = "data/usuarios_aleatorios.csv";

        if (args.length >= 2) {
            rutaIncidentes = args[0];
            rutaUsuarios = args[1];
            System.out.println(">> Leyendo desde consola -> Incidentes: " + rutaIncidentes + " | Usuarios: " + rutaUsuarios);
        } else {
            System.out.println(">> No se pasaron argumentos. Usando archivos por defecto.");
        }

        GestorArchivos gestorArchivos = new GestorArchivos(rutaIncidentes, rutaUsuarios);

        CargarUsuarios cargarUsuarios = new CargarUsuarios(gestorArchivos);
        GuardarUsuarios guardarUsuarios = new GuardarUsuarios(gestorArchivos);
        
        CargarIncidentes cargarIncidentes = new CargarIncidentes(gestorArchivos);
        GuardarIncidentes guardarIncidentes = new GuardarIncidentes(gestorArchivos);

        GestorUsuarios gestorUsuarios = new GestorUsuarios(cargarUsuarios, guardarUsuarios);
        GestorIncidentes gestorIncidentes = new GestorIncidentes(cargarIncidentes, guardarIncidentes);
        
        try {
            java.io.File archivo = new java.io.File(rutaUsuarios);
            
            if (!archivo.exists()) {
                java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(archivo, false));
                writer.println("id,nombre,email,contrasena,rol,direccion,telefono,rango,unidad,organismo");
                writer.println("CIV999,Prueba Usuario,prueba@vigia.com,Prueba123,Civil,Calle 99 #99-99,3123456789,Ninguno,Ninguna,Ninguno");
                writer.println("1,Admin Civil,civil@vigia.com,Civil123,Civil,Calle 1,3001234567,Ninguno,Ninguna,Ninguno");
                writer.close();
                System.out.println(">> Base de datos creada e inicializada por primera vez.");
            }

            gestorUsuarios.cargarUsuarios();
            EstacionesManizales.sembrar(gestorUsuarios);
            gestorIncidentes.cargarIncidentes(gestorUsuarios.getUsuarios());
            System.out.println(">> Base de datos sincronizada correctamente.");
        } catch (IOException e) {
            System.out.println(">> Error al procesar la base de datos.");
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LoginUI login = new LoginUI(gestorUsuarios, gestorIncidentes);
                login.setVisible(true);
                login.setLocationRelativeTo(null);
            }
        });
    }
}