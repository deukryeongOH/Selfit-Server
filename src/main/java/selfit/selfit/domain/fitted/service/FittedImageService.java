package selfit.selfit.domain.fitted.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FittedImageService {
    String fitting3D (Long userId, String clothPath);
    List<String> fittingList (Long userId);
}
