package com.unicauca.facade_service.dto;

public enum enumModalidad {
    INVESTIGACION(2),        // Art. 9: hasta 2 estudiantes
    PRACTICA_PROFESIONAL(1); // Art. 29: individual

    private final int maxEstudiantes;
    enumModalidad(int max) { this.maxEstudiantes = max; }
    public int getMaxEstudiantes() { return maxEstudiantes; }
}
