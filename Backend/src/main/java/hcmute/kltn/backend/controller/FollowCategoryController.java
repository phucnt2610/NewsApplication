package hcmute.kltn.backend.controller;

import hcmute.kltn.backend.dto.ArticleDTO;
import hcmute.kltn.backend.dto.CategoryDTO;
import hcmute.kltn.backend.dto.FollowCategoryDTO;
import hcmute.kltn.backend.service.FollowCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/follow-category")
@RequiredArgsConstructor
public class FollowCategoryController {
    private final FollowCategoryService followCategoryService;

    @PostMapping("/follow")
    public ResponseEntity<FollowCategoryDTO> followCategory(
            @RequestBody FollowCategoryDTO followCategoryDTO){
        return ResponseEntity.ok(followCategoryService.createFollow(followCategoryDTO));
    }

    @DeleteMapping("/un-follow")
    public ResponseEntity<String> unFollowCategory(
            @RequestParam String categoryId){
        return ResponseEntity.ok(followCategoryService.removeFollow(categoryId));
    }

    @GetMapping("/get-followed-articles")
    public ResponseEntity<List<ArticleDTO>> getFollowedArticles(){
        return ResponseEntity.ok(followCategoryService.getFollowedArticle());
    }

    @GetMapping("/get-followed-parent-cat")
    public ResponseEntity<List<CategoryDTO>> getFollowedParentCat(){
        return ResponseEntity.ok(followCategoryService.getFollowedParentCat());
    }

    @GetMapping("/get-followed-child-cat")
    public ResponseEntity<List<CategoryDTO>> getFollowedChildCat(
            @RequestParam("categoryId") String categoryId){
        return ResponseEntity.ok(followCategoryService.getFollowedChildCat(categoryId));
    }
}
