package hcmute.kltn.backend.dto;

import hcmute.kltn.backend.entity.Category;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryDTO {
    private String id;
    private String name;
    private String second_name;
    private LocalDateTime date_create;
    @ManyToOne
    private Category parent;
}
