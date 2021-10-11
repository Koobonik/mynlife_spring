package xyz.pwmw.mynlife.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetPasswordAuthCodeRepository extends JpaRepository<ResetPasswordAuthCode, Long> {
    ResetPasswordAuthCode findByCode(String code);
    ResetPasswordAuthCode findByAuthUserId(long id);
}