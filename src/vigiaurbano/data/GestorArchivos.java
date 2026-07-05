package vigiaurbano.data;

import java.io.*;

public class GestorArchivos{
    private String rutaIncidentes;
    private String rutaUsuarios;

    public GestorArchivos(){
        this("data/incidentes_aleatorios.csv","data/usuarios_aleatorios.csv");
    }



    public GestorArchivos(String rutaIncidentes, String rutaUsuarios){
        this.rutaIncidentes=rutaIncidentes;
        this.rutaUsuarios=rutaUsuarios;
    }
    
    public String getRutaIncidentes(){return rutaIncidentes;}
    public String getRutaUsuarios(){return rutaUsuarios;}

    public boolean existeUsuarios(){
        return new File(rutaUsuarios).exists();
    }

    public BufferedReader getReaderUsuarios() throws FileNotFoundException{
        return new BufferedReader(new FileReader(rutaUsuarios));
    }

    public PrintWriter getWriterUsuarios() throws IOException{
    return new PrintWriter(new FileWriter(rutaUsuarios));
    }

    public boolean existeIncidentes(){
        return new File(rutaIncidentes).exists();
    }

    public BufferedReader getReaderIncidentes() throws FileNotFoundException{
        return new BufferedReader(new FileReader(rutaIncidentes));
    }

    public PrintWriter getWriterIncidentes() throws IOException{
    return new PrintWriter(new FileWriter(rutaIncidentes));
    }

}