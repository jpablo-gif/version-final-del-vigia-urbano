package vigiaurbano.model;

public class Autoridad extends Usuario {

    private String rango;
    private String unidad;
    private String organismo;

    public Autoridad(String id, String nombre, String email, String contrasena, String rango, String unidad) {
        super(id, nombre, email, contrasena);
        this.rango = rango;
        this.unidad = unidad;

        String pertenencia = detectarOrganismo(email);
        if (pertenencia == null) {
            throw new IllegalArgumentException("Email no reconocido");
        }
        this.organismo = pertenencia;
    }

    public void setOrganismo(String organismo) {
        this.organismo = organismo;
    }

    private String detectarOrganismo(String email) {
        if (email == null) return null;

        String emailLimpio = email.trim();

        if (emailLimpio.endsWith("@policia.gov.co")) return "Policía Nacional";
        if (emailLimpio.endsWith("@dnbc.gov.co")) return "Bomberos";
        if (emailLimpio.endsWith("@defensacivil.gov.co")) return "Defensa Civil";
        if (emailLimpio.endsWith("@gestiondelriesgo.gov.co")) return "UNGRD";
        if (emailLimpio.endsWith("@ejercito.mil.co")) return "Ejército";
        if (emailLimpio.endsWith("@armada.mil.co")) return "Armada";
        if (emailLimpio.endsWith("@fac.mil.co")) return "Fuerza Aérea";
        if (emailLimpio.endsWith("@fiscalia.gov.co")) return "Fiscalía";
        if (emailLimpio.endsWith("@cruzrojacolombiana.org")) return "Cruz Roja";
        if (emailLimpio.endsWith(".gov.co")) return "Entidad Gubernamental";
        return null;
    }

    public String getRango() {
        return rango;
    }

    public String getUnidad() {
        return unidad;
    }

    public String getOrganismo() {
        return organismo;
    }

    @Override
    public String getRol() {
        return "Autoridad";
    }
}