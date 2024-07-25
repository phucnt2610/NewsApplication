package hcmute.kltn.backend.service.service_implementation;

import hcmute.kltn.backend.dto.TagDTO;
import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.Tag;
import hcmute.kltn.backend.entity.TagArticle;
import hcmute.kltn.backend.repository.ArticleRepo;
import hcmute.kltn.backend.repository.TagArticleRepo;
import hcmute.kltn.backend.repository.TagRepo;
import hcmute.kltn.backend.service.TagService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepo tagRepo;
    private final ModelMapper modelMapper;
    private final ArticleRepo articleRepo;
    private final TagArticleRepo tagArticleRepo;

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public TagDTO createTag(TagDTO tagDTO) {
        Tag tag = new Tag();
        boolean existedTag = tagRepo.existsByValue(tagDTO.getValue());
        if (existedTag) {
            throw new RuntimeException("Đã tồn tại tag: " + tagDTO.getValue());
        } else {
            tag.setValue(tagDTO.getValue());
            tagRepo.save(tag);
        }
        return modelMapper.map(tag, TagDTO.class);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public String deleteTag(String id) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tồn tại tag với id: " + id));

        List<TagArticle> tagArticleList =tagArticleRepo.findByTag(tag);
        if (!tagArticleList.isEmpty()) {
            tagArticleRepo.deleteAll(tagArticleList);
        }
        tagRepo.delete(tag);
        return "Xóa thành công.";
    }

    @Override
    public TagDTO findTagById(String id) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tồn tại tag với id: " + id));

        return modelMapper.map(tag, TagDTO.class);
    }

    @Override
    public List<TagDTO> findAll() {
        List<Tag> allTags = tagRepo.findAll();
        return allTags.stream()
                .map(tag -> modelMapper.map(tag, TagDTO.class))
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public TagDTO updateTag(TagDTO tagDTO, String id) {
        Tag tag = tagRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tồn tại tag với id: " + id));
        boolean existedTag = tagRepo.existsByValue(tagDTO.getValue());
        if (existedTag) {
            throw new RuntimeException("Giá trị tag cập nhật đã tồn tại.");
        } else {
            tag.setValue(tagDTO.getValue());
            tagRepo.save(tag);
        }
        return modelMapper.map(tag, TagDTO.class);
    }

    @Override
    public List<TagDTO> getTagsOfArticle(String articleId) {
        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new NullPointerException("Không tồn tại bài viết với id: " + articleId));
        List<Tag> tagList = tagRepo.findByArticleId(article.getId());
        return tagList.stream()
                .map(tag -> modelMapper.map(tag, TagDTO.class))
                .collect(Collectors.toList());
    }

}
