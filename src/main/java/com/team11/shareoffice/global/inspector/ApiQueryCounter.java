package com.team11.shareoffice.global.inspector;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
//@RequestScope
@Getter
@RequiredArgsConstructor
public class ApiQueryCounter {

    private int count;
    private Long time;

    public ApiQueryCounter(int count, long time) {
        this.count = count;
        this.time = time;
    }

    public void increaseCount() {
        this.count = this.count+1;
    }
}