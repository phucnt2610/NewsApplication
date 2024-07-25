package hcmute.kltn.backend.service.service_implementation;

import com.darkprograms.speech.translator.GoogleTranslate;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreSentence;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.nlp.Pipeline;
import hcmute.kltn.backend.repository.ArticleRepo;
import hcmute.kltn.backend.service.NlpService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class NlpServiceImpl implements NlpService {
    private final ArticleRepo articleRepo;

    public NlpServiceImpl(ArticleRepo articleRepo) {
        this.articleRepo = articleRepo;
    }

    @Override
    public String nerKeyword(String text) {
        String result = "";
        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
        CoreDocument coreDocument = new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreLabel> coreLabelList = coreDocument.tokens();
        for (CoreLabel coreLabel : coreLabelList) {
            String ner = coreLabel.get(CoreAnnotations.NamedEntityTagAnnotation.class);
            if (ner.equals("ORGANIZATION") || ner.equals("LOCATION") || ner.equals("PERSON")) {
                if (!result.contains(coreLabel.originalText())) {
                    result = result + " " + coreLabel.originalText();
                }
            }
        }
        return result;
    }

    @Override
    public String separateSentenceAndTranslate(String text) {
        String result = null;
        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();
        CoreDocument coreDocument = new CoreDocument(text);
        stanfordCoreNLP.annotate(coreDocument);
        List<CoreSentence> sentences = coreDocument.sentences();
        for (CoreSentence sentence : sentences) {
            String sentenceEnglish = translateViToEn(sentence.toString());
            result = result + sentenceEnglish + " ";
        }
        return result;
    }

    @Override
    public String nerKeyFromArt(String articleId) {
        Article article = articleRepo.findById(articleId)
                .orElseThrow();
        Document document = Jsoup.parse(article.getContent());
        String textContent = document.text();
        String textEnglish = separateSentenceAndTranslate(textContent);
        return nerKeyword(textEnglish);
    }

    @Override
    public Float calculateSimilarity(String str1, String str2) {
        str1 = str1.toLowerCase();
        str2 = str2.toLowerCase();
        String[] words1 = str1.split("\\s+");
        String[] words2 = str2.split("\\s+");
        int matchingWords = 0;
        for (String word1 : words1) {
            for (String word2 : words2) {
                if (word1.equals(word2)) {
                    matchingWords++;
                    break;
                }
            }
        }
        float result = (float) matchingWords / Math.max(words1.length, words2.length);
        return Math.round(result * 100) / 100f;
    }

    @Override
    public String translateViToEn(String text) {
        String result = text;
        try {
            String language = GoogleTranslate.detectLanguage(text);
            if (Objects.equals(language, "vi")) {
                result = GoogleTranslate.translate("en", text);
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
