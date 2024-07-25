package hcmute.kltn.backend.dto;

import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.Tag;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class TagArticleDTO {
    private String id;
    @ManyToOne
    private Article article;
    @ManyToOne
    private Tag tag;
}
