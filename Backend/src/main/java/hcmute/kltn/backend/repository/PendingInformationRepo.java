package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.PendingInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingInformationRepo extends JpaRepository<PendingInformation, String> {
    @Query(value = """
            SELECT * FROM pending_information WHERE is_hidden = false
                        """, nativeQuery = true)
    List<PendingInformation> getPendingInforNonHidden();
}
