package hcmute.kltn.backend.controller;

import hcmute.kltn.backend.dto.CategoryDTO;
import hcmute.kltn.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDTO));
    }

    @PostMapping("/update")
    public ResponseEntity<CategoryDTO> updateCategory(
            @RequestParam("categoryId") String categoryId,
            @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryDTO, categoryId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCategory(
            @RequestParam("categoryId") String categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

    @GetMapping("/anonymous/get-all-categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }


    @GetMapping("/anonymous/get-category")
    public ResponseEntity<CategoryDTO> getCategory(
            @RequestParam("categoryId") String categoryId) {
        return ResponseEntity.ok(categoryService.findCategoryById(categoryId));
    }


    @GetMapping("/anonymous/get-child")
    public ResponseEntity<List<CategoryDTO>> getChildCats(
            @RequestParam("categoryId") String categoryId) {
        return ResponseEntity.ok(categoryService.findChildCategories(categoryId));
    }

    @GetMapping("/anonymous/get-all-parent")
    public ResponseEntity<List<CategoryDTO>> getAllParentCats() {
        return ResponseEntity.ok(categoryService.findParentCategories());
    }

    @GetMapping("/anonymous/get-shorten-parent")
    public ResponseEntity<List<CategoryDTO>> getShortenParentCats() {
        return ResponseEntity.ok(categoryService.findParentCategoriesShorten());
    }

}
