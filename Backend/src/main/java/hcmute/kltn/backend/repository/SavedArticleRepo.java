package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.SavedArticle;
import hcmute.kltn.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedArticleRepo extends JpaRepository<SavedArticle, String> {
    // lấy bài viết đã lưu của user
    @Query("SELECT new SavedArticle (s.id, s.article, s.user) " +
            "FROM SavedArticle s WHERE s.user.id = :userId")
    List<SavedArticle> findByUserId(String userId);

    @Query("select s from SavedArticle s where s.article = ?1 and s.user = ?2")
    SavedArticle findByArticleAndUser(Article article, User user);

    @Query("select (count(s) > 0) from SavedArticle s where s.article = ?1 and s.user = ?2")
    boolean existsByArticleAndUser(Article article, User user);
}
