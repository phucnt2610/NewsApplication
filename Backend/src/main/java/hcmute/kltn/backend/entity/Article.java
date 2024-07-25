package hcmute.kltn.backend.entity;

import hcmute.kltn.backend.entity.enum_entity.ArtSource;
import hcmute.kltn.backend.entity.enum_entity.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "article")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String title;

    @Column(nullable = false)
    private LocalDateTime create_date;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String abstracts;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private float reading_time;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    private String avatar;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ArtSource artSource;

    @ManyToOne
    @JoinColumn(
            name = "category_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "article_fk_1")
    )
    private Category category;

}
