package hcmute.kltn.backend.controller;

import hcmute.kltn.backend.dto.PendingInformationDTO;
import hcmute.kltn.backend.service.PendingInforService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pending")
@RequiredArgsConstructor
public class PendingInforController {
    private final PendingInforService pendingInforService;

    @GetMapping("/get-all-pending")
    public ResponseEntity<List<PendingInformationDTO>> getAllPending() {
        return ResponseEntity.ok(pendingInforService.getPendingNonHidden());
    }

    @PostMapping("/public-article")
    public ResponseEntity<String> publicArticle(
            @RequestBody PendingInformationDTO pendingInformationDTO) {
        return ResponseEntity.ok(pendingInforService.publicArticle(pendingInformationDTO.getId()));
    }

    @PostMapping("/refuse-article")
    public ResponseEntity<String> refuseArticle(
            @RequestBody PendingInformationDTO pendingInformationDTO) {
        return ResponseEntity.ok(pendingInforService.refuseArticle(pendingInformationDTO.getId()));
    }
}
