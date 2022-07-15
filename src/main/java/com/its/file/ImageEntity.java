package com.its.file;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "image_table")
public class ImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @Column
    private String fileName;


    public static ImageEntity toSaveEntity(ImageDTO imageDTO) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.setFileName(imageDTO.getFileName());
        return imageEntity;
    }
}