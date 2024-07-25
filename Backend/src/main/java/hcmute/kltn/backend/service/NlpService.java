package hcmute.kltn.backend.service;

public interface NlpService {
    String nerKeyword(String text);

    Float calculateSimilarity(String str1, String str2);

    String translateViToEn(String text);

    String separateSentenceAndTranslate(String text);

    String nerKeyFromArt(String articleId);
}
