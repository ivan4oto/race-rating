package com.ivangochev.raceratingapi.avatar;

import com.ivangochev.raceratingapi.config.AwsProperties;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.utils.AvatarGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class AvatarService {
    private final S3Client s3Client;
    private final AwsProperties awsProperties;
    @Value("${aws.s3.root-folder}")
    private String s3RootFolder;


    public AvatarService(S3Client s3Client, AwsProperties awsProperties) {
        this.s3Client = s3Client;
        this.awsProperties = awsProperties;
    }

    public String getAvatarUrl(Long userId) {
        return "https://" + awsProperties.getBucketName() + ".s3.amazonaws.com/" + getAvatarKey(userId);
    }

    private String getAvatarKey(Long userId) {
        return s3RootFolder + "/avatars/" + userId + "/avatar.png";
    }

    @Async
    public CompletableFuture<Void> generateAndUploadAvatar(User user) {
        try {
            BufferedImage avatar = AvatarGenerator.generateAvatar(user.getUsername(), 128);
            uploadAvatarToS3(avatar, user.getId()); // Save as userID.png
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("Error while generating/uploading avatar", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private void uploadAvatarToS3(BufferedImage image, Long userId) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        byte[] imageBytes = os.toByteArray();

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsProperties.getBucketName())
                .key(getAvatarKey(userId))
                .contentType("image/png")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));
    }
}
