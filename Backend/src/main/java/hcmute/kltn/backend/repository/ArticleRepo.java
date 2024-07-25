package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.Category;
import hcmute.kltn.backend.entity.enum_entity.ArtSource;
import hcmute.kltn.backend.entity.enum_entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepo extends JpaRepository<Article, String> {

    // tìm cat theo catId
    @Query(value = """
            SELECT a.* FROM article a JOIN category c ON a.category_id=c.id\s
            WHERE c.parent_id=:categoryId AND a.`status`="PUBLIC" 
            ORDER BY a.create_date DESC
            """, nativeQuery = true)
    List<Article> findByParentCat(String categoryId);

    @Query(value = """
            SELECT a.* FROM article a WHERE a.`status`=\"PUBLIC\" 
            AND a.category_id=:categoryId 
            ORDER BY a.create_date DESC
            """, nativeQuery = true)
    List<Article> findByChildCat(String categoryId);

    // lấy bài viết đã lưu theo cat cha
    @Query(value = """
            SELECT a.* FROM article a JOIN saved_article s ON a.id=s.article_id JOIN category c 
                ON a.category_id=c.id WHERE c.parent_id=:categoryId AND s.user_id=:userId
            """, nativeQuery = true)
    List<Article> getSavedByParentCat(String categoryId, String userId);

    // lấy bài viết đã lưu theo cat con
    @Query(value = """
            SELECT a.* FROM article a JOIN saved_article s ON a.id=s.article_id 
                       WHERE a.category_id=:categoryId AND s.user_id=:userId
            """, nativeQuery = true)
    List<Article> getSavedByChildCat(String categoryId, String userId);

    // check exists article by title and abstracts
    @Query("select (count(a) > 0) from Article a where a.title = ?1 or a.abstracts = ?2")
    boolean existsByTitleOrAbstracts(String title, String abstracts);

    @Query("select (count(a) > 0) from Article a where a.artSource = ?1 and a.avatar = ?2 and a.create_date = ?3")
    boolean existsByArtSourceAndAvatarAndCreate_date(ArtSource artSource, String avatar, LocalDateTime createDate);

    // tìm article by category and status
    @Query("select a from Article a where a.category = ?1 and a.status = ?2")
    List<Article> findByCategoryAndStatus(Category category, Status status);

    @Query(value = """
            SELECT * FROM article WHERE status='PUBLIC' ORDER BY create_date DESC LIMIT 5
                        """, nativeQuery = true)
    List<Article> findTop5Newest();

    // tìm latest article mỗi parent category
    @Query(value = """
            SELECT a.* FROM article a JOIN category c ON a.category_id=c.id WHERE c.parent_id = :categoryId 
            AND a.status="PUBLIC" AND a.create_date = (SELECT MAX(a.create_date) FROM article a  
            JOIN category c ON a.category_id=c.id WHERE c.parent_id = :categoryId 
            AND a.status="PUBLIC") LIMIT 1
                """, nativeQuery = true)
    Article findLatestArtOfCat(String categoryId);

    // lấy 4 bài viết có SL react max
    @Query(value = """
            SELECT * FROM article a JOIN (SELECT article_id
            FROM react_emotion
            GROUP BY article_id
            ORDER BY COUNT(*) DESC
            LIMIT 6) react ON a.id = react.article_id""",
            nativeQuery = true)
    List<Article> findMostReactArticle();

    @Query(value = """
            SELECT * FROM article a where a.art_source='VN_EXPRESS' AND status='PUBLIC' ORDER BY a.create_date DESC LIMIT 6
            """, nativeQuery = true)
    List<Article> findByVnExpress();

    @Query(value = """
            SELECT * FROM article a where a.art_source='DAN_TRI' AND status='PUBLIC' ORDER BY a.create_date DESC LIMIT 6
            """, nativeQuery = true)
    List<Article> findByDanTri();

    @Query(value = """
            SELECT * FROM article WHERE (title LIKE CONCAT('% ', :keyword, ' %') 
            OR abstracts LIKE CONCAT('% ', :keyword, ' %'))AND status='PUBLIC'
            """, nativeQuery = true)
    List<Article> searchArticle(String keyword);

    // lấy article list theo tag
    @Query(value = """
            SELECT a.* FROM `article` a JOIN tag_article t ON a.id=t.article_id\s
            WHERE t.tags_id=:tagId AND a.`status`="PUBLIC"\s
            ORDER BY a.create_date DESC
            """, nativeQuery = true)
    List<Article> findByTag(String tagId);

    // lấy list order by average star DESC
    @Query(value = """
            SELECT article.* FROM article JOIN average_star ON article.id = average_star.article_id 
                             ORDER BY average_star.average_star DESC
                        """, nativeQuery = true)
    List<Article> getArticleOrderByAverageStar();

}
