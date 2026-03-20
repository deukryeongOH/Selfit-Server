package selfit.selfit.domain.image;

<<<<<<< HEAD
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
=======
>>>>>>> main
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageFileStorageService {

    private final S3Client s3Client;
    private final String bucketName;
    private final String region;

    public ImageFileStorageService(
            S3Client s3Client,
            @Value("${aws.s3.bucket}") String bucketName,
            @Value("${aws.region}") String region
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.region = region;
    }

    public String store(MultipartFile file) {
        String original = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx > 0) ext = original.substring(idx);
        String filename = UUID.randomUUID() + ext;

        try {
            if (file.isEmpty()) throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
            if (original.contains("..")) throw new IllegalArgumentException("상대 경로를 포함한 파일명은 허용되지 않습니다.");

            // 업로드 요청 생성
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            String url = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, filename);
            System.out.println("Uploaded URL: " + url);
            return url;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + filename, e);
        }
    }

    public void deleteS3File(String url) {
        try {
            URL parsedUrl = new URL(url);
            String key = parsedUrl.getPath().substring(1); // /file.jpg → file.jpg
            System.out.println("삭제 시도 URL: " + url);
            System.out.println("삭제 대상 key: " + key);

            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
            System.out.println("S3 파일 삭제 성공: " + key);
        } catch (Exception e) {
            System.err.println("S3 파일 삭제 실패: " + url);
            e.printStackTrace(); // 추가!
            throw new RuntimeException("S3 파일 삭제 실패: " + url, e);
        }
    }
}
