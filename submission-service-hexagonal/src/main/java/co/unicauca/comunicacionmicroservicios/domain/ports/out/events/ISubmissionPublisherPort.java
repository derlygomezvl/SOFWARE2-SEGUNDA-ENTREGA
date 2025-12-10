package co.unicauca.comunicacionmicroservicios.domain.ports.out.events;

/**
 * @author javiersolanop777
 */
public interface ISubmissionPublisherPort {

    public void publicarFormatoAEnviado(Object payload);

    public void publicarFormatoAReenviado(Object payload);

    public void publicarAnteproyectoEnviado(Object payload);

    public void publicarProyectoRechazoDefinitivo(Object payload);
}
