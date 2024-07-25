package hcmute.kltn.backend.service.service_implementation;

import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.AverageStar;
import hcmute.kltn.backend.repository.ArticleRepo;
import hcmute.kltn.backend.repository.AverageStarRepo;
import hcmute.kltn.backend.service.AverageStarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AverageStarServiceImpl implements AverageStarService {
    private final ArticleRepo articleRepo;
    private final AverageStarRepo averageStarRepo;

    @Override
    public Float getAverageStar(String articleId) {
        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Không tồn tại bài viết với id: " + articleId));

        Float result = (float) 0;
        AverageStar averageStar = averageStarRepo.findByArticle(article);
        if (averageStar != null) {
            return averageStar.getAverageStar();
        } else {
            return result;
        }
    }
}
