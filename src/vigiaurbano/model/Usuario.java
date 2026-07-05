
package vigiaurbano.model;


public abstract class Usuario{
 protected String id;
 protected String nombre;
 protected String email;
 protected  String contrasena;

 public Usuario(String id, String nombre, String email, String contrasena){
    this.id=id;
    this.nombre=nombre;
    if(!contrasenaValida(contrasena)){
        throw new IllegalArgumentException("La contraseña debe tener minimo 6 caracteres, 1 letra mayúscula y un numero");
    }
    if(!emailValido(email)){
        throw new IllegalArgumentException("El email debe tener @ ");
    }
    this.email=email;
    this.contrasena=contrasena;
 }

 public String getId(){
    return id;
 }

 public String getNombre(){
    return nombre;
 }
 public String getEmail(){
    return email;
 }
 public String getContrasena(){
    return contrasena;
 }

 public boolean emailValido(String email){
    return email != null && email.contains("@"); //email.contains("@") osea que si email contiene a @
 }
 public boolean contrasenaValida(String contrasena){
    if (contrasena == null || contrasena.length()<6) return false;
    boolean tieneMayuscula = !contrasena.equals(contrasena.toLowerCase());//convierte contraseña a minusculas y la compara con la original, si no son iguales, retorna falso (osea que si tenia mayusculas), pero lo cambiamos con ! a verdadero
    boolean tieneNumero = contrasena.matches(".*\\d.*");//matches sirve para ver si la cadena cumple un patron, y ".*\\d.*" significa cualquier caracter (.)+ un digito (\d)+ cualquier caracter (.) retorna true
    boolean tieneMinuscula = !contrasena.equals(contrasena.toUpperCase());
    return tieneMayuscula && tieneNumero && tieneMinuscula;
 }
 public abstract String getRol();
 } 
