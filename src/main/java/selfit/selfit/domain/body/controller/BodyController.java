package selfit.selfit.domain.body.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import selfit.selfit.domain.body.dto.BodySizeDto;
import selfit.selfit.domain.body.service.BodyService;
import selfit.selfit.domain.user.entity.User;
import selfit.selfit.global.dto.ApiResult;
import selfit.selfit.global.security.springsecurity.CustomUserDetails;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/body")
@RequiredArgsConstructor
public class BodyController {

    private final BodyService bodyService;

    @Operation(summary = "전신 사진 저장", description = "앞, 옆(왼쪽, 오른쪽), 뒷모습 사진들을 업로드해 저장합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 저장"),
                    @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @PostMapping("/shape")
    public ApiResult<List<String>> uploadFullBody(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                  @RequestParam("files") List<MultipartFile> files) throws IOException {
        User user = customUserDetails.getUser();

        List<String> paths = bodyService.uploadFullBody(user, files);
        return ApiResult.ok("전신 사진 업로드 성공", paths);
    }

    @Operation(summary = "얼굴 사진 저장", description = "얼굴 사진을 업로드해 저장합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 저장"),
                    @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @PostMapping("/face")
    public ApiResult<List<String>> uploadFace(@RequestParam("file") List<MultipartFile> files,
                                              @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {
        User user = customUserDetails.getUser();
        List<String> paths = bodyService.uploadFace(user, files);
        return ApiResult.ok("얼굴 사진 업로드 성공", paths);
    }

    @Operation(summary = "유저 신체 정보 등록", description = "신체 사이즈를 업데이트 합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 변경"),
                    @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @PostMapping("/size")
    public ApiResult<BodySizeDto> saveSize(@RequestBody BodySizeDto bodySizeDto,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId = customUserDetails.getId();
        bodyService.saveSize(userId, bodySizeDto);
        return ApiResult.ok("신체 정보 등록 완료", bodySizeDto);
    }

    @Operation(summary = "유저 신체 정보 사진기반 분석", description = "신체 사이즈를 업데이트 합니다. male, female",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 분석"),
                    @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @PostMapping("/size/photo")
    public ApiResult<BodySizeDto> saveSize(@RequestParam String gender,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId = customUserDetails.getId();
        BodySizeDto bodySizeDto = bodyService.saveSizePhoto(userId, gender);

        return ApiResult.ok("신체 정보 분석 완료", bodySizeDto);
    }

    @Operation(summary = "유저 신체 3D get", description = "유저 신체 3d를 얻습니다. url 형태 반환, 사진 등록 선 필수",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 반환"),
                    @ApiResponse(responseCode = "400", description = "잘못된 입력 값"),
                    @ApiResponse(responseCode = "500", description = "서버 오류 발생")
            })
    @PostMapping("/3d")
    public ApiResult<String> saveSize(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        Long userId = customUserDetails.getId();
        String result = bodyService.body3D(userId);

        return ApiResult.ok("신체 3D 제공", result);
    }
}
