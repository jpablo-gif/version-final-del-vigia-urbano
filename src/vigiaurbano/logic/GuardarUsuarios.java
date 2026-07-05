package vigiaurbano.logic;

import java.io.*;
import java.util.*;
import vigiaurbano.data.GestorArchivos;
import vigiaurbano.model.*;

public class GuardarUsuarios {
    private GestorArchivos gestorArchivos;

    public GuardarUsuarios() {
        this.gestorArchivos = new GestorArchivos();
    }

    public GuardarUsuarios(GestorArchivos gestorArchivos) {
        this.gestorArchivos = gestorArchivos;
    }

    public void guardar(List<Usuario> usuarios) throws IOException {

         Map<String, Usuario> unicos = new HashMap<>();
         for (Usuario u : usuarios) {
            unicos.put(u.getId(), u);
         }
         List<Usuario> unicosList = new ArrayList<>(unicos.values());





        try (PrintWriter writer = gestorArchivos.getWriterUsuarios()) {
            writer.println("id,nombre,email,contrasena,rol,direccion,telefono,rango,unidad,organismo");

            for (Usuario u : unicosList) {
                if (u instanceof Civil) {
                    Civil c = (Civil) u;
                    writer.printf("%s,%s,%s,%s,%s,%s,%s,,,%n",
                        c.getId().trim(),
                        c.getNombre().trim(),
                        c.getEmail().trim(),
                        c.getContrasena().trim(),
                        "Civil",
                        c.getDireccion().trim(),
                        c.getTelefono().trim());
                } else if (u instanceof Autoridad) {
                    Autoridad a = (Autoridad) u;
                    writer.printf("%s,%s,%s,%s,%s,,,%s,%s,%s%n",
                        a.getId().trim(),
                        a.getNombre().trim(),
                        a.getEmail().trim(),
                        a.getContrasena().trim(),
                        "Autoridad",
                        a.getRango().trim(),
                        a.getUnidad().trim(),
                        a.getOrganismo().trim());
                }
            }
        }
    }
}