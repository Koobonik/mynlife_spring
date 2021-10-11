package xyz.pwmw.mynlife.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileInfoRepository extends JpaRepository<FileInfo, String> {
    Optional<FileInfo> findByName(String name);
    Boolean existsByName(String name);
}