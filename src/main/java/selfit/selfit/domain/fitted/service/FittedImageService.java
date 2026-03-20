package selfit.selfit.domain.fitted.service;

import org.springframework.stereotype.Service;
import selfit.selfit.domain.fitted.dto.FittedImageDto;

import java.util.List;

import java.util.List;

@Service
public interface FittedImageService {
    String fitting3D (Long userId, String clothPath);
    List<String> fittingList (Long userId);
}
