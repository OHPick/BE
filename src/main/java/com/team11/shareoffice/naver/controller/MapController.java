//package com.team11.shareoffice.naver.controller;
//
//import com.team11.shareoffice.naver.service.NaverMapService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/maps")
//@RequiredArgsConstructor
//public class MapController {
//    private final NaverMapService naverMapService;
//
//    @GetMapping("/geocode")
//    public String geoCode(@RequestParam String address) {
//        return naverMapService.geocode(address);
//    }
//
//    @GetMapping("/gc")
//    public String reverseGeocode(@RequestParam String coords) {
//        return naverMapService.reverseGeocode(coords);
//    }
//}
