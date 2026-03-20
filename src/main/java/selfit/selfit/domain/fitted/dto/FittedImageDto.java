package selfit.selfit.domain.fitted.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import selfit.selfit.domain.fitted.entity.FittedImage;

@Getter
@Setter
@NoArgsConstructor
public class FittedImageDto {

    private String fitted_3D_url;
    private String fitted_2D_url;

    @Builder
    public FittedImageDto(String fitted_2D_url, String fitted_3D_url) {
        this.fitted_2D_url = fitted_2D_url;
        this.fitted_3D_url = fitted_3D_url;
    }

    public static FittedImageDto from(FittedImage fittedImage) {
        return FittedImageDto.builder()
                .fitted_2D_url(fittedImage.getFitted_url())
                .fitted_3D_url(null)
                .build();
    }

}
