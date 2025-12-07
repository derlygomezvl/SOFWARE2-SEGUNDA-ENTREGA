package co.unicauca.comunicacionmicroservicios.application.dto;

public class IdResponseDTO {
    private Long id;

    public IdResponseDTO() {}
    
    public IdResponseDTO(Long id) {
        this.id = id;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}