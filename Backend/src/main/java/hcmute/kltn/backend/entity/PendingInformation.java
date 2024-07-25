package hcmute.kltn.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pending_information")
public class PendingInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToOne
    @JoinColumn(
            name = "pending_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "pending_information_fk_1")
    )
    private Article pendingArt;

    @ManyToOne
    @JoinColumn(
            name = "duplicated_id",
            nullable = false,
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "pending_information_fk_2")
    )
    private Article duplicatedArt;

    @Column(nullable = false)
    private float similarity;

    @Column(nullable = false)
    private boolean isHidden;
}
