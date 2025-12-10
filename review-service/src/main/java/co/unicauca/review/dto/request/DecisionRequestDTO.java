package co.unicauca.review.dto.request;

import co.unicauca.review.enums.Decision;
import jakarta.validation.constraints.NotNull;

// Debe coincidir con la estructura que enviaste en el body
public record DecisionRequestDTO(
        @NotNull Decision decision,
        String observaciones
) {}