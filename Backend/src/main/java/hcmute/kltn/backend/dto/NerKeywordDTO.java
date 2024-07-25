package hcmute.kltn.backend.dto;

import hcmute.kltn.backend.entity.Article;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class NerKeywordDTO {
    private String id;
    @OneToOne
    private Article article;
    private String nerKeyword;
}
