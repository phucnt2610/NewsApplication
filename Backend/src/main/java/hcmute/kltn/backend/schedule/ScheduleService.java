package hcmute.kltn.backend.schedule;

import hcmute.kltn.backend.service.CrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final CrawlerService crawlerService;

    @Scheduled(fixedRate = 300000) // schedule per 5 minute
    public void crawlerArticle() {
        System.out.println("\n" + LocalDateTime.now() + ": " + "Crawl in VnExpress");
        crawlerService.crawlVnExpress();
        System.out.println(LocalDateTime.now() +  ": " + "Crawl in DanTri");
        crawlerService.crawlDanTri();
    }
}
