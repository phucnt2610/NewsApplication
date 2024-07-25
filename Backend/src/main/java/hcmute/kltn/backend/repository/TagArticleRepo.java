package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.Tag;
import hcmute.kltn.backend.entity.TagArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagArticleRepo extends JpaRepository<TagArticle, String> {
    List<TagArticle> findByTag(Tag tag);
}
