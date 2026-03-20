package selfit.selfit.domain.fitted.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import selfit.selfit.domain.body.entity.Body;
import selfit.selfit.domain.body.repository.BodyRepository;
import selfit.selfit.domain.fitted.dto.FittedImageDto;
import selfit.selfit.domain.fitted.entity.FittedImage;
import selfit.selfit.domain.fitted.repository.FittedImageRepository;
import selfit.selfit.domain.fitted.service.FittedImageService;
import selfit.selfit.domain.image.ImageFileStorageService;
import selfit.selfit.domain.user.entity.User;
import selfit.selfit.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FittedImageServiceImpl implements FittedImageService {
    private final BodyRepository bodyRepository;
    private final UserRepository userRepository;
    private final FittedImageRepository fittedImageRepository;
    private final ImageFileStorageService imageFileStorageService;

    public User getUserByUserId(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    public String fitting3D (Long userId, String clothPath){
        User user = getUserByUserId(userId);

        Body body = bodyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("신체 정보가 없습니다."));

        List<String> photos = body.getFullBodyPhotos();
        if (photos == null || photos.isEmpty()) {
            throw new IllegalArgumentException("전신 사진이 존재하지 않습니다.");
        }
        String imageUrl = photos.get(0);

        RestTemplate restTemplate = new RestTemplate();
        String pythonApiUrl = "http://localhost:8000/fitting/3D?body_image_url=" + imageUrl + "&clothes_image_url=" + clothPath;

        ResponseEntity<Map> response = restTemplate.getForEntity(pythonApiUrl, Map.class);
        Map measurements = response.getBody();

        FittedImage fittedImage = FittedImage.builder()
                .fitted_url(measurements.get("model_url").toString())
                .user(user)
                .build();

        fittedImageRepository.save(fittedImage);

        return measurements.get("model_url").toString();
    }

    public List<String> fittingList(Long userId) {
        User user = getUserByUserId(userId);

        List<FittedImage> list = fittedImageRepository.findAllByUserOrderByUpdateDateDesc(user);

        List<String> fittedUrlList = list.stream()
                .limit(5)
                .map(FittedImage::getFitted_url)
                .toList();

        return fittedUrlList;
    }

}
