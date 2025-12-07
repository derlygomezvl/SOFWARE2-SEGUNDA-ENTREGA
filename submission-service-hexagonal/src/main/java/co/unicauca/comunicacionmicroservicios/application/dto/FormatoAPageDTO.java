package co.unicauca.comunicacionmicroservicios.application.dto;

import java.util.List;

public class FormatoAPageDTO {
    private List<FormatoAViewDTO> content;
    private int page;
    private int size;
    private long totalElements;

    public List<FormatoAViewDTO> getContent() { return content; }
    public void setContent(List<FormatoAViewDTO> content) { this.content = content; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
}
