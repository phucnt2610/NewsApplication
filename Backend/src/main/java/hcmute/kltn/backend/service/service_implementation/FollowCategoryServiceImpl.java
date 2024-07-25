package hcmute.kltn.backend.service.service_implementation;

import hcmute.kltn.backend.dto.ArticleDTO;
import hcmute.kltn.backend.dto.CategoryDTO;
import hcmute.kltn.backend.dto.FollowCategoryDTO;
import hcmute.kltn.backend.entity.Article;
import hcmute.kltn.backend.entity.Category;
import hcmute.kltn.backend.entity.FollowCategory;
import hcmute.kltn.backend.entity.User;
import hcmute.kltn.backend.entity.enum_entity.Status;
import hcmute.kltn.backend.repository.ArticleRepo;
import hcmute.kltn.backend.repository.CategoryRepo;
import hcmute.kltn.backend.repository.FollowCategoryRepo;
import hcmute.kltn.backend.repository.UserRepo;
import hcmute.kltn.backend.service.FollowCategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowCategoryServiceImpl implements FollowCategoryService {
    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final FollowCategoryRepo followCategoryRepo;
    private final ArticleRepo articleRepo;

    @Override
    public FollowCategoryDTO createFollow(FollowCategoryDTO followCategoryDTO) {
        Category category = categoryRepo.findById(followCategoryDTO.getCategory().getId())
                .orElseThrow(() -> new NullPointerException("Không tồn tại chuyên mục với id: " +
                        followCategoryDTO.getCategory().getId()));
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();

        boolean exists = followCategoryRepo.existsByUserAndCategory(user, category);
        if (!exists) {
            FollowCategory followCategory = new FollowCategory();
            followCategory.setCategory(category);
            followCategory.setUser(user);
            followCategoryRepo.save(followCategory);

            Category parentCategory = categoryRepo.findParentCatByChild(category.getId());
            List<Category> childCategory = categoryRepo.findChildCategories(category.getId());
            if (!childCategory.isEmpty()) {
                for (Category childCat : childCategory) {
                    boolean exists2 = followCategoryRepo.existsByUserAndCategory(user, childCat);
                    if (!exists2) {
                        FollowCategory tempFollow = new FollowCategory();
                        tempFollow.setUser(user);
                        tempFollow.setCategory(childCat);
                        followCategoryRepo.save(tempFollow);
                    }
                }
            } else if (parentCategory != null) {
                boolean exists3 = followCategoryRepo.existsByUserAndCategory(user, parentCategory);
                if (!exists3) {
                    FollowCategory tempFollow = new FollowCategory();
                    tempFollow.setUser(user);
                    tempFollow.setCategory(parentCategory);
                    followCategoryRepo.save(tempFollow);
                }
            }
            return modelMapper.map(followCategory, FollowCategoryDTO.class);
        } else {
            return null;
        }
    }

    @Override
    public String removeFollow(String categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NullPointerException("Không tồn tại chuyên mục với id: " + categoryId));
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();

        FollowCategory followCategory = followCategoryRepo.findByUserAndCategory(user, category);
        if (followCategory == null) {
            throw new RuntimeException("Người dùng hoặc chuyên mục không hợp lệ.");
        } else {
            followCategoryRepo.delete(followCategory);

            List<Category> childCategory = categoryRepo.findChildCategories(category.getId());
            if (!childCategory.isEmpty()) { // là cate cha: xóa cate cha -> xóa all con
                for (Category childCat : childCategory) {
                    FollowCategory followChildCategory = followCategoryRepo.findByUserAndCategory(user, childCat);
                    if (followChildCategory != null) {
                        followCategoryRepo.delete(followChildCategory);
                    }
                }
                return "Bỏ theo dõi thành công chuyên mục: " + category.getName() + " và các chuyên mục con.";
            }
            return "Bỏ theo dõi thành công chuyên mục: " + category.getName() ;
        }
    }

    @Override
    public List<ArticleDTO> getFollowedArticle() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();

        List<Article> articleList = new ArrayList<>();
        List<FollowCategory> followCategoryList = followCategoryRepo.findFollowedChildCat(user.getId());
        for (FollowCategory followCat : followCategoryList) {
            List<Article> tempArticle = articleRepo.findByCategoryAndStatus(followCat.getCategory(),
                    Status.PUBLIC);
            articleList.addAll(tempArticle);
        }
        articleList.sort(Comparator.comparing(Article::getCreate_date, Comparator.reverseOrder()));

        return articleList.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getFollowedParentCat() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();

        List<Category> categoryList = categoryRepo.findFollowedParentCat(user.getId());
        return categoryList.stream().
                map((element) -> modelMapper.map(element, CategoryDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getFollowedChildCat(String categoryId) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name).orElseThrow();

        List<Category> categoryList = categoryRepo.findFollowedChildCat(categoryId,
                user.getId());
        return categoryList.stream().
                map((element) -> modelMapper.map(element, CategoryDTO.class))
                .collect(Collectors.toList());
    }
}
