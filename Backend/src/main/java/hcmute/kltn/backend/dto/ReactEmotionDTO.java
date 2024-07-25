package hcmute.kltn.backend.dto;

import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.User;
import hcmute.kltn.backend.entity.enum_entity.TypeReact;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class ReactEmotionDTO {
    private String id;
    @ManyToOne
    private Article article;

    @ManyToOne
    private User user;

    private TypeReact typeReact;
}
