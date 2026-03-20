package selfit.selfit.domain.body.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import selfit.selfit.domain.body.dto.BodySizeDto;
import selfit.selfit.domain.body.entity.Body;
import selfit.selfit.domain.body.repository.BodyRepository;
import selfit.selfit.domain.body.service.BodyService;
import selfit.selfit.domain.image.ImageFileStorageService;
import selfit.selfit.domain.user.entity.User;
import selfit.selfit.domain.user.repository.UserRepository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BodyServiceImpl implements BodyService {

    private final BodyRepository bodyRepository;
    private final UserRepository userRepository;
    private final ImageFileStorageService imageFileStorageService;

    public User getUserByUserId(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
    }

    /**
     *  신체 정보 저장
     */
    @Override
    public void saveSize(Long userId, BodySizeDto bodySizeDto) {
        User user = getUserByUserId(userId);

        Optional<Body> bodyOpt = bodyRepository.findByUser(user);

        if (bodyOpt.isPresent()) {
            Body body = bodyOpt.get();
            body.setWeight(bodySizeDto.getWeight());
            body.setHeight(bodySizeDto.getHeight());
            body.setLeg(bodySizeDto.getLeg());
            body.setWaist(bodySizeDto.getWaist());
            body.setPelvis(bodySizeDto.getPelvis());
            body.setShoulder(bodySizeDto.getShoulder());
            body.setChest(bodySizeDto.getChest());

            bodyRepository.save(body);
        }

        Body newBody = Body.builder()
                .user(user)
                .weight(bodySizeDto.getWeight())
                .leg(bodySizeDto.getLeg())
                .chest(bodySizeDto.getChest())
                .waist(bodySizeDto.getWaist())
                .pelvis(bodySizeDto.getPelvis())
                .shoulder(bodySizeDto.getShoulder())
                .height(bodySizeDto.getHeight())
                .build();

        user.setBody(newBody);
        userRepository.save(user);
        bodyRepository.save(newBody);
    }

    public void setSize(Body body, BodySizeDto dto){
        body.setHeight(dto.getHeight());
        body.setWeight(dto.getWeight());
        body.setWaist(dto.getWaist());
        body.setLeg(dto.getLeg());
        body.setShoulder(dto.getShoulder());
        body.setPelvis(dto.getPelvis());
        body.setChest(dto.getChest());
        body.setUpdate_date(new Date());
    }


    @Override
    public List<String> uploadFullBody(User user, List<MultipartFile> files) {
        if (files == null || files.size() != 4) {
            throw new IllegalArgumentException("전신 사진은 정확히 4장 업로드해야 합니다.");
        }
        User authUser = getUserByUserId(user.getId());

        Body body = bodyRepository.findByUser(authUser)
                .orElseGet(() -> Body.builder().user(user).build());

        List<String> storedPaths = files.stream()
                .map(imageFileStorageService::store)
                .collect(Collectors.toList());

        setFullBodyPhotos(body, storedPaths);
        bodyRepository.save(body);
        return storedPaths;
    }

    public void setFullBodyPhotos(Body body, List<String> paths){
        body.getFullBodyPhotos().clear();
        body.getFullBodyPhotos().addAll(paths);
        body.setUpdate_date(new Date());
    }

    /**
     * 얼굴 사진 최대 9장 업로드 로직
     */
    @Override
    public List<String> uploadFace(User user, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("최소 1장의 얼굴 사진을 업로드해야 합니다.");
        }
        if (files.size() > 9) {
            throw new IllegalArgumentException("얼굴 사진은 최대 9장까지 업로드 가능합니다.");
        }
        Body body = bodyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("신체 정보가 존재하지 않습니다."));

        List<String> storedPaths = files.stream()
                .map(imageFileStorageService::store)
                .collect(Collectors.toList());

        setFacePhotos(body, storedPaths);
        bodyRepository.save(body);
        return storedPaths;
    }

    public void setFacePhotos(Body body, List<String> paths) {
        body.getFacePhotos().clear();
        body.getFacePhotos().addAll(paths);
        body.setUpdate_date(new Date());
    }

    @Override
    public BodySizeDto saveSizePhoto(Long userId, String gender) {
        User user = getUserByUserId(userId);
        Body body = bodyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("신체 정보가 없습니다."));

        List<String> photos = body.getFullBodyPhotos();
        if (photos == null || photos.isEmpty()) {
            throw new IllegalArgumentException("전신 사진이 존재하지 않습니다.");
        }
        String imageUrl = photos.get(0);

        RestTemplate restTemplate = new RestTemplate();
        String pythonApiUrl = "http://localhost:8000/body?gender=" + gender + "&image_url=" + imageUrl;

        ResponseEntity<Map> response = restTemplate.getForEntity(pythonApiUrl, Map.class);
        Map measurements = response.getBody();

        String hipCircum;
        String chestCircum;
        hipCircum = measurements.get("waist_circum_cm").toString();
        chestCircum = measurements.get("chest_circum_cm").toString();
//        if (gender == "male"){
//            hipCircum = measurements.get("male_waist_circum").toString();
//            chestCircum = measurements.get("male_chest_circum").toString();
//        } else {
//            hipCircum = measurements.get("female_waist_circum").toString();
//            chestCircum = measurements.get("female_chest_circum").toString();
//        }

        BodySizeDto dto = new BodySizeDto();
        dto.setHeight(String.valueOf(body.getHeight()));
        dto.setWeight(String.valueOf(body.getWeight()));
        assert measurements != null;
        dto.setWaist(hipCircum);
        dto.setLeg(String.valueOf(measurements.get("left_leg_cm")));
        dto.setShoulder(String.valueOf(measurements.get("shoulder_width_cm")));
        dto.setPelvis(String.valueOf(measurements.get("hip_width_cm")));
        dto.setChest(chestCircum);

        body.setWaist(hipCircum);
        body.setLeg(String.valueOf(measurements.get("left_leg_cm")));
        body.setShoulder(String.valueOf(measurements.get("shoulder_width_cm")));
        body.setPelvis(String.valueOf(measurements.get("hip_width_cm")));
        body.setChest(chestCircum);
        body.setUpdate_date(new Date());
        bodyRepository.save(body);

        return dto;
    }


    @Override
    public String body3D(Long userId) {
        User user = getUserByUserId(userId);

        Body body = bodyRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("신체 정보가 없습니다."));

        List<String> photos = body.getFullBodyPhotos();
        if (photos == null || photos.isEmpty()) {
            throw new IllegalArgumentException("전신 사진이 존재하지 않습니다.");
        }
        String imageUrl = photos.get(0);

        RestTemplate restTemplate = new RestTemplate();
        String pythonApiUrl = "http://localhost:8000/body/3D?body_image_url=" + imageUrl;

        ResponseEntity<Map> response = restTemplate.getForEntity(pythonApiUrl, Map.class);
        Map measurements = response.getBody();

        String modelUrl = (String) measurements.get("model_url");

        return modelUrl;
    }
}
