package selfit.selfit.domain.wardrobe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import selfit.selfit.domain.clothes.dto.ClothesType;
import selfit.selfit.domain.clothes.entity.Clothes;
import selfit.selfit.domain.user.entity.User;
import selfit.selfit.domain.wardrobe.dto.WardrobeDto;
import selfit.selfit.domain.wardrobe.entity.Wardrobe;
import selfit.selfit.domain.wardrobe.service.WardrobeService;
import selfit.selfit.global.dto.ApiResult;
import selfit.selfit.global.security.springsecurity.CustomUserDetails;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/wardrobe")
@RequiredArgsConstructor
public class WardrobeController {

    private final WardrobeService wardrobeService;

    @Operation(summary = "소장 의류 등록", description = "사용자가 소유한 옷의 이미지를 등록하고, 등록한 옷의 경로를 반환합니다. type = TOP, BOTTOM",
            responses = {
                    @ApiResponse(responseCode = "200", description = "등록 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 필요")
            }
    )
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<String> registerClothesFromWardrobe(@RequestParam("type") ClothesType type,
                                                         @RequestParam("file") MultipartFile file,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId = customUserDetails.getId();
        String path = wardrobeService.saveClothes(userId, type, file);
        return ApiResult.ok("담은 옷 등록", path);
    }

    @Operation(summary = "소장 의류 삭제", description = "주어진 의류를 path를 통해 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 필요")
            }
    )
    @DeleteMapping("/delete")
    public ApiResult<String> deleteClothesFromWardrobe(@RequestParam String imageURL){
        wardrobeService.deleteClothes(imageURL);

        return ApiResult.ok("소장 의류 삭제 완료");
    }

    @Operation(summary = "소장 의류 제공", description = "사용자가 가진 소장의류를 전부 제공합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "제공 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "401", description = "인증 필요")
            }
    )
    @GetMapping("/provide")
    public ResponseEntity<List<WardrobeDto>> provideClothesFromWardrobe(@AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        Long userId = userDetails.getId();
        List<WardrobeDto> dtoList = wardrobeService.getClothes(userId);

        return ResponseEntity.ok()
                .body(dtoList);
    }

}
