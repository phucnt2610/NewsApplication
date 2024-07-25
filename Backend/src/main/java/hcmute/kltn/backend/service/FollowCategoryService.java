package hcmute.kltn.backend.service;

import hcmute.kltn.backend.dto.ArticleDTO;
import hcmute.kltn.backend.dto.CategoryDTO;
import hcmute.kltn.backend.dto.FollowCategoryDTO;

import java.util.List;

public interface FollowCategoryService {
    FollowCategoryDTO createFollow(FollowCategoryDTO followCategoryDTO);
    String removeFollow(String categoryId);
    List<ArticleDTO> getFollowedArticle();
    List<CategoryDTO> getFollowedParentCat();
    List<CategoryDTO> getFollowedChildCat(String categoryId);
}
