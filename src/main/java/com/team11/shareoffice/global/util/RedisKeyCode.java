package com.team11.shareoffice.global.util;

import lombok.Getter;


@Getter
public enum RedisKeyCode {
    BLACKLISTED("blacklisted"),
    EMAIL_VERIFIED("email_verified"),
    EMAIL_VERIF_CODE("email_verif_code");

    String keyName;

    RedisKeyCode(String keyName) {
        this.keyName = keyName;
    }
}
