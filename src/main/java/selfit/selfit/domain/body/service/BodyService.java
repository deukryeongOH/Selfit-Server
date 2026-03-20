package selfit.selfit.domain.body.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import selfit.selfit.domain.body.dto.BodySizeDto;
import selfit.selfit.domain.body.entity.Body;
import selfit.selfit.domain.user.entity.User;

import java.util.List;

@Service
public interface BodyService {
    void saveSize(Long userId, BodySizeDto bodySizeDto);
    List<String> uploadFullBody(User user, List<MultipartFile> files);
    BodySizeDto saveSizePhoto(Long userId, String gender);
    List<String> uploadFace(User user, List<MultipartFile> files);
    String body3D(Long userId);
}
