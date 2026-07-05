
package vigiaurbano.model;


public class Civil extends Usuario{
    private String direccion;
    private String telefono;
    public Civil(String id, String nombre, String email, String contrasena,String direccion, String telefono){
        super(id, nombre, email, contrasena);
        if(!direccionValida(direccion)){
            throw new IllegalArgumentException("Direccion no puede estar vacia");
        }
        if (!telefonoValido(telefono)){
            throw new IllegalArgumentException("Teléfono debe tener 10 digitos numérico");            
        }
        this.direccion=direccion;
        this.telefono=telefono;
    }

    private boolean direccionValida(String direccion){
        return direccion != null && !direccion.trim().isEmpty(); 
    }

    private boolean telefonoValido(String telefono){
        if(telefono==null)return false;
        String limpio = telefono.replaceAll("[^\\d]","");//replaceAll funciona asi: replaceAll("esto","por esto") 
        return limpio.matches("\\d{10}");
    }

    public String getDireccion(){
        return direccion;
    }
    public String getTelefono(){
        return telefono;
    }
    @Override
    public String getRol(){
        return "Civil";
    }

}