package hcmute.kltn.backend.service.service_implementation;

import hcmute.kltn.backend.dto.UserDTO;
import hcmute.kltn.backend.dto.request.UpdatePassRequest;
import hcmute.kltn.backend.entity.*;
import hcmute.kltn.backend.entity.enum_entity.UploadPurpose;
import hcmute.kltn.backend.repository.*;
import hcmute.kltn.backend.service.ImageUploadService;
import hcmute.kltn.backend.service.UserService;
import hcmute.kltn.backend.service.VoteStarService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final ImageUploadService imageUploadService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final VoteStarRepo voteStarRepo;
    private final FollowCategoryRepo followCategoryRepo;
    private final VoteStarService voteStarService;
    private final SavedArticleRepo savedArticleRepo;
    private final CommentRepo commentRepo;
    private final ReactEmotionRepo reactEmotionRepo;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> userRepo.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tồn tại người dùng với email: " + username));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public List<UserDTO> getAllUsers() {
        List<User> allUsers = userRepo.findAll();
        return allUsers.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserById(String id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tồn tại người dùng với id: " + id));
        return modelMapper.map(user, UserDTO.class);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public String deleteUser(String id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tồn tại người dùng với id: " + id));

        List<VoteStar> voteStarList = voteStarRepo.findByUser(user);
        if (voteStarList != null) {
            List<Article> articleList = new ArrayList<>();
            for (VoteStar voteStar : voteStarList) {
                articleList.add(voteStar.getArticle());
            }
            voteStarRepo.deleteAll(voteStarList);
            for (Article article : articleList) {
                voteStarService.addAverageStar(article.getId());
            }
        }
        List<FollowCategory> followCategoryList = followCategoryRepo.findByUser(user);
        if (followCategoryList != null) {
            followCategoryRepo.deleteAll(followCategoryList);
        }
        List<SavedArticle> savedArticleList = savedArticleRepo.findByUserId(user.getId());
        if (savedArticleList != null) {
            savedArticleRepo.deleteAll(savedArticleList);
        }
        List<Comment> commentList = commentRepo.findByUser(user);
        if (commentList != null) {
            commentRepo.deleteAll(commentList);
        }
        List<ReactEmotion> reactEmotionList = reactEmotionRepo.findByUser(user);
        if (reactEmotionList != null) {
            reactEmotionRepo.deleteAll(reactEmotionList);
        }

        userRepo.deleteById(user.getId());
        return "Đã xóa người dùng với email: " + user.getEmail() + ".";
    }

    @Override
    public UserDTO updateUserInfor(String id, UserDTO userDTO) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tồn tại người dùng với id: " + id));
        if (!userDTO.getFirstname().isEmpty()) {
            user.setFirstname(userDTO.getFirstname());
        }
        if (!userDTO.getLastname().isEmpty()) {
            user.setLastname(userDTO.getLastname());
        }
        if (userDTO.getDob() != null) {
            user.setDob(userDTO.getDob());
        }

        userRepo.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO updateUserAvatar(MultipartFile file) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("Không tồn tại người dùng."));

        try {
            String imageUrl = imageUploadService.saveImage(file, UploadPurpose.USER_AVATAR);
            user.setAvatar(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException("Xảy ra lỗi trong quá trình upload ảnh.");
        }
        userRepo.save(user);
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO getMyInfor() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepo.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("Không tồn tại user."));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public String updatePassword(UpdatePassRequest updatePassRequest) {

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepo.findByEmail(name)
                .orElseThrow(() -> new RuntimeException("Không tồn tại user."));
        boolean oldPassMatchOldPass = passwordEncoder.matches(updatePassRequest.getOldPassword(), user.getPassword());
        boolean newPassMatchOldPass = passwordEncoder.matches(updatePassRequest.getNewPassword(), user.getPassword());
        String newPassHash = passwordEncoder.encode(updatePassRequest.getNewPassword());

        if (!oldPassMatchOldPass) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng.");
        } else if (!updatePassRequest.getNewPassword().equals(updatePassRequest.getReEnterPassword())) {
            throw new RuntimeException("Mật khẩu nhập lại không khớp.");
        } else if (newPassMatchOldPass) {
            throw new RuntimeException("Mật khẩu mới không được giống với mật khẩu cũ.");
        } else {
            user.setPassword(newPassHash);
            userRepo.save(user);
            return "Cập nhật mật khẩu thành công.";
        }
    }

}

