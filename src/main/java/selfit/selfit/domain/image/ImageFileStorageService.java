package selfit.selfit.domain.image;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageFileStorageService {

    // 로컬 디렉토리 경로
    // properties에 입력
    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new IllegalStateException("Could not initialize storage directory", e);
        }
    }

    /**
     * MultipartFile을 받아 서버에 저장하고, 생성된 파일명을 반환합니다.
     */
    public String store(MultipartFile file) {
        String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        // 확장자 추출
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx > 0) {
            ext = original.substring(idx);
        }

        // UUID 기반 유니크 파일명 생성
        String filename = UUID.randomUUID() + ext;

        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Failed to store empty file");
            }
            if (original.contains("..")) {
                // 보안: 경로 조작 공격 방지
                throw new IllegalArgumentException("Cannot store file with relative path outside current directory " + original);
            }
            Path target = rootLocation.resolve(filename);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }

    /**
     * 저장된 파일명을 받아 절대 경로 또는 URL 형태로 반환합니다.
     */
    public String getFilePath(String filename) {
        // 만약 외부 URL을 쓰고 싶다면 이곳에서 변형할 수 있습니다.
        return rootLocation.resolve(filename).toString();
    }

    public void delete(String path) {
        try{
            Path file = Paths.get(path);
            Files.deleteIfExists(file);
        }catch (IOException e){
            throw new UncheckedIOException("Failed to delete file: " + path, e);
        }
    }
}
