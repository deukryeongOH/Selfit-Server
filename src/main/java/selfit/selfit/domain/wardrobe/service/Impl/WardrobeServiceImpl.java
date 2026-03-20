package selfit.selfit.domain.wardrobe.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import selfit.selfit.domain.clothes.dto.ClothesType;
import selfit.selfit.domain.clothes.repository.ClothesRepository;
import selfit.selfit.domain.image.ImageFileStorageService;
import selfit.selfit.domain.user.entity.User;
import selfit.selfit.domain.user.repository.UserRepository;
import selfit.selfit.domain.wardrobe.dto.WardrobeDto;
import selfit.selfit.domain.wardrobe.entity.Wardrobe;
import selfit.selfit.domain.wardrobe.repository.WardrobeRepository;
import selfit.selfit.domain.wardrobe.service.WardrobeService;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WardrobeServiceImpl implements WardrobeService {

    private final WardrobeRepository wardrobeRepository;
    private final UserRepository userRepository;
    private final ImageFileStorageService imageFileStorageService;

    /**
     * 소장 의류 등록
     */
    @Override
    public String saveClothes(Long userId, ClothesType type, MultipartFile file) {
        if (file == null){
            throw new IllegalArgumentException("한 장의 옷 사진을 업로드해야 합니다.");
        }

        String filename = imageFileStorageService.store(file);

        User user = userRepository.findById(userId).orElseThrow();

        Wardrobe wardrobe = Wardrobe.builder()
                .type(type)
                .path(filename)
                .user(user)
                .build();

        wardrobeRepository.save(wardrobe);

        return filename;
    }

    /**
     * 소장 의류 삭제
     */
    @Override
    public void deleteClothes(String imageURL) {
        Wardrobe wardrobe = wardrobeRepository.findByPath(imageURL)
                .orElseThrow(() -> new IllegalArgumentException("의류가 존재하지 않습니다."));

        imageFileStorageService.delete(imageURL);
        wardrobeRepository.delete(wardrobe);

    }

    /**
     * 소장 의류 경로 제공
     */
    @Override
    public List<WardrobeDto> getClothes(Long userId) {
        Optional<User> user = userRepository.findById(userId);

        if (user.isEmpty()){
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        List<Wardrobe> wardrobeList = wardrobeRepository.findByUser(user.get());

        if (wardrobeList.isEmpty()){
            throw new RuntimeException("등록된 의류가 없습니다.");
        }

        List<WardrobeDto> dtoList = wardrobeList.stream()
                .map(wardrobe -> WardrobeDto.builder()
                        .path(wardrobe.getPath())
                        .type(wardrobe.getType())
                        .build())
                .collect(Collectors.toList());

        return dtoList;
    }


}
