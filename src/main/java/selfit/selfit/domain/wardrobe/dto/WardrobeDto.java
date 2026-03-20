package selfit.selfit.domain.wardrobe.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import selfit.selfit.domain.clothes.dto.ClothesType;
import selfit.selfit.domain.clothes.entity.Clothes;

@Getter
@Setter
@NoArgsConstructor
public class WardrobeDto {
    private String path;
    private ClothesType type;

    @Builder
    public WardrobeDto(String path, ClothesType type) {
        this.path = path;
        this.type = type;
    }
}
