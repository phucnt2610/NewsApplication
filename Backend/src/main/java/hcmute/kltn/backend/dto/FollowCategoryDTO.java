package hcmute.kltn.backend.dto;

import hcmute.kltn.backend.entity.Category;
import hcmute.kltn.backend.entity.User;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class FollowCategoryDTO {
    private String id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Category category;
}
