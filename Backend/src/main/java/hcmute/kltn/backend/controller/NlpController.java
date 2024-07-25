package hcmute.kltn.backend.controller;

import hcmute.kltn.backend.service.NlpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// API test
@RestController
@RequestMapping("/api/v1/nlp")
@RequiredArgsConstructor
public class NlpController {
    private final NlpService nlpService;

    @GetMapping("/anonymous/test-ner")
    public ResponseEntity<String> getNerKeyword(@RequestParam("articleId") String articleId) {
        return ResponseEntity.ok(nlpService.nerKeyFromArt(articleId));
    }

    @GetMapping("/anonymous/test-calculate")
    public ResponseEntity<Float> getSimilarity(
            @RequestParam("text1") String text1,
            @RequestParam("text2") String text2) {
        return ResponseEntity.ok(nlpService.calculateSimilarity(text1, text2));
    }
}
