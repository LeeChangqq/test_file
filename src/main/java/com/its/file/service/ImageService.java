package com.its.file.service;

import com.its.file.ImageDTO;
import com.its.file.ImageEntity;
import com.its.file.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    public void save(ImageDTO imageDTO) throws IOException {
        MultipartFile imageFile = imageDTO.getImageFile();
        String fileName = imageFile.getOriginalFilename();
        fileName = System.currentTimeMillis() + "_" + fileName;
        String savePath = "D:\\springboot_img\\" + fileName;
        if(!imageFile.isEmpty()){
            imageFile.transferTo(new File(savePath));
        }
        imageDTO.setFileName(fileName);
        ImageEntity imageEntity = ImageEntity.toSaveEntity(imageDTO);
        imageRepository.save(imageEntity);
    }
 }

