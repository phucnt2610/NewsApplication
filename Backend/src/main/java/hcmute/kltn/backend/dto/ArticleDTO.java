package hcmute.kltn.backend.dto;

import hcmute.kltn.backend.entity.Category;
import hcmute.kltn.backend.entity.User;
import hcmute.kltn.backend.entity.enum_entity.ArtSource;
import hcmute.kltn.backend.entity.enum_entity.Status;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDTO {
    private String id;
    private String title;
    private String abstracts;
    private String content;
    private LocalDateTime create_date;
    private float reading_time;
    private Status status;
    private String avatar;
    private ArtSource artSource;
    @ManyToOne
    private Category category;
}
