package hcmute.kltn.backend.dto;

import hcmute.kltn.backend.entity.Article;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AverageStarDTO {
    private String id;
    private float averageStar;
    @ManyToOne
    private Article article;

}
