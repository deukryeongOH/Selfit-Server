package selfit.selfit.domain.clothes.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ClothesDto {
    private String path;
    private ClothesType type;

    @Builder
    public ClothesDto(String path, ClothesType type) {
        this.path = path;
        this.type = type;
    }
}
