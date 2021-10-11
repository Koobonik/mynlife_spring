package xyz.pwmw.mynlife.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Users findByUserId(long id);

    Users findByUserEmail(String email);

    Users findByUserNickname(String name);
    Users findByUserEmailAndUserNickname(String email, String name);

}
