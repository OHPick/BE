package com.team11.shareoffice.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    @GetMapping("/test")
    public String signup() {
        return "this is a testing 2";
    }
}
