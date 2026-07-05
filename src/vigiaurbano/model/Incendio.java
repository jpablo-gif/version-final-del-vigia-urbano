
package vigiaurbano.model;


import java.util.Arrays;
import java.util.List;

public class Incendio extends Incidente{

    public Incendio(String id, String categoria, String descripcion, String ubicacion, Usuario reportadoPor){
        super(id,categoria,descripcion,ubicacion,reportadoPor);
    }

    @Override
    public String generarAlerta(){
        return " Incendio en "+ ubicacion +" : "+descripcion;
    }

    @Override
    public List<String> getOrganismosResponsables(){
        return Arrays.asList("Bomberos","Defensa Civil","Hospitales");
    }
    @Override
    public boolean esCompetenciaDe(String organismo) {
    if (organismo == null) return false;
    String org = organismo.trim().toLowerCase();
    return org.contains("bombero");
}
}