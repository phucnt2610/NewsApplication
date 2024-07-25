package hcmute.kltn.backend.dto.request;

import hcmute.kltn.backend.entity.Category;
import hcmute.kltn.backend.entity.TagArticle;
import hcmute.kltn.backend.entity.User;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
public class ArticleRequest {
    private String title;
    private String abstracts;
    private String content;
    @ManyToOne
    private Category category;
    @ManyToOne
    private User user;

}
