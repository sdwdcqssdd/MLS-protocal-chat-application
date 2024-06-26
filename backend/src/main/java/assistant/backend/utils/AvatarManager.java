package assistant.backend.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * handling saving avatars
 */
@Component
public class AvatarManager {
    public void updateAvatar(String oldPath, Path newPath, MultipartFile newAvatar) throws IOException {
        if (oldPath != null) {
            File oldAvatar = new File(oldPath);
            oldAvatar.delete();
        }
        newAvatar.transferTo(newPath);
    }
}
