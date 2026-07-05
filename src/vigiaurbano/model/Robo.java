
package vigiaurbano.model;


import java.util.Arrays;
import java.util.List;

public class Robo extends Incidente{

    public Robo(String id, String categoria, String descripcion, String ubicacion, Usuario reportadoPor){
        super(id,categoria,descripcion,ubicacion,reportadoPor);
    }

    @Override
    public String generarAlerta(){
        return " Robo en "+ ubicacion +" : "+descripcion;
    }

    @Override
    public List<String> getOrganismosResponsables(){
        return Arrays.asList("Policia nacional");
    }

    @Override
    public boolean esCompetenciaDe(String organismo) {
    if (organismo == null) return false;
    String org = organismo.trim().toLowerCase();
    return org.contains("policia") || org.contains("policía");
}
}