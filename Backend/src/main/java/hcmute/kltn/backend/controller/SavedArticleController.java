package hcmute.kltn.backend.controller;

import hcmute.kltn.backend.dto.SavedArticleDTO;
import hcmute.kltn.backend.service.SavedArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/saved-articles")
@RequiredArgsConstructor
public class SavedArticleController {
    private final SavedArticleService savedArticleService;

    // lưu bài viết vào danh sách
    @PostMapping("/add")
    public ResponseEntity<SavedArticleDTO> addToList(
            @RequestBody SavedArticleDTO savedArticleDTO) {
        return ResponseEntity.ok(savedArticleService.addToList(savedArticleDTO));
    }

    // xóa bài viết khỏi danh sách
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeFromList(
            @RequestParam("articleId") String articleId) {
        return ResponseEntity.ok(savedArticleService.removeFromList(articleId));
    }

    // danh sách các bài viết đã lưu
    @GetMapping("/get-list")
    public ResponseEntity<List<SavedArticleDTO>> getList(){
        return ResponseEntity.ok(savedArticleService.findList());
    }

    // lấy thông tin một bài viết trong danh sách đã lưu
    @GetMapping("get-one")
    public ResponseEntity<SavedArticleDTO> getOne(
            @RequestParam("articleId") String articleId){
        return ResponseEntity.ok(savedArticleService.findOne(articleId));
    }
}
