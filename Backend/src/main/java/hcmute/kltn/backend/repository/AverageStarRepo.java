package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.AverageStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AverageStarRepo extends JpaRepository<AverageStar, String> {

    @Query("select a from AverageStar a where a.article = ?1")
    AverageStar findByArticle(Article article);
}
