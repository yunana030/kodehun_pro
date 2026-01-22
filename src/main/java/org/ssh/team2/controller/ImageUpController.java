package org.ssh.team2.controller;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.ssh.team2.repository.ImageRepository;
import org.ssh.team2.domain.tbl_image;
import org.ssh.team2.dto.upload.UploadFileDTO;
import org.ssh.team2.dto.upload.UploadResultDTO;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@Log4j2
@RequestMapping("/image")
public class ImageUpController {
    private final ImageRepository imageRepository;
    @Value("${org.ssh.team2.upload.path}")
    private String uploadPath;

    public ImageUpController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping("/imageUpload")
    public void uploadForm(){
        log.info("imageUpload");
    }
    @PostMapping("/uploadPro")
    public void upload(UploadFileDTO uploadFileDTO, Model model){
        log.info("upload"+uploadFileDTO);
        List<UploadResultDTO> list = new ArrayList<>();
        if(uploadFileDTO.getFiles() != null) {
            uploadFileDTO.getFiles().forEach(multiFile -> {
                String originalFileName = multiFile.getOriginalFilename();
                log.info("originalFileName"+originalFileName);
                String uuid = UUID.randomUUID().toString();
                Path savePath = Paths.get(uploadPath, uuid+"_"+originalFileName);
                boolean image = false;
                try {
                    multiFile.transferTo(savePath);
                    if(Files.probeContentType(savePath).startsWith("image")) {
                        image = true;
                        File thumbnail = new File(uploadPath, "s_"+uuid+"_"+originalFileName);
                        Thumbnailator.createThumbnail(savePath.toFile(), thumbnail, 200, 200);
                    }

                    // db 저장
                    tbl_image img = tbl_image.builder()
                            .uuid(uuid)
                            .filename(originalFileName)
                            .image(image)
                            .build();
                    imageRepository.save(img);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                list.add(UploadResultDTO.builder()
                                .uuid(uuid)
                                .fileName(originalFileName)
                                .image(image)
                        .build());
            });
            model.addAttribute("fileList", list);
        }
    }
}
