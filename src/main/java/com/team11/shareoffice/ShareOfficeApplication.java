package com.team11.shareoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ShareOfficeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShareOfficeApplication.class, args);
    }

}
