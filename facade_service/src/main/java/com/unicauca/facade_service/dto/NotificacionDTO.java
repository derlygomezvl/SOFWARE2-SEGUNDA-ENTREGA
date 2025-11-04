package com.unicauca.facade_service.dto;

/**
 * DTO para representar una notificación a coordinador.
 * @author javiersolanop777
 */
public class NotificacionDTO {

    private String atrAsunto;
    private String atrMensaje;

    public NotificacionDTO() {}

    public NotificacionDTO(String atrAsunto, String atrMensaje)
    {
        this.atrAsunto = atrAsunto;
        this.atrMensaje = atrMensaje;
    }

    public String getAtrAsunto()
    {
        return atrAsunto;
    }

    public void setAtrAsunto(String atrAsunto)
    {
        this.atrAsunto = atrAsunto;
    }

    public String getAtrMensaje()
    {
        return atrMensaje;
    }

    public void setAtrMensaje(String atrMensaje)
    {
        this.atrMensaje = atrMensaje;
    }
}
