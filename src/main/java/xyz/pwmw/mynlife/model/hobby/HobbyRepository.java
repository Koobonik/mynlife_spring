package xyz.pwmw.mynlife.model.hobby;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    List<Hobby> findAll();
}
