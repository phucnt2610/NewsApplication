package hcmute.kltn.backend.service.service_implementation;

import hcmute.kltn.backend.dto.SavedArticleDTO;
import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.SavedArticle;
import hcmute.kltn.backend.entity.User;
import hcmute.kltn.backend.repository.ArticleRepo;
import hcmute.kltn.backend.repository.SavedArticleRepo;
import hcmute.kltn.backend.repository.UserRepo;
import hcmute.kltn.backend.service.SavedArticleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedArticleServiceImpl implements SavedArticleService {
    private final SavedArticleRepo savedArticleRepo;
    private final ArticleRepo articleRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public SavedArticleDTO addToList(SavedArticleDTO savedArticleDTO) {
        Article article = articleRepo.findById(savedArticleDTO.getArticle().getId())
                .orElseThrow(() -> new NullPointerException("Không tồn tại bài viết với id: " + savedArticleDTO.getArticle().getId()));
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();

        boolean exists = savedArticleRepo.existsByArticleAndUser(article, user);
        if (!exists) {
            SavedArticle savedArticle = new SavedArticle();
            savedArticle.setArticle(article);
            savedArticle.setUser(user);
            savedArticleRepo.save(savedArticle);
            return modelMapper.map(savedArticle, SavedArticleDTO.class);
        } else {
            throw new RuntimeException("Dữ liệu đã tồn tại.");
        }
    }

    @Override
    public String removeFromList(String articleId) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();
        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new NullPointerException("Không tồn tại bài viết với id: " + articleId));

        SavedArticle savedArticle = savedArticleRepo.findByArticleAndUser(article, user);
        if (savedArticle != null) {
            savedArticleRepo.delete(savedArticle);
            return "Đã xóa khỏi danh sách lưu.";
        } else {
            return "Không tìm thấy dữ liệu.";
        }

    }

    @Override
    public List<SavedArticleDTO> findList() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();
        List<SavedArticle> listSavedArticle = savedArticleRepo.findByUserId(user.getId());
        return listSavedArticle.stream()
                .map(article -> modelMapper.map(article, SavedArticleDTO.class))
                .collect(Collectors.toList());
    }


    @Override
    public SavedArticleDTO findOne(String articleId) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();
        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new NullPointerException("Không tồn tại bài viết với id: " + articleId));
        SavedArticle savedArticle = savedArticleRepo.findByArticleAndUser(article, user);
        return modelMapper.map(savedArticle, SavedArticleDTO.class);
    }
}
