package selfit.selfit.domain.image;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class ImageFileStorageService {

    public void delete(String imageURL) {
        String tempDir = System.getProperty("java.io.tmpdir");

        try {
            Path filePath = Paths.get(tempDir, imageURL);

            boolean isDeleted = Files.deleteIfExists(filePath);

            if (isDeleted) {
                System.out.println("[Success] 파일이 삭제되었습니다: " + filePath);
            } else {
                System.out.println("[Info] 삭제할 파일이 존재하지 않습니다: " + filePath);
            }

        } catch (SecurityException e) {
            System.err.println("[Error] 파일 삭제 권한이 없습니다: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("[Error] 파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }

    }

    public String store(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            return "파일이 비어있습니다.";
        }

        String tempDir = System.getProperty("java.io.tmpdir");

        String originalFilename = multipartFile.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFileName = UUID.randomUUID().toString() + extension;

        Path savePath = Paths.get(tempDir, storedFileName);

        try {
            multipartFile.transferTo(savePath.toFile());

            System.out.println("[Success] 파일 저장 완료: " + savePath);

            return storedFileName;

        } catch (IOException e) {
            System.err.println("[Error] 파일 저장 중 오류 발생: " + e.getMessage());
            return "파일 저장 실패";
        }
    }
}
