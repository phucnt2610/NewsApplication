package hcmute.kltn.backend.service.service_implementation;

import com.darkprograms.speech.translator.GoogleTranslate;
import hcmute.kltn.backend.dto.ArticleDTO;
import hcmute.kltn.backend.entity.*;
import hcmute.kltn.backend.repository.*;
import hcmute.kltn.backend.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepo articleRepo;

    private final ModelMapper modelMapper;

    private final CategoryRepo categoryRepo;

    private final TagRepo tagRepo;
    private final UserRepo userRepo;

    @Override
    public ArticleDTO findById(String id) {
        Article article = articleRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết."));
        return modelMapper.map(article, ArticleDTO.class);
    }

    @Override
    public List<ArticleDTO> getTop3StarArticle() {
        List<Article> articleList = articleRepo.getArticleOrderByAverageStar();
        return articleList.subList(0, 3).stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDTO> getTop5NewestArticle() {
        List<Article> result = articleRepo.findTop5Newest();
        return result.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDTO> getLatestArtPer4Cat() {
        List<Category> categoryList = categoryRepo.find4ParentCatHaveMaxArticle();
        List<Article> result = new ArrayList<>();
        for (Category category : categoryList) {
            Article article = articleRepo.findLatestArtOfCat(category.getId());
            result.add(article);
        }
        return result.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDTO> getLatestArtPerParentCat() {
        List<Category> categoryList = categoryRepo.findParentCategories();
        List<Article> result = new ArrayList<>();
        for (Category category : categoryList) {
            Article article = articleRepo.findLatestArtOfCat(category.getId());
            result.add(article);
        }
        return result.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDTO> getTop6ReactArt() {
        List<Article> articleList = articleRepo.findMostReactArticle();
        return articleList.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDTO> getLatestByVnExpress(int count) {
        List<Article> articleList = articleRepo.findByVnExpress();
        return articleList.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDTO> getLatestByDanTri(int count) {
        List<Article> articleList = articleRepo.findByDanTri();
        return articleList.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDTO> getRandomArtSameCat(String catId) {
        List<Article> articleList = articleRepo.findByChildCat(catId);
        Collections.shuffle(articleList);
        if (articleList.size() < 5) {
            return articleList.stream()
                    .map(article -> modelMapper.map(article, ArticleDTO.class))
                    .collect(Collectors.toList());
        } else {
            return articleList.subList(0, 5).stream()
                    .map(article -> modelMapper.map(article, ArticleDTO.class))
                    .collect(Collectors.toList());
        }

    }

    @Override
    public List<ArticleDTO> findByCatId(String categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NullPointerException("Không tồn tại chuyên mục với id: " + categoryId));
        List<Category> categoryList = categoryRepo.findChildCategories(category.getId());
        List<Article> articleList;
        if (!categoryList.isEmpty()) {
            // là chuyên mục cha
            articleList = articleRepo.findByParentCat(categoryId);
        } else {
            // là chuyên mục con
            articleList = articleRepo.findByChildCat(categoryId);
        }
        return articleList.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDTO> findByTagId(String tagId) {
        Tag tag = tagRepo.findById(tagId)
                .orElseThrow(() -> new NullPointerException("Không tồn tại tag với id: " + tagId));
        List<Article> articleList = articleRepo.findByTag(tag.getId());
        return articleList.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleDTO> searchArticle(List<String> keyList) {
        List<String> vnKeyList = new ArrayList<>();
        List<Article> searchResult = new ArrayList<>();
        if (!keyList.isEmpty()) {
            for (String word : keyList) {
                word = translateEnToVi(word);
                vnKeyList.add(word);
            }
            for (String keyword : vnKeyList) {
                List<Article> articleList = articleRepo.searchArticle(keyword);
                for (Article article : articleList) {
                    if (!checkExistsInResult(searchResult, article)) {
                        searchResult.add(article);
                    }
                }
            }
        }
        return searchResult.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Float readingTime(String content) {
        int count = content.split("\\s+").length;
        int avgReadingSpeed = 200;
        return (float) (count / avgReadingSpeed);

    }

    @Override
    public List<ArticleDTO> getSavedArtByCat(String categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NullPointerException("Không tồn tại chuyên mục với id: " + categoryId));
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();
        boolean isParent = categoryRepo.existsByIdAndParentIdIsNull(category.getId());
        List<Article> articleList;
        if (isParent) {
            articleList = articleRepo.getSavedByParentCat(categoryId, user.getId());

        } else {
            articleList = articleRepo.getSavedByChildCat(categoryId, user.getId());
        }
        return articleList.stream()
                .map((element) -> modelMapper.map(element, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    private boolean checkExistsInResult(List<Article> articleList, Article article) {
        if (!articleList.isEmpty()) {
            for (Article art : articleList) {
                if (art.getId().equals(article.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String translateEnToVi(String word) {
        String result = word;
        try {
            String language = GoogleTranslate.detectLanguage(word);
            if (Objects.equals(language, "en")) {
                result = GoogleTranslate.translate("vi", word);
                if (result.equals("con mèo"))
                    result = "mèo";
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
