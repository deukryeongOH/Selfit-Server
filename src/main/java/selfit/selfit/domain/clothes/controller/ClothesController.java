package selfit.selfit.domain.clothes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import selfit.selfit.domain.clothes.dto.ClothesDto;
import selfit.selfit.domain.clothes.dto.ClothesType;
import selfit.selfit.domain.clothes.service.ClothesService;
import selfit.selfit.global.dto.ApiResult;
import selfit.selfit.global.security.springsecurity.CustomUserDetails;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/clothes")
@RequiredArgsConstructor
public class ClothesController {

    private final ClothesService clothesService;

    @Operation(summary = "담은 옷 등록", description = "담은 옷을 등록합니다.",
    responses = {
            @ApiResponse(responseCode = "200", description = "성공적으로 등록"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<String> registerClothes(@RequestParam("type") ClothesType type,
                                                 @RequestParam("file") MultipartFile file,
                                                 @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException{

        Long userId = customUserDetails.getId();
        String path = clothesService.saveClothes(userId, type, file);
        return ApiResult.ok("담은 옷 등록", path);
    }

    @Operation(summary = "담은 옷 삭제", description = "담은 옷 중 하나를 선택해 삭제합니다. URL로 삭제 1개씩",
    responses = {
            @ApiResponse(responseCode = "200", description = "성공적으로 삭제"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @DeleteMapping("/delete")
    public ApiResult<String> deleteClothes(@RequestParam String imageURL) throws IOException{
        clothesService.deleteClothes(imageURL);
        return ApiResult.ok("선택한 사진 삭제 성공");
    }

    @Operation(summary = "담은 옷 제공", description = "담은 옷 중 하나를 선택해 제공합니다.",
    responses = {
            @ApiResponse(responseCode = "200", description = "성공적으로 제공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    @GetMapping("/provide")
    public ResponseEntity<List<ClothesDto>> provideClothes(@AuthenticationPrincipal CustomUserDetails userDetails) throws Exception {
        Long userId = userDetails.getId();
        List<ClothesDto> dtoList = clothesService.provideClothes(userId);

        return ResponseEntity.ok()
                .body(dtoList);
    }
}
