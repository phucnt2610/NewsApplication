package hcmute.kltn.backend.service;

import hcmute.kltn.backend.dto.ArticleDTO;

import java.util.List;

public interface ArticleService {
    Float readingTime(String content);
    List<ArticleDTO> getSavedArtByCat(String categoryId);

    ArticleDTO findById(String id);

    List<ArticleDTO> getTop3StarArticle();

    List<ArticleDTO> getTop5NewestArticle();

    List<ArticleDTO> getLatestArtPer4Cat();

    List<ArticleDTO> getLatestArtPerParentCat();

    List<ArticleDTO> getTop6ReactArt();

    List<ArticleDTO> getLatestByVnExpress(int count);

    List<ArticleDTO> getLatestByDanTri(int count);

    List<ArticleDTO> getRandomArtSameCat(String catId);

    List<ArticleDTO> findByCatId(String categoryId);

    List<ArticleDTO> findByTagId(String tagId);

    List<ArticleDTO> searchArticle(List<String> keyList);

}
