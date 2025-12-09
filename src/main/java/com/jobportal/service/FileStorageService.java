package com.jobportal.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FileStorageService {

    // Base folder (relative to app working dir)
    private final Path uploadsRoot = Paths.get("./uploads");

    // subfolder for profile pictures
    private final Path profilePicsFolder = uploadsRoot.resolve("profile-pics");

    public FileStorageService() {
        try {
            if (Files.notExists(uploadsRoot)) {
                Files.createDirectories(uploadsRoot);
            }
            if (Files.notExists(profilePicsFolder)) {
                Files.createDirectories(profilePicsFolder);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create uploads directories", e);
        }
    }

    /**
     * Saves the file to ./uploads/profile-pics and returns the relative path
     * to be stored in the DB (e.g. "profile-pics/xyz.png").
     */
    public String storeProfilePicture(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename());
        // create a safe filename (timestamp + original)
        String filename = System.currentTimeMillis() + "_" + original.replaceAll("[^a-zA-Z0-9.\\-_]", "_");

        Path target = profilePicsFolder.resolve(filename);

        try {
            // Copy file (replace if exists)
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            // Return relative path used in URLs and DB
            return "profile-pics/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }
}
