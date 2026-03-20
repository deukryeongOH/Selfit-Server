package selfit.selfit.domain.body.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import selfit.selfit.domain.body.entity.Body;
import selfit.selfit.domain.user.entity.User;

import java.util.Optional;

public interface BodyRepository extends JpaRepository<Body, Long> {
    Optional<Body> findByUser(User user);

    // 3D 아바타 생성 등 사진이 즉시 필요할 때 사용하는 전용 메서드
    @Query("SELECT b FROM Body b JOIN FETCH b.fullBodyPhotos WHERE b.user = :user")
    Optional<Body> findByUserWithBodyPhotos(@Param("user") User user);

    @Query("SELECT b FROM Body b JOIN FETCH b.facePhotos WHERE b.user = :user")
    Optional<Body> findByUserWithFacePhotos(@Param("user") User user);

}
