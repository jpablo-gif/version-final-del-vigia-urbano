package vigiaurbano.logic;

import java.io.*;
import java.util.*;
import vigiaurbano.data.GestorArchivos;
import vigiaurbano.model.*;

public class GuardarIncidentes{
    private GestorArchivos gestorArchivos;

    public GuardarIncidentes(){
        this.gestorArchivos = new GestorArchivos();
    }

    public GuardarIncidentes(GestorArchivos gestorArchivos){
        this.gestorArchivos=gestorArchivos;
    }

    public void guardar(List<Incidente> incidentes)throws IOException{
        try(PrintWriter writer = gestorArchivos.getWriterIncidentes()){
            writer.println("id;categoria;descripcion;ubicacion;fechaHora;estado;reportadoPorId");

            for(Incidente i: incidentes){
                    writer.printf("%s;%s;%s;%s;%s;%s;%s%n",
                    i.getId().trim(),
                    i.getCategoria().trim(),
                    i.getDescripcion().trim().replace(";",","),
                    i.getUbicacion().trim().replace(";",","),
                    i.getFechaHora(),
                    i.getEstado().toString(),
                    i.getReportadoPor().getId().trim());

                }
          }
        }
    }