package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.ReactEmotion;
import hcmute.kltn.backend.entity.User;
import hcmute.kltn.backend.entity.enum_entity.TypeReact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReactEmotionRepo extends JpaRepository<ReactEmotion, String> {
    ReactEmotion findByArticle_IdAndUser_Id(String articleId, String userId);

    List<ReactEmotion> findByTypeReactAndArticle_Id(TypeReact typeReact, String articleId);

    @Query("select r from ReactEmotion r where r.user = ?1")
    List<ReactEmotion> findByUser(User user);
}
