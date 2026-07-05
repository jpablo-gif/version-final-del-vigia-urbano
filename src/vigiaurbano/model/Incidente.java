
package vigiaurbano.model;


import java.time.LocalDateTime;
import java.util.List;

public abstract class Incidente{

    protected String id;
    protected String categoria;
    protected String descripcion;
    protected String ubicacion;
    protected LocalDateTime fechaHora;
    protected EstadoIncidente estado;
    protected Usuario reportadoPor;

    public Incidente(String id, String categoria, String descripcion, String ubicacion, Usuario reportadoPor){

        this.id=id;
        this.categoria = categoria;
        this.descripcion=descripcion;

        if(!ubicacionValida(ubicacion)){

            throw new IllegalArgumentException("ubicacion  no valida");
            
        }  
        
        this.ubicacion=ubicacion;
        this.reportadoPor=reportadoPor;
        this.fechaHora=LocalDateTime.now();
        this.estado=EstadoIncidente.ACTIVO;

    }

    private  boolean ubicacionValida(String ubicacion){
        if(ubicacion == null || ubicacion.trim().isEmpty())return false;
        String[] partes = ubicacion.split(",");
        return partes.length >=2;//minimo calle y ciudad
    }

    public String getId(){return id;}
    public String getCategoria(){return categoria;}
    public String getDescripcion(){return descripcion;}
    public String getUbicacion(){return ubicacion;}
    public LocalDateTime getFechaHora(){return fechaHora;}
    public EstadoIncidente getEstado(){return estado; }
    public Usuario getReportadoPor(){return reportadoPor;}

    public void setEstado(EstadoIncidente estado){this.estado=estado; }

    public abstract String generarAlerta();

    public abstract List<String> getOrganismosResponsables();

    public abstract boolean esCompetenciaDe(String organismo);


@Override
public String toString(){
    return categoria + " | "+ ubicacion +" | "+ estado +" | "+fechaHora;
}
}
