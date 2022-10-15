package xyz.pwmw.mynlife.model.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmailAuthCodeRepository extends JpaRepository<EmailAuthCode, Long> {
    @Query(
            nativeQuery = true,
            value = "select * from EmailAuthCode where isCanUse = true AND authNumber = :auth_number"
    )
    EmailAuthCode findByAuthNumber(String auth_number);

    EmailAuthCode findBySecret(String secret);

    @Query(
            nativeQuery = true,
            value = "select * from EmailAuthCode where isCanUse = true AND secret = :secret order by id DESC"
    )
    List<EmailAuthCode> findAllBySecretOrderByIdDesc(String secret);

    @Query(
            nativeQuery = true,
            value = "select * from EmailAuthCode where isCanUse = true order by id DESC"
    )
    List<EmailAuthCode> findAllByCanUse();
}