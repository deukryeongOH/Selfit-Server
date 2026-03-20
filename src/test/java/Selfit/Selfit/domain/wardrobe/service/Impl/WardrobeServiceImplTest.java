//package Selfit.Selfit.domain.wardrobe.service.Impl;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.io.TempDir;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.Resource;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import selfit.selfit.domain.body.entity.Body;
//import selfit.selfit.domain.clothes.repository.ClothesRepository;
//import selfit.selfit.domain.image.ImageFileStorageService;
//import selfit.selfit.domain.user.entity.User;
//import selfit.selfit.domain.user.repository.UserRepository;
//import selfit.selfit.domain.wardrobe.entity.Wardrobe;
//import selfit.selfit.domain.wardrobe.repository.WardrobeRepository;
//import selfit.selfit.domain.wardrobe.service.WardrobeService;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest(properties = "file.upload-dir=${java.io.tmpdir}/wardrobe-test")
//@Transactional
//public class WardrobeServiceImplTest {
//
//    @Autowired private WardrobeService wardrobeService;
//    @Autowired private UserRepository userRepository;
//    @Autowired private WardrobeRepository wardrobeRepository;
//    @Autowired private ImageFileStorageService imageFileStorageService;
//
//    private User user;
//
//    @TempDir
//    Path tempDir;
//
//    @BeforeEach
//    void setUp() throws IOException {
//        // configure upload directory
//        Path uploadDir = tempDir.resolve("wardrobe-test");
//        Files.createDirectories(uploadDir);
//        System.setProperty("file.upload-dir", uploadDir.toString());
//
//        // create test user and wardrobe
//        user = User.builder()
//                .accountId("testuser")
//                .password("password")
//                .email("test@example.com")
//                .build();
//        Wardrobe w = Wardrobe.builder().user(user).build();
//        user.setWardrobe(w);
//        userRepository.save(user);
//        wardrobeRepository.save(w);
//    }
//
//    @Test
//    @DisplayName("소장 의류 등록")
//    void saveClothes() throws IOException {
//        MultipartFile f1 = new MockMultipartFile("files", "one.png", "image/png", "data1".getBytes());
//        MultipartFile f2 = new MockMultipartFile("files", "two.jpg", "image/jpeg", "data2".getBytes());
//        List<MultipartFile> files = Arrays.asList(f1, f2);
//
//        List<String> paths = wardrobeService.saveClothes(user.getId(), files);
//
//        assertThat(paths).hasSize(2);
//
//        for (String p : paths) {
//            assertThat(Files.exists(Path.of(p))).isTrue();
//        }
//
//        Wardrobe updated = wardrobeRepository.findByUserId(user.getId()).orElseThrow();
//        assertThat(updated.getClothesPhotos()).containsExactlyElementsOf(paths);
//    }
//
//    @Test
//    @DisplayName("소장 의류 삭제")
//    void deleteClothes() throws IOException {
//        MultipartFile f1 = new MockMultipartFile("files", "a.png", "image/png", "A".getBytes());
//        MultipartFile f2 = new MockMultipartFile("files", "b.png", "image/png", "B".getBytes());
//        MultipartFile f3 = new MockMultipartFile("files", "c.png", "image/png", "C".getBytes());
//        List<String> initial = wardrobeService.saveClothes(user.getId(), Arrays.asList(f1, f2, f3));
//
//
//        List<String> remaining = wardrobeService.deleteClothes(user.getId(), 1);
//        assertThat(remaining).hasSize(2);
//        assertThat(remaining).contains(initial.get(0), initial.get(2));
//
//        assertThat(Files.exists(Path.of(initial.get(1)))).isFalse();
//    }
//
//    @Test
//    @DisplayName("소장 의류 제공")
//    void provideClothesResource() throws IOException {
//        byte[] data = "hello".getBytes();
//        MultipartFile file = new MockMultipartFile("files", "hello.txt", "text/plain", data);
//        wardrobeService.saveClothes(user.getId(), List.of(file));
//
//        Resource res = wardrobeService.provideClothesResource(user.getId(), 0);
//        assertThat(res.exists()).isTrue();
//        try (InputStream is = res.getInputStream()) {
//            byte[] read = is.readAllBytes();
//            assertThat(read).isEqualTo(data);
//        }
//    }
//
//    @Test
//    @DisplayName("소장 의류 삭제 시 인덱스 유효성 검사")
//    void deleteClothes_invalidIndex() {
//        assertThrows(IllegalArgumentException.class, () ->
//                wardrobeService.deleteClothes(user.getId(), 5)
//        );
//    }
//
//    @Test
//    @DisplayName("소장 의류 제공 시 인덱스 유효성 검사")
//    void provideClothesResource_invalidIndex() {
//        assertThrows(IllegalArgumentException.class, () ->
//                wardrobeService.provideClothesResource(user.getId(), 1)
//        );
//    }
//}