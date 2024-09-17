package ru.dragomirov.cloudfilestorage.minio.download;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.InputStream;

@Controller
@RequiredArgsConstructor
public class DownloadFileController {
    private final DownloadFileService downloadFileService;

    @GetMapping("/download-file")
    public ResponseEntity<InputStreamResource> get(
            @RequestParam(name = "objectName") String objectName,
            Authentication authentication
    ) {
        String username = authentication.getName();
        String bucketNameHome = "user-" + username;

        InputStream fileStream = downloadFileService.getFileStream(bucketNameHome, objectName);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + objectName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(fileStream));
    }
}
