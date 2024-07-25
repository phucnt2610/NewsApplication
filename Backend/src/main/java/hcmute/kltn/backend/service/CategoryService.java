package hcmute.kltn.backend.service;

import hcmute.kltn.backend.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    String deleteCategory(String id);
    CategoryDTO findCategoryById(String id);
    List<CategoryDTO> findAll();
    CategoryDTO updateCategory(CategoryDTO categoryDTO, String id);
    List<CategoryDTO> findChildCategories(String parentId);
    List<CategoryDTO> findParentCategories();
    List<CategoryDTO> findParentCategoriesShorten();
}
