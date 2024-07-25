package hcmute.kltn.backend.service.service_implementation;

import hcmute.kltn.backend.entity.*;
import hcmute.kltn.backend.entity.enum_entity.ArtSource;
import hcmute.kltn.backend.entity.enum_entity.Status;
import hcmute.kltn.backend.repository.*;
import hcmute.kltn.backend.service.ArticleService;
import hcmute.kltn.backend.service.CrawlerService;
import hcmute.kltn.backend.service.ImageUploadService;
import hcmute.kltn.backend.service.NlpService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CrawlerServiceImpl implements CrawlerService {
    private final ArticleService articleService;
    private final CategoryRepo categoryRepo;
    private final TagArticleRepo tagArticleRepo;
    private final TagRepo tagRepo;
    private final ArticleRepo articleRepo;
    private final ImageUploadService imageUploadService;
    private final NlpService nlpService;
    private final NerKeywordRepo nerKeywordRepo;
    private final PendingInformationRepo pendingInformationRepo;

    @Override
    public void crawlVnExpress() {
        String url = "https://vnexpress.net/tin-tuc-24h";

        try {
            Document document = Jsoup.connect(url).get();
            Elements articles = document.select(".item-news.item-news-common");
            // lấy 10 bài viết mới nhất thỏa mãn
            Elements newestArts = new Elements();
            for (Element art : articles) {
                boolean check = checkValidVnExpress(art);
                if (check) {
                    newestArts.add(art);
                }
                if (newestArts.size() == 10) break;
            }
            for (Element newestArt : newestArts) {
                Article article = new Article();

                String title = newestArt.select("h3.title-news > a").text();
                String abstracts = newestArt.select("p.description a").text();
                String linkArticle = newestArt.select("h3.title-news a").attr("href");

                article.setTitle(title);
                article.setAbstracts(abstracts);

                boolean existedArt = articleRepo.existsByTitleOrAbstracts(article.getTitle(), article.getAbstracts());
                if (!existedArt) {

                    Article articleVnExpress = mainContentVnExpress(linkArticle);
                    boolean existedArt2 = articleRepo.existsByArtSourceAndAvatarAndCreate_date(
                            ArtSource.VN_EXPRESS, articleVnExpress.getAvatar(), articleVnExpress.getCreate_date());
                    if (!existedArt2) {
                        articleVnExpress.setTitle(article.getTitle());
                        articleVnExpress.setAbstracts(article.getAbstracts());

                        // save article, get and save tag
                        if (articleVnExpress.getCategory() != null) {
                            List<NerKeyword> listNerKeyArtDanTri = nerKeywordRepo.getDanTriWithin2DaysFromNow();
                            Document newArtDoc = Jsoup.parse(articleVnExpress.getContent());
                            String newArtText = newArtDoc.text();
                            String newArtTextEng = nlpService.separateSentenceAndTranslate(newArtText);
                            String nerKeyNewArt = nlpService.nerKeyword(newArtTextEng);
                            Article duplicatedArt = new Article();
                            Float pointSimilarity = 0f;
                            boolean checkFlag = false; // check bài có trùng với bài nào cùng ngày bên Dân Trí?
                            for (NerKeyword nerKeyArtDanTri : listNerKeyArtDanTri) {
                                if (!nerKeyArtDanTri.getNerKeyword().isEmpty()) { // none list ner keyword
                                    pointSimilarity = nlpService.calculateSimilarity(nerKeyNewArt, nerKeyArtDanTri.getNerKeyword());
                                    if (pointSimilarity > 0.5) {
                                        checkFlag = true; // có trùng, thoát khỏi vòng for
                                        duplicatedArt = nerKeyArtDanTri.getArticle();
                                        break;
                                    }
                                }
                            }
                            if (articleVnExpress.getAvatar() != null) {
                                if (imageUploadService.sizeChecker(articleVnExpress.getAvatar())) {
                                    String newUrl = imageUploadService.saveImageViaUrl(articleVnExpress.getAvatar());
                                    articleVnExpress.setAvatar(newUrl);
                                }
                            }
                            if (checkFlag) {
                                articleVnExpress.setStatus(Status.PENDING);
                                articleRepo.save(articleVnExpress);
                                // save list ner keyword
                                savedNerKeyArt(articleVnExpress, nerKeyNewArt);
                                // save pending information
                                PendingInformation pendingInformation = new PendingInformation();
                                pendingInformation.setSimilarity(pointSimilarity);
                                pendingInformation.setPendingArt(articleVnExpress);
                                pendingInformation.setDuplicatedArt(duplicatedArt);
                                pendingInformation.setHidden(false);
                                pendingInformationRepo.save(pendingInformation);
                            } else {
                                articleRepo.save(articleVnExpress);
                                // save list ner keyword
                                savedNerKeyArt(articleVnExpress, nerKeyNewArt);
                            }
                            List<String> listTags = getTagsVnExpress(linkArticle);
                            assert listTags != null;
                            saveTagArticle(listTags, articleVnExpress);
                        }
                    }
                }
            }
            System.out.println(LocalDateTime.now() + ": " + "End Crawl VnExpress");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void crawlDanTri() {
        String url = "https://dantri.com.vn/tin-moi-nhat.htm";
        try {
            Document document = Jsoup.connect(url).get();
            Elements articles = document.select(".article-list.article-item");
            Elements newestArts = new Elements();
            for (Element art : articles) {
                boolean check = checkValidDanTri(art);
                if (check) {
                    newestArts.add(art);
                }
                if (newestArts.size() == 10) break;
            }

            for (int i = 1; i < newestArts.size(); i++) {
                Article article = new Article();
                String title = newestArts.get(i).select("h3.article-title > a").text();
                String abstracts = newestArts.get(i).select("div.article-excerpt a").text();
                String linkArticle = "https://dantri.com.vn/" + newestArts.get(i).select("div.article-excerpt a").attr("href");
                article.setTitle(title);
                article.setAbstracts(abstracts);
                boolean existedArt = articleRepo.existsByTitleOrAbstracts(article.getTitle(), article.getAbstracts());
                if (!existedArt) {
                    Article articleDT = mainContentDanTri(linkArticle);
                    boolean existedArt2 = articleRepo.existsByArtSourceAndAvatarAndCreate_date(ArtSource.DAN_TRI, articleDT.getAvatar(), articleDT.getCreate_date());
                    if (!existedArt2) {
                        articleDT.setTitle(article.getTitle());
                        articleDT.setAbstracts(article.getAbstracts());
                        if (articleDT.getCategory() != null) {
                            List<NerKeyword> listNerKeyArtVnExpress = nerKeywordRepo.getVnExpressWithin2DaysFromNow();
                            Document newArtDoc = Jsoup.parse(articleDT.getContent());
                            String newArtText = newArtDoc.text();
                            String newArtTextEng = nlpService.separateSentenceAndTranslate(newArtText);
                            String nerKeyNewArt = nlpService.nerKeyword(newArtTextEng);
                            Article duplicatedArt = new Article();
                            Float pointSimilarity = 0f;
                            boolean checkFlag = false; // check bài có trùng với bài nào cùng ngày bên Dân Trí?
                            for (NerKeyword nerKeyArtVnExpress : listNerKeyArtVnExpress) {
                                if (!nerKeyArtVnExpress.getNerKeyword().isEmpty()) { // none list ner keyword
                                    pointSimilarity = nlpService.calculateSimilarity(nerKeyNewArt, nerKeyArtVnExpress.getNerKeyword());
                                    if (pointSimilarity > 0.5) {
                                        checkFlag = true; // có trùng, thoát khỏi vòng for
                                        duplicatedArt = nerKeyArtVnExpress.getArticle();
                                        break;
                                    }
                                }
                            }
                            if (articleDT.getAvatar() != null) {
                                if (imageUploadService.sizeChecker(articleDT.getAvatar())) {
                                    String newUrl = imageUploadService.saveImageViaUrl(articleDT.getAvatar());
                                    articleDT.setAvatar(newUrl);
                                }
                            }
                            if (checkFlag) {
                                articleDT.setStatus(Status.PENDING);
                                articleRepo.save(articleDT);
                                // save list ner keyword
                                savedNerKeyArt(articleDT, nerKeyNewArt);
                                // save pending information
                                PendingInformation pendingInformation = new PendingInformation();
                                pendingInformation.setSimilarity(pointSimilarity);
                                pendingInformation.setPendingArt(articleDT);
                                pendingInformation.setDuplicatedArt(duplicatedArt);
                                pendingInformation.setHidden(false);
                                pendingInformationRepo.save(pendingInformation);
                            } else {
                                articleRepo.save(articleDT);
                                // save list ner keyword
                                savedNerKeyArt(articleDT, nerKeyNewArt);
                            }
                            List<String> listTags = getTagsDanTri(linkArticle);
                            saveTagArticle(listTags, articleDT);
//                            if (!listTags.isEmpty()) {

//                                for (String listTag : listTags) {
//                                    TagArticle tagArticle = new TagArticle();
//                                    tagArticle.setArticle(articleDT);
//                                    Tag tag = tagRepo.findByValue(listTag);
//                                    if (tag == null) {
//                                        Tag newTag = new Tag();
//                                        newTag.setValue(listTag);
//                                        tagRepo.save(newTag);
//                                        tagArticle.setTag(newTag);
//                                    } else {
//                                        tagArticle.setTag(tag);
//                                    }
//                                    tagArticleRepo.save(tagArticle);
//                                }
//                            }
//                            }
                        }
                    }
                }
            }
            System.out.println(LocalDateTime.now() + ": " + "End Crawl DanTri");
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void savedNerKeyArt(Article article, String nerKeyArt) {
        NerKeyword nerKeyword = new NerKeyword();
        nerKeyword.setArticle(article);
        nerKeyword.setNerKeyword(nerKeyArt);

        nerKeywordRepo.save(nerKeyword);
    }

    private boolean checkValidVnExpress(Element elementArt) {
        // Check quảng cáo dựa theo thẻ của title
        String title = elementArt.select("h3.title-news > a").text();
        String html = elementArt.html();
        Document document = Jsoup.parse(html);
        // Check bài media
        Element mediaElement = document.selectFirst("span.icon_thumb_videophoto");
        return !title.isEmpty() && mediaElement == null;
    }

    private Article mainContentVnExpress(String url) {
        Article article = new Article();

        try {
            Document document = Jsoup.connect(url).get();

            // get create_date
            String date = document.select(".date").text();
            int commaIndex = date.indexOf(",");
            String trimmedString = date.substring(commaIndex + 1).trim();
            trimmedString = trimmedString.replaceAll("\\(.*\\)", "").trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy, HH:mm");
            LocalDateTime dateTime = LocalDateTime.parse(trimmedString, formatter);
            article.setCreate_date(dateTime);

            // get category
            Elements elementsCat = document.select("ul.breadcrumb li");
            if (elementsCat.size() == 1) {
                Category category;
                category = categoryRepo.findParentCatByName(elementsCat.get(0).text());
                if (category != null) {
                    Random random = new Random();
                    List<Category> childCat = categoryRepo.findChildCategories(category.getId());
                    article.setCategory(childCat.get(random.nextInt(childCat.size())));
                }
            } else {
                Category category, categoryParent;
                categoryParent = categoryRepo.findParentCatByName(elementsCat.get(0).text());
                if (categoryParent != null) {
                    category = categoryRepo.findByNameAndParent(elementsCat.get(1).text(), categoryParent);
                    if (category != null) {
                        article.setCategory(category);
                    }
                }
            }

            // get image avatar
            Element imgElement = document.selectFirst("img[itemprop=contentUrl]");
            if (imgElement != null) {
                String src = imgElement.attr("data-src");
                article.setAvatar(src);
            }

            // get content
            Element contentElement = document.selectFirst(".fck_detail");

            if (contentElement != null) {
                String content = contentElement.outerHtml();
                content = content.replace("amp;", "");
                content = content.replace("src=\"data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==\"", "");
                content = content.replace("data-src=", "src=");
                article.setContent(content);
            }

            article.setReading_time(articleService.readingTime(article.getContent()));
            article.setStatus(Status.PUBLIC);
            article.setArtSource(ArtSource.VN_EXPRESS);

            return article;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void saveTagArticle(List<String> listTags, Article article) {
        for (String tagValue : listTags) {
            TagArticle tagArticle = new TagArticle();
            tagArticle.setArticle(article);
            Tag tag = tagRepo.findByValue(tagValue);
            if (tag == null) {
                Tag newTag = new Tag();
                newTag.setValue(tagValue);
                tagRepo.save(newTag);
                tagArticle.setTag(newTag);
            } else {
                tagArticle.setTag(tag);
            }
            tagArticleRepo.save(tagArticle);
        }
    }

    private List<String> getTagsVnExpress(String url) {
        try {
            String headContent = Jsoup.connect(url).get().head().html();

            Document documentTag = Jsoup.parse(headContent);
            Element tagElement = documentTag.selectFirst("meta[name=keywords]");
            if (tagElement != null) {
                String tagContent = tagElement.attr("content");
                return Arrays.asList(tagContent.split(","));
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkValidDanTri(Element elementArt) {
        // Check bài photo
        Element photoElement = elementArt.selectFirst("div.photostory.article-category");
        // Check DNews
        Element dNewsElement = elementArt.selectFirst("div.dnews.article-category");
        //  Check Tâm điểm
        Element tamDiemElement = elementArt.selectFirst("div.blog.article-category");
        // Check dMagazine
        Element dMagazineElement = elementArt.selectFirst("div.dmagazine.article-category");
        return dNewsElement == null && photoElement == null && tamDiemElement == null && dMagazineElement == null;
    }

    private Article mainContentDanTri(String url) {
        Article article = new Article();

        try {
            Document document = Jsoup.connect(url).get();

            // get create_date
            String date = document.select("time.author-time").text();
            int commaIndex = date.indexOf(",");
            String trimmedString = date.substring(commaIndex + 1).trim();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm");
            LocalDateTime localDateTime = LocalDateTime.parse(trimmedString, formatter);
            article.setCreate_date(localDateTime);

            // get category
            Elements elementsCat = document.select("ul.dt-text-c808080.dt-text-base.dt-leading-5.dt-p-0.dt-list-none > li");
            Category category;
            Category parentCat = categoryRepo.findParentCatByNameOrSecond(elementsCat.get(0).text());
            if (elementsCat.size() == 1) {
                if (parentCat != null) {
                    Random random = new Random();
                    List<Category> childCat = categoryRepo.findChildCategories(parentCat.getId());
                    article.setCategory(childCat.get(random.nextInt(childCat.size())));
                }
            } else {
                category = categoryRepo.findBySecondOrName(elementsCat.get(1).text());
                if (category != null) {
                    article.setCategory(category);
                } else {
                    if (parentCat != null) {
                        Random random = new Random();
                        List<Category> childCat = categoryRepo.findChildCategories(parentCat.getId());
                        article.setCategory(childCat.get(random.nextInt(childCat.size())));
                    }
                }
            }

            // get image avatar
            Element imgElement = document.selectFirst("img[data-content-name=article-content-image]");
            if (imgElement != null) {
                String src = imgElement.attr("data-original");
                article.setAvatar(src);
            }

            // get content
            Element authorElement = document.selectFirst("div.author-name");
            Element contentElement = document.selectFirst("div.singular-content");
            if (contentElement != null) {
                String content = contentElement.outerHtml();
                content = content.replace("amp;", "");
                content = content.replace("src=\"data:image/svg+xml", "s=\"data:image/svg+xml");
                content = content.replace("data-src=", "src=");
                if (authorElement != null) {
                    content = content + authorElement.outerHtml();
                }
                article.setContent(content);
            }

            article.setReading_time(articleService.readingTime(article.getContent()));
            article.setStatus(Status.PUBLIC);
            article.setArtSource(ArtSource.DAN_TRI);

            return article;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<String> getTagsDanTri(String url) {
        try {
            List<String> stringList = new ArrayList<>();
            Document document = Jsoup.connect(url).get();

            Elements listTags = document.select("ul.tags-wrap.mt-30 > li");
            if (listTags.size() > 1) {
                for (int i = 1; i < listTags.size(); i++) {
                    stringList.add(listTags.get(i).text());
                }

            }
            return stringList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
