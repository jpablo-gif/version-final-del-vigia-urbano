
package vigiaurbano.model;


import java.util.Arrays;
import java.util.List;

public class Tiroteo extends Incidente{

    public Tiroteo(String id, String categoria, String descripcion, String ubicacion, Usuario reportadoPor){
        super(id,categoria,descripcion,ubicacion,reportadoPor);
    }

    @Override
    public String generarAlerta(){
        return " TIROTEO en "+ ubicacion +" : "+descripcion;
    }

    @Override
    public boolean esCompetenciaDe(String organismo) {
    if (organismo == null) return false;
    String org = organismo.trim().toLowerCase();
    return org.contains("policia") || org.contains("policía") || 
           org.contains("hospital") || org.contains("medicina");
}

    @Override
    public List<String> getOrganismosResponsables(){
        return Arrays.asList("Policía Nacional","Hospitales","Medicina Legal");
    }
}