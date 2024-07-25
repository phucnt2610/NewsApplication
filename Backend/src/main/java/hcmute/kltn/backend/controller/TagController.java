package hcmute.kltn.backend.controller;

import hcmute.kltn.backend.dto.TagDTO;
import hcmute.kltn.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tag")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @PostMapping("/create")
    public ResponseEntity<TagDTO> createTag (@RequestBody TagDTO tagDTO){
        return ResponseEntity.ok(tagService.createTag(tagDTO));
    }

    @PostMapping("/update")
    public ResponseEntity<TagDTO> updateTag(
            @RequestParam("tagId") String tagId,
            @RequestBody TagDTO tagDTO){
        return ResponseEntity.ok(tagService.updateTag(tagDTO, tagId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteTag (@RequestParam("tagId") String tagId){
        return ResponseEntity.ok(tagService.deleteTag(tagId));
    }

    @GetMapping("/anonymous/get-all-tags")
    public ResponseEntity<List<TagDTO>> getAllTags(){
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/anonymous/get-tag")
    public ResponseEntity<TagDTO> getTag(@RequestParam("tagId") String tagId){
        return ResponseEntity.ok(tagService.findTagById(tagId));
    }

    @GetMapping("/anonymous/get-tags-of-art")
    public ResponseEntity<List<TagDTO>> getTagsOfArt(
            @RequestParam("articleId") String articleId){
        return ResponseEntity.ok(tagService.getTagsOfArticle(articleId));
    }
}
