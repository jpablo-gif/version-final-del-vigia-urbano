package vigiaurbano.logic;

import java.io.*;
import java.util.*;
import vigiaurbano.data.GestorArchivos;
import vigiaurbano.model.*;

public class CargarUsuarios {
    private GestorArchivos gestorArchivos;

    public CargarUsuarios() {
        this.gestorArchivos = new GestorArchivos();
    }

    public CargarUsuarios(GestorArchivos gestorArchivos) {
        this.gestorArchivos = gestorArchivos;
    }

    public List<Usuario> cargar() throws IOException {
        List<Usuario> usuarios = new ArrayList<>();

        if (!gestorArchivos.existeUsuarios()) {
            return usuarios;
        }

        try (BufferedReader reader = gestorArchivos.getReaderUsuarios()) {
            String linea = reader.readLine(); 

            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",", 10);

                if (partes.length >= 7) {
                    String id = partes[0].trim();
                    String nombre = partes[1].trim();
                    String email = partes[2].trim();
                    String contrasena = partes[3].trim();
                    String rol = partes[4].trim();

                    if (rol.equalsIgnoreCase("Civil")) {
                        String direccion = partes[5].trim();
                        String telefono = partes[6].trim();
                        usuarios.add(new Civil(id, nombre, email, contrasena, direccion, telefono));

                    } else if (rol.equalsIgnoreCase("Autoridad") && partes.length >= 9) {
                        String rango = partes[7].trim();
                        String unidad = partes[8].trim();
                        String organismo = partes[9].trim();

                        Autoridad a = new Autoridad(id, nombre, email, contrasena, rango, unidad);
                        a.setOrganismo(organismo);  

                        usuarios.add(a);
                    } else {
                        System.err.println("Línea ignorada: campos insuficientes - " + linea);
                    }
                }
            }
        }

        return usuarios;
    }
}