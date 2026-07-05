package vigiaurbano.logic;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import vigiaurbano.model.*;

public class GestorIncidentes{
    private List<Incidente> incidentes;
    private Usuario usuarioActual;
    private CargarIncidentes cargador;
    private GuardarIncidentes guardador;

    public GestorIncidentes(){
        this.incidentes = new ArrayList<>();
        this.cargador= new CargarIncidentes();
        this.guardador= new GuardarIncidentes();
        this.usuarioActual=null;
    }

    public GestorIncidentes(CargarIncidentes cargador, GuardarIncidentes guardador){
        this.incidentes= new ArrayList<>();
        this.cargador=cargador;
        this.guardador = guardador;
        this.usuarioActual=null;
    }

    public GestorIncidentes(CargarIncidentes cargador){
        this(cargador,new GuardarIncidentes());
    }

    public GestorIncidentes(GuardarIncidentes guardador){
        this(new CargarIncidentes(),guardador);
    }

    public void cargarIncidentes(List<Usuario> usuarios) throws IOException{
        this.incidentes = cargador.cargar(usuarios);
    }

    public void guardarIncidentes() throws IOException{
        guardador.guardar(incidentes);
    }

    public void agregarIncidente(Incidente incidente) throws IOException{
        incidentes.add(incidente);
        guardarIncidentes();
    }

    public List<Incidente> getIncidentesActivos(){
        return incidentes.stream()
               .filter(i->i.getEstado()==EstadoIncidente.ACTIVO)
               .collect(Collectors.toList());
    }

    public List<Incidente> getIncidentesAtendidos(){
        return incidentes.stream()
               .filter(i ->i.getEstado()== EstadoIncidente.ATENDIDO)
               .collect(Collectors.toList());
    }

    public List<Incidente> getIncidentesPorCategoria(String categoria){
        return incidentes.stream()
               .filter(i -> i.getCategoria().equalsIgnoreCase(categoria))
               .collect(Collectors.toList());
    }

    public List<Incidente> getIncidentesPorOrganismo(String organismo){

        return incidentes.stream()
               .filter(i->i.getOrganismosResponsables().contains(organismo))
               .collect(Collectors.toList());
    }

    public boolean marcarComoAtendido(String id) throws IOException{
        for(Incidente i: incidentes){
            if(i.getId().equals(id)){
                i.setEstado(EstadoIncidente.ATENDIDO);
                guardarIncidentes();
                return true;
            }
        }
        return false;
    }
    public Map<String,Integer> getEstadisticaPorCategoria(){
        Map<String,Integer> stats = new HashMap<>();
        for(Incidente i: incidentes){
        String categoria = i.getCategoria().toLowerCase();  
        stats.put(categoria, stats.getOrDefault(categoria, 0) + 1);
                }
        return stats;
    }
public Map<String,Integer> getEstadisticasPorUbicacion(){
    Map<String,Integer> stats = new HashMap<>();
    for(Incidente i: incidentes){
        String ubicacion = i.getUbicacion();
        String barrio = ubicacion.contains(",") ? 
                         ubicacion.split(",")[0].trim() : 
                         ubicacion;
        stats.put(barrio, stats.getOrDefault(barrio, 0) + 1);  
    }
    return stats;
}

    //  NUEVO MÉTODO: Obtener incidentes activos por organismo
    public List<Incidente> getActivosPorOrganismo(String organismo) {
        if (organismo == null || organismo.isEmpty()) {
            return getIncidentesActivos();
        }
        return incidentes.stream()
            .filter(i -> i.getEstado() == EstadoIncidente.ACTIVO)
            .filter(i -> i.esCompetenciaDe(organismo))
            .collect(Collectors.toList());
    }

    //  NUEVO MÉTODO: Obtener incidentes atendidos por organismo
    public List<Incidente> getAtendidosPorOrganismo(String organismo) {
        if (organismo == null || organismo.isEmpty()) {
            return getIncidentesAtendidos();
        }
        return incidentes.stream()
            .filter(i -> i.getEstado() == EstadoIncidente.ATENDIDO)
            .filter(i -> i.esCompetenciaDe(organismo))
            .collect(Collectors.toList());
    }

    // NUEVO MÉTODO: Buscar incidente por ID
    public Incidente buscarPorId(String id) {
        return incidentes.stream()
            .filter(i -> i.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public List<Incidente> getTodosIncidentes(){
        return new ArrayList<>(incidentes);
    }

    public void setUsuarioActual(Usuario usuario){
        this.usuarioActual = usuario;
    }

    public Usuario getUsuarioActual(){
        return usuarioActual;
    }

    public boolean hayIncidentes(){
        return !incidentes.isEmpty();
    }

    public int getTotalIncidentes(){
        return incidentes.size();
    }

    public int getTotalActivos(){
        return getIncidentesActivos().size();
    }

    public int getTotalAtendidos(){
        return getIncidentesAtendidos().size();
    }
}