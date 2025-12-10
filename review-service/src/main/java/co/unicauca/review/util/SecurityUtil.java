package co.unicauca.review.util;

import co.unicauca.review.enums.EvaluatorRole;
import co.unicauca.review.exception.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class SecurityUtil {

    // Métodos estáticos existentes...
    public static Long getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String userIdHeader = request.getHeader("X-User-Id");
            if (userIdHeader != null && !userIdHeader.isEmpty()) {
                try {
                    return Long.parseLong(userIdHeader);
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Invalid X-User-Id header");
                }
            }
        }
        throw new IllegalStateException("X-User-Id header not found");
    }

    public static String getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String roleHeader = request.getHeader("X-User-Role");
            if (roleHeader != null && !roleHeader.isEmpty()) {
                return roleHeader;
            }
        }
        throw new IllegalStateException("X-User-Role header not found");
    }

    public static String getCurrentUserEmail() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            String emailHeader = request.getHeader("X-User-Email");
            if (emailHeader != null && !emailHeader.isEmpty()) {
                return emailHeader;
            }
        }
        return null;
    }

    private static HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    // NUEVOS MÉTODOS DE VALIDACIÓN

    /**
     * Valida que el usuario tenga un rol específico
     * @param userRole Rol del usuario (obtenido del header)
     * @param requiredRole Rol requerido para la operación
     * @throws UnauthorizedException si el rol no es válido
     */
    public void validateRole(String userRole, EvaluatorRole requiredRole) {
        if (userRole == null || !userRole.equals(requiredRole.name())) {
            throw new UnauthorizedException(
                    "Usuario no autorizado. Rol requerido: " + requiredRole.name() +
                            ", Rol actual: " + userRole
            );
        }
    }

    /**
     * Valida que el usuario tenga uno de los roles permitidos
     * @param userRole Rol del usuario
     * @param allowedRoles Roles permitidos
     * @throws UnauthorizedException si el rol no está en la lista
     */
    public void validateAnyRole(String userRole, EvaluatorRole... allowedRoles) {
        if (userRole == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        for (EvaluatorRole allowedRole : allowedRoles) {
            if (userRole.equals(allowedRole.name())) {
                return;
            }
        }

        throw new UnauthorizedException(
                "Usuario no autorizado. Rol actual: " + userRole
        );
    }

    /**
     * Valida que el ID del usuario coincida con un ID específico
     * Útil para validar que un usuario solo acceda a sus propios recursos
     * @param userId ID del usuario (obtenido del header)
     * @param targetId ID que intenta acceder
     * @throws UnauthorizedException si no coinciden
     */
    public void validateUserAccess(Long userId, Long targetId) {
        if (userId == null || !userId.equals(targetId)) {
            throw new UnauthorizedException(
                    "Acceso denegado. No tiene permisos para acceder a este recurso"
            );
        }
    }

    /**
     * Método de conveniencia para validar rol usando los headers actuales
     * @param requiredRole Rol requerido
     * @throws UnauthorizedException si el rol no es válido
     */
    public void validateCurrentUserRole(EvaluatorRole requiredRole) {
        String userRole = getCurrentUserRole();
        validateRole(userRole, requiredRole);
    }

    /**
     * Obtiene el rol actual como enum EvaluatorRole
     * @return Rol actual del usuario
     * @throws IllegalArgumentException si el rol no es válido
     */
    public EvaluatorRole getCurrentUserRoleAsEnum() {
        String roleStr = getCurrentUserRole();
        try {
            return EvaluatorRole.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException("Rol no válido: " + roleStr);
        }
    }
}