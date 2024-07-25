package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.Comment;
import hcmute.kltn.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepo extends JpaRepository<Comment, String> {
    @Query(value = "SELECT * FROM `comment` c WHERE c.article_id= :articleId " +
            "AND c.parent_id is NULL ORDER BY c.create_date DESC", nativeQuery = true)
    List<Comment> findParentByArticleIdOrderNewest(String articleId);

    @Query(value = "SELECT * FROM `comment` c WHERE c.parent_id= :commentId" +
            " ORDER BY c.create_date DESC", nativeQuery = true)
    List<Comment> findChildByIdOrderNewest(String commentId);

    @Query("select c from Comment c where c.user = ?1")
    List<Comment> findByUser(User user);
}
