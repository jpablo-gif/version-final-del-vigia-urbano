package vigiaurbano.logic;

import java.io.*;
import java.util.*;
import vigiaurbano.model.*;


public class GestorUsuarios{

    private Map<String,Usuario> mapaUsuarios;
    private Usuario usuarioActual;
    private CargarUsuarios cargador;
    private GuardarUsuarios guardador;

public GestorUsuarios(){
    this.mapaUsuarios=new HashMap<>();
    this.cargador= new CargarUsuarios();
    this.guardador= new GuardarUsuarios();
    this.usuarioActual=null;
}

public GestorUsuarios(CargarUsuarios cargador, GuardarUsuarios guardador){
    this.mapaUsuarios=new HashMap<>();
    this.cargador= cargador;
    this.guardador= guardador;
    this.usuarioActual=null;
}

public GestorUsuarios(CargarUsuarios cargador) {
        this(cargador, new GuardarUsuarios());
    }

public void cargarUsuarios() throws IOException{
    List<Usuario> listaUsuarios = cargador.cargar();
    for (Usuario u : listaUsuarios){
        mapaUsuarios.put(u.getEmail(),u);
        mapaUsuarios.put(u.getId(), u);
    }
}

public void guardarUsuarios() throws IOException{
    List<Usuario> listaUsuarios = new ArrayList<>(mapaUsuarios.values());
    guardador.guardar(listaUsuarios);
}

public void registrarUsuario(Usuario usuario) throws IOException {

    if(mapaUsuarios.containsKey(usuario.getEmail())){
        throw new IllegalArgumentException("El email ya esta registrado");
    }
    mapaUsuarios.put(usuario.getId(),usuario);
    mapaUsuarios.put(usuario.getEmail(),usuario);
    guardarUsuarios();
}

public Usuario autenticar(String email, String contrasena){
    Usuario u = mapaUsuarios.get(email);
    if(u !=null && u.getContrasena().equals(contrasena)){

    this.usuarioActual=u;
    return u;
        }
        return null;
    }


public Usuario buscarPorEmail(String email){
   return mapaUsuarios.get(email);
}

public Usuario getUsuarioActual(){
    return usuarioActual;
}

public void setUsuarioActual(Usuario usuario){
    this.usuarioActual=usuario;
}

public List<Usuario> getUsuarios(){
    Map<String, Usuario> unicosPorId = new HashMap<>();
    for (Usuario u : mapaUsuarios.values()) {
        unicosPorId.put(u.getId(), u);
    }
    return new ArrayList<>(unicosPorId.values());
}

public boolean hayUsuarioActual(){
    return usuarioActual != null;
}

public void cerrarSesion(){
    this.usuarioActual=null;
}
}