package xyz.pwmw.mynlife.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.pwmw.mynlife.dto.responseDto.UploadFileResponse;
import xyz.pwmw.mynlife.service.FileStorageService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/file")
public class FileController {

    private final FileStorageService fileStorageService;

    @RequestMapping(value = "/uploadFile", method = {RequestMethod.GET, RequestMethod.POST})
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
//        String fileName = fileStorageService.storeFile(file);
//        log.info(fileName);
//        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/file/downloadFile/")
//                .path(fileName).toUriString();
//        fileStorageService.saveFileInfo(fileName, fileDownloadUri, file.getContentType(), file.getSize());
        return fileStorageService.saveFile(file);
    }

    @RequestMapping(value = "/uploadMultipleFiles", method = {RequestMethod.GET, RequestMethod.POST})
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        log.info("들어올 겨를도 없었구만");
        return Arrays.stream(files).map(file -> {
            try {
                log.info("여기 뜨나?");
                return uploadFile(file);
            } catch (IOException e) {
                log.info("에러?");
                e.printStackTrace();
            }
            log.info("null 반환?!");
            return null;
        }).collect(Collectors.toList());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws MalformedURLException {
        // // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}