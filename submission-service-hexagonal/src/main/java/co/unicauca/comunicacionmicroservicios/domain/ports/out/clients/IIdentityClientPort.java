package co.unicauca.comunicacionmicroservicios.domain.ports.out.clients;

import java.util.Map;

/**
 * @author javiersolanop777
 */
public interface IIdentityClientPort {

    /**
     * Obtiene el email del coordinador del programa.
     * Busca el primer usuario con rol COORDINADOR.
     *
     * Endpoint: GET /api/auth/users/search?rol=COORDINADOR
     *
     * @return Email del coordinador
     */
    public String getCoordinadorEmail();

    /**
     * Obtiene el email del jefe de departamento.
     * Busca el primer usuario con rol JEFE_DEPARTAMENTO.
     *
     * Endpoint: GET /api/auth/users/search?rol=JEFE_DEPARTAMENTO
     *
     * @return Email del jefe de departamento
     */
    public String getJefeDepartamentoEmail();

    /**
     * Busca un usuario por su ID.
     *
     * Endpoint: GET /api/auth/users/search?query={userId}
     *
     * @param userId ID del usuario
     * @return Map con datos del usuario (email, nombres, apellidos, etc.)
     */
    public Map<String, Object> getUserInfo(String userId);

    /**
     * Obtiene el email de un usuario por su ID.
     *
     * @param userId ID del usuario
     * @return Email del usuario
     */
    public String getUserEmail(String userId);

    /**
     * Obtiene el nombre completo de un usuario por su ID.
     *
     * @param userId ID del usuario
     * @return Nombre completo del usuario (nombres + apellidos)
     */
    public String getUserName(String userId);
}
