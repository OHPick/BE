package com.team11.shareoffice.reservation.controller;


import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.reservation.dto.ReservationRequestDto;
import com.team11.shareoffice.reservation.dto.ReservationResponseDto;
import com.team11.shareoffice.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class ReservationController {

    private final ReservationService reservationService;
    @Operation(summary = "예약된 게시물 정보 API")
    @GetMapping("/{postId}/reserve")
    public ResponseDto<?> showReservedPost(@PathVariable Long postId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.setSuccess(reservationService.showReservedPost(postId, userDetails.getMember()));
    }

    @Operation(summary = "예약하기 API")
    @PostMapping("/{postId}/reserve")
    public ResponseDto<?> reservePost(@PathVariable Long postId,
                                      @RequestBody ReservationRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reservationService.reservePost(postId, requestDto,
                userDetails.getMember());
        return ResponseDto.setSuccess(null);
    }

    @Operation(summary = "예약취소 API")
    @DeleteMapping("/{postId}/reserve")
    public ResponseDto<?> cancelReservePost(@PathVariable Long postId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reservationService.cancelReservePost(postId, userDetails.getMember());
        return ResponseDto.setSuccess(null);
    }

    @Operation(summary = "예약하고 싶은 게시물 예약날짜 보기 API")
    @GetMapping("/{postId}/reserved")
    public ResponseDto<?> showReservedDate(@PathVariable Long postId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseDto.setSuccess(reservationService.showReservedDate(postId, userDetails.getMember()));
    }

}
