package selfit.selfit.domain.clothes.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import selfit.selfit.domain.clothes.dto.ClothesDto;
import selfit.selfit.domain.clothes.dto.ClothesType;
import selfit.selfit.domain.clothes.entity.Clothes;
import selfit.selfit.domain.clothes.repository.ClothesRepository;
import selfit.selfit.domain.clothes.service.ClothesService;
import selfit.selfit.domain.image.ImageFileStorageService;
import selfit.selfit.domain.user.entity.User;
import selfit.selfit.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {

    @Autowired private final ClothesRepository clothesRepository;
    @Autowired private final ImageFileStorageService imageFileStorageService;
    @Autowired private final UserRepository userRepository;

    /**
     *  담은 옷 저장
     */
    @Override
    public String saveClothes(Long userId, ClothesType type, MultipartFile file){
        if(file == null){
            throw new IllegalArgumentException("한 장의 옷 사진을 업로드해야 합니다.");
        }

        String filename = imageFileStorageService.store(file);

        User user = userRepository.findById(userId).orElseThrow();

        Clothes clothes = Clothes.builder()
                .user(user)
                .type(type)
                .path(filename)
                .build();

        clothesRepository.save(clothes);

        return filename;
    }

    /**
     *  담은 옷 삭제
     * */
    @Override
    public void deleteClothes(String imageURL) {
        deleteClothesPath(imageURL);
    }

    private void deleteClothesPath(String path) {
        Clothes clothes = clothesRepository.findByPath(path)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이미지 경로입니다: " + path));
        clothesRepository.delete(clothes);
        imageFileStorageService.deleteS3File(path);
    }

    /**
     * 담은 옷 제공
     */
    @Override
    public List<ClothesDto> provideClothes(Long userId) {
        List<Clothes> clothesList = clothesRepository.findByUserId(userId);

        if (clothesList.isEmpty()){
            throw new RuntimeException("유저를 찾을 수 없습니다.");
        }

        List<ClothesDto> dtoList = clothesList.stream()
                .map(clothes -> ClothesDto.builder()
                        .path(clothes.getPath())
                        .type(clothes.getType())
                        .build())
                .collect(Collectors.toList());

        return dtoList;
    }
}
