package hcmute.kltn.backend.dto.request;

import hcmute.kltn.backend.entity.Tag;
import lombok.Data;

import java.util.List;

@Data
public class TagArticleRequest {
    private List<Tag> tagList;
}
