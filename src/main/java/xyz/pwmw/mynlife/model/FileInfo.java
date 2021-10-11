package xyz.pwmw.mynlife.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, columnDefinition = "TEXT")
    private String name;

    @Column(unique = true, columnDefinition = "TEXT")
    private String downloadUri;

    @Column(columnDefinition = "TEXT")
    private String type;

    private Long size;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public FileInfo(String name, String downloadUri, String type, Long size) {
        this.name = name;
        this.downloadUri = downloadUri;
        this.type = type;
        this.size = size;
    }

    public FileInfo build(String name, String downloadUri, String type, Long size) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(name);
        fileInfo.setDownloadUri(downloadUri);
        fileInfo.setType(type);
        fileInfo.setSize(size);
        return fileInfo;
    }
}