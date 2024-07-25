package hcmute.kltn.backend.repository;

import hcmute.kltn.backend.entity.Category;
import hcmute.kltn.backend.entity.FollowCategory;
import hcmute.kltn.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowCategoryRepo extends JpaRepository<FollowCategory, String> {
    FollowCategory findByUserAndCategory(User user, Category category);

    @Query("select f from FollowCategory f where f.user = ?1")
    List<FollowCategory> findByUser(User user);

    @Query("select (count(f) > 0) from FollowCategory f where f.user = ?1 and f.category = ?2")
    boolean existsByUserAndCategory(User user, Category category);

    @Query(value = "SELECT f.* FROM follow_category f JOIN category c ON " +
            "f.category_id=c.id WHERE f.user_id=:userId AND c.parent_id IS NOT NULL", nativeQuery = true)
    List<FollowCategory> findFollowedChildCat(String userId);


}
