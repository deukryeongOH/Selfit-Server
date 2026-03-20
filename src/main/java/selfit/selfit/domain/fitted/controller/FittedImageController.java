package selfit.selfit.domain.fitted.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import selfit.selfit.domain.clothes.dto.ClothesDto;
import selfit.selfit.domain.fitted.dto.FittedImageDto;
import selfit.selfit.domain.fitted.service.FittedImageService;
import selfit.selfit.global.security.springsecurity.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("api/fitting")
@RequiredArgsConstructor
public class FittedImageController {

    private final FittedImageService fittedImageService;

    @Operation(summary = "가상 피팅", description = "3d, 2d url 제공",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 제공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @GetMapping("")
    public ResponseEntity<String> provideClothes(@RequestParam String clothPath,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        Long userId = userDetails.getId();
        String result = fittedImageService.fitting3D(userId, clothPath);

        return ResponseEntity.ok(result);
    }

    @Operation(summary = "가상 피팅 최근 기록 조회", description = "3d url 제공, 최대 5개",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 제공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @GetMapping("/list")
    public ResponseEntity<List<String>> provideClothesList(@AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        Long userId = userDetails.getId();
        List<String> result = fittedImageService.fittingList(userId);
        return ResponseEntity.ok(result);
    }
}
