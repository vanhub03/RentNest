package com.example.rentnest.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
@Service
public class CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
        return (String) uploadResult.get("secure_url");
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

