package com.ivangochev.raceratingapi.logo;


import com.ivangochev.raceratingapi.aws.S3UploadFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class LogoProcessor {
    private final RestTemplate restTemplate = new RestTemplate();
    private final S3UploadFileService s3UploadFileService;
    @Value("${aws.s3.root-folder}")
    private String s3RootFolder;

    public LogoProcessor(S3UploadFileService s3UploadFileService) {
        this.s3UploadFileService = s3UploadFileService;
    }

    @Async
    public CompletableFuture<Void> processAndUploadLogoAsync(String logoUrl, Long raceId) {
        try {
            // Step 1: Create headers to mimic a browser
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
            headers.setAccept(List.of(MediaType.ALL));
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // Step 2: Make HTTP request with custom headers
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    logoUrl,
                    HttpMethod.GET,
                    requestEntity,
                    byte[].class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("Failed to download image. Status: " + response.getStatusCode());
                throw new RuntimeException("Failed to download image. Status: " + response.getStatusCode());
            }

            // Step 3: Read image from response
            byte[] imageBytes = response.getBody();
            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            if (originalImage == null) {
                throw new RuntimeException("Downloaded data is not a valid image.");
            }

            // Step 4: Resize image
            BufferedImage resizedImage = resizeImage(originalImage, 200, 200);

            // Step 5: Convert resized image to PNG bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "png", baos);
            byte[] finalImageBytes = baos.toByteArray();

            // Step 6: Upload to S3
            String key = s3RootFolder + "/race-logos/" + raceId + "/logo.png";
            s3UploadFileService.uploadFile(key, finalImageBytes);

            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            // Optional: log error or store failure state
            log.error("Error processing race ID: " + raceId, e);
            return CompletableFuture.failedFuture(e);
        }
    }


    private BufferedImage resizeImage(BufferedImage originalImage, int maxWidth, int maxHeight) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        double widthRatio = (double) maxWidth / width;
        double heightRatio = (double) maxHeight / height;
        double scale = Math.min(widthRatio, heightRatio);

        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);

        Image scaledInstance = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(scaledInstance, 0, 0, null);
        g2d.dispose();

        return outputImage;
    }

}
