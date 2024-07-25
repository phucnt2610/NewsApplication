package hcmute.kltn.backend.controller;

import hcmute.kltn.backend.dto.ReactEmotionDTO;
import hcmute.kltn.backend.entity.enum_entity.TypeReact;
import hcmute.kltn.backend.service.ReactEmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/react-emotion")
@RequiredArgsConstructor
public class ReactEmotionController {
    private final ReactEmotionService reactEmotionService;

    @PostMapping("/react")
    public ResponseEntity<ReactEmotionDTO> react(
            @RequestBody ReactEmotionDTO reactEmotionDTO) {
        return ResponseEntity.ok(reactEmotionService.reactEmotion(reactEmotionDTO));
    }

    @GetMapping("/anonymous/get-react-quantity")
    public ResponseEntity<Integer> getUsers(
            @RequestParam("articleId") String articleId,
            @RequestParam("typeReact") TypeReact typeReact){
        return ResponseEntity.ok(reactEmotionService.getReactQuantity(articleId, typeReact));
    }
}
