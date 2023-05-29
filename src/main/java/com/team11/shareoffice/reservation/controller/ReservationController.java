package com.team11.shareoffice.reservation.controller;


import com.team11.shareoffice.global.dto.ResponseDto;
import com.team11.shareoffice.global.security.UserDetailsImpl;
import com.team11.shareoffice.reservation.dto.ReservationRequestDto;
import com.team11.shareoffice.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{postId}/reserve")
    public ResponseDto<?> reservePost(@PathVariable Long postId,
                                      @RequestBody ReservationRequestDto requestDto,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reservationService.reservePost(postId, requestDto,
                userDetails.getMember());
    }

    @DeleteMapping("/{postId}/reserve")
    public ResponseDto<?> cancelReservePost(@PathVariable Long postId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reservationService.cancelReservePost(postId, userDetails.getMember());
    }

}
