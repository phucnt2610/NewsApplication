package hcmute.kltn.backend.controller;

import hcmute.kltn.backend.dto.CommentDTO;
import hcmute.kltn.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/create")
    public ResponseEntity<CommentDTO> createComment(
            @RequestBody CommentDTO commentDTO){
        return ResponseEntity.ok(commentService.createComment(commentDTO));
    }

    @PostMapping("/update")
    public ResponseEntity<CommentDTO> updateComment(
            @RequestBody CommentDTO commentDTO,
            @RequestParam("commentId") String commentId){
        return ResponseEntity.ok(commentService.updateComment(commentDTO, commentId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteComment(
            @RequestParam("commentId") String commentId){
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }

    @GetMapping("/anonymous/get-parent-comments")
    public ResponseEntity<List<CommentDTO>> getParentComments(
            @RequestParam("articleId") String articleId) {
        return ResponseEntity.ok(commentService.getParentComments(articleId));
    }

    @GetMapping("/anonymous/get-child-comments")
    public ResponseEntity<List<CommentDTO>> getChildComments(
            @RequestParam("commentId") String commentId) {
        return ResponseEntity.ok(commentService.getChildComments(commentId));
    }
}
