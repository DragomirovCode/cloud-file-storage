package ru.dragomirov.cloudfilestorage.minio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Controller
@RequiredArgsConstructor
public class FileUploadController {

    private final MinioService minioService;
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    @PostMapping("/upload")
    public String uploadFiles(@ModelAttribute FileDto nFile,
                              @RequestParam(name = "bucketName", defaultValue = "home") String bucketName,
                              @RequestParam(name = "files") MultipartFile[] files) {

        for (MultipartFile file : files) {
            try {
                InputStream fileStream = file.getInputStream();

                String objectName = file.getOriginalFilename();

                String minioPath = bucketName + "/" + objectName;

                nFile.setName(objectName);

                nFile.setMinioPath(minioPath);

                File newFile = fileMapper.toEntity(nFile);

                fileRepository.save(newFile);

                minioService.uploadFile(bucketName, objectName, fileStream);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/";
    }
}

