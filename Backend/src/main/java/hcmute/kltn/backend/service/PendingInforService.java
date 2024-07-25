package hcmute.kltn.backend.service;

import hcmute.kltn.backend.dto.PendingInformationDTO;

import java.util.List;

public interface PendingInforService {
    List<PendingInformationDTO> getPendingNonHidden();
    String publicArticle(String id);
    String refuseArticle(String id);

}
