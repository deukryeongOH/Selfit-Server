package selfit.selfit.domain.clothes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import selfit.selfit.domain.clothes.entity.Clothes;

import java.util.List;
import java.util.Optional;

public interface ClothesRepository extends JpaRepository<Clothes,Long> {
    Optional<Clothes> findByPath(String path);
    List<Clothes> findByUserId(Long userId);
}
