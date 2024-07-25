package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepo extends JpaRepository<Tag, String> {
    boolean existsByValue(String value);

    Tag findByValue(String value);

    @Query(value = "SELECT tag.* FROM tag JOIN tag_article ON tag.id = tag_article.tags_id  WHERE article_id = ?1", nativeQuery = true)
    List<Tag> findByArticleId(String id);

}
