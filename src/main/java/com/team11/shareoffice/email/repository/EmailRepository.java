package com.team11.shareoffice.email.repository;

import com.team11.shareoffice.email.entity.Email;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<Email, String> {
}
