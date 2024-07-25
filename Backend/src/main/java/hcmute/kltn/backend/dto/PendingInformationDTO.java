package hcmute.kltn.backend.dto;

import hcmute.kltn.backend.entity.Article;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class PendingInformationDTO {
    private String id;
    @OneToOne
    private Article pendingArt;
    @ManyToOne
    private Article duplicatedArt;
    private float similarity;
    private boolean isHidden;


}
