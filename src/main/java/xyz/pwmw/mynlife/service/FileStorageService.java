package xyz.pwmw.mynlife.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import xyz.pwmw.mynlife.dto.responseDto.UploadFileResponse;
import xyz.pwmw.mynlife.model.FileInfo;
import xyz.pwmw.mynlife.model.FileInfoRepository;
import xyz.pwmw.mynlife.service.exception.FileStorageException;
import xyz.pwmw.mynlife.service.exception.MyFileNotFoundException;
import xyz.pwmw.mynlife.util.yml.FileStorageProperties;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Log4j2
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.",
                    ex);
        }
    }

    public void saveFileInfo(String name, String downloadUri, String type, Long size) {
        FileInfo fileInfo = new FileInfo();

        if (fileInfoRepository.existsByName(name)) {
            fileInfo = fileInfoRepository.findByName(name)
                    .orElseThrow(() -> new FileStorageException("FileInfo not found with name : " + name));
        }

        fileInfo.setName(name);
        fileInfo.setDownloadUri(downloadUri);
        fileInfo.setType(type);
        fileInfo.setSize(size);

        fileInfoRepository.save(fileInfo);
    }

    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = UUID.randomUUID() + StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }

    public UploadFileResponse saveFile(MultipartFile file){
        String fileName = storeFile(file);
        log.info(fileName);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/file/downloadFile/")
                .path(fileName).toUriString();
        saveFileInfo(fileName, fileDownloadUri, file.getContentType(), file.getSize());
        return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }
}