package hcmute.kltn.backend.service.service_implementation;

import hcmute.kltn.backend.dto.PendingInformationDTO;
import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.PendingInformation;
import hcmute.kltn.backend.entity.enum_entity.Status;
import hcmute.kltn.backend.repository.ArticleRepo;
import hcmute.kltn.backend.repository.PendingInformationRepo;
import hcmute.kltn.backend.service.PendingInforService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PendingInforServiceImpl implements PendingInforService {
    private final ModelMapper modelMapper;
    private final PendingInformationRepo pendingInformationRepo;
    private final ArticleRepo articleRepo;

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public List<PendingInformationDTO> getPendingNonHidden() {
        List<PendingInformation> allData = pendingInformationRepo.getPendingInforNonHidden();
        return allData.stream()
                .map(data -> modelMapper.map(data, PendingInformationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String publicArticle(String id) {
        PendingInformation pendingInformation = pendingInformationRepo.findById(id)
                .orElseThrow(() -> new NullPointerException("Không tồn tại dữ liệu với id: " + id));
        pendingInformation.setHidden(true);
        pendingInformationRepo.save(pendingInformation);

        Article pendingArt = pendingInformation.getPendingArt();
        pendingArt.setStatus(Status.PUBLIC);
        articleRepo.save(pendingArt);
        return "Xác nhận thông tin bài viết không trùng lặp.";
    }

    @Override
    public String refuseArticle(String id) {
        PendingInformation pendingInformation = pendingInformationRepo.findById(id)
                .orElseThrow(() -> new NullPointerException("Không tồn tại dữ liệu với id: " + id));
        pendingInformation.setHidden(true);
        pendingInformationRepo.save(pendingInformation);

        Article pendingArt = pendingInformation.getPendingArt();
        pendingArt.setStatus(Status.REFUSED);
        articleRepo.save(pendingArt);
        return "Xác nhận thông tin bài viết trùng lặp.";
    }


}
