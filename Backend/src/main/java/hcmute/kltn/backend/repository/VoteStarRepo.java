package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.User;
import hcmute.kltn.backend.entity.VoteStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteStarRepo extends JpaRepository<VoteStar, String> {
    @Query("SELECT new VoteStar (v.id, v.article, v.user, v.star) " +
            "FROM VoteStar v WHERE v.article.id = :articleId and v.user.id = :userId ")
    VoteStar findExistedVote(String articleId, String userId);

    // lấy những vote của bài viết chỉ định
    @Query("SELECT new VoteStar(v.id, v.article, v.user, v.star) " +
            "FROM VoteStar v WHERE v.article.id = :articleId ")
    List<VoteStar> findListVote(String articleId);

    @Query("select v from VoteStar v where v.user = ?1")
    List<VoteStar> findByUser(User user);
}
