package hcmute.kltn.backend.service;

import hcmute.kltn.backend.dto.TagDTO;

import java.util.List;

public interface TagService {
    TagDTO createTag(TagDTO tagDTO);
    String deleteTag(String id);
    TagDTO findTagById(String id);
    List<TagDTO> findAll();
    TagDTO updateTag(TagDTO tagDTO, String id);
    List<TagDTO> getTagsOfArticle(String articleId);
}
