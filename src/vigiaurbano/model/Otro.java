package vigiaurbano.model;

import java.util.Arrays;
import java.util.List;

public class Otro extends Incidente {
    private List<String> organismosSeleccionados;

    public Otro(String id, String categoria, String descripcion, String ubicacion, Usuario reportadoPor) {
        super(id, categoria, descripcion, ubicacion, reportadoPor);
        this.organismosSeleccionados = Arrays.asList(
            "Policía Nacional", "Bomberos", "Defensa Civil", "UNGRD", 
            "Ejército", "Fiscalía", "Cruz Roja", "Entidad Gubernamental"
        );
    }

    public Otro(String id, String categoria, String descripcion, String ubicacion, 
                Usuario reportadoPor, List<String> organismosSeleccionados) {
        super(id, categoria, descripcion, ubicacion, reportadoPor);
        this.organismosSeleccionados = organismosSeleccionados;
    }

    @Override
    public String generarAlerta() {
        return " OTRO INCIDENTE en " + ubicacion + " : " + descripcion;
    }

    @Override
    public List<String> getOrganismosResponsables() {
        return organismosSeleccionados; 
    }

    @Override
    public boolean esCompetenciaDe(String organismo) {
        if (organismo == null || organismo.isEmpty()) return false;
        if (organismosSeleccionados == null) return false; 
        return organismosSeleccionados.stream().anyMatch(o -> 
            o.equalsIgnoreCase(organismo.trim())
        );
    }
}