package hcmute.kltn.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ner_keyword")
public class NerKeyword {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(
            name = "article_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "nlp_generate_key_fk_1")
    )
    private Article article;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String nerKeyword;
}
