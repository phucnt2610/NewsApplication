package hcmute.kltn.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tag_article")
public class TagArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(
            name = "article_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "tag_article_fk_1")
    )
    private Article article;

    @ManyToOne
    @JoinColumn(
            name = "tags_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "tags_articles_fk_2")
    )
    private Tag tag;
}
