package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.NerKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NerKeywordRepo extends JpaRepository<NerKeyword, String> {
    @Query(value = """
            SELECT n.* FROM article a JOIN ner_keyword n ON a.id=n.article_id 
                       WHERE art_source='VN_EXPRESS' AND create_date 
                           BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 DAY) 
                           AND CURDATE() + INTERVAL 1 DAY
            """, nativeQuery = true)
    List<NerKeyword> getVnExpressWithin2DaysFromNow();

    @Query(value = """
            SELECT n.* FROM article a JOIN ner_keyword n ON a.id=n.article_id 
                       WHERE art_source='DAN_TRI' AND create_date 
                           BETWEEN DATE_SUB(CURDATE(), INTERVAL 3 DAY) 
                           AND CURDATE() + INTERVAL 1 DAY
            """, nativeQuery = true)
    List<NerKeyword> getDanTriWithin2DaysFromNow();
}
