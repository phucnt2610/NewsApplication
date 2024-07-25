package hcmute.kltn.backend.service.service_implementation;

import hcmute.kltn.backend.dto.ReactEmotionDTO;
import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.ReactEmotion;
import hcmute.kltn.backend.entity.User;
import hcmute.kltn.backend.entity.enum_entity.TypeReact;
import hcmute.kltn.backend.repository.ArticleRepo;
import hcmute.kltn.backend.repository.ReactEmotionRepo;
import hcmute.kltn.backend.repository.UserRepo;
import hcmute.kltn.backend.service.ReactEmotionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactEmotionServiceImpl implements ReactEmotionService {
    private final ModelMapper modelMapper;
    private final ReactEmotionRepo reactEmotionRepo;
    private final UserRepo userRepo;
    private final ArticleRepo articleRepo;

    @Override
    public ReactEmotionDTO reactEmotion(ReactEmotionDTO reactEmotionDTO) {
        Article article = articleRepo.findById(reactEmotionDTO.getArticle().getId())
                .orElseThrow(() -> new NullPointerException("No article with id: " + reactEmotionDTO.getArticle().getId()));

        if (!reactEmotionDTO.getTypeReact().name().equals("LIKE") && (!reactEmotionDTO.getTypeReact().name().equals("CLAP")) &&
                (!reactEmotionDTO.getTypeReact().name().equals("STAR")) && (!reactEmotionDTO.getTypeReact().name().equals("HEART"))) {
            throw new RuntimeException("Loại cảm xúc không hợp lệ.");
        }
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();

        ReactEmotion foundReactEmotion = reactEmotionRepo.findByArticle_IdAndUser_Id(article.getId(), user.getId());
        ReactEmotion reactEmotion = new ReactEmotion();
        reactEmotion.setArticle(article);
        reactEmotion.setUser(user);
        reactEmotion.setTypeReact(reactEmotionDTO.getTypeReact());

        if (foundReactEmotion == null) {
            reactEmotionRepo.save(reactEmotion);
            return modelMapper.map(reactEmotion, ReactEmotionDTO.class);
        } else {
            if (reactEmotion.getTypeReact() == foundReactEmotion.getTypeReact()) {
                reactEmotionRepo.delete(foundReactEmotion);
                return null;
            } else {
                foundReactEmotion.setTypeReact(reactEmotion.getTypeReact());
                reactEmotionRepo.save(foundReactEmotion);
                return modelMapper.map(foundReactEmotion, ReactEmotionDTO.class);
            }
        }
    }

    @Override
    public int getReactQuantity(String articleId, TypeReact typeReact) {
        Article article = articleRepo.findById(articleId)
                .orElseThrow(() -> new NullPointerException("Không tồn tại bài viết với id: " + articleId));
        List<ReactEmotion> reactEmotionList = reactEmotionRepo.findByTypeReactAndArticle_Id(typeReact, article.getId());
        return reactEmotionList.size();
    }

}
