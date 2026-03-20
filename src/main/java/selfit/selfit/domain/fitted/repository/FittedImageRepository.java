package selfit.selfit.domain.fitted.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selfit.selfit.domain.fitted.entity.FittedImage;
import selfit.selfit.domain.user.entity.User;

import java.util.List;

public interface FittedImageRepository extends JpaRepository<FittedImage, Integer> {
    List<FittedImage> findAllByUserOrderByUpdateDateDesc(User user);
}
