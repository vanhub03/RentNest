package com.example.rentnest.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        return (String) uploadResult.get("secure_url");
    }

    public String uploadContractFile(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if(contentType != null && contentType.startsWith("image/")) {
            return uploadImage(file);
        }
        String publicId = "contracts/" + UUID.randomUUID() + resolveExtension((file.getOriginalFilename()));
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "raw", "public_id", publicId, "access_mode", "public"));
        return (String) uploadResult.get("secure_url");
    }

    private String resolveExtension(String originalFileName){
        if(originalFileName == null){
            return "";
        }
        int dotIndex = originalFileName.lastIndexOf(".");
        if(dotIndex < 0 || dotIndex == originalFileName.length() - 1){
            return "";
        }
        return originalFileName.substring(dotIndex).toLowerCase();
    }

    public String extractPublicId(String imageUrl) {
        String[] parts = imageUrl.split("/");
        String filename = parts[parts.length - 1];
        return filename.substring(0, filename.lastIndexOf('.'));
    }

    public void deleteImageByUrl(String imageUrl) {
        try{
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

