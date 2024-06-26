package assistant.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * configuration class for files
 */
@Configuration
@PropertySource("classpath:resources.properties")
public class ResourcesConfig {

    /**
     * the path where avatars are stored
     */
    @Value("${avatar.path}")
    public String avatarPath;

    /**
     * the path to the default avatar
     */
    @Value("${avatar.default}")
    public String defaultAvatar;

    /**
     * the size limit of user's avatar
     */
    @Value("${avatar.sizeLimit}")
    public Long avatarSizeLimit;

    /**
     * the path where assignment attachments are stored
     */
    @Value("${assignment.attachment.path}")
    public String assignmentAttachmentPath;

    public String getAvatarPath() {
        return avatarPath;
    }

    public String getDefaultAvatar() {
        return defaultAvatar;
    }

    public Long getAvatarSizeLimit() {
        return avatarSizeLimit;
    }

    public String getAssignmentAttachmentPath() {
        return assignmentAttachmentPath;
    }
}