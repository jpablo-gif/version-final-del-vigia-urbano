
package vigiaurbano.model;


public enum EstadoIncidente{
    ACTIVO,
    ATENDIDO;

    @Override
    public String toString(){
        return this.name();
    }
}