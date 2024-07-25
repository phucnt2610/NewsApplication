package hcmute.kltn.backend.service.service_implementation;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import hcmute.kltn.backend.entity.enum_entity.UploadPurpose;
import hcmute.kltn.backend.service.ImageUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Override
    public String saveImage(MultipartFile file, UploadPurpose uploadPurpose) throws IOException {
        Cloudinary cloudinary = new Cloudinary(
                "cloudinary://"
                        + apiKey + ":"
                        + apiSecret + "@"
                        + cloudName
        );
        Map<String, String> params = new HashMap<>();
        if (uploadPurpose.name().equals(UploadPurpose.USER_AVATAR.name())) {
            params.put("folder", "user_avatar");
        } else if (uploadPurpose.name().equals(UploadPurpose.ARTICLE_AVATAR.name())) {
            params.put("folder", "article_avatar");
        } else {
            throw new RuntimeException("Invalid upload purpose.");
        }
        File uploadedFile = File.createTempFile("temp", file.getOriginalFilename());
        file.transferTo(uploadedFile);
        Map uploadResult = cloudinary.uploader().upload(uploadedFile, params);

        String imageUrl = (String) uploadResult.get("secure_url");
        return resizeImage(imageUrl);
    }

    @Override
    public String saveImageViaUrl(String urlImageWeb) {
        Cloudinary cloudinary = new Cloudinary(
                "cloudinary://"
                        + apiKey + ":"
                        + apiSecret + "@"
                        + cloudName
        );
        try {
            Map uploadResult = cloudinary.uploader().upload(urlImageWeb, ObjectUtils.asMap(
                    "resource_type", "image",
                    "folder", "article_avatar"
            ));
            String imageUrl = (String) uploadResult.get("secure_url");
            return resizeImage(imageUrl);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public boolean sizeChecker(String imageUrl) throws IOException {

        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        long sizeInBytes = connection.getContentLengthLong();
        long tenMB = 10 * 1024 * 1024;
        return sizeInBytes < tenMB;

    }

    // crop size -> w: 680 x h: 450
    private String resizeImage(String imageUrl) {
        String newUrl;
        Pattern pattern = Pattern.compile("upload/(.*?)/article_avatar");
        Matcher matcher = pattern.matcher(imageUrl);
        if (matcher.find()) {
            String target = matcher.group(1);
            newUrl = imageUrl.replace(target, "w_1000,ar_16:9,c_fill,g_auto,e_sharpen");
        } else {
            newUrl = imageUrl;
        }
        return newUrl;
    }


}
