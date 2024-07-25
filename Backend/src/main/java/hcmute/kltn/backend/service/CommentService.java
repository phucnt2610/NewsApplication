package hcmute.kltn.backend.service;

import hcmute.kltn.backend.dto.CommentDTO;

import java.util.List;

public interface CommentService {
    CommentDTO createComment(CommentDTO commentDTO);
    String deleteComment(String commentId);
    CommentDTO updateComment(CommentDTO commentDTO, String commentId);
    List<CommentDTO> getParentComments(String articleId);
    List<CommentDTO> getChildComments(String commentId);
}
