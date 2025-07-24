package com.ivangochev.raceratingapi.runner;

import com.ivangochev.raceratingapi.avatar.AvatarService;
import com.ivangochev.raceratingapi.security.oauth2.OAuth2Provider;
import com.ivangochev.raceratingapi.user.User;
import com.ivangochev.raceratingapi.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class UserAvatarImageCreateJob implements CommandLineRunner {
    @Value("${migration.job.userAvatarCreate.enabled:false}")
    private boolean enabled;
    private final UserRepository userRepository;
    private final AvatarService avatarService;

    public UserAvatarImageCreateJob(UserRepository userRepository, AvatarService avatarService) {
        this.userRepository = userRepository;
        this.avatarService = avatarService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!enabled) return;

        List<User> users = userRepository.findAllByProvider(OAuth2Provider.LOCAL);
        for (User user : users) {
            if (user.getImageUrl() == null) {
                try {
                    log.info("Processing user ID: " + user.getId());
                    avatarService.generateAndUploadAvatar(user).get(); // Generate an avatar and upload to S3.
                    user.setImageUrl(avatarService.getAvatarUrl(user.getId()));
                    userRepository.save(user);
                    log.info("User avatar with ID: " + user.getId() + " has been created");
                } catch (Exception e) {
                    log.error("Error processing user avatar with ID: " + user.getId(), e);
                }
            }
        }

    }
}
