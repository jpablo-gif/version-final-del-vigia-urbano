package vigiaurbano.logic;

import java.io.*;
import java.util.*;
import vigiaurbano.data.GestorArchivos;
import vigiaurbano.model.*;

public class CargarIncidentes {
    private GestorArchivos gestorArchivos;

    public CargarIncidentes() {
        this.gestorArchivos = new GestorArchivos();
    }

    public CargarIncidentes(GestorArchivos gestorArchivos) {
        this.gestorArchivos = gestorArchivos;
    }

    public List<Incidente> cargar(List<Usuario> usuarios) throws IOException {
        List<Incidente> incidentes = new ArrayList<>();

        if (!gestorArchivos.existeIncidentes()) {
            return incidentes;
        }

        Map<String, Usuario> mapaUsuarios = new HashMap<>();
        for (Usuario u : usuarios) {
            mapaUsuarios.put(u.getId(), u);
        }

        try (BufferedReader reader = gestorArchivos.getReaderIncidentes()) {
            String linea = reader.readLine(); // Saltar cabecera

            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(";", 7);

                if (partes.length >= 7) {
                    String id = partes[0].trim();
                    String categoria = partes[1].trim();
                    String descripcion = partes[2].trim();
                    String ubicacion = partes[3].trim();
                    String fechaHoraStr = partes[4].trim();
                    String estadoStr = partes[5].trim();
                    String reportadoPorId = partes[6].trim();

                    Usuario reportero = mapaUsuarios.get(reportadoPorId);
                    if (reportero == null) {
                        System.err.println("Reportero no encontrado: " + reportadoPorId + " - " + linea);
                        continue;
                    }

                    Incidente incidente = null;
                    String categoriaLower = categoria.toLowerCase();

                    switch (categoriaLower) {
                        case "tiroteo":
                            incidente = new Tiroteo(id, categoria, descripcion, ubicacion, reportero);
                            break;
                        case "incendio":
                            incidente = new Incendio(id, categoria, descripcion, ubicacion, reportero);
                            break;
                        case "robo":
                            incidente = new Robo(id, categoria, descripcion, ubicacion, reportero);
                            break;
                        case "sospechoso":
                            incidente = new Sospechoso(id, categoria, descripcion, ubicacion, reportero);
                            break;
                        case "otro":
                            incidente = new Otro(id, categoria, descripcion, ubicacion, reportero);
                            break;
                        default:
                            System.err.println("Categoría no reconocida: " + categoria + " - " + linea);
                            continue;
                    }

                    try {
                        incidente.setEstado(EstadoIncidente.valueOf(estadoStr));
                    } catch (IllegalArgumentException e) {
                        incidente.setEstado(EstadoIncidente.ACTIVO);
                    }

                    incidentes.add(incidente);
                } else {
                    System.err.println("Línea ignorada: campos insuficientes - " + linea);
                }
            }
        }
        return incidentes;
    }
}