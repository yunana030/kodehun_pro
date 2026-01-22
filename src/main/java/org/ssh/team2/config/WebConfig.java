package org.ssh.team2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${org.ssh.team2.upload.path}")
    private String uploadPath;

//    외부에 이미지 저장
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException ignored) {}

        String uploadLocation = "file:" + uploadDir.toString().replace("\\", "/") + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(0);

        Path avatarDir = Paths.get(uploadPath, "avatars").toAbsolutePath().normalize();
        try { Files.createDirectories(avatarDir); } catch (IOException ignored) {}
        String location = "file:" + avatarDir.toString().replace("\\", "/") + "/";

        registry.addResourceHandler("/avatars/**")
                .addResourceLocations(location)
                .setCachePeriod(0);
    }

}