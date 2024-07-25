package hcmute.kltn.backend.dto;

import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.Comment;
import hcmute.kltn.backend.entity.User;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private String id;
    @ManyToOne
    private Article article;
    @ManyToOne
    private User user;
    private String comment;
    private LocalDateTime create_date;
    @ManyToOne
    private Comment parent;
}
